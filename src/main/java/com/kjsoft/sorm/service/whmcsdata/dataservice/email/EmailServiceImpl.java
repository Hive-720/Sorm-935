package com.kjsoft.sorm.service.whmcsdata.dataservice.email;

import com.kjsoft.sorm.service.whmcsdata.filter.EmailFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import com.kjsoft.sorm.service.whmcsdata.dataservice.email.model.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final EmailRepository repository;


    @Override
    public Mono<EmailSearchResult> search(EmailFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }

    @Override
    public Mono<List<Email>> getLatestAllByClientId(@NonNull Set<Long> clientIds) {
        if (clientIds.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.getLatestAllByClientId(clientIds)
                .collectList();
    }

    @Override
    public Mono<Boolean> saveAll(@NonNull List<Email> emails) {
        if (emails.isEmpty()) {
            return Mono.just(true);
        }

        return repository.addAll(emails);
    }
}