package websocket;

import repl.GameRepl;

public class Main {

    public static void main(String[] args) {
        GameRepl repl = new GameRepl("70585341-e833-4b5b-96b5-b4ec31fde829", 1);
        repl.run();
    }
}