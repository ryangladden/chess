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

    public void run() {
        System.out.println(client.help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            result = client.eval(input);
            System.out.println(result);
        }
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_FAINT + "CHESS >>> " + RESET_TEXT_BOLD_FAINT);
    }
}
