package com.winitech.system.domain.code;

import java.util.List;
import java.util.UUID;

public interface CodeReader {

    Code findById(UUID codeId);

    List<Code> findAllByParentIdAndContainsKeyword(UUID parentCodeId, String keyword);

    List<Code> findLevel1Code(String keyword);
}
