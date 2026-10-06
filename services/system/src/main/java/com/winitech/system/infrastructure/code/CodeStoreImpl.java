package com.winitech.system.infrastructure.code;

import com.winitech.system.domain.code.Code;
import com.winitech.system.domain.code.CodeCommand;
import com.winitech.system.domain.code.CodeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CodeStoreImpl implements CodeStore {

    private final CodeRepository codeRepository;

    @Override
    public UUID store(Code code) {
        return codeRepository.save(code).getId();
    }

    @Override
    public void modify(Code code, CodeCommand.UpdateCommand updateCommand) {
        code.setName(updateCommand.getCodeName());
        code.setDescription(updateCommand.getCodeDescription());
        code.setOrderNo(updateCommand.getCodeOrderNo());
        code.setUseStatus(updateCommand.getCodeUseStatus());
    }

    @Override
    public void delete(UUID codeId) {
        codeRepository.softDelete(codeId);
    }
}
