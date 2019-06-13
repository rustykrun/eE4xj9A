package hom.ed.webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpFileResponse {

    private String version;
    private String status;
    private Map<String,Object> headers;
    private byte[] body;

    private HttpFileResponse(ResponseBuilder builder) {
        this.status = builder.status;
        this.version = builder.version;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpFileResponse{" +
                "version='" + version + '\'' +
                ", status='" + status + '\'' +
                ", headers=" + headers +
                '}';
    }

    public static ResponseBuilder withStatus(String status) {
        return new ResponseBuilder(status);
    }

    public static class ResponseBuilder {
        private String version;
        private String status;
        private Map<String,Object> headers = new HashMap<>();
        private byte[] body;

        private ResponseBuilder(String status) {
            this.status = status;
        }

        public ResponseBuilder ofVersion(String version) {
            this.version = version;
            return this;
        }

        public ResponseBuilder withHeaders(Map<String, Object> headers) {
            this.headers = headers;
            return this;
        }

        public ResponseBuilder withBody(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpFileResponse build() {
            return new HttpFileResponse(this);
        }
    }
}
