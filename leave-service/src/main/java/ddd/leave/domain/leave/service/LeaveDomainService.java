package ddd.leave.domain.leave.service;

import ddd.leave.domain.leave.entity.LeaveRecord;
import ddd.leave.domain.leave.entity.valueobject.ApprovalType;
import ddd.leave.domain.leave.entity.valueobject.Approver;
import ddd.leave.domain.leave.event.LeaveEvent;
import ddd.leave.domain.leave.event.LeaveEventType;
import ddd.leave.domain.leave.repository.facade.LeaveRepositoryInterface;
import ddd.leave.domain.leave.repository.po.LeavePO;
import ddd.leave.infrastructure.common.event.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LeaveDomainService {

    @Autowired
    EventPublisher eventPublisher;
    @Autowired
    LeaveRepositoryInterface leaveRepositoryInterface;
    @Autowired
    LeaveFactory leaveFactory;

    @Transactional
    public void createLeave(LeaveRecord leaveRecord, int leaderMaxLevel, Approver approver) {
        leaveRecord.setLeaderMaxLevel(leaderMaxLevel);
        leaveRecord.setApprover(approver);
        leaveRecord.create();
        leaveRepositoryInterface.save(leaveFactory.createLeavePO(leaveRecord));
        LeaveEvent event = LeaveEvent.create(LeaveEventType.CREATE_EVENT, leaveRecord);
        leaveRepositoryInterface.saveEvent(leaveFactory.createLeaveEventPO(event));
        eventPublisher.publish(event);
    }

    @Transactional
    public void updateLeaveInfo(LeaveRecord leaveRecord) {
        LeavePO po = leaveRepositoryInterface.findById(leaveRecord.getId());
        if (null == po) {
            throw new RuntimeException("leaveRecord does not exist");
        }
        leaveRepositoryInterface.save(leaveFactory.createLeavePO(leaveRecord));
    }

    @Transactional
    public void submitApproval(LeaveRecord leaveRecord, Approver approver) {
        LeaveEvent event;
        if ( ApprovalType.REJECT == leaveRecord.getCurrentApprovalInfo().getApprovalType()) {
            //reject, then the leaveRecord is finished with REJECTED status
            leaveRecord.reject(approver);
            event = LeaveEvent.create(LeaveEventType.REJECT_EVENT, leaveRecord);
        } else {
            if (approver != null) {
                //agree and has next approver
                leaveRecord.agree(approver);
                event = LeaveEvent.create(LeaveEventType.AGREE_EVENT, leaveRecord);
            } else {
                //agree and hasn't next approver, then the leaveRecord is finished with APPROVED status
                leaveRecord.finish();
                event = LeaveEvent.create(LeaveEventType.APPROVED_EVENT, leaveRecord);
            }
        }
        leaveRecord.addHistoryApprovalInfo(leaveRecord.getCurrentApprovalInfo());
        leaveRepositoryInterface.save(leaveFactory.createLeavePO(leaveRecord));
        leaveRepositoryInterface.saveEvent(leaveFactory.createLeaveEventPO(event));
        eventPublisher.publish(event);
    }

    public LeaveRecord getLeaveInfo(String leaveId) {
        LeavePO leavePO = leaveRepositoryInterface.findById(leaveId);
        return leaveFactory.getLeave(leavePO);
    }

    public List<LeaveRecord> queryLeaveInfosByApplicant(String applicantId) {
        List<LeavePO> leavePOList = leaveRepositoryInterface.queryByApplicantId(applicantId);
        return leavePOList.stream()
                .map(leavePO -> leaveFactory.getLeave(leavePO))
                .collect(Collectors.toList());
    }

    public List<LeaveRecord> queryLeaveInfosByApprover(String approverId) {
        List<LeavePO> leavePOList = leaveRepositoryInterface.queryByApproverId(approverId);
        return leavePOList.stream()
                .map(leavePO -> leaveFactory.getLeave(leavePO))
                .collect(Collectors.toList());
    }
}