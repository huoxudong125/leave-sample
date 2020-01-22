package ddd.leave.interfaces.facade;

import ddd.leave.application.service.LeaveApplicationService;
import ddd.leave.domain.leave.entity.LeaveRecord;
import ddd.leave.infrastructure.common.api.Response;
import ddd.leave.interfaces.assembler.LeaveAssembler;
import ddd.leave.interfaces.dto.LeaveDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/leave")
@Slf4j
public class LeaveApi {

    @Autowired
    LeaveApplicationService leaveApplicationService;

    @PostMapping
    public Response createLeaveInfo(LeaveDTO leaveDTO){
        LeaveRecord leaveRecord = LeaveAssembler.toDO(leaveDTO);
        leaveApplicationService.createLeaveInfo(leaveRecord);
        return Response.ok();
    }

    @PutMapping
    public Response updateLeaveInfo(LeaveDTO leaveDTO){
        LeaveRecord leaveRecord = LeaveAssembler.toDO(leaveDTO);
        leaveApplicationService.updateLeaveInfo(leaveRecord);
        return Response.ok();
    }

    @PostMapping("/submit")
    public Response submitApproval(LeaveDTO leaveDTO){
        LeaveRecord leaveRecord = LeaveAssembler.toDO(leaveDTO);
        leaveApplicationService.submitApproval(leaveRecord);
        return Response.ok();
    }

    @PostMapping("/{leaveId}")
    public Response findById(@PathVariable String leaveId){
        LeaveRecord leaveRecord = leaveApplicationService.getLeaveInfo(leaveId);
        return Response.ok(LeaveAssembler.toDTO(leaveRecord));
    }

    /**
     * 根据申请人查询所有请假单
     * @param applicantId
     * @return
     */
    @PostMapping("/query/applicant/{applicantId}")
    public Response queryByApplicant(@PathVariable String applicantId){
        List<LeaveRecord> leaveRecordList = leaveApplicationService.queryLeaveInfosByApplicant(applicantId);
        List<LeaveDTO> leaveDTOList = leaveRecordList.stream().map(leave -> LeaveAssembler.toDTO(leave)).collect(Collectors.toList());
        return Response.ok(leaveDTOList);
    }

    /**
     * 根据审批人id查询待审批请假单（待办任务）
     * @param approverId
     * @return
     */
    @PostMapping("/query/approver/{approverId}")
    public Response queryByApprover(@PathVariable String approverId){
        List<LeaveRecord> leaveRecordList = leaveApplicationService.queryLeaveInfosByApprover(approverId);
        List<LeaveDTO> leaveDTOList = leaveRecordList.stream().map(leave -> LeaveAssembler.toDTO(leave)).collect(Collectors.toList());
        return Response.ok(leaveDTOList);
    }
}
