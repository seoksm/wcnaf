package com.winitech.smartAsset.domain.software;

import java.util.UUID;

public interface SoftwareStore {

    UUID store(Software software);

    void modify(Software software, SoftwareCommand.UpdateCommand updateCommand);

    void delete(Software software);
}
