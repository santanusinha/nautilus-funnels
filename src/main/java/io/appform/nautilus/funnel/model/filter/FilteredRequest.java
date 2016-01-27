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

package io.appform.nautilus.funnel.model.filter;

import io.dropwizard.util.Duration;
import lombok.*;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public abstract class FilteredRequest {
    @NotNull
    @Valid
    private List<Filter> sessionFilters = Collections.emptyList();

    @NotNull
    @Valid
    private List<Filter> stateFilters = Collections.emptyList();

    private TimeWindow timeWindow = TimeWindow.builder()
            .start(DateTime.now())
            .duration(Duration.hours(-24))
            .timeField("timestamp")
            .build();

    protected FilteredRequest() {

    }

    protected FilteredRequest(List<Filter> sessionFilters,
                              List<Filter> stateFilters,
                              TimeWindow timeWindow) {
        this.sessionFilters = sessionFilters;
        this.stateFilters = stateFilters;
        this.timeWindow = timeWindow;
    }
}
