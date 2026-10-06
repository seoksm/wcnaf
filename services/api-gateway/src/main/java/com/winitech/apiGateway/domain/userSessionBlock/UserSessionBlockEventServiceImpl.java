package com.winitech.apiGateway.domain.userSessionBlock;

import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.userSessionBlock.UserSessionBlockEventRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSessionBlockEventServiceImpl implements UserSessionBlockEventService {
    private final UserSessionBlockService userSessionBlockService;

    @Override
    public void process(UserSessionBlockEventRequestDto requestDto, String topic) {
        userSessionBlockService.registerUserSessionBlock(
                UserSessionBlockCommand.RegisterRequestCommand.builder()
                        .userSessionId(requestDto.getUserSessionId())
                        .accessTokenExpiresAt(requestDto.getAccessTokenExpiresAt())
                        .userId(requestDto.getUserId())
                        .build());
    }
}
