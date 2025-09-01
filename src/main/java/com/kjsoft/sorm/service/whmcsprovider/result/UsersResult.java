package com.kjsoft.sorm.service.whmcsprovider.result;

import com.kjsoft.sorm.service.whmcsprovider.dto.UserProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class UsersResult {
    private List<UserProviderDto> elements;
    private boolean hashNextPage;
    private Long nextOffset;
}