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
package io.appform.nautilus.funnel.model.filter.impl.general;

import io.appform.nautilus.funnel.model.filter.Filter;
import io.appform.nautilus.funnel.model.filter.FilterType;
import io.appform.nautilus.funnel.model.filter.FilterVisitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Any extends Filter {

    public Any() {
        super(FilterType.any);
    }

    @Builder
    public Any(String field) {
        super(FilterType.any, field);
    }

    @Override
    public void accept(FilterVisitor visitor) throws Exception {
        visitor.visit(this);
    }

}

