package de.dhbw.ui;

public final class ConsoleUI {
    private static final String ANSI_CLEAR_AND_HOME = "\u001B[2J\u001B[H";

    private ConsoleUI() {
    }

    public static void clearScreen() {
        try {
            // Emit ANSI to the current terminal session. This works in modern
            // Windows Terminal / PowerShell and Unix terminals.
            System.out.print(ANSI_CLEAR_AND_HOME);
            System.out.flush();
        } catch (Exception e) {
            // Fallback for consoles that do not support ANSI.
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static void renderPage(String pageContent) {
        clearScreen();
        System.out.print(pageContent);
        System.out.flush();
    }
}
