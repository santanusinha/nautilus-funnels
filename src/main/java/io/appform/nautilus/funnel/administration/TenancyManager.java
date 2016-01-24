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

import io.appform.nautilus.funnel.elasticsearch.ESConnection;
import io.appform.nautilus.funnel.model.session.Session;
import io.appform.nautilus.funnel.utils.Constants;
import io.appform.nautilus.funnel.utils.ESUtils;
import io.appform.nautilus.funnel.utils.TypeUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by santanu.s on 23/01/16.
 */
public class TenancyManager {
    private static final String AGGREGATION_NAME = "tenants";
    private final ESConnection connection;

    public TenancyManager(ESConnection connection) {
        this.connection = connection;
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
        if(response.status() == RestStatus.OK) {
            Terms terms = response.getAggregations().get(AGGREGATION_NAME);
            return terms.getBuckets().stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toCollection(ArrayList::new));
        }
        return Collections.emptyList();
    }
}
