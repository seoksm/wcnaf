package com.winitech.smartAsset.domain.rental;

import java.util.UUID;

public interface RentalStore {

    UUID store(RentalAsset rentalAsset);

    void modify(RentalAsset rentalAsset, RentalCommand.UpdateCommand updateCommand);

    void cancel(RentalAsset rentalAsset);
}
