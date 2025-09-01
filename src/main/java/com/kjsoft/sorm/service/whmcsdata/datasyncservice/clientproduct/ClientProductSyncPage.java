package com.kjsoft.sorm.service.whmcsdata.datasyncservice.clientproduct;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ClientProductSyncPage {
    private List<ClientProduct> elements;
    private boolean hasNextPage;
    private Long nextOffset;
}