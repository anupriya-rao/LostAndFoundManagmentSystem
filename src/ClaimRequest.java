public class ClaimRequest {
    private final int claimId;
    private final int itemId;
    private final int requesterUserId;
    private final String proof;
    private ClaimStatus status;

    public ClaimRequest(int claimId, int itemId, int requesterUserId, String proof, ClaimStatus status) {
        this.claimId = claimId;
        this.itemId = itemId;
        this.requesterUserId = requesterUserId;
        this.proof = proof;
        this.status = status;
    }

    public int getClaimId() {
        return claimId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getRequesterUserId() {
        return requesterUserId;
    }

    public String getProof() {
        return proof;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClaimRequest{" +
                "claimId=" + claimId +
                ", itemId=" + itemId +
                ", requesterUserId=" + requesterUserId +
                ", proof='" + proof + '\'' +
                ", status=" + status +
                '}';
    }
}
