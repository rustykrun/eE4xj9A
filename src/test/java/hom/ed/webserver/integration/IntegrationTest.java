package hom.ed.webserver.integration;

import hom.ed.webserver.*;
import hom.ed.webserver.services.DispatcherService;
import hom.ed.webserver.services.HandlerService;
import hom.ed.webserver.services.impl.DefaultExceptionHandler;
import hom.ed.webserver.services.impl.FileDispatcherService;
import hom.ed.webserver.services.impl.HttpHandlerService;
import hom.ed.webserver.utils.MonitoringExecutor;
import hom.ed.webserver.utils.TestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private static final String CLIENT1 = "c1";
    private static final String CLIENT2 = "c2";
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private int port = 8080;
    private WebServerWithThreadPooling webServer;
    private MonitoringExecutor serverExecutor;
    private ExecutorService testExecutor;
    private HandlerService handlerService;
    private RequestFactory requestFactory;
    private ResponseFactory responseFactory;
    private DispatcherService dispatcherService;
    private DefaultExceptionHandler exceptionHandler;


    @Before
    public void setUp() {

        serverExecutor = new MonitoringExecutor(Executors.newFixedThreadPool(THREADS));
        testExecutor = Executors.newCachedThreadPool();
        requestFactory = new RequestFactory();
        responseFactory = new ResponseFactory();
        dispatcherService = new FileDispatcherService(responseFactory);
        handlerService = new HttpHandlerService(dispatcherService, requestFactory);
        exceptionHandler = new DefaultExceptionHandler();
        webServer = WebServerWithThreadPooling
                .aServer().withPort(port)
                .withExecutorService(serverExecutor).withHandlerService(handlerService)
                .withExceptionHandler(exceptionHandler).build();

    }

    @After
    public void tearDown() {
        serverExecutor.shutdownNow();
        testExecutor.shutdownNow();
    }

    @Test
    public void given_keep_alive_request_then_reuse_tcp_connection() throws InterruptedException {

        testExecutor.execute(() -> webServer.start());

        TestClient client = new TestClient(port, CLIENT1);

        CompletableFuture.supplyAsync(() -> client, testExecutor)
                .thenAcceptAsync(tc -> {
                    tc.sendRequestWithKeepAlive();
                    tc.sendRequestWithKeepAlive();
                    tc.sendRequestWithoutKeepAlive();
                }, testExecutor).thenRunAsync(() -> client.stopConnection());

        serverExecutor.awaitTermination(200, TimeUnit.MILLISECONDS);
        testExecutor.execute(() -> webServer.stop());

        assertEquals(1, webServer.getConnectionHashes().size());
    }

    @Test
    public void given_not_keep_alive_request_then_use_new_tcp_connection() throws InterruptedException {

        testExecutor.execute(() -> webServer.start());

        TestClient request1 = new TestClient(port, CLIENT1);
        TestClient request2 = new TestClient(port, CLIENT2);


        CompletableFuture.runAsync(() -> request1.sendRequestWithoutKeepAlive(), testExecutor).thenRunAsync(
                () -> request2.sendRequestWithoutKeepAlive(), testExecutor).thenRunAsync(
                () -> request1.stopConnection(), testExecutor).thenRunAsync(() -> request2.stopConnection(),
                testExecutor);

        serverExecutor.awaitTermination(200, TimeUnit.MILLISECONDS);
        testExecutor.execute(() -> webServer.stop());

        assertEquals(2, webServer.getConnectionHashes().size());
    }

}
