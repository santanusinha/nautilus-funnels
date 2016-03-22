package io.appform.nautilus.funnel.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.nautilus.funnel.graphmanagement.GraphRequest;
import io.appform.nautilus.funnel.model.session.SessionActivity;
import io.appform.nautilus.funnel.model.session.SessionActivitySet;
import io.appform.nautilus.funnel.model.support.Context;
import io.appform.nautilus.funnel.sessionmanagement.SessionActivityHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * @author karims
 */
public class ActivityResourceTest {

    private ActivityResource activityResource;
    private ObjectMapper mapper;
    private SessionActivityHandler activityHandler;

    @Before
    public void setup() throws Exception {
        activityHandler = mock(SessionActivityHandler.class);
        mapper = new ObjectMapper();
        activityResource = new ActivityResource(activityHandler);
    }

    @Test
    public void shouldSave() throws Exception {
        SessionActivity homeActivity = new SessionActivity();
        homeActivity.setState("home");
        homeActivity.setTimestamp(System.currentTimeMillis());

        SessionActivitySet sessionActivitySet = new SessionActivitySet();
        sessionActivitySet.setSessionId("id_1");
        sessionActivitySet.setSessionStartTime(System.currentTimeMillis());

        List<SessionActivity> activities = new ArrayList<>();
        activities.add(homeActivity);
        sessionActivitySet.setActivities(activities);

        ApiResponse response = activityResource.save("tenant", sessionActivitySet);
        Assert.assertEquals(false, response.isError());
    }

    @Test
    public void testGraphResponseException() throws Exception {
        doThrow(new WebApplicationException()).when(activityHandler).handle(anyString(), any(SessionActivitySet.class));
        try{
            SessionActivitySet sessionActivitySet = mock(SessionActivitySet.class);
            ApiResponse response = activityResource.save("tenant", sessionActivitySet);
        }catch (WebApplicationException we){
            Assert.assertEquals(we.getResponse().getStatus(), 500);
        }

    }

}
