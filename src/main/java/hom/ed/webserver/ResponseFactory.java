package hom.ed.webserver;

import hom.ed.webserver.services.FileLoader;

import java.util.HashMap;
import java.util.Map;

public class ResponseFactory {

    public HttpFileResponse createResponse(HttpFileRequest request) {
        Map<String,Object> headers = new HashMap<>();
        if (request.hasErrors()) {
            return createErrorResponse(request, request.getErrors());
        } else {
            String fileName = request.getPath();
            FileLoader fileLoader;
            try {
                fileLoader = new FileLoader(fileName);
            } catch (Exception e) {
                return createErrorResponse(request, StatusCodes.INTERNAL_SERVER_ERROR);
            }
            headers.put("Content-Length", fileLoader.getContentLenght());
            headers.put("Content-Type", fileLoader.getContentType());
            headers.put("Connection", request.isKeepAlive()? "keep-alive": "close");
            return HttpFileResponse.withStatus(StatusCodes.OK.toString()).ofVersion(request.getVersion())
                    .withHeaders(headers).withBody(fileLoader.getContentAsBytes()).build();
        }
    }

    private HttpFileResponse createErrorResponse(HttpFileRequest request, StatusCodes errCode) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Connection", "close");
        return HttpFileResponse.withStatus(errCode.toString()).ofVersion(request.getVersion())
                .withHeaders(headers).build();
    }

}
