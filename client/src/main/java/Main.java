import chess.*;
import model.UserData;
import server.ServerFacade;

public class Main {
    static final ServerFacade server = new ServerFacade("http://localhost:8080");
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        server.register(new UserData("joemama", "password", "email@email.com"));
    }
}