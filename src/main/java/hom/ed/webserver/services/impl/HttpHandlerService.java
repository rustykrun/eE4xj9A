package hom.ed.webserver.services.impl;


import hom.ed.webserver.ApplicationException;
import hom.ed.webserver.HttpFileRequest;
import hom.ed.webserver.RequestFactory;
import hom.ed.webserver.StatusCodes;
import hom.ed.webserver.services.DispatcherService;
import hom.ed.webserver.services.HandlerService;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;
import java.util.logging.Logger;

public class HttpHandlerService implements HandlerService {

    private static Logger LOGGER = Logger.getLogger(HttpHandlerService.class.getName());

    private DispatcherService dispatcherService;
    private RequestFactory requestFactory;

    public HttpHandlerService(DispatcherService dispatcherService, RequestFactory requestFactory) {
        this.dispatcherService = dispatcherService;
        this.requestFactory = requestFactory;
    }

    @Override
    public void handleRequest(Socket connection, Optional<StatusCodes> error) {
        boolean keepAlive = true;
        try {
            while (keepAlive) {
                HttpFileRequest httpRequest = requestFactory.createRequest(connection, error);
                keepAlive = httpRequest.isKeepAlive();
                try {
                    connection.setKeepAlive(keepAlive);
                } catch (SocketException e) {
                    throw new ApplicationException(e);
                }
                dispatcherService.dispatchRequest(httpRequest);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    throw new ApplicationException(e);
                }
            }
        }
    }

}
