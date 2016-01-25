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

package io.appform.nautilus.funnel.resources;

import io.appform.nautilus.funnel.administration.TenancyManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Bunch of utility APIs
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1/tenants")
public class TenancyManagementResource {
    private final TenancyManager tenancyManager;

    public TenancyManagementResource(TenancyManager tenancyManager) {
        this.tenancyManager = tenancyManager;
    }

    @GET
    public Map<String, List<String>> tenants() {
        return Collections.singletonMap("tenants", tenancyManager.tenants());
    }

    @GET
    @Path("/{tenant}/mappings")
    public Object mappings(@PathParam("tenant") final String tenant) throws Exception {
        return tenancyManager.mappings(tenant);
    }
}
