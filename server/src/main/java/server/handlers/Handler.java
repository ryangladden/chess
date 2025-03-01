package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import server.InvalidRequest;
import server.request.ListGameRequest;
import server.request.LogoutRequest;
import server.request.RecordRequest;
import spark.Request;

public interface Handler {


    default String errorToJson(String message) {
        return "{ \"message\": \"" + message + "\" }";
    }

    default <T extends RecordRequest> RecordRequest parseRequest(Request req, Class<T> recordType) throws InvalidRequest {
        try {
            if (recordType == LogoutRequest.class) {
                return new LogoutRequest(getAuthToken(req));
            } else if (recordType == ListGameRequest.class) {
                return new ListGameRequest(getAuthToken(req));
            }
            Gson serializer = new Gson();
            RecordRequest request = serializer.fromJson(req.body(), recordType);
            if (request.requiresAuth()) {
                return request.addAuth(getAuthToken(req));
            }
            return serializer.fromJson(req.body(), recordType);
        } catch (JsonSyntaxException e) {
            throw new InvalidRequest("Error: bad request");
        }
    }

    default String getAuthToken(Request req) {
        return req.headers("Authorization");
    }
}
