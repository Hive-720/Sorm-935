package com.kjsoft.sorm.service.whmcsdata.datasyncservice.email;

import com.kjsoft.sorm.service.whmcsdata.dataservice.email.model.Email;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class EmailSyncPage {

    public static final EmailSyncPage EMPTY = EmailSyncPage.builder()
            .emails(List.of())
            .hasNextPage(false)
            .cursor(null)
            .build();

    private List<Email> emails;
    private boolean hasNextPage;
    private Long cursor;
}