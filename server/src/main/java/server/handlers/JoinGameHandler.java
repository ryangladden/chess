package server.handlers;

import dataaccess.ColorTakenException;
import dataaccess.InvalidGameID;
import dataaccess.UnauthorizedException;
import server.InvalidRequest;
import server.request.JoinGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler implements Handler {

    GameService service;

    public JoinGameHandler(GameService service) {
        this.service = service;
    }

    public String joinGame(Request req, Response res) {
        try {
            JoinGameRequest joinReq = (JoinGameRequest) parseRequest(req, JoinGameRequest.class);
            service.joinGame(joinReq);
            return "";
        } catch (UnauthorizedException e) {
            res.status(401);
            return errorToJson(e.getMessage());
        } catch (InvalidRequest e) {
            res.status(400);
            return errorToJson(e.getMessage());
        } catch (ColorTakenException e) {
            res.status(403);
            return errorToJson(e.getMessage());
        } catch (InvalidGameID e) {
            res.status(400);
            return errorToJson(e.getMessage());
        } catch (Exception e) {
            res.status(400);
            return errorToJson("Error: bad request in joining game");
        }

    }
}
