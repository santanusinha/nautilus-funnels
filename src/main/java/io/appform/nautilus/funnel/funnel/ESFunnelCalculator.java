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

package io.appform.nautilus.funnel.funnel;

import com.google.common.collect.Maps;
import io.appform.nautilus.funnel.model.session.Session;
import io.appform.nautilus.funnel.model.support.Context;
import io.appform.nautilus.funnel.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.Map;

/**
 * Funnel calculation based on ES Aggregation
 */
@Slf4j
public class ESFunnelCalculator implements FunnelCalculator {

    @Override
    public Funnel calculate(String tenant, Context context, FunnelRequest funnelRequest) throws Exception {

        SearchRequestBuilder request = context.getEsConnection()
                .client()
                .prepareSearch(ESUtils.getAllIndicesForTenant(tenant))
                .setQuery(ESUtils.query(funnelRequest))
                .setTypes(TypeUtils.typeName(Session.class))
                .setFetchSource(false)
                .setSize(0)
                .setIndicesOptions(IndicesOptions.lenientExpandOpen())
                .addAggregation(AggregationBuilders
                        .terms("paths")
                        .field(Constants.NORMALIZED_PATH_FIELD_NAME)
                        .size(0)
                );
        log.info("Generated query for filter request: {}", request);
        SearchResponse response = request
                .execute()
                .actionGet();
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get("paths");
        Map<String, Long> funnelStages = Maps.newLinkedHashMap();
        funnelRequest.getStates().stream().forEach(stage -> funnelStages.put(stage, 0L));
        Map<String, String> regexes = RegexUtils.separateRegexes(funnelRequest.getStates());
        for (Terms.Bucket buckets : terms.getBuckets()) {
            final String flatPath = buckets.getKey().toString();
            final long count = buckets.getDocCount();
            regexes.entrySet().stream().filter(entry -> flatPath.matches(entry.getKey())).forEach(entry -> {
                final String[] stage = PathUtils.transformBack(flatPath).split(Constants.PATH_STATE_SEPARATOR);
                for (final String key : stage) {
                    if (funnelStages.containsKey(key)) {
                        funnelStages.put(key, funnelStages.get(key) + count);
                    }
                }
            });
        }
        return Funnel.builder()
                .stages(funnelStages)
                .build();
    }
}
