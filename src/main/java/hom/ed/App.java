package hom.ed;

import hom.ed.webserver.*;
import hom.ed.webserver.services.DispatcherService;
import hom.ed.webserver.services.HandlerService;
import hom.ed.webserver.services.impl.FileDispatcherService;
import hom.ed.webserver.services.impl.HttpHandlerService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class App 
{
    public static void main( String[] args ) {

        ExecutorService serverExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        RequestFactory requestFactory = new RequestFactory();
        ResponseFactory responseFactory = new ResponseFactory();
        DispatcherService dispatcherService = new FileDispatcherService(responseFactory);
        HandlerService handlerService = new HttpHandlerService(dispatcherService, requestFactory);

        WebServerWithThreadPooling webServer = WebServerWithThreadPooling.aServer().withPort(8080)
                .withExecutorService(serverExecutor)
                .withHandlerService(handlerService).build();

        webServer.start();

    }
}
