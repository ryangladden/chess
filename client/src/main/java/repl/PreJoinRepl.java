package repl;

import client.PreJoinClient;
import model.AuthData;
import server.ServerConnectionError;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreJoinRepl {

    private final PreJoinClient client;
    private AuthData auth;

    public PreJoinRepl(ServerFacade server) {
        this.client = new PreJoinClient(server);
        auth = null;
    }

    public AuthData run() {
        printWelcome();
        System.out.println(client.help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (auth == null && !result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            try {
                result = client.eval(input);
                printResult(result);
            } catch (ServerConnectionError e) {
                result = printConnectionError();
            }
            auth = client.auth;
        }
        return auth;
    }

    private void printWelcome() {
        System.out.println("\n\n\n" + SET_TEXT_BOLD + BLACK_ROOK + " Chess " + BLACK_ROOK +
                RESET_TEXT_BOLD_FAINT + SET_TEXT_FAINT + "  v0.1" + RESET_TEXT_BOLD_FAINT);
        System.out.println("Sign in or register to get started");
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_FAINT + "CHESS LOGIN >>> " + RESET_TEXT_BOLD_FAINT);
    }

    private String printConnectionError() {
        System.out.println(SET_TEXT_COLOR_RED + "Uh oh! There was an issue connecting to the server\n"
                + RESET_TEXT_COLOR + "Check your internet connection and try again." +
                "\nIf the problem persists, the server may be down and you should try again later.");
        return "quit";
    }

    private void printResult(String result) {
        if (result != "quit") {
            System.out.println(result);
        } else {
            System.out.println("Exiting");
        }
    }
}
