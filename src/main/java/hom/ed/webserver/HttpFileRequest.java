package hom.ed.webserver;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpFileRequest {

    private OutputStream outputStream;
    private HttpMethod method;
    private String path = "index.html";
    private String version;
    private boolean keepAlive;
    private StatusCodes errors;
    private Map<String, String> headers = new HashMap<>();


    private HttpFileRequest(RequestBuilder builder) {
        this.path = builder.path;
        this.version = builder.version;
        this.headers = builder.headers;
        this.outputStream = builder.outputStream;
        this.keepAlive = builder.keepAlive;
        this.errors = builder.errors;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public boolean hasErrors() {
        return errors != null;
    }

    public StatusCodes getErrors() {
        return errors;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public static RequestBuilder getRequest() {
        return new RequestBuilder(HttpMethod.GET);
    }

    public static RequestBuilder ofType(HttpMethod method) {
        return new RequestBuilder(method);
    }

    @Override
    public String toString() {
        return "HttpFileRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                ", keepAlive=" + keepAlive +
                ", headers=" + headers +
                '}';
    }

    public static class RequestBuilder {
        private OutputStream outputStream;
        private HttpMethod method;
        private String path;
        private String version;
        private boolean keepAlive;
        private StatusCodes errors;
        private Map<String, String> headers = new HashMap<>();

        private RequestBuilder(HttpMethod method) {
            this.method = method;
        }

        public RequestBuilder forFile(String file) {
            path = file;
            return this;
        }

        public RequestBuilder withWriter(OutputStream out) {
            outputStream = out;
            return this;
        }

        public RequestBuilder withHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestBuilder ofVersion(String version) {
            this.version = version;
            return this;
        }

        public RequestBuilder isKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public RequestBuilder withErrors(StatusCodes errors) {
            if (errors != null) {
                this.errors = errors;
            }
            return this;
        }

        public HttpFileRequest build() {
            return new HttpFileRequest(this);
        }
    }
}
