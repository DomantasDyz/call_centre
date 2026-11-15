package lt.callcentre.model;

import java.time.LocalDateTime;

public class Call {
    private final int id;
    private final String operatorId;
    private final String phoneNumber;
    private boolean vip;
    private final LocalDateTime createdAt;
    private CallStatus status;

    public Call(int id, String operatorId, String phoneNumber, boolean vip, LocalDateTime createdAt) {
        this.id = id;
        this.operatorId = operatorId;
        this.phoneNumber = phoneNumber;
        this.vip = vip;
        this.createdAt = createdAt;
        this.status = CallStatus.WAITING;
    }

    public int getId() { return id; }
    public String getOperatorId() { return operatorId; }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean isVip() { return vip; }
    public void setVip(boolean vip) { this.vip = vip; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public CallStatus getStatus() { return status; }
    public void setStatus(CallStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "ID=" + id
                + ", numeris=" + phoneNumber
                + ", VIP=" + vip
                + ", statusas=" + status
                + ", sukurtas=" + createdAt;
    }
}
