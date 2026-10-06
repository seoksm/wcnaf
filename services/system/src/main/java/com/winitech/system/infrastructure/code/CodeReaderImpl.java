package com.winitech.system.infrastructure.code;

import com.winitech.system.domain.code.Code;
import com.winitech.system.domain.code.CodeReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CodeReaderImpl implements CodeReader {

    private final CodeRepository codeRepository;

    @Override
    public Code findById(UUID codeId) {
        return codeRepository.findById(codeId).orElseThrow();
    }

    @Override
    public List<Code> findAllByParentIdAndContainsKeyword(UUID parentCodeId, String keyword) {
        return codeRepository.findAllByParentIdAndContainsKeyword(parentCodeId, keyword);
    }

    @Override
    public List<Code> findLevel1Code(String keyword) {
        return codeRepository.findLevel1Code(keyword);
    }
}
