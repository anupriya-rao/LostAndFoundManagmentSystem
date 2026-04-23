import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemService {
    private final Map<Integer, Item> items;
    private int nextItemId;

    public ItemService() {
        items = new HashMap<>();
        loadItems();
    }

    public Item reportLostItem(String name, String description, String category, String location, String date, String owner, int userId) {
        validateItemInput(name, category, location, date);
        Item item = new LostItem(nextItemId++, name.trim(), description.trim(), category.trim(), location.trim(), date.trim(), ItemStatus.LOST, owner.trim(), userId);
        items.put(item.getItemId(), item);
        saveItems();
        return item;
    }

    public Item reportFoundItem(String name, String description, String category, String location, String date, String owner, int userId) {
        validateItemInput(name, category, location, date);
        Item item = new FoundItem(nextItemId++, name.trim(), description.trim(), category.trim(), location.trim(), date.trim(), ItemStatus.FOUND, owner.trim(), userId);
        items.put(item.getItemId(), item);
        saveItems();
        return item;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<Item> getItemsByType(String type) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getType().equalsIgnoreCase(type)) {
                result.add(item);
            }
        }
        return result;
    }

    public Item getItemById(int id) {
        return items.get(id);
    }

    public boolean deleteItem(int id) {
        if (items.remove(id) != null) {
            saveItems();
            return true;
        }
        return false;
    }

    public List<Item> searchItems(String query, String field) {
        String normalized = query.trim().toLowerCase();
        List<Item> result = new ArrayList<>();

        for (Item item : items.values()) {
            String target;
            switch (field.toLowerCase()) {
                case "name":
                    target = item.getItemName();
                    break;
                case "category":
                    target = item.getCategory();
                    break;
                case "location":
                    target = item.getLocation();
                    break;
                default:
                    target = "";
            }

            if (target.toLowerCase().contains(normalized)) {
                result.add(item);
            }
        }
        return result;
    }

    public void saveItems() {
        List<String> lines = new ArrayList<>();
        for (Item item : items.values()) {
            lines.add(item.getItemId() + "|" +
                    item.getType() + "|" +
                    FileStorageUtil.escape(item.getItemName()) + "|" +
                    FileStorageUtil.escape(item.getDescription()) + "|" +
                    FileStorageUtil.escape(item.getCategory()) + "|" +
                    FileStorageUtil.escape(item.getLocation()) + "|" +
                    FileStorageUtil.escape(item.getDate()) + "|" +
                    item.getStatus() + "|" +
                    FileStorageUtil.escape(item.getOwner()) + "|" +
                    item.getReportedByUserId());
        }
        FileStorageUtil.writeLines(FileStorageUtil.ITEMS_FILE, lines);
    }

    private void loadItems() {
        List<String> lines = FileStorageUtil.readLines(FileStorageUtil.ITEMS_FILE);
        int maxId = 0;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            List<String> parts = FileStorageUtil.splitEscaped(line);
            if (parts.size() != 10) {
                continue;
            }

            int id = Integer.parseInt(parts.get(0));
            String type = parts.get(1);
            String name = parts.get(2);
            String description = parts.get(3);
            String category = parts.get(4);
            String location = parts.get(5);
            String date = parts.get(6);
            ItemStatus status = ItemStatus.valueOf(parts.get(7));
            String owner = parts.get(8);
            int reportedByUserId = Integer.parseInt(parts.get(9));

            Item item = type.equalsIgnoreCase("LOST")
                    ? new LostItem(id, name, description, category, location, date, status, owner, reportedByUserId)
                    : new FoundItem(id, name, description, category, location, date, status, owner, reportedByUserId);
            items.put(id, item);
            if (id > maxId) {
                maxId = id;
            }
        }

        nextItemId = maxId + 1;
        if (nextItemId < 1000) {
            nextItemId = 1000;
        }
    }

    private void validateItemInput(String name, String category, String location, String date) {
        if (name.trim().isEmpty() || category.trim().isEmpty() || location.trim().isEmpty() || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Name, category, location, and date are required.");
        }
    }
}
