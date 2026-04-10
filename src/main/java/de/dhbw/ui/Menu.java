package de.dhbw.ui;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
    protected final InputHandler inputHandler;
    protected final String title;
    protected final List<MenuItem> menuItems;

    public Menu(String title, InputHandler inputHandler) {
        this.title = title;
        this.inputHandler = inputHandler;
        menuItems = new ArrayList<>();
    }

    protected void addMenuItem(String label, Runnable action) {
        menuItems.add(new MenuItem(menuItems.size() + 1, label, action));
    }

    public void display() {
        while (true) {
            OutputFormatter.printHeader(title);

            for (MenuItem item : menuItems) {
                System.out.println(item.number + ". " + item.label);
            }
            System.out.println("0. " + (isMainMenu() ? "Exit" : "Back"));

            OutputFormatter.printSeparator();

            int choice = inputHandler.readInt("Enter your choice: ", 0, menuItems.size());

            if (choice == 0) { break; }

            System.out.println();
            try {
                menuItems.get(choice - 1).action.run();
            } catch (Exception e) {
                OutputFormatter.printError(e.getMessage());
            }
            inputHandler.waitForEnter();
        }
    }

    protected boolean isMainMenu() {
        return false;
    }

    private static class MenuItem {
        final int number;
        final String label;
        final Runnable action;

        MenuItem(int number, String label, Runnable action) {
            this.number = number;
            this.label = label;
            this.action = action;
        }
    }
}
