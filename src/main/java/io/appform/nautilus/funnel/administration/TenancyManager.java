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

package io.appform.nautilus.funnel.administration;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.appform.nautilus.funnel.elasticsearch.ESConnection;
import io.appform.nautilus.funnel.model.session.Session;
import io.appform.nautilus.funnel.model.session.StateTransition;
import io.appform.nautilus.funnel.utils.Constants;
import io.appform.nautilus.funnel.utils.ESUtils;
import io.appform.nautilus.funnel.utils.TypeUtils;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tenancy management
 */
public class TenancyManager {
    private static final String AGGREGATION_NAME = "tenants";
    private final ESConnection connection;
    private ObjectMapper mapper;

    public TenancyManager(ESConnection connection, ObjectMapper mapper) {
        this.connection = connection;
        this.mapper = mapper;
    }

    public List<String> tenants() {
        SearchResponse response = connection.client()
                .prepareSearch(ESUtils.getAllIndices())
                .setIndicesOptions(IndicesOptions.lenientExpandOpen())
                .setTypes(TypeUtils.typeName(Session.class))
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms(AGGREGATION_NAME).field("tenant"))
                .setSize(0)
                .execute()
                .actionGet();
        if(response.status() == RestStatus.OK && null != response.getAggregations()) {
            Terms terms = response.getAggregations().get(AGGREGATION_NAME);
            return terms.getBuckets().stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toCollection(ArrayList::new));
        }
        return Collections.emptyList();
    }

    public Map<String, Map<String, String>> mappings(final String tenant) throws Exception {
        GetMappingsResponse response = connection.client()
                .admin()
                .indices()
                .prepareGetMappings(ESUtils.getAllIndicesForTenant(tenant))
                .execute()
                .actionGet();
        Map<String, Map<String, String>> mappings = Maps.newHashMap();
        for (ObjectCursor<String> index : response.getMappings().keys()) {
            updateMappingsForType(response, index, mappings, Session.class);
            updateMappingsForType(response, index, mappings, StateTransition.class);
            //mappings.addAll(mappingParser.getFieldMappings(mappingData));
            //System.out.println(typeMeta);
        }
        return mappings;
    }

    public List<String> states(final String tenant) {
        return ESUtils.terms(tenant, StateTransition.class, "from", connection);
    }

    private void updateMappingsForType(GetMappingsResponse response, ObjectCursor<String> index, Map<String, Map<String, String>> mappings, Class<?> entity) throws Exception {
        final String typeName = TypeUtils.typeName(entity);
        if(!mappings.containsKey(typeName)) {
            mappings.put(typeName, Maps.<String, String>newHashMap());
        }
        Map<String, String> mappingsForType = mappings.get(typeName);
        mappingsForType.putAll(readMappings(response, index, typeName));
    }

    private Map<String, String> readMappings(GetMappingsResponse response, ObjectCursor<String> index, String typeName) throws Exception {
        ImmutableMap.Builder<String, String> type = ImmutableMap.builder();
        MappingMetaData mappingData = response.mappings().get(index.value).get(typeName);
        JsonNode typeMeta = mapper.readTree(mappingData.source().toString()).get(typeName).get("properties");
        if(typeMeta.has(Constants.ATTRIBUTE_FIELD_NAME)) {
            JsonNode attributeMeta = typeMeta.get(Constants.ATTRIBUTE_FIELD_NAME);
            Iterator<Map.Entry<String, JsonNode>> elements = attributeMeta.get("properties").fields();
            while (elements.hasNext()) {
                Map.Entry<String, JsonNode> attribute = elements.next();
                type.put(attribute.getKey(), attribute.getValue().get("type").asText());
            }
        }
        return type.build();
    }
}
