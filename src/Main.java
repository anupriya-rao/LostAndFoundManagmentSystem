import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        FileStorageUtil.ensureDataDirectory();

        AuthService authService = new AuthService();
        ItemService itemService = new ItemService();
        ClaimService claimService = new ClaimService();

        System.out.println("=== University Lost & Found Item Management System ===");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = readInt("Choose option: ");

            switch (choice) {
                case 1:
                    handleRegister(authService);
                    break;
                case 2:
                    User user = handleLogin(authService);
                    if (user != null) {
                        if (user.getRole() == Role.ADMIN) {
                            adminMenu(user, authService, itemService, claimService);
                        } else {
                            userMenu(user, itemService, claimService);
                        }
                    }
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void handleRegister(AuthService authService) {
        System.out.println("\n--- Register ---");
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        try {
            User user = authService.registerUser(username, password);
            System.out.println("Registration successful. User ID: " + user.getId());
        } catch (IllegalArgumentException ex) {
            System.out.println("Registration failed: " + ex.getMessage());
        }
    }

    private static User handleLogin(AuthService authService) {
        System.out.println("\n--- Login ---");
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        User user = authService.login(username, password);
        if (user == null) {
            System.out.println("Login failed: Invalid username/password.");
            return null;
        }

        System.out.println("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        return user;
    }

    private static void userMenu(User user, ItemService itemService, ClaimService claimService) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- User Dashboard ---");
            System.out.println("1. Report Lost Item");
            System.out.println("2. Report Found Item");
            System.out.println("3. View All Lost Items");
            System.out.println("4. View All Found Items");
            System.out.println("5. Search Items");
            System.out.println("6. Request to Claim Item");
            System.out.println("7. View My Claim Request Status");
            System.out.println("8. Logout");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1:
                    reportItem(itemService, user, true);
                    break;
                case 2:
                    reportItem(itemService, user, false);
                    break;
                case 3:
                    displayItems(itemService.getItemsByType("LOST"));
                    break;
                case 4:
                    displayItems(itemService.getItemsByType("FOUND"));
                    break;
                case 5:
                    searchItems(itemService);
                    break;
                case 6:
                    requestClaim(claimService, itemService, user);
                    break;
                case 7:
                    viewMyClaims(claimService, user);
                    break;
                case 8:
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void adminMenu(User admin, AuthService authService, ItemService itemService, ClaimService claimService) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Admin Panel ---");
            System.out.println("1. View All Reported Items");
            System.out.println("2. Approve/Reject Claim Requests");
            System.out.println("3. Mark Item as Returned");
            System.out.println("4. Delete Fake/Invalid Item Report");
            System.out.println("5. Manage Users");
            System.out.println("6. Logout");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1:
                    displayItems(itemService.getAllItems());
                    break;
                case 2:
                    processClaims(claimService, itemService);
                    break;
                case 3:
                    markReturned(itemService);
                    break;
                case 4:
                    deleteItem(itemService);
                    break;
                case 5:
                    manageUsers(authService, admin);
                    break;
                case 6:
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void reportItem(ItemService itemService, User user, boolean isLost) {
        System.out.println(isLost ? "\n--- Report Lost Item ---" : "\n--- Report Found Item ---");
        String name = readLine("Item name: ");
        String description = readLine("Description: ");
        String category = readLine("Category: ");
        String location = readLine("Location: ");
        String date = readLine("Date (YYYY-MM-DD): ");
        String owner = readLine("Owner (if known): ");

        try {
            Item item = isLost
                    ? itemService.reportLostItem(name, description, category, location, date, owner, user.getId())
                    : itemService.reportFoundItem(name, description, category, location, date, owner, user.getId());
            System.out.println("Item reported successfully. Item ID: " + item.getItemId());
        } catch (IllegalArgumentException ex) {
            System.out.println("Failed to report item: " + ex.getMessage());
        }
    }

    private static void searchItems(ItemService itemService) {
        System.out.println("\nSearch by:");
        System.out.println("1. Name");
        System.out.println("2. Category");
        System.out.println("3. Location");

        int option = readInt("Choose field: ");
        String field;
        switch (option) {
            case 1:
                field = "name";
                break;
            case 2:
                field = "category";
                break;
            case 3:
                field = "location";
                break;
            default:
                System.out.println("Invalid search option.");
                return;
        }

        String query = readLine("Enter search keyword: ");
        List<Item> result = itemService.searchItems(query, field);
        displayItems(result);
    }

    private static void requestClaim(ClaimService claimService, ItemService itemService, User user) {
        int itemId = readInt("Enter Item ID to claim: ");
        String proof = readLine("Provide proof/description: ");
        try {
            ClaimRequest request = claimService.createClaimRequest(itemId, user.getId(), proof, itemService);
            System.out.println("Claim request submitted. Claim ID: " + request.getClaimId());
        } catch (IllegalArgumentException ex) {
            System.out.println("Unable to submit claim: " + ex.getMessage());
        }
    }

    private static void viewMyClaims(ClaimService claimService, User user) {
        List<ClaimRequest> claims = claimService.getClaimsByUserId(user.getId());
        if (claims.isEmpty()) {
            System.out.println("No claims found.");
            return;
        }
        for (ClaimRequest claim : claims) {
            System.out.println(claim);
        }
    }

    private static void processClaims(ClaimService claimService, ItemService itemService) {
        List<ClaimRequest> claims = claimService.getAllClaims();
        if (claims.isEmpty()) {
            System.out.println("No claim requests available.");
            return;
        }

        for (ClaimRequest claim : claims) {
            System.out.println(claim);
        }

        int claimId = readInt("Enter Claim ID to process: ");
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        int decision = readInt("Choose decision: ");

        boolean processed = claimService.processClaim(claimId, decision == 1, itemService);
        if (processed) {
            System.out.println("Claim processed successfully.");
        } else {
            System.out.println("Unable to process claim.");
        }
    }

    private static void markReturned(ItemService itemService) {
        int itemId = readInt("Enter Item ID to mark returned: ");
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }
        item.setStatus(ItemStatus.RETURNED);
        itemService.saveItems();
        System.out.println("Item marked as RETURNED.");
    }

    private static void deleteItem(ItemService itemService) {
        int itemId = readInt("Enter Item ID to delete: ");
        boolean deleted = itemService.deleteItem(itemId);
        if (deleted) {
            System.out.println("Item deleted.");
        } else {
            System.out.println("Item not found.");
        }
    }

    private static void manageUsers(AuthService authService, User admin) {
        System.out.println("\n--- Manage Users ---");
        for (User user : authService.getAllUsers()) {
            System.out.println(user);
        }

        int userId = readInt("Enter user ID to delete (0 to cancel): ");
        if (userId == 0) {
            return;
        }

        if (userId == admin.getId()) {
            System.out.println("You cannot delete yourself.");
            return;
        }

        boolean deleted = authService.deleteUser(userId);
        if (deleted) {
            System.out.println("User deleted.");
        } else {
            System.out.println("Unable to delete user (not found or admin account).");
        }
    }

    private static void displayItems(List<Item> items) {
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        for (Item item : items) {
            System.out.println(item);
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }
}
