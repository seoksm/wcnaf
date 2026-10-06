package com.winitech.system.interfaces.inboundAdapter.web.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.winitech.common.interfaces.inboundAdapter.web.CommonFileDto;
import com.winitech.system.domain.notice.Notice;
import com.winitech.system.domain.notice.NoticeCommand;
import com.winitech.system.domain.notice.NoticeInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class NoticeDto {

    @Getter
    @Builder
    @ToString
    public static class RegisterNoticeRequest {
        @NotNull(message = "제목은 필수 값입니다.")
        @ApiModelProperty(value = "제목", example = "설연휴 공지", required = true)
        private String title;
        @NotNull(message = "내용은 필수 값입니다.")
        @ApiModelProperty(value = "내용", example = "이번 설은 정상영업합니다.", required = true)
        private String content;
        @ApiModelProperty(value = "사용여부", example = "USED", required = true)
        private Notice.UseStatus useStatus;
        @ApiModelProperty(value = "공지여부", example = "NOTICE", required = true)
        private Notice.NoticeStatus noticeStatus;
        @ApiModelProperty(value = "비밀글여부", example = "PUBLIC", required = true)
        private Notice.VisibilityStatus visibilityStatus;
        @ApiModelProperty(value = "공지시작일자", example = "2025-02-01")
        private Date startDate;
        @ApiModelProperty(value = "공지종료일자", example = "2025-02-15")
        private Date endDate;
        @ApiModelProperty(value = "비밀번호", example = "\"1234\"", dataType = "string")
        private String pw;
        @ApiModelProperty(value = "첨부파일", example = "[]")
        private List<String> fileIds;

        public NoticeCommand toCommand(UUID creatorId) {
            return NoticeCommand.builder()
                    .title(title)
                    .content(content)
                    .useStatus(useStatus)
                    .noticeStatus(noticeStatus)
                    .visibilityStatus(visibilityStatus)
                    .startDate(startDate == null ? null : startDate.toInstant().atOffset(ZoneOffset.ofHours(9)))
                    .endDate(endDate == null ? null : endDate.toInstant().atOffset(ZoneOffset.ofHours(9)))
                    .pw(pw)
                    .creatorId(creatorId)
                    .build();
        }
    }

    @Getter
    @ToString
    public static class NoticeStoreResponse {
        @ApiModelProperty(value = "공지 ID", example = "01948bf3-a345-7ff6-a9e9-9496d51b7aae")
        private final String noticeId;

        public NoticeStoreResponse (UUID noticeId) {
            this.noticeId = noticeId.toString();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class ModifyNoticeRequest {
        @NotNull(message = "제목은 필수 값입니다.")
        @ApiModelProperty(value = "제목", example = "설연휴 공지", required = true)
        private String title;
        @NotNull(message = "내용은 필수 값입니다.")
        @ApiModelProperty(value = "내용", example = "이번 설은 정상영업합니다.", required = true)
        private String content;
        @ApiModelProperty(value = "사용여부", example = "USED", required = true)
        private Notice.UseStatus useStatus;
        @ApiModelProperty(value = "공지여부", example = "NOTICE", required = true)
        private Notice.NoticeStatus noticeStatus;
        @ApiModelProperty(value = "비밀글여부", example = "PUBLIC", required = true)
        private Notice.VisibilityStatus visibilityStatus;
        @ApiModelProperty(value = "공지시작일자", example = "2025-02-01")
        private Date startDate;
        @ApiModelProperty(value = "공지종료일자", example = "2025-02-15")
        private Date endDate;
        @ApiModelProperty(value = "비밀번호", example = "\"1234\"", dataType = "string")
        private String pw;
        @ApiModelProperty(value = "첨부파일", example = "[]")
        private List<String> fileIds;

        public NoticeCommand.UpdateCommand toCommand(UUID noticeId, UUID updaterId) {
            return NoticeCommand.UpdateCommand.builder()
                    .noticeId(noticeId)
                    .title(title)
                    .content(content)
                    .useStatus(useStatus)
                    .noticeStatus(noticeStatus)
                    .visibilityStatus(visibilityStatus)
                    .startDate(startDate == null ? null : startDate.toInstant().atOffset(ZoneOffset.ofHours(9)))
                    .endDate(endDate == null ? null : endDate.toInstant().atOffset(ZoneOffset.ofHours(9)))
                    .pw(pw)
                    .updaterId(updaterId)
                    .build();
        }
    }

    @Getter
    @ToString
    public static class CheckNoticeRequest {
        @ApiModelProperty(value = "비밀번호", example = "1234", dataType = "string")
        private String pw;
    }

    @Getter
    @ToString
    public static class NoticeResponse {
        @ApiModelProperty(value = "공지 ID", example = "01948bf3-a345-7ff6-a9e9-9496d51b7aae")
        private final String noticeId;
        @ApiModelProperty(value = "제목", example = "설연휴 공지", required = true)
        private final String title;
        @ApiModelProperty(value = "내용", example = "이번 설은 정상영업합니다.", required = true)
        private final String content;
        @ApiModelProperty(value = "사용여부", example = "USED", required = true)
        private final Notice.UseStatus useStatus;
        @ApiModelProperty(value = "공지여부", example = "NOTICE", required = true)
        private final Notice.NoticeStatus noticeStatus;
        @ApiModelProperty(value = "비밀글여부", example = "PUBLIC", required = true)
        private final Notice.VisibilityStatus visibilityStatus;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @ApiModelProperty(value = "공지시작일자", example = "2025-02-01")
        private final Date startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @ApiModelProperty(value = "공지종료일자", example = "2025-02-15")
        private final Date endDate;
        @ApiModelProperty(value = "비밀번호", example = "1234")
        private final String pw;
        @ApiModelProperty(value="조회수", example = "10", required = true)
        private final Integer viewCount;
        @ApiModelProperty(value="데이터 생성자", example = "01948bf3-a345-7ff6-a9e9-9496d51b7aae", required = true)
        private final String creatorName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @ApiModelProperty(value="데이터 생성 시각", example = "2021-11-17T13:46:40.108365", required = true)
        private final OffsetDateTime createAt;
        @ApiModelProperty(value="데이터 수정자", example = "01948bf3-a345-7ff6-a9e9-9496d51b7aae", required = true)
        private final String updaterName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @ApiModelProperty(value="데이터 수정 시각", example = "2021-11-17T13:46:40.108365", required = true)
        private final OffsetDateTime updateAt;
        @ApiModelProperty(value = "첨부파일")
        private final List<com.winitech.common.interfaces.inboundAdapter.web.CommonFileDto.FileInfoResponse> fileList;

        public NoticeResponse (NoticeInfo noticeInfo, List<CommonFileDto.FileInfoResponse> fileList) {
            this.noticeId = noticeInfo.getId();
            this.title = noticeInfo.getTitle();
            this.content = noticeInfo.getContent();
            this.useStatus = noticeInfo.getUseStatus();
            this.noticeStatus = noticeInfo.getNoticeStatus();
            this.visibilityStatus = noticeInfo.getVisibilityStatus();
            this.startDate = noticeInfo.getStartDate() == null ? null : noticeInfo.getStartDate();
            this.endDate = noticeInfo.getEndDate() == null ? null : noticeInfo.getEndDate();
            this.pw = noticeInfo.getPw();
            this.viewCount = noticeInfo.getViewCount();
            this.creatorName = noticeInfo.getCreator();
            this.createAt = noticeInfo.getCreateAt();
            this.updaterName = noticeInfo.getUpdater();
            this.updateAt = noticeInfo.getUpdateAt();
            this.fileList = fileList;
        }
    }

    @Getter
    @ToString
    public static class NoticeUserResponse {
        @ApiModelProperty(value = "공지 ID", example = "01948bf3-a345-7ff6-a9e9-9496d51b7aae")
        private final String noticeId;
        @ApiModelProperty(value = "제목", example = "설연휴 공지", required = true)
        private final String title;
        @ApiModelProperty(value = "내용", example = "이번 설은 정상영업합니다.", required = true)
        private final String content;
        @ApiModelProperty(value="데이터 생성자", example = "01948bf3-a345-7ff6-a9e9-9496d51b7aae", required = true)
        private final String creatorName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @ApiModelProperty(value="데이터 생성 시각", example = "2021-11-17T13:46:40.108365", required = true)
        private final OffsetDateTime createAt;
        @ApiModelProperty(value = "첨부파일")
        private final List<com.winitech.common.interfaces.inboundAdapter.web.CommonFileDto.FileInfoResponse> fileList;

        public NoticeUserResponse (NoticeInfo.forUserInfo noticeInfo, List<CommonFileDto.FileInfoResponse> fileList) {
            this.noticeId = noticeInfo.getId();
            this.title = noticeInfo.getTitle();
            this.content = noticeInfo.getContent();
            this.creatorName = noticeInfo.getCreator();
            this.createAt = noticeInfo.getCreateAt();
            this.fileList = fileList;
        }
    }

    @Getter
    @ToString
    public static class NoticeListResponse {
        @ApiModelProperty(value = "공지 ID", example = "01948bf3-a345-7ff6-a9e9-9496d51b7aae")
        private final String noticeId;
        @ApiModelProperty(value = "제목", example = "설연휴 공지", required = true)
        private final String title;
        @ApiModelProperty(value="데이터 생성자", example = "01948bf3-a345-7ff6-a9e9-9496d51b7aae", required = true)
        private final String creatorName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @ApiModelProperty(value="데이터 생성 시각", example = "2021-11-17T13:46:40.108365", required = true)
        private final OffsetDateTime createAt;
        @ApiModelProperty(value="조회수", example = "10", required = true)
        private final int viewCount;
        @ApiModelProperty(value = "첨부파일")
        private final List<com.winitech.common.interfaces.inboundAdapter.web.CommonFileDto.FileInfoResponse> fileList;

        public NoticeListResponse (Notice notice, List<CommonFileDto.FileInfoResponse> fileList) {
            this.noticeId = notice.getId().toString();
            this.title = notice.getTitle();
            this.creatorName = notice.getCreator().getFullName();
            this.createAt = notice.getCreateAt();
            this.viewCount = notice.getViewCount();
            this.fileList = fileList;
        }
    }
}
