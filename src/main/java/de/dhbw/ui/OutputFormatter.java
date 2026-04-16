package de.dhbw.ui;

import de.dhbw.domain.fine.Fine;
import de.dhbw.domain.fine.FineStatus;
import de.dhbw.domain.loan.Loan;
import de.dhbw.domain.loan.LoanStatus;
import de.dhbw.domain.media.Media;
import de.dhbw.domain.reservation.Reservation;
import de.dhbw.domain.user.User;
import de.dhbw.util.DateUtils;

import java.util.List;
import java.util.Scanner;
import de.dhbw.util.UUID;
import java.util.function.Consumer;

public class OutputFormatter {
    private static final int SEPARATOR_LENGTH = 80;
    private static final int PAGE_SIZE = 10;
    private static final int CONTROL_SECTION_WIDTH = 30;

    /**
     * printHeader prints out the header of a menu
     * @param title the title of the menu
     */
    public static void printHeader(String title) {
        ConsoleUI.clearScreen();
        System.out.println();
        System.out.println("=".repeat(SEPARATOR_LENGTH));
        System.out.println(centerText(title));
        System.out.println("=".repeat(SEPARATOR_LENGTH));
    }

    /**
     * printSeparator prints out a line to separate sections
     */
    public static void printSeparator() {
        System.out.println("-".repeat(SEPARATOR_LENGTH));
    }

    /**
     * printSuccess prints out a success message
     * @param message description of the successful action
     */
    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    /**
     * printError prints out an error message
     * @param message description of the error
     */
    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    /**
     * printWarning prints out a warning message
     * @param message the warning
     */
    public static void printWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    /**
     * printInfo prints out information
     * @param message the information
     */
    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    /**
     * printUser prints all relevant information known about a user
     * @param user the user, whose information should be printed
     */
    public static void printUser(User user) {
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Name: " + (user.getFullName() != null ? user.getFullName() : "N/A"));
        System.out.println("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
        System.out.println("Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A"));
        System.out.println("Role: " + (user.getRole() != null ? user.getRole() : "N/A"));
        System.out.println("Status: " +  (user.getStatus() != null ? user.getStatus() : "N/A"));
        System.out.println("Registration Date: " + (user.getRegistrationDate() != null ? DateUtils.format(user.getRegistrationDate()) : "N/A"));
        System.out.println("Loaned Media: " + user.getBorrowedMediaIds().size());
        System.out.println("Outstanding fines: â‚¬" + String.format("%.2f", user.getOutstandingFines()));
    }

    /**
     * printUserList prints out a list of all users and information about them
     * @param users the list of users
     */
    public static void printUserList(List<User> users) {
        if (users.isEmpty()) {
            printInfo("No users found.");
            return;
        }
        printPaginatedTable(
            users,
            "Users",
            99,
            () -> System.out.printf("%-15s %-25s %-30s %-15s %-10s%n",
                "User ID", "Name", "Email", "Status", "Borrowed"),
            user -> System.out.printf("%-15s %-25s %-30s %-15s %-10s%n",
                formatUuid(user.getUserId()),
                truncate(user.getFullName(), 25),
                truncate(user.getEmail(), 30),
                user.getStatus(),
                user.getBorrowedMediaIds().size() + "/" + user.getMaxBorrowLimit())
        );
    }

    /**
     * printMedia prints all relevant information known about a media
     * @param media the media, of which information should be printed
     */
    public static void printMedia(Media media) {
        System.out.println("Media ID: " + media.getMediaId());
        System.out.println("Type: " + (media.getMediaType() != null ? media.getMediaType() : "N/A"));
        System.out.println("Title: " + (media.getTitle() != null ? media.getTitle() : "N/A"));
        System.out.println("Author/Artist: " + (media.getAuthor() != null ? media.getAuthor() : "N/A"));
        System.out.println("Publisher: " +  (media.getPublisher()  != null ? media.getPublisher() : "N/A"));
        System.out.println("Status: " + (media.getStatus() != null ? media.getStatus() : "N/A"));
        System.out.println("ISBN: " + (media.getIsbn() != null ? media.getIsbn() : "N/A"));
        if (media.getCurrentBorrowerId() != null ) {
            System.out.println("Loaned to: " + media.getCurrentBorrowerId());
            System.out.println("Loaned until: " + (media.getDueDate() != null ? DateUtils.format(media.getDueDate()) : "N/A"));
        }
    }

