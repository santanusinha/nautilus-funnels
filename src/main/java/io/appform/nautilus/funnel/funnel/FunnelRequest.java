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

import io.appform.nautilus.funnel.model.filter.Filter;
import io.appform.nautilus.funnel.model.filter.FilteredRequest;
import io.appform.nautilus.funnel.model.filter.TimeWindow;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Request to build a funnel.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class FunnelRequest extends FilteredRequest {
    @NotNull
    @NotEmpty
    private List<String> states;
    @Builder
    public FunnelRequest(@Singular List<Filter> sessionFilters,
                         @Singular List<Filter> stateFilters,
                         TimeWindow timeWindow,
                         @Singular List<String> states) {
        super(sessionFilters, stateFilters, timeWindow);
        this.states = states;
    }

}
