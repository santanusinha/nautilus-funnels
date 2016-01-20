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

package io.appform.nautilus.funnel.utils;

import io.appform.nautilus.funnel.common.ErrorMessageTable;
import io.appform.nautilus.funnel.common.NautilusException;
import io.appform.nautilus.funnel.model.filter.TimeWindow;
import org.joda.time.DateTime;

public class TimeWindowNormalizer {
    public static TimeWindow normalize(TimeWindow timeWindow) throws Exception {
        return normalize(timeWindow, DateTime.now());
    }

    public static TimeWindow normalize(TimeWindow timeWindow, final DateTime currentTime) throws Exception {

        if(null != timeWindow.getStart()) {
            if(null != timeWindow.getEnd()) {
                //If start and end are both present then return them back ordered
                return orderedWindow(timeWindow.getStart(), timeWindow.getEnd())
                        .timeField(timeWindow.getTimeField())
                        .build();
            }
            if(null != timeWindow.getDuration()) {
                //If end is not specified, then return a window from start to duration
                final DateTime endTime = timeWindow.getStart().plus(timeWindow.getDuration().toMilliseconds());
                return orderedWindow(timeWindow.getStart(), endTime)
                        .timeField(timeWindow.getTimeField())
                        .build();
            }
            else {
                //Return a window from start to current time
                return orderedWindow(timeWindow.getStart(), currentTime)
                        .timeField(timeWindow.getTimeField())
                        .build();
            }
        }
        else {
            if(null != timeWindow.getEnd()) {
                if(null != timeWindow.getDuration()) {
                    //If only end is specified and duration is there return an ordered windonw deduced from them
                    final DateTime startTime = timeWindow.getEnd()
                                                         .minus(timeWindow.getDuration().toMilliseconds());
                    return orderedWindow(startTime, timeWindow.getEnd())
                            .timeField(timeWindow.getTimeField())
                            .build();
                }
                else {
                    //Return a window from start to current time
                    return orderedWindow(timeWindow.getEnd(), currentTime)
                            .timeField(timeWindow.getTimeField())
                            .build();
                }
            }
            else {
                if(null != timeWindow.getDuration()) {
                    return orderedWindow(currentTime, currentTime.minus(timeWindow.getDuration().toMilliseconds()))
                            .timeField(timeWindow.getTimeField())
                            .build();
                }
            }
        }
        throw new NautilusException(ErrorMessageTable.ErrorCode.INVALID_TIME_WINDOW, "No start, end, duration");
    }

    private static TimeWindow.TimeWindowBuilder orderedWindow(final DateTime start, final DateTime end) {
        if (start.isBefore(end)) {
            return TimeWindow.builder().start(start).end(end);
        }
        return TimeWindow.builder().start(end).end(start);
    }
}
