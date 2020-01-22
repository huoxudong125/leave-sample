package ddd.leave.interfaces.assembler;

import ddd.leave.domain.leave.entity.ApprovalInfo;
import ddd.leave.domain.leave.entity.LeaveRecord;
import ddd.leave.infrastructure.util.DateUtil;
import ddd.leave.interfaces.dto.ApprovalInfoDTO;
import ddd.leave.interfaces.dto.LeaveDTO;

import java.util.List;
import java.util.stream.Collectors;

public class LeaveAssembler {

    public static LeaveDTO toDTO(LeaveRecord leaveRecord){
        LeaveDTO dto = new LeaveDTO();
        dto.setLeaveId(leaveRecord.getId());
        dto.setLeaveType(leaveRecord.getType().toString());
        dto.setStatus(leaveRecord.getStatus().toString());
        dto.setStartTime(DateUtil.formatDateTime(leaveRecord.getStartTime()));
        dto.setEndTime(DateUtil.formatDateTime(leaveRecord.getEndTime()));
        dto.setCurrentApprovalInfoDTO(ApprovalInfoAssembler.toDTO(leaveRecord.getCurrentApprovalInfo()));
        List<ApprovalInfoDTO> historyApprovalInfoDTOList = leaveRecord.getHistoryApprovalInfos()
                .stream()
                .map(historyApprovalInfo -> ApprovalInfoAssembler.toDTO(leaveRecord.getCurrentApprovalInfo()))
                .collect(Collectors.toList());
        dto.setHistoryApprovalInfoDTOList(historyApprovalInfoDTOList);
        dto.setDuration(leaveRecord.getDuration());
        return dto;
    }

    public static LeaveRecord toDO(LeaveDTO dto){
        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setId(dto.getLeaveId());
        leaveRecord.setApplicant(ApplicantAssembler.toDO(dto.getApplicantDTO()));
        leaveRecord.setApprover(ApproverAssembler.toDO(dto.getApproverDTO()));
        leaveRecord.setCurrentApprovalInfo(ApprovalInfoAssembler.toDO(dto.getCurrentApprovalInfoDTO()));
        List<ApprovalInfo> historyApprovalInfoDTOList = dto.getHistoryApprovalInfoDTOList()
                .stream()
                .map(historyApprovalInfoDTO -> ApprovalInfoAssembler.toDO(historyApprovalInfoDTO))
                .collect(Collectors.toList());
        leaveRecord.setHistoryApprovalInfos(historyApprovalInfoDTOList);
        return leaveRecord;
    }

}
