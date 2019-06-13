package hom.ed.webserver.doubles;

import hom.ed.webserver.services.HandlerService;
import hom.ed.webserver.StatusCodes;

import java.net.Socket;
import java.util.Optional;

public class FakeHttpHandlerService implements HandlerService {

    @Override
    public void handleRequest(Socket connection, Optional<StatusCodes> error) {
    }

}
