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
package io.appform.nautilus.funnel.model.filter.impl.numeric;

import io.appform.nautilus.funnel.model.filter.Filter;
import io.appform.nautilus.funnel.model.filter.FilterType;
import io.appform.nautilus.funnel.model.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Filter to select between to two numeric values
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Between extends Filter {
    @NotNull
    private Number from;
    @NotNull
    private Number to;

    public Between() {
        super(FilterType.between);
    }

    public Between(String field, Number from, Number to) {
        super(FilterType.between, field);
        this.from = from;
        this.to = to;
    }

    @Override
    public void accept(FilterVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
