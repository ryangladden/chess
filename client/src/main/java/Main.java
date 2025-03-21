import chess.*;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

public class Main {
    static final ServerFacade server = new ServerFacade("http://localhost:8080");
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        AuthData auth = server.login(new UserData("joemam", "password", null));
        System.out.println(auth);
        Integer game = server.createGame("kewl game", auth.authToken());
        System.out.println(game);
        server.listGames(auth.authToken());
        server.joinGame(game, "WHITE", auth.authToken());
        server.listGames(auth.authToken());
        server.logout(auth.authToken());

    }
}