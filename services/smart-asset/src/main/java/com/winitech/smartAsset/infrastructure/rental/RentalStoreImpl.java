package com.winitech.smartAsset.infrastructure.rental;

import com.winitech.smartAsset.domain.rental.RentalAsset;
import com.winitech.smartAsset.domain.rental.RentalCommand;
import com.winitech.smartAsset.domain.rental.RentalStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RentalStoreImpl implements RentalStore {

    private final RentalRepository rentalRepository;

    @Override
    public UUID store(RentalAsset rentalAsset) {
        return rentalRepository.save(rentalAsset).getId();
    }

    @Override
    public void modify(RentalAsset rentalAsset, RentalCommand.UpdateCommand updateCommand) {
        rentalAsset.modify(updateCommand);
    }

    @Override
    public void cancel(RentalAsset rentalAsset) {
        rentalAsset.cancel();
    }
}
