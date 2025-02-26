package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import server.InvalidRequest;
import service.Service;
import spark.Request;

public interface Handler {



     default String errorToJson(String message) {
        return "{ \"message\": \"" + message + "\" }";
    }

    default <T> T parseRequest(Request req, Class<T> recordType) throws InvalidRequest {
        try {
            Gson serializer = new Gson();
            T logReq = serializer.fromJson(req.body(), recordType);
            return logReq;
        } catch (JsonSyntaxException e) {
            System.out.println("oopsy");
            throw new InvalidRequest("Error: bad request");
        }
    }

    default String getAuthToken(Request req) {
         return req.headers("Authorization");
    }
}
