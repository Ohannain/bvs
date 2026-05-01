package de.dhbw.ui.menus;

import de.dhbw.application.user.UserService;
import de.dhbw.application.user.UserValidator;
import de.dhbw.domain.user.User;
import de.dhbw.ui.InputHandler;
import de.dhbw.ui.Menu;
import de.dhbw.ui.OutputFormatter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.dhbw.util.UUID;

public class UserMenu extends Menu {

    private final UserService userService;

    public UserMenu(String title, InputHandler inputHandler, UserService userService) {
        super(title, inputHandler);
        this.userService = userService;

        initializeMenuItems();
    }

    private void initializeMenuItems() {
        addMenuItem("Create New User", this::createUser);
        addMenuItem("Search User", this::searchUser);
        addMenuItem("List All Users", this::listAllUsers);
        addMenuItem("Update User", this::updateUser);
        addMenuItem("Suspend User", this::suspendUser);
        addMenuItem("Activate User", this::activateUser);
        addMenuItem("Delete User", this::deleteUser);
    }

    private void createUser() {
        OutputFormatter.printHeader("User Management - Create New User");

        String firstName = inputHandler.readNonEmptyString("First Name*: ");
        String lastName = inputHandler.readNonEmptyString("Last Name*: ");
        String email = inputHandler.readNonEmptyString("Email*: ");
        String phone = inputHandler.readString("Phone (optional): ");
        String address = inputHandler.readString("Address (optional): ");

        try {
            User user = userService.createUser(firstName, lastName, email, phone, address);
            OutputFormatter.printSuccess("User created successfully");
            OutputFormatter.printUser(user);
        } catch (Exception e) {
            OutputFormatter.printError("Failed to create user: " + e.getMessage());
        }
    }

    private void searchUser() {
        String searchString = inputHandler.readNonEmptyString("Enter Name (or part of name): ");
        
        List<User> userById = UUID.parseUuid(searchString)
            .map(userService::getUserById)
            .orElseGet(List::of);
        
        List<User> usersByName = userService.searchUsersByName(searchString);
        
        List<User> userList = Stream.concat(usersByName.stream(), userById.stream())
            .distinct()
            .collect(Collectors.toList());
        
        OutputFormatter.printUserList(userList);
    }

    private void listAllUsers() {
        List<User> users = userService.getAllUsers();
        OutputFormatter.printUserList(users);
    }

    private void updateUser() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<UUID> userUuid = UUID.parseUuid(userId);
        if (userUuid.isEmpty()) {
            return;
        }
        List<User> userList = userService.getUserById(userUuid.get());

        if (userList.isEmpty()) {
            OutputFormatter.printWarning("User not found.");
            return;
        }

        User user = userList.getFirst();
        System.out.println("Leave blank to keep current value");

        String firstName = inputHandler.readString(
                "First Name [" + user.getFirstName() + "]: "
        );
        if (!firstName.isEmpty()) user.setFirstName(firstName);

        String lastName = inputHandler.readString("Last Name [" + user.getLastName() + "]: ");
        if (!lastName.isEmpty()) user.setLastName(lastName);

        String email = inputHandler.readString("Email [" + user.getEmail() + "]: ");
        if (!email.isEmpty()) user.setEmail(email);

        String phone = inputHandler.readString("Phone [" + (user.getPhone() == null ? "" : user.getPhone()) + "]: ");
        while (!phone.isEmpty() && !UserValidator.isValidPhone(phone)) {
            System.out.println("Invalid phone format. Enter a valid phone or press Enter to skip.");
            phone = inputHandler.readString("Phone [" + (user.getPhone() == null ? "" : user.getPhone()) + "]: ");
        }
        if (!phone.isEmpty()) user.setPhone(phone);

        String address = inputHandler.readString("Address [" + (user.getAddress() == null ? "" : user.getAddress()) + "]: ");
        if (!address.isEmpty()) user.setAddress(address);

        try {
            userService.updateUser(user);
            OutputFormatter.printSuccess("User updated successfully!");
        } catch (Exception e) {
            OutputFormatter.printError("Failed to update user: " + e.getMessage());
        }
    }

    private void suspendUser() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        String reason = inputHandler.readNonEmptyString("Reason for suspension: ");
        Optional<UUID> userUuid = UUID.parseUuid(userId);
        if (userUuid.isEmpty()) {
            return;
        }

        try {
            userService.suspendUser(userUuid.get(), reason);
            OutputFormatter.printSuccess("User suspended successfully.");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void activateUser() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<UUID> userUuid = UUID.parseUuid(userId);
        if (userUuid.isEmpty()) {
            return;
        }

        try {
            userService.activateUser(userUuid.get());
            OutputFormatter.printSuccess("User activated successfully.");
        } catch (Exception e) {
            OutputFormatter.printError(e.getMessage());
        }
    }

    private void deleteUser() {
        String userId = inputHandler.readNonEmptyString("Enter User ID: ");
        Optional<UUID> userUuid = UUID.parseUuid(userId);
        if (userUuid.isEmpty()) {
            return;
        }
        boolean confirm = inputHandler.readBoolean(
                "Are you sure you want to delete this user?"
        );

        if (confirm) {
            try {
                userService.deleteUser(userUuid.get());
                OutputFormatter.printSuccess("User deleted successfully.");
            } catch (Exception e) {
                OutputFormatter.printError(e.getMessage());
            }
        }
    }
}
