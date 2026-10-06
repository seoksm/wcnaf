package com.winitech.smartAsset.domain.software;

import java.util.List;
import java.util.UUID;

public interface SoftwareService {

    UUID createSoftware(SoftwareCommand command);

    void updateSoftware(SoftwareCommand.UpdateCommand updateCommand);

    void deleteSoftware(UUID softwareId);

    List<SoftwareInfo> loadSoftwareList(String keyword);

    SoftwareInfo loadSoftware(UUID softwareId);

    /** R2 자동완성 - 이름이 이미 있으면 그 소프트웨어를, 없으면 새로 만들어 반환한다 */
    Software findOrCreateByName(String name);
}
