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

/**
 * Created by santanu on 4/7/15.
 */
public class NautilusException extends Exception {
    final ErrorMessageTable.ErrorCode errorCode;

    public NautilusException(Throwable t, ErrorMessageTable.ErrorCode errorCode, final Object... args) {
        super(ErrorMessageTable.errorMessage(errorCode, args), t);
        this.errorCode = errorCode;
    }
    public NautilusException(ErrorMessageTable.ErrorCode errorCode, final Object... args) {
        super(ErrorMessageTable.errorMessage(errorCode, args));
        this.errorCode = errorCode;
    }

    public ErrorMessageTable.ErrorCode getErrorCode() {
        return errorCode;
    }
}
