package hom.ed.webserver.services.impl;

import hom.ed.webserver.ApplicationException;
import hom.ed.webserver.HttpFileRequest;
import hom.ed.webserver.HttpFileResponse;
import hom.ed.webserver.ResponseFactory;
import hom.ed.webserver.services.DispatcherService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;

public class FileDispatcherService implements DispatcherService {

    public static final String NEW_LINE = "\r\n";
    public static final String SPACE = " ";
    private ResponseFactory responseFactory;

    public FileDispatcherService(ResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    @Override
    public void dispatchRequest(HttpFileRequest request) {
        HttpFileResponse response = responseFactory.createResponse(request);
        BufferedOutputStream writer = new BufferedOutputStream(request.getOutputStream());
        try {
            writer.write(response.getVersion().getBytes());
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
        try {
            writer.write(SPACE.getBytes());
            writer.write(response.getStatus().getBytes());
            writer.write(NEW_LINE.getBytes());
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
        StringBuilder headers = new StringBuilder();
        for (Map.Entry header : response.getHeaders().entrySet()) {
            headers.append(header.getKey()).append(": ").append(header.getValue());
            headers.append(NEW_LINE);
        }
        headers.append(NEW_LINE);
        try {
            writer.write(headers.toString().getBytes());
            writer.write(response.getBody());
            writer.flush();
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

}
