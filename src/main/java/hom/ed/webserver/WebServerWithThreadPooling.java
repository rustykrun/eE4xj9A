package hom.ed.webserver;


import hom.ed.webserver.services.HandlerService;
import hom.ed.webserver.services.impl.DefaultExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class WebServerWithThreadPooling {

    private int port;
    private ExecutorService executorService;
    private ServerSocket serverSocket;
    private HandlerService handlerService;
    private DefaultExceptionHandler exceptionHandler;
    private Set<Integer> connectionHashes;


    private WebServerWithThreadPooling(WebServerWithThreadPoolingBuilder builder) {
        port = builder.port;
        executorService = builder.executorService;
        handlerService = builder.handlerService;
        exceptionHandler = builder.exceptionHandler;
    }

    public static WebServerWithThreadPoolingBuilder aServer() {
        return new WebServerWithThreadPoolingBuilder();
    }

    public int getPort() {
        return port;
    }


    public void start() {
        connectionHashes = new HashSet<>();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            exceptionHandler.handleError(e);
        }
        while (!executorService.isShutdown()) {

            try {
                Socket connection = serverSocket.accept();
                connectionHashes.add(connection.hashCode());
                try {
                    executorService.execute(() -> handlerService.handleRequest(connection, Optional.empty()));
                } catch (RejectedExecutionException e) {
                    exceptionHandler.handleError(e);
                    if (!executorService.isShutdown()) {
                        handlerService.handleRequest(connection, Optional.of(StatusCodes.TOO_MANY_CONNECTIONS));
                    }
                }
            } catch (IOException e) {
            }
        }
    }


    public void stop() {
        executorService.shutdown();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            exceptionHandler.handleError(e);
        }
    }


    public boolean isRunning() {
        return !executorService.isShutdown();
    }

    public HandlerService getHandlerService() {
        return handlerService;
    }

    public Set<Integer> getConnectionHashes() {
        return connectionHashes;
    }

    public static class WebServerWithThreadPoolingBuilder {
        private int port;
        private ExecutorService executorService;
        private HandlerService handlerService;
        private DefaultExceptionHandler exceptionHandler;


        public WebServerWithThreadPoolingBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        public WebServerWithThreadPoolingBuilder withExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public WebServerWithThreadPoolingBuilder withExceptionHandler(DefaultExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public WebServerWithThreadPoolingBuilder withHandlerService(HandlerService handlerService) {
            this.handlerService = handlerService;
            return this;
        }

        public WebServerWithThreadPooling build() {
            return new WebServerWithThreadPooling(this);
        }
    }


}