    /**
     * printMediaList prints out a list of all media and information about them
     * @param mediaList the list of media
     */
    public static void printMediaList(List<Media> mediaList) {
        if (mediaList.isEmpty()) {
            printInfo("No media found.");
            return;
        }
        printPaginatedTable(
            mediaList,
            "Media",
            99,
            () -> System.out.printf("%-15s %-8s %-35s %-25s %-12s%n",
                "Media ID", "Type", "Title", "Author/Artist", "Status"),
            media -> System.out.printf("%-15s %-8s %-35s %-25s %-12s%n",
                formatUuid(media.getMediaId()),
                media.getMediaType(),
                truncate(media.getTitle(), 35),
                truncate(media.getAuthor(), 25),
                media.getStatus())
        );
    }

    /**
     * printLoan prints all relevant information known about a loan
     * @param loan the loan, of which information should be printed
     */
    public static void printLoan(Loan loan) {
        System.out.println("Loan ID: " + loan.getLoanId());
        System.out.println("User ID: " + (loan.getUserId() != null ? loan.getUserId() : "N/A"));
        System.out.println("Loan date: " + (loan.getIssueDate() != null ? DateUtils.format(loan.getIssueDate()) : "N/A"));
        System.out.println("Due date: " + (loan.getDueDate() != null ? DateUtils.format(loan.getDueDate()) : "N/A"));
        System.out.println("Return date: " + (loan.getReturnDate() != null ? DateUtils.format(loan.getReturnDate()) : "Not returned"));
        System.out.println("Status: " + (loan.getStatus() != null ? loan.getStatus() : "N/A"));
        if (loan.getStatus() == LoanStatus.OVERDUE) {
            System.out.println("Days overdue: " + loan.getDaysOverdue());
        }
    }

    /**
     * printLoanList prints out a list of all loans and information about them
     * @param loans the list of loans
     */
    public static void printLoanList(List<Loan> loans) {
        if (loans.isEmpty()) {
            printInfo("No loans found.");
            return;
        }
        printPaginatedTable(
            loans,
            "Loans",
            67,
            () -> System.out.printf("%-15s %-15s %-12s %-12s %-10s%n",
                "Loan ID", "User ID", "Loan Date", "Due Date", "Status"),
            loan -> System.out.printf("%-15s %-15s %-12s %-12s %-10s%n",
                formatUuid(loan.getLoanId()),
                formatUuid(loan.getUserId()),
                DateUtils.format(loan.getIssueDate()),
                DateUtils.format(loan.getDueDate()),
                loan.getStatus())
        );
    }

    /**
     * printFine prints all relevant information known about a fine
     * @param fine the fine, of which the information should be printed
     */
    public static void printFine(Fine fine) {
        System.out.println("Fine ID: " + fine.getFineId());
        System.out.println("User ID: " + (fine.getUserId() != null ? fine.getUserId() : "N/A"));
        System.out.println("Loan ID: " + (fine.getLoanId() != null ? fine.getLoanId() : "N/A"));
        System.out.println("Amount: â‚¬" + String.format("%.2f", fine.getAmount()));
        System.out.println("Issue Date: " + (fine.getIssueDate() != null ? DateUtils.format(fine.getIssueDate()) : "N/A"));
        System.out.println("Status: " +  (fine.getStatus() != null ? fine.getStatus() : "N/A"));
        System.out.println("Note: " +  (fine.getNote() != null ? fine.getNote() : "No note added."));
        if (fine.getStatus() == FineStatus.OVERDUE) {
            System.out.println("Days overdue: " + fine.getDaysOverdue());
        }
    }

    /**
     * printFineList prints out a list of all fines and information about them
     * @param fines the list of fines
     */
    public static void printFineList(List<Fine> fines) {
        if (fines.isEmpty()) {
            printInfo("No fines found.");
            return;
        }
        printPaginatedTable(
            fines,
            "Fines",
            90,
            () -> System.out.printf("%-15s %-15s %-15s %-10s %-12s %-10s%n",
                "Fine ID", "User ID", "Loan ID", "Amount", "Issue Date", "Status"),
            fine -> System.out.printf("%-15s %-15s %-15s â‚¬%-9.2f %-12s %-10s%n",
                formatUuid(fine.getFineId()),
                formatUuid(fine.getUserId()),
                formatUuid(fine.getLoanId()),
                fine.getAmount(),
                DateUtils.format(fine.getIssueDate()),
                fine.getStatus())
        );
    }

    /**
     * printReservation prints all relevant information known about a reservation
     * @param reservation the reservation, of which information should be printed
     */
    public static void printReservation(Reservation reservation) {

    }

