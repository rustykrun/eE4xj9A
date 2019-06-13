package hom.ed.webserver.services;

import hom.ed.webserver.ApplicationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileLoader {

    private static final ClassLoader CL = Thread.currentThread().getContextClassLoader();
    private static final URL HOME_PAGE = CL.getResource("index.html");

    private String path;
    private String contentType;
    private byte[] content;
    private int contentLength;


    public FileLoader(String path) {
        this.path = path;
        load();
    }

    private void load() {
        URL url = CL.getResource(path);
        if (url == null) {
            url = HOME_PAGE;
        }
        try {
            try {
                content = Files.readAllBytes(Paths.get(url.toURI()));
                contentType = contentType();
                contentLength = content.length;
            } catch (URISyntaxException e) {
                throw new ApplicationException(e);
            }
        } catch (IOException e) {
            throw new ApplicationException(e);
        }


    }

    public byte[] getContentAsBytes() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLenght() {
        return contentLength;
    }

    private String contentType() {
        if (path.isEmpty()) {
            return "text/html";
        } else {
            return URLConnection.getFileNameMap().getContentTypeFor(path);
        }
    }

}
