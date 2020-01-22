package ddd.leave.application.service;

import ddd.leave.domain.leave.entity.LeaveRecord;
import ddd.leave.domain.leave.entity.valueobject.Approver;
import ddd.leave.domain.leave.service.LeaveDomainService;
import ddd.leave.domain.person.entity.Person;
import ddd.leave.domain.person.service.PersonDomainService;
import ddd.leave.domain.rule.service.ApprovalRuleDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveApplicationService{

    @Autowired
    LeaveDomainService leaveDomainService;
    @Autowired
    PersonDomainService personDomainService;
    @Autowired
    ApprovalRuleDomainService approvalRuleDomainService;

    /**
     * 创建一个请假申请并为审批人生成任务
     * @param leaveRecord
     */
    public void createLeaveInfo(LeaveRecord leaveRecord){
        //get approval leader max level by rule
        int leaderMaxLevel = approvalRuleDomainService.getLeaderMaxLevel(leaveRecord.getApplicant().getPersonType(), leaveRecord.getType().toString(), leaveRecord.getDuration());
        //find next approver
        Person approver = personDomainService.findFirstApprover(leaveRecord.getApplicant().getPersonId(), leaderMaxLevel);
        leaveDomainService.createLeave(leaveRecord, leaderMaxLevel, Approver.fromPerson(approver));
    }

    /**
     * 更新请假单基本信息
     * @param leaveRecord
     */
    public void updateLeaveInfo(LeaveRecord leaveRecord){
        leaveDomainService.updateLeaveInfo(leaveRecord);
    }

    /**
     * 提交审批，更新请假单信息
     * @param leaveRecord
     */
    public void submitApproval(LeaveRecord leaveRecord){
        //find next approver
        Person approver = personDomainService.findNextApprover(leaveRecord.getApprover().getPersonId(), leaveRecord.getLeaderMaxLevel());
        leaveDomainService.submitApproval(leaveRecord, Approver.fromPerson(approver));
    }

    public LeaveRecord getLeaveInfo(String leaveId){
        return leaveDomainService.getLeaveInfo(leaveId);
    }

    public List<LeaveRecord> queryLeaveInfosByApplicant(String applicantId){
        return leaveDomainService.queryLeaveInfosByApplicant(applicantId);
    }

    public List<LeaveRecord> queryLeaveInfosByApprover(String approverId){
        return leaveDomainService.queryLeaveInfosByApprover(approverId);
    }
}