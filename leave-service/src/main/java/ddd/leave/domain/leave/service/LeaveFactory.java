package ddd.leave.domain.leave.service;

import com.alibaba.fastjson.JSON;
import ddd.leave.domain.leave.entity.ApprovalInfo;
import ddd.leave.domain.leave.entity.LeaveRecord;
import ddd.leave.domain.leave.entity.valueobject.Applicant;
import ddd.leave.domain.leave.entity.valueobject.Approver;
import ddd.leave.domain.leave.event.LeaveEvent;
import ddd.leave.domain.leave.repository.po.ApprovalInfoPO;
import ddd.leave.domain.leave.repository.po.LeaveEventPO;
import ddd.leave.domain.leave.repository.po.LeavePO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaveFactory {

    public LeavePO createLeavePO(LeaveRecord leaveRecord) {
        LeavePO leavePO = new LeavePO();
        leavePO.setId(UUID.randomUUID().toString());
        leavePO.setApplicantId(leaveRecord.getApplicant().getPersonId());
        leavePO.setApplicantName(leaveRecord.getApplicant().getPersonName());
        leavePO.setApproverId(leaveRecord.getApprover().getPersonId());
        leavePO.setApproverName(leaveRecord.getApprover().getPersonName());
        leavePO.setStartTime(leaveRecord.getStartTime());
        leavePO.setStatus(leaveRecord.getStatus());
        List<ApprovalInfoPO> historyApprovalInfoPOList = approvalInfoPOListFromDO(leaveRecord);
        leavePO.setHistoryApprovalInfoPOList(historyApprovalInfoPOList);
        return leavePO;
    }

    public LeaveRecord getLeave(LeavePO leavePO) {
        LeaveRecord leaveRecord = new LeaveRecord();
        Applicant applicant = Applicant.builder()
                .personId(leavePO.getApplicantId())
                .personName(leavePO.getApplicantName())
                .build();
        leaveRecord.setApplicant(applicant);
        Approver approver = Approver.builder()
                .personId(leavePO.getApproverId())
                .personName(leavePO.getApproverName())
                .build();
        leaveRecord.setApprover(approver);
        leaveRecord.setStartTime(leaveRecord.getStartTime());
        leaveRecord.setStatus(leaveRecord.getStatus());
        List<ApprovalInfo> approvalInfos = getApprovalInfos(leavePO.getHistoryApprovalInfoPOList());
        leaveRecord.setHistoryApprovalInfos(approvalInfos);
        return leaveRecord;
    }

    public LeaveEventPO createLeaveEventPO(LeaveEvent leaveEvent){
        LeaveEventPO eventPO = new LeaveEventPO();
        eventPO.setLeaveEventType(leaveEvent.getLeaveEventType());
        eventPO.setSource(leaveEvent.getSource());
        eventPO.setTimestamp(leaveEvent.getTimestamp());
        eventPO.setData(JSON.toJSONString(leaveEvent.getData()));
        return eventPO;
    }

    private List<ApprovalInfoPO> approvalInfoPOListFromDO(LeaveRecord leaveRecord) {
        return leaveRecord.getHistoryApprovalInfos()
                .stream()
                .map(approvalInfo -> approvalInfoPOFromDO(approvalInfo))
                .collect(Collectors.toList());
    }

    private ApprovalInfoPO approvalInfoPOFromDO(ApprovalInfo approvalInfo){
        ApprovalInfoPO po = new ApprovalInfoPO();
        po.setApproverId(approvalInfo.getApprover().getPersonId());
        po.setApproverLevel(approvalInfo.getApprover().getLevel());
        po.setApproverName(approvalInfo.getApprover().getPersonName());
        po.setApprovalInfoId(approvalInfo.getApprovalInfoId());
        po.setMsg(approvalInfo.getMsg());
        po.setTime(approvalInfo.getTime());
        return po;
    }

    private ApprovalInfo approvalInfoFromPO(ApprovalInfoPO approvalInfoPO){
        ApprovalInfo approvalInfo = new ApprovalInfo();
        approvalInfo.setApprovalInfoId(approvalInfoPO.getApprovalInfoId());
        Approver approver = Approver.builder()
                .personId(approvalInfoPO.getApproverId())
                .personName(approvalInfoPO.getApproverName())
                .level(approvalInfoPO.getApproverLevel())
                .build();
        approvalInfo.setApprover(approver);
        approvalInfo.setMsg(approvalInfoPO.getMsg());
        approvalInfo.setTime(approvalInfoPO.getTime());
        return approvalInfo;
    }

    private List<ApprovalInfo> getApprovalInfos(List<ApprovalInfoPO> approvalInfoPOList){
        return approvalInfoPOList.stream()
                .map(approvalInfoPO -> approvalInfoFromPO(approvalInfoPO))
                .collect(Collectors.toList());
    }
}
