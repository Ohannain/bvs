package de.dhbw.ui;

import de.dhbw.util.DateUtils;

import java.time.LocalDate;
import java.util.Scanner;

public class InputHandler implements AutoCloseable {
    private final Scanner scanner;

    public InputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

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

    public void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    @Override
    public void close() {
        scanner.close();
    }
}
