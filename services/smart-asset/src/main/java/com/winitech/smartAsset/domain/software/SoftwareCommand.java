package com.winitech.smartAsset.domain.software;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class SoftwareCommand {

    private String name;
    private String publisher;
    private String category;
    private String memo;

    public Software toEntity() {
        return Software.builder()
                .name(name)
                .publisher(publisher)
                .category(category)
                .memo(memo)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID softwareId;
        private String name;
        private String publisher;
        private String category;
        private String memo;
    }
}
