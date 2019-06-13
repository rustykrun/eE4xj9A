package hom.ed.webserver;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestFactory {

    public HttpFileRequest createRequest(Socket connection, Optional<StatusCodes> errorOptional) {
        OutputStream outputStream;
        BufferedReader reader;
        StatusCodes errors = null;
        if (errorOptional.isPresent())
            errors = errorOptional.get();
        boolean keepAlive = false;
        try {
            outputStream = connection.getOutputStream();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            reader = new BufferedReader(isr);
            String firstLine = reader.readLine();
            if (firstLine != null) {
                String[] firstLineParts = firstLine.split(" ", 3);
                HttpMethod method = HttpMethod.valueOf(firstLineParts[0]);
                if (!HttpMethod.GET.equals(method)) {
                    errors = StatusCodes.METHOD_NOT_ALLOWED;
                }
                String requestPath = firstLineParts[1].substring(1);
                String path = "index.html";
                if (!requestPath.isEmpty()) {
                    path = requestPath;
                }
                String version = firstLineParts[2];
                String headersLine = reader.readLine();
                Map<String, String> headers = new HashMap<>();
                while (!headersLine.equals("")) {
                    String[] lineParts = headersLine.split(":", 2);
                    if (lineParts.length == 2) {
                        headers.put(lineParts[0], lineParts[1].trim());
                    }
                    headersLine = reader.readLine();
                }

                if ("keep-alive".equalsIgnoreCase(headers.get("Connection")) && errors == null) {
                    keepAlive = true;
                }
                return HttpFileRequest.ofType(method).forFile(path).ofVersion(version)
                        .withErrors(errors)
                        .withWriter(outputStream)
                        .withHeaders(headers).isKeepAlive(keepAlive)
                        .build();
            }
            return null;

        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
