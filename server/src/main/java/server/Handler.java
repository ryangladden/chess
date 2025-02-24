package server;

import spark.Request;

public abstract class Handler {

    public abstract Record parseRequest(Request req);
}
