public abstract class Item {
    private final int itemId;
    private String itemName;
    private String description;
    private String category;
    private String location;
    private String date;
    private ItemStatus status;
    private String owner;
    private final int reportedByUserId;

    protected Item(int itemId,
                   String itemName,
                   String description,
                   String category,
                   String location,
                   String date,
                   ItemStatus status,
                   String owner,
                   int reportedByUserId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.location = location;
        this.date = date;
        this.status = status;
        this.owner = owner;
        this.reportedByUserId = reportedByUserId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public int getReportedByUserId() {
        return reportedByUserId;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", type='" + getType() + '\'' +
                ", itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", status=" + status +
                ", owner='" + owner + '\'' +
                ", reportedByUserId=" + reportedByUserId +
                '}';
    }
}
