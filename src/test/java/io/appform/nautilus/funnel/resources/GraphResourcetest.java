package io.appform.nautilus.funnel.resources;

import io.appform.nautilus.funnel.graphmanagement.GraphBuilder;
import io.appform.nautilus.funnel.graphmanagement.GraphRequest;
import io.appform.nautilus.funnel.graphmanagement.PathsRequest;
import io.appform.nautilus.funnel.model.graph.Graph;
import io.appform.nautilus.funnel.model.graph.GraphEdge;
import io.appform.nautilus.funnel.model.graph.GraphNode;
import io.appform.nautilus.funnel.model.graph.Paths;
import io.appform.nautilus.funnel.model.session.FlatPath;
import io.appform.nautilus.funnel.model.support.Context;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * @author karims
 */
public class GraphResourceTest {
    private GraphResource graphResource;
    private Context contextMock;
    private GraphBuilder graphBuilderMock;
    private GraphRequest graphRequestMock;
    private List<GraphEdge> edges;
    private List<GraphNode> vertices;
    private PathsRequest pathsRequestMock;
    private List<GraphNode> pathVertices;
    private List<FlatPath> paths;

    @Before
    public void setup() throws Exception {
        contextMock = mock(Context.class);
        graphBuilderMock = mock(GraphBuilder.class);
        graphRequestMock = mock(GraphRequest.class);
        pathsRequestMock = mock(PathsRequest.class);
        graphResource = new GraphResource(contextMock, graphBuilderMock);
        edges = new ArrayList<>();
        vertices = new ArrayList<>();
        paths = new ArrayList<>();
        pathVertices = new ArrayList<>();
    }

    @Test
    public void testGraphResponse() throws Exception {
        doReturn(getSampleGraph()).when(graphBuilderMock).build("tenant", contextMock, graphRequestMock);
        ApiResponse response = graphResource.calculateGraph("tenant", graphRequestMock);
        Graph outputGraph = (Graph) response.getData();

        Assert.assertEquals(edges, outputGraph.getEdges());
        Assert.assertEquals(vertices, outputGraph.getVertices());

    }

    @Test
    public void testPathResponse() throws Exception {
        doReturn(getSamplePath()).when(graphBuilderMock).build("tenant", contextMock, pathsRequestMock);
        ApiResponse response = graphResource.calculatePaths("tenant", pathsRequestMock);
        Paths outputPaths = (Paths) response.getData();

        Assert.assertEquals(paths, outputPaths.getPaths());
        Assert.assertEquals(pathVertices, outputPaths.getVertices());

    }

    @Test
    public void testGraphResponseException() throws Exception {
        doThrow(new WebApplicationException()).when(graphBuilderMock).build(anyString(), any(Context.class), any(GraphRequest.class));
        try{
            ApiResponse response = graphResource.calculateGraph("tenant", graphRequestMock);
        }catch (WebApplicationException we){
            Assert.assertEquals(we.getResponse().getStatus(), 500);
        }

    }

    @Test
    public void testPathsResponseException() throws Exception {
        doThrow(new WebApplicationException()).when(graphBuilderMock).build(anyString(), any(Context.class), any(PathsRequest.class));
        try{
            ApiResponse response = graphResource.calculatePaths("tenant", pathsRequestMock);
        }catch (WebApplicationException we){
            Assert.assertEquals(we.getResponse().getStatus(), 500);
        }

    }

    private Paths getSamplePath() {

        paths.add(FlatPath.builder()
                            .path("home->booking")
                            .count(100)
                            .build());
        paths.add(FlatPath.builder()
                .path("booking->complete")
                .count(75)
                .build());
        paths.add(FlatPath.builder()
                .path("home->booking->complete")
                .count(75)
                .build());

        pathVertices.add(GraphNode.builder()
                .id(1)
                .name("home")
                .rank(0)
                .start(false)
                .build());
        pathVertices.add(GraphNode.builder()
                .id(2)
                .name("booking")
                .rank(0)
                .start(false)
                .build());
        pathVertices.add(GraphNode.builder()
                .id(3)
                .name("complete")
                .rank(0)
                .start(false)
                .build());

        return Paths.builder()
                    .paths(paths)
                    .vertices(pathVertices)
                    .build();
    }

    private Graph getSampleGraph() {

        edges = new ArrayList<>();
        vertices = new ArrayList<>();
        edges.add(GraphEdge.builder()
                .from("home")
                .to("booking")
                .value(100)
                .build());
        edges.add(GraphEdge.builder()
                .from("booking")
                .to("complete")
                .value(75)
                .build());
        vertices.add(GraphNode.builder()
                .id(1)
                .name("home")
                .rank(0)
                .start(false)
                .build());
        vertices.add(GraphNode.builder()
                .id(2)
                .name("booking")
                .rank(0)
                .start(false)
                .build());
        vertices.add(GraphNode.builder()
                .id(3)
                .name("complete")
                .rank(0)
                .start(false)
                .build());


        return Graph.builder()
                    .edges(edges)
                    .vertices(vertices)
                    .build();
    }
}
