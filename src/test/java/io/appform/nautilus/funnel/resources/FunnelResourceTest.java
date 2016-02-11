package io.appform.nautilus.funnel.resources;

import com.google.common.collect.Maps;
import io.appform.nautilus.funnel.funnel.ESFunnelCalculator;
import io.appform.nautilus.funnel.funnel.Funnel;
import io.appform.nautilus.funnel.funnel.FunnelCalculator;
import io.appform.nautilus.funnel.funnel.FunnelRequest;
import io.appform.nautilus.funnel.model.support.Context;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * @author karims
 */
public class FunnelResourceTest {

    private FunnelResource funnelResource;
    private Context contextMock;
    private FunnelCalculator funnelCalculatorMock;
    private FunnelRequest funnelRequestMock;
    private Map<String, Long> stages;

    @Before
    public void setup() throws Exception {
        contextMock = mock(Context.class);
        funnelCalculatorMock = mock(ESFunnelCalculator.class);
        funnelRequestMock = mock(FunnelRequest.class);
        funnelResource = new FunnelResource(contextMock, funnelCalculatorMock);
        stages = Maps.newHashMap();
    }

    @Test
    public void testFunnelResponseStages() throws Exception {

        doReturn(getSampleFunnel()).when(funnelCalculatorMock).calculate("tenant", contextMock, funnelRequestMock);
        ApiResponse response = funnelResource.response("tenant", funnelRequestMock);
        Funnel funnel = (Funnel) response.getData();
        Assert.assertEquals(stages, funnel.getStages());
    }

    @Test
    public void testFunnelResponseError() throws Exception {

        doReturn(getSampleFunnel()).when(funnelCalculatorMock).calculate("tenant", contextMock, funnelRequestMock);
        ApiResponse response = funnelResource.response("tenant", funnelRequestMock);
        Assert.assertEquals(response.isError(), false);
    }

    @Test
    public void testFunnelResponseException() throws Exception {
        doThrow(new WebApplicationException()).when(funnelCalculatorMock).calculate(anyString(), any(Context.class), any());
        try{
            ApiResponse response = funnelResource.response("tenant", funnelRequestMock);
        }catch (WebApplicationException we){
            Assert.assertEquals(we.getResponse().getStatus(), 500);
        }

    }

    private Funnel getSampleFunnel() {
        stages.put("home", 10L);
        stages.put("booking", 5L);

        Funnel funnel = new Funnel();
        funnel.setStages(stages);

        return funnel;
    }
}
