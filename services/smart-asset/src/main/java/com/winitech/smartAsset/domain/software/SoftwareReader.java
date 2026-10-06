package com.winitech.smartAsset.domain.software;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SoftwareReader {

    Software findById(UUID softwareId);

    Optional<Software> findByName(String name);

    List<Software> findAllByContainsKeyword(String keyword);
}
