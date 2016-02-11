package io.appform.nautilus.funnel.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import io.appform.nautilus.funnel.administration.TenancyManager;
import io.appform.nautilus.funnel.model.session.SessionActivitySet;
import io.appform.nautilus.funnel.model.support.Context;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * @author karims
 */
public class TenancyManagementResourceTest {

    private TenancyManager tenancyManagerMock;
    private TenancyManagementResource tenancyManagementResource;
    private List<String> tenantList;
    private Map<String, Map<String, String>> mappings = Maps.newHashMap();
    private List<String> states;
    private ObjectMapper mapper;

    @Before
    public void setup() throws Exception {
        tenancyManagerMock = mock(TenancyManager.class);
        tenancyManagementResource = new TenancyManagementResource(tenancyManagerMock);
        mapper = new ObjectMapper();
    }

    @Test
    public void testGraphResponse() throws Exception {
        doReturn(getTenants()).when(tenancyManagerMock).tenants();
        ApiResponse response = tenancyManagementResource.tenants();
        Map<String, List<String>> actualOutput = (Map<String, List<String>>) response.getData();

        Assert.assertEquals(Collections.singletonMap("tenants", tenantList), actualOutput);

    }

    @Test
    public void testStatesResponse() throws Exception {
        String tenant = "test_tenant";
        doReturn(getTenantStates()).when(tenancyManagerMock).states(tenant);
        ApiResponse response = tenancyManagementResource.states(tenant);
        Map<String, List<String>> actualOutput = mapper.convertValue(response.getData(), Map.class);
        Assert.assertEquals(Collections.singletonMap("states", states), actualOutput);
    }

    @Test
    public void testStatesResponseException() throws Exception {
        doThrow(new WebApplicationException()).when(tenancyManagerMock).states(any());
        try{
            ApiResponse response = tenancyManagementResource.states("test");
        }catch (WebApplicationException we){
            Assert.assertEquals(we.getResponse().getStatus(), 500);
        }

    }

    @Test
    public void testMappingsResponseException() throws Exception {
        doThrow(new WebApplicationException()).when(tenancyManagerMock).mappings(anyString());
        try{
            ApiResponse response = tenancyManagementResource.mappings("test");
        }catch (WebApplicationException we){
            Assert.assertEquals(we.getResponse().getStatus(), 500);
        }

    }

    @Test
    public void testTenantsResponseException() throws Exception {
        doThrow(new WebApplicationException()).when(tenancyManagerMock).tenants();
        try{
            ApiResponse response = tenancyManagementResource.tenants();
        }catch (WebApplicationException we){
            Assert.assertEquals(we.getResponse().getStatus(), 500);
        }

    }
    private List<String> getTenantStates() {
        states = new ArrayList<>();
        states.add("home");
        states.add("booking");
        states.add("completed");
        states.add("cancelled");

        return states;
    }


    private List<String> getTenants() {
        tenantList = new ArrayList<>();
        tenantList.add("tenant1");
        tenantList.add("tenant2");
        tenantList.add("tenant3");
        return tenantList;
    }
}
