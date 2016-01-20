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
import io.appform.nautilus.funnel.utils.TimeWindowNormalizer;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;

/**
 * Created by santanu.s on 20/01/16.
 */
public class ESFilterGenerator implements FilterVisitor {
    private BoolQueryBuilder rootBuilder = QueryBuilders.boolQuery();

    public QueryBuilder build(List<Filter> filters) throws Exception {
        return build(filters, null);
    }

    public QueryBuilder build(List<Filter> filters, final TimeWindow timeWindow) throws Exception {
        if(null != filters) {
            for (Filter filter : filters) {
                filter.accept(this);
            }
        }
        if(null != timeWindow) {
            final TimeWindow window = TimeWindowNormalizer.normalize(timeWindow);
            rootBuilder.filter(
                        QueryBuilders.rangeQuery(window.getTimeField())
                                    .from(window.getStart().getMillis())
                                    .to(window.getEnd().getMillis()));
        }
        return rootBuilder;
    }
}
