package com.winitech.system.application.code;

import com.winitech.system.domain.code.CodeCommand;
import com.winitech.system.domain.code.CodeInfo;
import com.winitech.system.domain.code.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeFacade {

    private final CodeService codeService;

    public UUID postCode(CodeCommand codeCommand) {
        return codeService.createCode(codeCommand);
    }

    public void reviseCode(CodeCommand.UpdateCommand updateCommand) {
        codeService.updateCode(updateCommand);
    }

    public void removeCode(UUID codeId) {
        codeService.deleteCode(codeId);
    }

    public List<CodeInfo> getCodeList(UUID upperCodeId, String keyword) {
        return codeService.loadCodeList(upperCodeId, keyword);
    }

    public CodeInfo getCode(UUID codeId) {
        return codeService.loadCode(codeId);
    }
}
