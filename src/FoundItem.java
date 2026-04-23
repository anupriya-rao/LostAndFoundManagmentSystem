public class FoundItem extends Item {
    public FoundItem(int itemId,
                     String itemName,
                     String description,
                     String category,
                     String location,
                     String date,
                     ItemStatus status,
                     String owner,
                     int reportedByUserId) {
        super(itemId, itemName, description, category, location, date, status, owner, reportedByUserId);
    }

    @Override
    public String getType() {
        return "FOUND";
    }
}
