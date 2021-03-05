package morn.xposed.alipay.ui;

public class AliFriend extends AlipayId {
    private String logonId;
    private String showName;
    private int friendStatus;
    private String userName;
    private String userId;
    private long version;

    public String getLogonId() {
        return this.logonId;
    }

    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }

    public void serVersion(long version) {
        this.version = version;
    }

    public void setLogonId(String logonId) {
        this.logonId = logonId;
    }

    public String getShowName() {
        return this.showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getFriendStatus() {
        return this.friendStatus;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public long getVersion() {
        return this.version;
    }
}