package server.handlers;

import spark.Request;

public abstract class Handler {

    protected abstract Record parseRequest(Request req);
}
