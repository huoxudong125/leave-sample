package ddd.leave.domain.rule.entity;

import ddd.leave.domain.leave.entity.LeaveRecord;
import lombok.Data;

@Data
public class ApprovalRule {

    String personType;
    String leaveType;
    long duration;
    int maxLeaderLevel;

    public static ApprovalRule getByLeave(LeaveRecord leaveRecord){
        ApprovalRule rule = new ApprovalRule();
        rule.setPersonType(leaveRecord.getApplicant().getPersonType());
        rule.setLeaveType(leaveRecord.getType().toString());
        rule.setDuration(leaveRecord.getDuration());
        return rule;
    }
}
