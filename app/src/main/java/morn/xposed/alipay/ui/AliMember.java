package morn.xposed.alipay.ui;

public class AliMember extends AlipayId implements Comparable<AliMember> {
    public int canAddNum;
    public int friendNum;
    public boolean isCurrentUserQuit;
    public int memberNum;

    @Override
    public int compareTo(AliMember member) {
        int t = this.canAddNum - member.canAddNum;
        if (t == 0) {
            t = this.friendNum - member.friendNum;
            if (t == 0) {
                return this.memberNum - member.memberNum;
            }
            return t;
        }
        return t;
    }
}