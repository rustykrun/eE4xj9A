package hom.ed.webserver.services.impl;



import hom.ed.webserver.services.ExceptionHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultExceptionHandler implements ExceptionHandler {

    private Logger logger = Logger.getLogger(DefaultExceptionHandler.class.getName());


    @Override
    public void handleError(Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
    }

}
