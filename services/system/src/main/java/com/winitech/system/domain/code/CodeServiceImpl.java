package com.winitech.system.domain.code;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CodeServiceImpl extends EgovAbstractServiceImpl implements CodeService {

    private final CodeReader codeReader;
    private final CodeStore codeStore;

    @Transactional
    @Override
    public UUID createCode(CodeCommand codeCommand) {

        Code code = codeCommand.toEntity(
                codeCommand.getUpperCodeId() == null ? null : codeReader.findById(codeCommand.getUpperCodeId())
        );

        return codeStore.store(code);
    }

    @Transactional
    @Override
    public void updateCode(CodeCommand.UpdateCommand updateCommand) {

        Code code = codeReader.findById(updateCommand.getCodeId());
        codeStore.modify(code, updateCommand);
    }

    @Transactional
    @Override
    public void deleteCode(UUID codeId) {
        codeStore.delete(codeId);
    }

    @Override
    public List<CodeInfo> loadCodeList(UUID upperCodeId, String keyword) {

        List<Code> codeList;
        if (upperCodeId == null) {
            codeList = codeReader.findLevel1Code(keyword);
        } else {
            codeList = codeReader.findAllByParentIdAndContainsKeyword(upperCodeId, keyword);
        }

        return codeList.stream().map(CodeInfo::new).collect(Collectors.toList());
    }

    @Override
    public CodeInfo loadCode(UUID codeId) {

        Code code = codeReader.findById(codeId);
        return new CodeInfo(code);
    }
}
