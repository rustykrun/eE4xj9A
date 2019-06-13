package hom.ed.webserver.utils;

import hom.ed.webserver.ApplicationException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TestClient {

    private static final String HOST = "localhost";
    private String name;
    private int port;
    private URL server;
    private URLConnection conn;

    public TestClient(int port, String name) {
        this.name = name;
        this.port = port;
        try {
            server = new URL("http", HOST, port, "/index.html");
        } catch (MalformedURLException e) {
        }
    }

    public void sendRequestWithKeepAlive() {
        prepareConnection(true);
        readServerResponse();
    }

    public void sendRequestWithoutKeepAlive() {
        prepareConnection(false);
        readServerResponse();
    }

    public void stopConnection() {
        ((HttpURLConnection) conn).disconnect();
    }

    private void prepareConnection(boolean keepAlive) {
        try {
            conn = server.openConnection();
            if (!keepAlive) {
                conn.setRequestProperty("Connection", "close");
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    private void readServerResponse() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String decodedString;
            while ((decodedString = reader.readLine()) != null) {
                System.out.println(decodedString);
            }
        } catch (IOException e) {
            readErrorStream((HttpURLConnection) conn);
        }
    }

    private void readErrorStream(HttpURLConnection kaConnection) {
        try {
            InputStream es = kaConnection.getErrorStream();
            BufferedReader er = new BufferedReader(new InputStreamReader(es));
            String err;
            // read the response body
            while ((err = er.readLine()) != null) {
                System.out.println(err);
            }
            er.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
