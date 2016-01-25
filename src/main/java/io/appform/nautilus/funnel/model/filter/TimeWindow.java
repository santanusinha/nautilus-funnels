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

import io.appform.nautilus.funnel.utils.Constants;
import io.dropwizard.util.Duration;
import lombok.*;
import org.joda.time.DateTime;

/**
 * Abstract base class for a window of time
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TimeWindow {
    @Getter
    @Setter
    private DateTime start;

    @Getter
    @Setter
    private DateTime end;

    @Getter
    @Setter
    private Duration duration;

    @Getter
    @Setter
    private String timeField = Constants.TIMESTAMP_FIELD_NAME;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeWindow that = (TimeWindow) o;

        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        return !(end != null ? !end.equals(that.end) : that.end != null);

    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
