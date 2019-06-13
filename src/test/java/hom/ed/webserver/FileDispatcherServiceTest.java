package hom.ed.webserver;

import hom.ed.webserver.services.impl.FileDispatcherService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileDispatcherServiceTest {

    private ByteArrayOutputStream outputStream;
    private Map<String,Object> headers;

    @Mock
    private ResponseFactory responseFactory;

    @Mock
    private HttpFileRequest request;

    @InjectMocks
    private FileDispatcherService dispatcherService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        outputStream = new ByteArrayOutputStream();
        headers = new HashMap<>();
    }

    @Test
    public void create() {
        assertNotNull(dispatcherService);
    }

    @Test
    public void given_valid_request_should_stream_the_response() {
        when(request.getOutputStream()).thenReturn(outputStream);

        byte[] body = "test".getBytes();
        headers.put("Content-Length", body.length);
        headers.put("Content-Type", "text/html");
        HttpFileResponse response = HttpFileResponse.withStatus("200 OK")
                .ofVersion("HTTP/1.1").withHeaders(headers).withBody(body).build();
        when(responseFactory.createResponse(any(HttpFileRequest.class))).thenReturn(response);

        dispatcherService.dispatchRequest(request);

        String reply = new String(outputStream.toByteArray());
        String[] replyParts = reply.split("\r\n");
        assertEquals(5, replyParts.length);
        assertEquals("HTTP/1.1 200 OK", replyParts[0]);
        assertEquals("Content-Length: 4", replyParts[1]);
        assertEquals("Content-Type: text/html", replyParts[2]);
        assertEquals("test", replyParts[4]);
    }
}