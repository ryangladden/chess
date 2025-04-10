package repl;

import client.PostJoinClient;
import model.AuthData;
import model.GameData;
import server.ServerFacade;

import java.util.Scanner;

import static repl.PostJoinResult.ResultType.*;
import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.SET_TEXT_FAINT;

public class PostJoinRepl {

    private final PostJoinClient client;
    private final ServerFacade server;
    private final AuthData auth;

    public PostJoinRepl(ServerFacade server, AuthData auth) {
        this.server = server;
        this.auth = auth;
        this.client = new PostJoinClient(server, auth);
    }

    public PostJoinResult run() {
        System.out.println(client.help() + "\n\n");
        System.out.println(client.listGames());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            result = client.eval(input);
            if (result.equals("logout")) {
                return new PostJoinResult(LOGOUT);
            } else if (result.split(" ")[0].equals("join")) {
                String[] join = result.split(" ");
                System.out.println(join[0]);
                int gameID = Integer.parseInt(join[1]);
                return new PostJoinResult(new PostJoinResult.GameInfo(gameID, auth.authToken(), join[2]));
            }
            printResult(result);
        }
        return new PostJoinResult(QUIT);
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
