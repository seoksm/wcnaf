package com.winitech.system.domain.code;

import java.util.UUID;

public interface CodeStore {

    UUID store(Code code);

    void modify(Code code, CodeCommand.UpdateCommand updateCommand);

    void delete(UUID codeId);
}
