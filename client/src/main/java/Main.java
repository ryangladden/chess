import chess.*;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

public class Main {
    static final ServerFacade server = new ServerFacade("http://localhost:8080");
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        AuthData auth = server.register(new UserData("joemam", "password", "email@email.com"));
        System.out.println(auth);
        server.logout(auth.authToken());
    }
}