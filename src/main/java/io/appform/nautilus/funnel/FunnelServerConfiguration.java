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

package io.appform.nautilus.funnel;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.appform.nautilus.funnel.elasticsearch.ESConfiguration;
import io.dropwizard.Configuration;
import lombok.Getter;

import javax.validation.Valid;

/**
 * Configuration to be read in from config.yml.
 */

public class FunnelServerConfiguration extends Configuration {
    /**
     * Elasticsearch configuration
     */
    @Valid
    @Getter
    @JsonProperty
    private ESConfiguration elasticsearch = new ESConfiguration();
}
