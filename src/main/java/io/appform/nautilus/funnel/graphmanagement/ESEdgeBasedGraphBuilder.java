/*
 * Copyright 2016 Santanu Sinha <santanu.sinha@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appform.nautilus.funnel.graphmanagement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.appform.nautilus.funnel.common.ErrorMessageTable;
import io.appform.nautilus.funnel.common.NautilusException;
import io.appform.nautilus.funnel.model.graph.Graph;
import io.appform.nautilus.funnel.model.graph.GraphEdge;
import io.appform.nautilus.funnel.model.graph.GraphNode;
import io.appform.nautilus.funnel.model.session.FlatPath;
import io.appform.nautilus.funnel.model.session.Session;
import io.appform.nautilus.funnel.model.session.StateTransition;
import io.appform.nautilus.funnel.model.support.Context;
import io.appform.nautilus.funnel.utils.Constants;
import io.appform.nautilus.funnel.utils.ESUtils;
import io.appform.nautilus.funnel.utils.TypeUtils;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Graph builder that aggregates the parents anc children based on transitions.
 */
public class ESEdgeBasedGraphBuilder implements GraphBuilder {
    private static final Logger log = LoggerFactory.getLogger(ESGraphBuilder.class);

    @Override
    public Graph build(final String tenant, Context analyticsContext, GraphRequest graphRequest) throws Exception {
        try {
            MultiSearchResponse multiSearchResponse = analyticsContext.getEsConnection()
                    .client()
                    .prepareMultiSearch()
                    .add(
                            analyticsContext
                                    .getEsConnection()
                                    .client()
                                    .prepareSearch(ESUtils.getAllIndicesForTenant(tenant)) //TODO::SELECT RELEVANT INDICES ONLY
                                    .setQuery(
                                            QueryBuilders.boolQuery()
                                                    .filter(QueryBuilders.hasParentQuery(
                                                            TypeUtils.typeName(Session.class),
                                                            ESUtils.query(graphRequest))))
                                    .setTypes(TypeUtils.typeName(StateTransition.class))
                                    .setFetchSource(false)
                                    .setSize(0)
                                    .setIndicesOptions(IndicesOptions.lenientExpandOpen())
                                    .addAggregation(AggregationBuilders
                                            .terms("from_nodes")
                                            .field("from")
                                            .subAggregation(
                                                    AggregationBuilders
                                                            .terms("to_nodes")
                                                            .field("to")
                                                            .size(0)
                                                            .subAggregation(
                                                                    AggregationBuilders
                                                                        .terms("pathBreakup")
                                                                        .field("normalizedPath")
                                                                        .size(0)
                                                            )
                                            )))
                    .add(
                            analyticsContext.getEsConnection()
                                    .client()
                                    .prepareSearch(ESUtils.getAllIndicesForTenant(tenant))
                                    .setQuery(ESUtils.query(graphRequest))
                                    .setTypes(TypeUtils.typeName(Session.class))
                                    .setFetchSource(false)
                                    .setSize(0)
                                    .setIndicesOptions(IndicesOptions.lenientExpandOpen())
                                    .addAggregation(AggregationBuilders
                                            .terms("paths")
                                            .field("normalizedPath")
                                            .size(0)
                                    ))
                    .execute()
                    .actionGet();


            List<GraphEdge> edges = Lists.newArrayList();


            {
                SearchResponse edgeGroupingResponse = multiSearchResponse.getResponses()[0].getResponse();
                Aggregations aggregations = edgeGroupingResponse.getAggregations();
                Terms terms = aggregations.get("from_nodes");

                for (Terms.Bucket fromBucket : terms.getBuckets()) {
                    final String fromNodeName = fromBucket.getKey().toString();
                    Terms toTerms = fromBucket.getAggregations().get("to_nodes");
                    for (Terms.Bucket toBucket : toTerms.getBuckets()) {
                        Terms paths = toBucket.getAggregations().get("pathBreakup");
                        List<FlatPath> pathList =
                        paths.getBuckets()
                                .stream()
                                .map((Terms.Bucket pathBucket) -> FlatPath.builder()
                                        .path(pathBucket.getKey().toString())
                                        .count(pathBucket.getDocCount())
                                        .build())
                                .collect(Collectors.toCollection(ArrayList::new));
                        final String toNodeName = toBucket.getKey().toString();
                        edges.add(GraphEdge
                                .builder()
                                .from(fromNodeName)
                                .to(toNodeName)
                                .value(toBucket.getDocCount())
                                .paths(pathList)
                                .build());
                    }
                }
            }

            Map<String, GraphNode> vertices = Maps.newHashMap();
            int nodeCounter = 0;
            ImmutableList.Builder<FlatPath> flatPathListBuilder = ImmutableList.builder();
            {
                SearchResponse response = multiSearchResponse.getResponses()[1].getResponse();
                Aggregations aggregations = response.getAggregations();
                Terms terms = aggregations.get("paths");
                for (Terms.Bucket buckets : terms.getBuckets()) {
                    final String flatPath = buckets.getKey().toString();
                    final long count = buckets.getDocCount();
                    final String pathNodes[] = flatPath.split(Constants.PATH_STATE_SEPARATOR);
                    flatPathListBuilder.add(FlatPath.builder().path(flatPath).count(count).build());
                    for (final String pathNode : pathNodes) {
                        if (!vertices.containsKey(pathNode)) {
                            vertices.put(pathNode, GraphNode.builder().id(nodeCounter++).name(pathNode).build());
                        }

                    }
                }
            }

            ArrayList<GraphNode> verticesList = new ArrayList<>(vertices.values());
            verticesList.sort((GraphNode lhs, GraphNode rhs) -> Integer.compare(lhs.getId(), rhs.getId()));
            return Graph.builder()
                    .vertices(verticesList)
                    .edges(edges)
                    .paths(flatPathListBuilder.build())
                    .build();
        }  catch (Exception e) {
            log.error("Error running grouping: ", e);
            throw new NautilusException(
                    ErrorMessageTable.ErrorCode.ANALYTICS_ERROR, e);
        }
    }
}
