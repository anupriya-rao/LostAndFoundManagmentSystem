import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileStorageUtil {
    public static final String USERS_FILE = "data/users.txt";
    public static final String ITEMS_FILE = "data/items.txt";
    public static final String CLAIMS_FILE = "data/claims.txt";

    public static void ensureDataDirectory() {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create data directory.", e);
        }
    }

    public static List<String> readLines(String path) {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    public static void writeLines(String path, List<String> lines) {
        Path filePath = Paths.get(path);
        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + path, e);
        }
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("|", "\\|").replace("\n", "\\n");
    }

    public static String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (escaping) {
                if (ch == 'n') {
                    result.append('\n');
                } else {
                    result.append(ch);
                }
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static List<String> splitEscaped(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (escaping) {
                current.append('\\').append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == '|') {
                parts.add(unescape(current.toString()));
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        parts.add(unescape(current.toString()));
        return parts;
    }
}
