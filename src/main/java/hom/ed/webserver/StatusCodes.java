package hom.ed.webserver;

public enum StatusCodes {

    OK("200 OK"),
    METHOD_NOT_ALLOWED("405 Method Not Allowed"),
    TOO_MANY_CONNECTIONS("429 Too Many Requests"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");

    private String errMessage;

    StatusCodes(String errMessage) {
        this.errMessage = errMessage;
    }

    @Override
    public String toString() {
        return errMessage;
    }
}
