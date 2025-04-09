package repl;

import chess.ChessGame;
import client.BoardPrinter;
import client.GameClient;
import com.google.gson.Gson;
import websocket.NotificationHandler;
import websocket.WebsocketFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.SET_TEXT_FAINT;

public class GameRepl implements NotificationHandler {

    private final GameClient client;
    private final WebsocketFacade websocket;
    private boolean printed = false;

    public GameRepl(String authToken, int gameID) {
        this.websocket = new WebsocketFacade("http://localhost:8080", this, authToken, gameID);
        this.client = new GameClient(this.websocket, authToken, gameID);
        this.websocket.connect();

    }

    public void run() {
        System.out.println("Welcome to the chess game");


        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            if (client.getGame() != null) {
                if (!printed) {
                    System.out.println(BoardPrinter.printBoard(client.getGame(), "white") + "\n\n");
                    printed = true;
                }
                printPrompt();
                String input = scanner.nextLine();
                result = client.eval(input);
                System.out.println(result);
            }
        }
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_FAINT + "CHESS GAME >>> " + RESET_TEXT_BOLD_FAINT);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> message.getMessage();
            case LOAD_GAME -> setBoard(message.getGame());
            case ERROR -> message.getMessage();
        };
    }

    private void setBoard(ChessGame game) {
        printed = false;
        client.setGame(game);
    }
}
