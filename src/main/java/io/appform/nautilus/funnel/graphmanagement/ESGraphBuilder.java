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
import com.google.common.collect.Maps;
import io.appform.nautilus.funnel.common.ErrorMessageTable;
import io.appform.nautilus.funnel.common.NautilusException;
import io.appform.nautilus.funnel.model.graph.Graph;
import io.appform.nautilus.funnel.model.graph.GraphEdge;
import io.appform.nautilus.funnel.model.graph.GraphNode;
import io.appform.nautilus.funnel.model.graph.Paths;
import io.appform.nautilus.funnel.model.session.FlatPath;
import io.appform.nautilus.funnel.model.session.StateTransition;
import io.appform.nautilus.funnel.model.support.Context;
import io.appform.nautilus.funnel.utils.ESUtils;
import io.appform.nautilus.funnel.utils.TypeUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by santanu on 16/7/15.
 */
public class ESGraphBuilder implements GraphBuilder {
    private static final Logger log = LoggerFactory.getLogger(ESGraphBuilder.class);

    @Override
    public Graph build(final String tenant, Context context, GraphRequest graphRequest) throws Exception {
        try {
            TermsBuilder rootBuilder = null;
            TermsBuilder termsBuilder
                    = AggregationBuilders
                        .terms("paths")
                        .field("normalizedPath");

            SearchRequestBuilder query = context
                    .getEsConnection()
                    .client()
                    .prepareSearch(ESUtils.getAllIndicesForTenant(tenant))
                    .setQuery(ESUtils.query(graphRequest))
                    .setTypes(TypeUtils.typeName(StateTransition.class))
                    .setFetchSource(false)
                    .setSize(0)
                    .addAggregation(termsBuilder);

            SearchResponse response = query.execute().actionGet();
            Aggregations aggregations = response.getAggregations();
            Terms terms = aggregations.get("paths");
            Map<String, GraphNode> vertices = Maps.newHashMap();
            Map<String, GraphEdge> edges = Maps.newHashMap();
            int nodeCounter = 0;
            ImmutableList.Builder<FlatPath> flatPathListBuilder = ImmutableList.builder();
            for(Terms.Bucket buckets : terms.getBuckets()) {
                final String flatPath = buckets.getKey().toString();
                final long count = buckets.getDocCount();
                final String pathNodes[] = flatPath.split("->");
                GraphNode lastNode = null;
                flatPathListBuilder.add(FlatPath.builder().path(flatPath).count(count).build());
                for(final String pathNode : pathNodes) {
                    if (!vertices.containsKey(pathNode)) {
                        vertices.put(pathNode, GraphNode.builder().id(nodeCounter++).name(pathNode).build());
                    }
                    GraphNode vertex = vertices.get(pathNode);
                    if(null != lastNode) {
                        final String edgeKey = String.format("%s->%s", lastNode.getName(), vertex.getName());
                        if(!edges.containsKey(edgeKey)) {
                            GraphEdge edge = GraphEdge.builder()
                                    .from(lastNode.getName())
                                    .to(vertex.getName())
                                    .value(count)
                                    .build();
                            edges.put(edgeKey, edge);
                        }
                        else {
                            final GraphEdge existingEdge = edges.get(edgeKey);
                            existingEdge.setValue(existingEdge.getValue() + count);
                        }
                    }
                    lastNode = vertex;
                }
            }
            return Graph.builder()
                    .vertices(ImmutableList.copyOf(vertices.values()))
                    .edges(ImmutableList.copyOf(edges.values()))
                    //.paths(flatPathListBuilder.build())
                    .build();
        }  catch (Exception e) {
            log.error("Error running grouping: ", e);
            throw new NautilusException(
                    ErrorMessageTable.ErrorCode.ANALYTICS_ERROR, e);
        }

    }

    @Override
    public Paths build(String tenant, Context context, PathsRequest pathsRequest) throws Exception {
        return null;
    }
}
