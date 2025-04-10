package repl;

import chess.ChessGame;
import client.BoardPrinter;
import client.GameClient;
import com.google.gson.Gson;
import server.ServerFacade;
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
//    private String observeColor;

    public GameRepl(ServerFacade server, String authToken, int gameID, String color) {
        this.websocket = new WebsocketFacade(server.getServerUrl(), this, authToken, gameID);
        this.client = new GameClient(this.websocket, authToken, gameID, color);
        System.out.println(color);
//        this.observeColor = color.equals("observer") ? "white" : color;
        this.websocket.connect();

    }

    public void run() {
        System.out.println("Welcome to the chess game");


        Scanner scanner = new Scanner(System.in);
        String result = "";


        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_FAINT + "CHESS GAME >>> " + RESET_TEXT_BOLD_FAINT);
    }

    @Override
    public void notify(ServerMessage message) {
        System.out.println("\n");
        System.out.println(switch (message.getServerMessageType()) {
                    case NOTIFICATION -> message.getMessage();
                    case LOAD_GAME -> setBoard(message.getGame());
                    case ERROR -> message.getMessage();
                }
        );
        System.out.println("\n");
        printPrompt();
    }

    private String setBoard(ChessGame game) {
        client.setGame(game);
        return client.getBoard();
    }
}
