package hom.ed.webserver.services;

import hom.ed.webserver.HttpFileRequest;

public interface DispatcherService {


    void dispatchRequest(HttpFileRequest request);

}
