package com.winitech.smartAsset.interfaces.inboundAdapter.web.loan;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.loan.LoanFacade;
import com.winitech.smartAsset.domain.loan.Loan;
import com.winitech.smartAsset.domain.loan.LoanInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class LoanApiController implements LoanApi {

    private final LoanFacade loanFacade;

    @Override
    public CommonResponse<LoanPageResponseDto> searchAllLoan(String status, Integer page, Integer size) {
        Loan.Status statusFilter = status == null ? null : Loan.Status.valueOf(status);
        Page<LoanInfo> loanPage = loanFacade.getLoanList(statusFilter, page, size);

        List<LoanResponseDto> content = loanPage.getContent().stream()
                .map(LoanDtoMapper.INSTANCE::toLoanResponseDto)
                .collect(Collectors.toList());

        LoanPageResponseDto res = LoanPageResponseDto.builder()
                .content(content)
                .currentPage(loanPage.getNumber())
                .pageSize(loanPage.getSize())
                .totalElements(loanPage.getTotalElements())
                .totalPages(loanPage.getTotalPages())
                .build();

        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<List<LoanResponseDto>> searchAllPendingLoan() {
        List<LoanResponseDto> res = loanFacade.getPendingApprovalList().stream()
                .map(LoanDtoMapper.INSTANCE::toLoanResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<List<LoanAvailabilityResponseDto>> searchAvailableLoanAsset() {
        List<LoanAvailabilityResponseDto> res = loanFacade.getAvailableAssets().stream()
                .map(LoanDtoMapper.INSTANCE::toLoanAvailabilityResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<List<LoanResponseDto>> searchMyLoan() {
        List<LoanResponseDto> res = loanFacade.getMyLoans().stream()
                .map(LoanDtoMapper.INSTANCE::toLoanResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<LoanIdResponseDto> registerLoanByAdmin(LoanRegisterByAdminRequestDto dto) {
        java.util.UUID loanId = loanFacade.borrowByAdmin(dto.getTangibleAssetId(), dto.getMemberId());
        return CommonResponse.success(LoanIdResponseDto.builder().loanId(loanId).build());
    }

    @Override
    public CommonResponse<LoanIdResponseDto> registerLoanBySelf(LoanRegisterBySelfRequestDto dto) {
        java.util.UUID loanId = loanFacade.borrowBySelf(dto.getTangibleAssetId());
        return CommonResponse.success(LoanIdResponseDto.builder().loanId(loanId).build());
    }

    @Override
    public CommonResponse<String> approveLoan(java.util.UUID loanId) {
        loanFacade.approveLoan(loanId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> rejectLoan(java.util.UUID loanId, LoanRejectRequestDto dto) {
        loanFacade.rejectLoan(loanId, dto.getReason());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> returnLoanBySelf(java.util.UUID loanId, LoanReturnRequestDto dto) {
        boolean abnormal = dto != null && Boolean.TRUE.equals(dto.getAbnormal());
        loanFacade.returnLoanBySelf(loanId, abnormal);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> extendLoanBySelf(java.util.UUID loanId) {
        loanFacade.extendLoanBySelf(loanId);
        return CommonResponse.success("OK");
    }
}
