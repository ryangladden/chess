package server.request;

import spark.Request;

public interface RecordRequest  {

    RecordRequest addAuth(String authToken);

    boolean requiresAuth();

    boolean hasBody();
}
