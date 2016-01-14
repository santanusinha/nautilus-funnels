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

package io.appform.nautilus.funnel.common;

public class ErrorMessageTable {
    public enum ErrorCode {
        ENTITY_SAVE_ERROR(1001, "Error saving entity. ID=%s, Type=%s"),
        ENTITY_BULK_SAVE_ERROR(1002, "Error saving entities."),

        INVALID_TIME_WINDOW(1002, "An invalid time window was sent in the request: %s"),

        ANALYTICS_ERROR(2001, "Error running analytics");

        private final int errorCode;
        private final String errorMessageTemplate;

        ErrorCode(int errorCode, String errorMessageTemplate) {
            this.errorCode = errorCode;
            this.errorMessageTemplate = errorMessageTemplate;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessageTemplate() {
            return errorMessageTemplate;
        }
    }

    public static String errorMessage(final ErrorCode errorCode, final Object... args) {
        return String.format(errorCode.getErrorMessageTemplate(), args);
    }

}
