package com.winitech.smartAsset.domain.software;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SoftwareServiceImpl extends EgovAbstractServiceImpl implements SoftwareService {

    private final SoftwareReader softwareReader;
    private final SoftwareStore softwareStore;

    @Transactional
    @Override
    public UUID createSoftware(SoftwareCommand command) {
        return softwareStore.store(command.toEntity());
    }

    @Transactional
    @Override
    public void updateSoftware(SoftwareCommand.UpdateCommand updateCommand) {
        Software software = softwareReader.findById(updateCommand.getSoftwareId());
        softwareStore.modify(software, updateCommand);
    }

    @Transactional
    @Override
    public void deleteSoftware(UUID softwareId) {
        Software software = softwareReader.findById(softwareId);
        softwareStore.delete(software);
    }

    @Override
    public List<SoftwareInfo> loadSoftwareList(String keyword) {
        return softwareReader.findAllByContainsKeyword(keyword).stream().map(SoftwareInfo::new).collect(Collectors.toList());
    }

    @Override
    public SoftwareInfo loadSoftware(UUID softwareId) {
        return new SoftwareInfo(softwareReader.findById(softwareId));
    }

    @Transactional
    @Override
    public Software findOrCreateByName(String name) {
        return softwareReader.findByName(name)
                .orElseGet(() -> {
                    Software software = Software.builder().name(name).build();
                    UUID id = softwareStore.store(software);
                    return softwareReader.findById(id);
                });
    }
}
