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

/**
 * Level where this filter is applied.
 */
public enum FilterLevel {
    /**
     * Filter will be applied to {@link io.appform.nautilus.funnel.model.session.Session} attributes.
     */
    session,

    /**
     * Filter will be applicable to {@link io.appform.nautilus.funnel.model.session.StateTransition} attributes.
     */
    state
}