    /**
     * printReservationList prints out a list of all reservations and information about them
     * @param reservationList the list of reservations
     */
    public static void printReservationList(List<Reservation> reservationList) {

    }

    /**
     * centerText tries to centre text inside the terminal
     * @param text the text to centre
     * @return the centred text
     */
    private static String centerText(String text) {
        int padding = (SEPARATOR_LENGTH - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    /**
     * truncate tries to truncate text that is too long for the application frame
     * @param text the text to truncate
     * @param maxLength the max number of characters
     * @return the truncated text
     */
    private static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }

    private static String formatUuid(UUID id) {
        if (id == null) {
            return "N/A";
        }
        String value = id.toString();
        if (value.length() <= 15) {
            return value;
        }
        return value.substring(0, 8) + "..." + value.substring(value.length() - 4);
    }

    public static void clearScreen() {
        try {
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
        } catch (Exception e) {
            for (int i = 0; i < 100; i++) System.out.println();
        }
    }

    private static <T> void printPaginatedTable(List<T> rows,
                                                String tableName,
                                                int tableWidth,
                                                Runnable headerPrinter,
                                                Consumer<T> rowPrinter) {
        if (rows.isEmpty()) {
            return;
        }

        int totalPages = (rows.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        int currentPage = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Clear screen and print current page
            clearScreen();
            printPage(rows, currentPage, totalPages, tableName, tableWidth, headerPrinter, rowPrinter);

            // Show navigation info
            System.out.println();
            printTableSeparator(tableWidth);
            System.out.println(formatPagingControls(tableWidth));
            printTableSeparator(tableWidth);
            System.out.printf("Page %d/%d | Enter page number (1-%d) or command: ", currentPage + 1, totalPages, totalPages);

            String input = scanner.nextLine().trim().toLowerCase();

            if (input.isEmpty()) {
                continue;
            }

            if (input.equals("n") || input.equals("next")) {
                if (currentPage < totalPages - 1) {
                    currentPage++;
                } else {
                    System.out.println("Already on the last page.");
                }
                continue;
            }

            if (input.equals("p") || input.equals("previous") || input.equals("prev")) {
                if (currentPage > 0) {
                    currentPage--;
                } else {
                    System.out.println("Already on the first page.");
                }
                continue;
            }

            if (input.equals("b") || input.equals("back") || input.equals("q") || input.equals("quit") || input.equals("0")) {
                return;
            }

            try {
                int pageNum = Integer.parseInt(input);
                if (pageNum >= 1 && pageNum <= totalPages) {
                    currentPage = pageNum - 1;
                } else {
                    System.out.printf("Invalid page. Please enter a number between 1 and %d.%n", totalPages);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid command. Use p (previous), b (back), n (next), or a page number.");
            }
        }
    }

    private static <T> void printPage(List<T> rows,
                                      int pageIndex,
                                      int totalPages,
                                      String tableName,
                                      int tableWidth,
                                      Runnable headerPrinter,
                                      Consumer<T> rowPrinter) {
        int start = pageIndex * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, rows.size());

        int effectiveWidth = Math.max(SEPARATOR_LENGTH, tableWidth);
        System.out.println("=".repeat(effectiveWidth));
        System.out.println(centerTextToWidth(tableName, effectiveWidth));
        System.out.println("=".repeat(effectiveWidth));
        System.out.printf("Entries %d-%d of %d | Page %d/%d%n",
            start + 1,
            end,
            rows.size(),
            pageIndex + 1,
            totalPages);
        printTableSeparator(tableWidth);
        headerPrinter.run();
        printTableSeparator(tableWidth);

        for (int i = start; i < end; i++) {
            rowPrinter.accept(rows.get(i));
        }
    }

    private static void printTableSeparator(int width) {
        System.out.println("-".repeat(Math.max(SEPARATOR_LENGTH, width)));
    }

    private static String centerTextToWidth(String text, int width) {
        int padding = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(padding) + text;
    }

    private static String formatPagingControls(int totalWidth) {
        int width = Math.max(SEPARATOR_LENGTH, totalWidth);
        String left = "p (previous page)";
        String middle = "b (back)";
        String right = "n (next page)";

        if (width <= (CONTROL_SECTION_WIDTH * 3)) {
            return left + " | " + middle + " | " + right;
        }

        return String.format("%-" + CONTROL_SECTION_WIDTH + "s%-" + CONTROL_SECTION_WIDTH + "s%-" + CONTROL_SECTION_WIDTH + "s",
                left,
                middle,
                right);
    }
}
