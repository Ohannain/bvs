package de.dhbw.ui;

import de.dhbw.util.DateUtils;

import java.time.LocalDate;
import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner;

    public InputHandler() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * readString reads a string from the user
     */
    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * readNonEmptyString tries to read a string from the user, which cannot be empty
     *
     * @param prompt the prompt to show the user
     * @return the input from the user
     */
    public String readNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * readInt tries to read a number from the user
     *
     * @param prompt the prompt to show the user
     * @return the input from the user
     */
    public int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    /**
     * readInt tries to read a number from the user, where a min and max value are defined.
     *
     * @param prompt the prompt to show the user
     * @param min the smallest number allowed
     * @param max the highest number allowed
     * @return the input from the user
     */
    public int readInt(String prompt, int min, int max) {
        int value;
        do {
            value = readInt(prompt);
            if (value < min || value > max) {
                System.out.println("Please enter a number between " + min + " and " + max);
            }
        } while (value < min || value > max);
        return value;
    }

    /**
     * readDouble tries to read a number from the user
     *
     * @param prompt the prompt to show the user
     * @return the input from the user
     */
    public double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    /**
     * readBoolean gets a yes/no decision by the user.
     *
     * @param prompt the prompt to show the user
     * @return the decision from the user
     */
    public boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter 'y' or 'n'.");
            }
        }
    }

    /**
     * readDate tries to read a date from the user.
     * the date is formatted as dd.MM.yyyy
     *
     * @param prompt the prompt to show the user
     * @return the input from the user
     */
    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (dd.MM.yyyy): ");
            String input = scanner.nextLine().trim();
            LocalDate date = DateUtils.parse(input);
            if (date != null) {
                return date;
            }
            System.out.println("Invalid date format. Please use dd.MM.yyyy");
        }
    }

    /**
     * waitForEnter waits until the user confirms with enter to continue
     */
    public void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * close closes the scanner
     */
    public void close() {
        scanner.close();
    }
}