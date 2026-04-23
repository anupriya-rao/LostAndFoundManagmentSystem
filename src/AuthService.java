import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {
    private final Map<String, User> usersByUsername;
    private int nextUserId;

    public AuthService() {
        this.usersByUsername = new HashMap<>();
        loadUsers();
        ensureDefaultAdmin();
    }

    public User registerUser(String username, String password) {
        String normalizedUsername = username.trim();
        if (normalizedUsername.isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }
        if (usersByUsername.containsKey(normalizedUsername.toLowerCase())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        User user = new User(nextUserId++, normalizedUsername, password, Role.USER);
        usersByUsername.put(normalizedUsername.toLowerCase(), user);
        saveUsers();
        return user;
    }

    public User login(String username, String password) {
        User user = usersByUsername.get(username.trim().toLowerCase());
        if (user == null || !user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersByUsername.values());
    }

    public User getUserById(int userId) {
        for (User user : usersByUsername.values()) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    public boolean deleteUser(int userId) {
        User target = getUserById(userId);
        if (target == null || target.getRole() == Role.ADMIN) {
            return false;
        }
        usersByUsername.remove(target.getUsername().toLowerCase());
        saveUsers();
        return true;
    }

    private void ensureDefaultAdmin() {
        if (!usersByUsername.containsKey("admin")) {
            usersByUsername.put("admin", new Admin(nextUserId++, "admin", "admin123"));
            saveUsers();
        }
    }

    private void loadUsers() {
        List<String> lines = FileStorageUtil.readLines(FileStorageUtil.USERS_FILE);
        int maxId = 0;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            List<String> parts = FileStorageUtil.splitEscaped(line);
            if (parts.size() != 4) {
                continue;
            }

            int id = Integer.parseInt(parts.get(0));
            String username = parts.get(1);
            String password = parts.get(2);
            Role role = Role.valueOf(parts.get(3));

            User user = role == Role.ADMIN
                    ? new Admin(id, username, password)
                    : new User(id, username, password, Role.USER);

            usersByUsername.put(username.toLowerCase(), user);
            if (id > maxId) {
                maxId = id;
            }
        }

        nextUserId = maxId + 1;
        if (nextUserId < 1) {
            nextUserId = 1;
        }
    }

    public void saveUsers() {
        List<String> lines = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            lines.add(user.getId() + "|" +
                    FileStorageUtil.escape(user.getUsername()) + "|" +
                    FileStorageUtil.escape(user.getPassword()) + "|" +
                    user.getRole());
        }
        FileStorageUtil.writeLines(FileStorageUtil.USERS_FILE, lines);
    }
}
