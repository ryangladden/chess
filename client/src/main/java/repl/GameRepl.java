package repl;

import client.GameClient;
import websocket.WebsocketFacade;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.SET_TEXT_FAINT;

public class GameRepl {

    private final GameClient client;

    public GameRepl(WebsocketFacade webSocket) {
        this.client = new GameClient(webSocket)
    }

    public void run() {
        System.out.println("Welcome to the chess game");

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while(!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            result = client.eval(input);
            System.out.println(result);
        }

    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_FAINT + "CHESS GAME >>> " + RESET_TEXT_BOLD_FAINT);
    }

}
