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

import io.appform.nautilus.funnel.model.filter.Filter;
import io.appform.nautilus.funnel.model.filter.FilterVisitor;
import io.appform.nautilus.funnel.model.filter.TimeWindow;
import io.appform.nautilus.funnel.model.filter.impl.general.*;
import io.appform.nautilus.funnel.model.filter.impl.numeric.*;
import io.appform.nautilus.funnel.model.filter.impl.string.Contains;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;

import static io.appform.nautilus.funnel.utils.ESUtils.toAttributeFieldName;

/**
 * Parses filters and time window in {@link io.appform.nautilus.funnel.model.filter.FilteredRequest}
 * and generates elasticsearch queries.
 */
public class ESFilterGenerator implements FilterVisitor {
    private BoolQueryBuilder rootBuilder = QueryBuilders.boolQuery();

    public QueryBuilder build(TimeWindow window) {
        return QueryBuilders.rangeQuery(window.getTimeField())
                .from(window.getStart().getMillis())
                .to(window.getEnd().getMillis());
    }

    public BoolQueryBuilder build(List<Filter> filters) throws Exception {
        if(null != filters) {
            for (Filter filter : filters) {
                filter.accept(this);
            }
        }
        return rootBuilder;
    }

    @Override
    public void visit(Between between) throws Exception {
        rootBuilder.filter(
                QueryBuilders.rangeQuery(between.getAttribute())
                                .from(between.getFrom())
                                .to(between.getTo())
                                .includeLower(true)
                                .includeUpper(true));
    }

    @Override
    public void visit(Equals equals) throws Exception {
        rootBuilder.filter(QueryBuilders.termQuery(toAttributeFieldName(equals.getAttribute()), equals.getValue()));
    }

    @Override
    public void visit(NotEquals notEquals) throws Exception {
        rootBuilder.filter(
                QueryBuilders.boolQuery()
                        .mustNot(QueryBuilders.termQuery(toAttributeFieldName(notEquals.getAttribute()), notEquals.getValue())));
    }

    @Override
    public void visit(Contains stringContainsElement) throws Exception {
        rootBuilder.filter(
                QueryBuilders.queryStringQuery(stringContainsElement.getValue())
                        .field(toAttributeFieldName(stringContainsElement.getAttribute())));
    }

    @Override
    public void visit(GreaterThan greaterThan) throws Exception {
        rootBuilder.filter(
                QueryBuilders.rangeQuery(toAttributeFieldName(greaterThan.getAttribute()))
                        .gt(greaterThan.getValue()));
    }

    @Override
    public void visit(GreaterEqual greaterEqual) throws Exception {
        rootBuilder.filter(
                QueryBuilders.rangeQuery(toAttributeFieldName(greaterEqual.getAttribute()))
                                .gte(greaterEqual.getValue()));
    }

    @Override
    public void visit(LessThan lessThan) throws Exception {
        rootBuilder.filter(
                QueryBuilders.rangeQuery(toAttributeFieldName(lessThan.getAttribute()))
                        .lt(lessThan.getValue()));
    }

    @Override
    public void visit(LessEqual lessEqual) throws Exception {
        rootBuilder.filter(
                QueryBuilders.rangeQuery(toAttributeFieldName(lessEqual.getAttribute()))
                        .lte(lessEqual.getValue()));
    }

    @Override
    public void visit(Any any) throws Exception {
        rootBuilder.filter(
                QueryBuilders.matchAllQuery());
    }

    @Override
    public void visit(In in) throws Exception {
        rootBuilder.filter(
                QueryBuilders.termsQuery(toAttributeFieldName(in.getAttribute()), in.getValues()));
    }

    @Override
    public void visit(Exists exists) throws Exception {
        rootBuilder.filter(
                QueryBuilders.existsQuery(toAttributeFieldName(exists.getAttribute())));
    }

    @Override
    public void visit(Missing missing) throws Exception {
        rootBuilder.filter(QueryBuilders.missingQuery(toAttributeFieldName(missing.getAttribute())));
    }
}
