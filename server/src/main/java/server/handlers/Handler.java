package server.handlers;

import service.Service;
import spark.Request;

public abstract class Handler {

    protected abstract Record parseRequest(Request req);
}
