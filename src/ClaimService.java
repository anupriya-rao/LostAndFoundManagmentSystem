import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimService {
    private final Map<Integer, ClaimRequest> claims;
    private int nextClaimId;

    public ClaimService() {
        claims = new HashMap<>();
        loadClaims();
    }

    public ClaimRequest createClaimRequest(int itemId, int requesterUserId, String proof, ItemService itemService) {
        if (proof.trim().isEmpty()) {
            throw new IllegalArgumentException("Proof cannot be empty.");
        }

        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found.");
        }
        if (item.getStatus() == ItemStatus.RETURNED) {
            throw new IllegalArgumentException("This item is already returned.");
        }

        ClaimRequest request = new ClaimRequest(nextClaimId++, itemId, requesterUserId, proof.trim(), ClaimStatus.PENDING);
        claims.put(request.getClaimId(), request);
        item.setStatus(ItemStatus.CLAIMED);
        itemService.saveItems();
        saveClaims();
        return request;
    }

    public List<ClaimRequest> getAllClaims() {
        return new ArrayList<>(claims.values());
    }

    public List<ClaimRequest> getClaimsByUserId(int userId) {
        List<ClaimRequest> result = new ArrayList<>();
        for (ClaimRequest claim : claims.values()) {
            if (claim.getRequesterUserId() == userId) {
                result.add(claim);
            }
        }
        return result;
    }

    public boolean processClaim(int claimId, boolean approve, ItemService itemService) {
        ClaimRequest request = claims.get(claimId);
        if (request == null || request.getStatus() != ClaimStatus.PENDING) {
            return false;
        }

        Item item = itemService.getItemById(request.getItemId());
        if (item == null) {
            return false;
        }

        if (approve) {
            request.setStatus(ClaimStatus.APPROVED);
            item.setStatus(ItemStatus.RETURNED);
        } else {
            request.setStatus(ClaimStatus.REJECTED);
            if (item.getStatus() != ItemStatus.RETURNED) {
                item.setStatus(item.getType().equals("LOST") ? ItemStatus.LOST : ItemStatus.FOUND);
            }
        }

        itemService.saveItems();
        saveClaims();
        return true;
    }

    private void loadClaims() {
        List<String> lines = FileStorageUtil.readLines(FileStorageUtil.CLAIMS_FILE);
        int maxId = 0;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            List<String> parts = FileStorageUtil.splitEscaped(line);
            if (parts.size() != 5) {
                continue;
            }

            int claimId = Integer.parseInt(parts.get(0));
            int itemId = Integer.parseInt(parts.get(1));
            int requesterUserId = Integer.parseInt(parts.get(2));
            String proof = parts.get(3);
            ClaimStatus status = ClaimStatus.valueOf(parts.get(4));

            claims.put(claimId, new ClaimRequest(claimId, itemId, requesterUserId, proof, status));
            if (claimId > maxId) {
                maxId = claimId;
            }
        }

        nextClaimId = maxId + 1;
        if (nextClaimId < 5000) {
            nextClaimId = 5000;
        }
    }

    public void saveClaims() {
        List<String> lines = new ArrayList<>();
        for (ClaimRequest claim : claims.values()) {
            lines.add(claim.getClaimId() + "|" +
                    claim.getItemId() + "|" +
                    claim.getRequesterUserId() + "|" +
                    FileStorageUtil.escape(claim.getProof()) + "|" +
                    claim.getStatus());
        }
        FileStorageUtil.writeLines(FileStorageUtil.CLAIMS_FILE, lines);
    }
}
