package hom.ed.webserver;

import hom.ed.webserver.doubles.FakeHttpHandlerService;
import hom.ed.webserver.services.HandlerService;
import hom.ed.webserver.utils.MonitoringExecutor;
import hom.ed.webserver.utils.TestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class WebServerWithThreadPoolingTest {

    private static final String CLIENT1 = "c1";
    private static final String CLIENT2 = "c2";
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private int port = 8080;
    private WebServerWithThreadPooling webServer;
    private MonitoringExecutor serverExecutor;
    private ExecutorService testExecutor;
    private HandlerService handlerService;


    @Before
    public void setUp() {

        serverExecutor = new MonitoringExecutor(Executors.newFixedThreadPool(THREADS));
        testExecutor = Executors.newCachedThreadPool();
        handlerService = new FakeHttpHandlerService();
        webServer = WebServerWithThreadPooling
                .aServer().withPort(port)
                .withExecutorService(serverExecutor).withHandlerService(handlerService).build();
    }

    @After
    public void tearDown() {
        serverExecutor.shutdownNow();
        testExecutor.shutdownNow();
    }

    @Test
    public void create_a_web_server() {
        assertEquals(port, webServer.getPort());
        assertEquals(handlerService, webServer.getHandlerService());
    }

    @Test
    public void start_and_stop() throws InterruptedException {
        testExecutor.execute(() -> webServer.start());
        assertTrue(webServer.isRunning());
        testExecutor.execute(() -> webServer.stop());
        serverExecutor.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertFalse(webServer.isRunning());
    }

    @Test
    public void accept_a_connection() throws Exception {
        testExecutor.execute(() -> webServer.start());
        TestClient client = new TestClient(port, CLIENT1);

        CompletableFuture.supplyAsync(() -> client, testExecutor)
                .thenAcceptAsync(c -> c.sendRequestWithoutKeepAlive(), testExecutor);

        serverExecutor.awaitTermination(100, TimeUnit.MILLISECONDS);
        testExecutor.execute(() -> webServer.stop());
        assertEquals(1, serverExecutor.getTaskCounter());
    }

    @Test
    public void accept_multiple_connections() throws InterruptedException {
        testExecutor.execute(() -> webServer.start());
        TestClient client1 = new TestClient(port, CLIENT1);
        TestClient client2 = new TestClient(port, CLIENT2);

        CompletableFuture.supplyAsync(() -> client1, testExecutor)
                .thenAcceptAsync(tc -> tc.sendRequestWithoutKeepAlive(), testExecutor);
        CompletableFuture.supplyAsync(() -> client2, testExecutor)
               .thenAcceptAsync(tc -> tc.sendRequestWithoutKeepAlive(), testExecutor);

        serverExecutor.awaitTermination(100, TimeUnit.MILLISECONDS);
        testExecutor.execute(() -> webServer.stop());
        assertEquals(2, serverExecutor.getTaskCounter());
    }

}