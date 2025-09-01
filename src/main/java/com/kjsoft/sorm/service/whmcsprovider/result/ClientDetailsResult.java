package com.kjsoft.sorm.service.whmcsprovider.result;

import com.kjsoft.sorm.service.whmcsprovider.dto.ClientProviderDto;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ClientDetailsResult {
   private ClientProviderDto element;
   private boolean deleted;
}