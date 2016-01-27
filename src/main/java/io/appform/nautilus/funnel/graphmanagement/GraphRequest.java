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
import io.appform.nautilus.funnel.model.filter.FilteredRequest;
import io.appform.nautilus.funnel.model.filter.TimeWindow;
import io.dropwizard.util.Duration;
import lombok.*;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * A graph request.
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class GraphRequest extends FilteredRequest {

    @Builder
    public GraphRequest(@Singular List<Filter> sessionFilters,
                        @Singular List<Filter> stateFilters,
                        TimeWindow timeWindow) {
        super(sessionFilters, stateFilters, timeWindow);
    }

/*    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        final String data = "{\n" +
                "    \"sessionFilters\" : [\n" +
                "            {\n" +
                "                \"filter\" : \"exists\",\n" +
                "                \"attribute\" : \"color\"\n" +
                "            }\n" +
                "        ],\n" +
                "    \"stateFilters\" : [\n" +
                "            {\n" +
                "                \"filter\" : \"equals\",\n" +
                "                \"attribute\" : \"channel\",\n" +
                "                \"value\" : \"mobile\"\n" +
                "            }\n" +
                "        ],\n" +
                "    \"timeWindow\" : {\n" +
                "        \"duration\" : \"30m\"\n" +
                "    }    \n" +
                "}";
        GraphRequest r = objectMapper.readValue(data, GraphRequest.class);
        System.out.println(r);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<GraphRequest>> res = validator.validate(r);
        System.out.println(res);
    }*/
}
