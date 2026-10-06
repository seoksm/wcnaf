package com.winitech.system.domain.code;

import java.util.List;
import java.util.UUID;

public interface CodeService {

    UUID createCode(CodeCommand codeCommand);

    void updateCode(CodeCommand.UpdateCommand updateCommand);

    void deleteCode(UUID codeId);

    List<CodeInfo> loadCodeList(UUID upperCodeId, String keyword);

    CodeInfo loadCode(UUID codeId);
}
