package repl;

import client.PostJoinClient;
import model.AuthData;
import server.ServerConnectionError;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.SET_TEXT_FAINT;

public class PostJoinRepl {

    private final PostJoinClient client;

    public PostJoinRepl(ServerFacade server, AuthData auth) {
        this.client = new PostJoinClient(server, auth);
    }

    public boolean run() {
        System.out.println(client.help() + "\n\n");
        System.out.println(client.listGames());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            result = client.eval(input);
            if (result == "logout") {
                return true;
            }
            printResult(result);
        }
        return false;
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_FAINT + "CHESS >>> " + RESET_TEXT_BOLD_FAINT);
    }

    private void printResult(String result) {
        if (result != "quit") {
            System.out.println(result);
        } else {
            System.out.println("Exiting");
        }
    }
}
