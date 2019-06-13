package hom.ed.webserver.services;

import hom.ed.webserver.StatusCodes;

import java.net.Socket;
import java.util.Optional;

public interface HandlerService {


    void handleRequest(Socket connection, Optional<StatusCodes> error);

}
