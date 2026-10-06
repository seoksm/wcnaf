package com.winitech.smartAsset.infrastructure.software;

import com.winitech.smartAsset.domain.software.Software;
import com.winitech.smartAsset.domain.software.SoftwareReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SoftwareReaderImpl implements SoftwareReader {

    private final SoftwareRepository softwareRepository;

    @Override
    public Software findById(UUID softwareId) {
        return softwareRepository.findById(softwareId).orElseThrow();
    }

    @Override
    public Optional<Software> findByName(String name) {
        return softwareRepository.findByNameAndStatus(name, Software.Status.ENABLE);
    }

    @Override
    public List<Software> findAllByContainsKeyword(String keyword) {
        return softwareRepository.findAllByContainsKeyword(keyword);
    }
}
