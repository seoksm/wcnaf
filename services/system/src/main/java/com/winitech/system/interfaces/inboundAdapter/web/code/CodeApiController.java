package com.winitech.system.interfaces.inboundAdapter.web.code;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.application.code.CodeFacade;
import com.winitech.system.domain.code.CodeCommand;
import com.winitech.system.domain.code.CodeInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class CodeApiController implements CodeApi {

    private final CodeFacade codeFacade;

    @Override
    public CommonResponse<CodeIdResponseDto> modifyCode(UUID codeId, CodeModifyRequestDto codeModifyRequestDto) {
        CodeCommand.UpdateCommand updateCommand = CodeDtoMapper.INSTANCE.toModifyRequestCommand(codeModifyRequestDto);

        updateCommand.setCodeId(codeId);

        codeFacade.reviseCode(updateCommand);
        return CommonResponse.success(CodeIdResponseDto.builder().codeId(codeId).build());
    }

    @Override
    public CommonResponse<CodeIdResponseDto> registerCode(CodeRegisterRequestDto codeRegisterRequestDto) {
        CodeCommand registerCommand = CodeDtoMapper.INSTANCE.toRegisterRequestCommand(codeRegisterRequestDto);

        UUID codeId = codeFacade.postCode(registerCommand);
        return CommonResponse.success(CodeIdResponseDto.builder().codeId(codeId).build());
    }

    @Override
    public CommonResponse<String> removeCode(UUID codeId) {
        codeFacade.removeCode(codeId);

        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<CodeResponseDto>> searchAllCode(UUID upperCodeId, String keyword) {
        List<CodeInfo> codeList = codeFacade.getCodeList(upperCodeId, keyword);

        List<CodeResponseDto> codeListRes = codeList.stream().map(CodeDtoMapper.INSTANCE::toCodeResponseDto).collect(Collectors.toList());

        return CommonResponse.success(codeListRes);
    }

    @Override
    public CommonResponse<CodeResponseDto> searchCode(UUID codeId) {
        CodeInfo code = codeFacade.getCode(codeId);

        CodeResponseDto res = CodeDtoMapper.INSTANCE.toCodeResponseDto(code);

        return CommonResponse.success(res);
    }
}
