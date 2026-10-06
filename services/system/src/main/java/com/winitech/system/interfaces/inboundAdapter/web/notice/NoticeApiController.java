package com.winitech.system.interfaces.inboundAdapter.web.notice;

import com.winitech.common.application.commonFile.CommonFileFacade;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.interfaces.inboundAdapter.web.CommonFileDto;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.response.CommonResponseMetadata;
import com.winitech.system.application.notice.NoticeFacade;
import com.winitech.system.domain.notice.Notice;
import com.winitech.system.domain.notice.NoticeCommand;
import com.winitech.system.domain.notice.NoticeInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(tags = "공지 서비스 API")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/system/notice")
public class NoticeApiController {

    private final NoticeFacade noticeFacade;
    private final CommonFileFacade commonFileFacade;
    private final LoginUserContext loginUserContext;

    private UUID getCurrentUserId() {
        return loginUserContext.getUserId();
    }

    @PostMapping
    public CommonResponse registerNotice(@RequestBody @Valid NoticeDto.RegisterNoticeRequest request) {
        NoticeCommand noticeCommand = request.toCommand(getCurrentUserId());
        UUID noticeId = noticeFacade.postNotice(noticeCommand);
        var response = new NoticeDto.NoticeStoreResponse(noticeId);

        List<String> fileIds = request.getFileIds();
        commonFileFacade.applyFileIdList(Notice.class, noticeId, fileIds);

        return CommonResponse.success(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "noticeId", value = "공지 ID", required = true, dataType = "UUID", paramType = "path"),
    })
    @GetMapping("/{noticeId}")
    public CommonResponse searchNotice(@Valid @PathVariable UUID noticeId) {
        NoticeInfo noticeInfo = noticeFacade.getNotice(noticeId, getCurrentUserId());

        List<CommonFileDto.FileInfoResponse> fileList = commonFileFacade.searchCommonFileListByEntity(Notice.class, noticeId)
                .stream()
                .map(CommonFileDto.FileInfoResponse::new)
                .collect(Collectors.toList());

        var response = new NoticeDto.NoticeResponse(noticeInfo, fileList);
        return CommonResponse.success(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "제목으로 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "내용으로 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "author", value = "작성자로 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "페이지 번호", required = true, dataType = "Integer", paramType = "query", example = "0"),
            @ApiImplicitParam(name = "pageSize", value = "요소 개수", required = true, dataType = "Integer", paramType = "query", example = "0")
    })
    @GetMapping
    public CommonResponse searchAllNotice(
            @Valid @RequestParam int page, @Valid @RequestParam int pageSize,
            @RequestParam(required = false) String title, @RequestParam(required = false) String content, @RequestParam(required = false) String author
    ) {
        Page<Notice> noticeList = noticeFacade.getNoticeList(page, pageSize, title, content, author);

        // make CommonResponseMetadata
        CommonResponseMetadata metadata = CommonResponseMetadata.ofPaging(noticeList.getNumber(), noticeList.getTotalElements(), noticeList.getSize());

        List<NoticeDto.NoticeListResponse> response = noticeList.getContent().stream()
                .map(notice -> new NoticeDto.NoticeListResponse(notice, getFileList(notice.getId()))).collect(Collectors.toList());
        return CommonResponse.success(response, metadata);
    }

    private List<CommonFileDto.FileInfoResponse> getFileList(UUID noticeId) {
        return commonFileFacade.searchCommonFileListByEntity(Notice.class, noticeId)
                .stream()
                .map(CommonFileDto.FileInfoResponse::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "{noticeId}", method = RequestMethod.PATCH)
    public CommonResponse modifyNotice(@Valid @PathVariable UUID noticeId, @RequestBody NoticeDto.ModifyNoticeRequest request) {
        NoticeCommand.UpdateCommand noticeCommand = request.toCommand(noticeId, getCurrentUserId());
        noticeFacade.reviseNotice(noticeCommand);

        List<String> fileIds = request.getFileIds();
        commonFileFacade.applyFileIdList(Notice.class, noticeId, fileIds);

        var response = new NoticeDto.NoticeStoreResponse(noticeId);
        return CommonResponse.success(response);
    }

    @DeleteMapping("/{noticeId}")
    public CommonResponse removeNotice(@Valid @PathVariable UUID noticeId) {
        noticeFacade.removeNotice(noticeId);
        return CommonResponse.success("OK");
    }

    @PostMapping("/{noticeId}")
    public CommonResponse checkNoticePw(@Valid @PathVariable UUID noticeId, @RequestBody NoticeDto.CheckNoticeRequest request) {
        noticeFacade.checkNoticePw(noticeId, request.getPw());
        return CommonResponse.success("OK");
    }
}
