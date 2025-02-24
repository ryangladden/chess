package server;

import com.google.gson.Gson;
import spark.Request;

public class RegisterHandler extends Handler {

    private RegisterRequest req;

    RegisterHandler(Request req) {
        this.req = parseRequest(req);
        System.out.println("Request serialized into Record");
    }

    @Override
    public RegisterRequest parseRequest(Request req) {
        Gson serializer = new Gson();
        RegisterRequest serializedReq = serializer.fromJson(req.body(), RegisterRequest.class);
        return serializedReq;
    }
}
