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
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

/**
 * Bunch of utility APIs
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1/tenants")
@Slf4j
public class TenancyManagementResource {
    private final TenancyManager tenancyManager;

    public TenancyManagementResource(TenancyManager tenancyManager) {
        this.tenancyManager = tenancyManager;
    }

    @GET
    public ApiResponse tenants() {
        try {
            return ApiResponse.builder()
                        .error(false)
                        .data(Collections.singletonMap("tenants", tenancyManager.tenants()))
                        .build();
        } catch (Exception e) {
            log.error("Error getting tenants", e);
            throw new WebApplicationException(
                    Response.status(500)
                            .entity(ApiResponse
                                    .builder()
                                    .error(true)
                                    .data(Collections.singletonMap("message", "Could not get tenants"))
                                    .build())
                            .build()
            );
        }
    }

    @GET
    @Path("/{tenant}/mappings")
    public Object mappings(@PathParam("tenant") final String tenant) throws Exception {
        try {
            return tenancyManager.mappings(tenant);
        } catch (Exception e) {
            log.error("Error getting tenants", e);
            throw new WebApplicationException(
                Response.status(500)
                        .entity(ApiResponse
                                .builder()
                                .error(true)
                                .data(Collections.singletonMap("message", "Could not get tenants"))
                                .build())
                        .build()
            );
        }
    }
}
