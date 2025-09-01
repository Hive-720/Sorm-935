package com.kjsoft.sorm.service.whmcsdata.datasyncservice.email;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.kjsoft.sorm.service.whmcsdata.dataservice.email.EmailService;
import com.kjsoft.sorm.service.whmcsdata.dataservice.email.model.Email;
import com.kjsoft.sorm.service.whmcsprovider.WhmcsProvider;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.EmailMapper;
import com.kjsoft.sorm.service.whmcsprovider.result.EmailsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class EmailSyncServiceImpl implements EmailSyncService {
    private final WhmcsProvider whmcs;
    private final ClientService clientService;
    private final EmailService emailService;
    private final EmailMapper emailMapper;


    @Override
    public Mono<EmailSyncPage> getActualData(Long clintCursorById, int limit, long applyDataLimit,
                                             OffsetDateTime historyDepthDate, List<String> maskedPatterns) {
        return clientService.getAllClient(clintCursorById, limit)
                .filter(idsResult -> !idsResult.clientIds().isEmpty())
                .flatMap(idsResult -> emailService.getLatestAllByClientId(new HashSet<>(idsResult.clientIds()))
                        .flatMapMany(Flux::fromIterable)
                        .collectMap(Email::getClientId, Email::getDate)
                        .flatMapMany(emailCursors -> Flux.fromIterable(idsResult.clientIds())
                                .flatMap(clientId -> {
                                    var minDate = getMinDate(emailCursors.get(clientId), historyDepthDate);
                                    return fetchEmailsForClient(clientId, limit, minDate, maskedPatterns)
                                            .take(applyDataLimit + 1);
                                })
                        )
                        .collectList()
                        .map(list -> {
                                    var hasMoreByLimit = list.size() > applyDataLimit;
                                    var newCursor = getNewCursor(hasMoreByLimit, list, applyDataLimit,
                                            idsResult.clientIds(), clintCursorById);

                                    return EmailSyncPage.builder()
                                            .emails(list)
                                            .hasNextPage(hasMoreByLimit || idsResult.hasNextPage())
                                            .cursor(newCursor)
                                            .build();
                                }
                        )
                )
                .switchIfEmpty(Mono.just(EmailSyncPage.EMPTY));
    }

    private Long getNewCursor(boolean hasMoreByLimit, List<Email> list, long applyDataLimit,
                              List<Long> clientsIds, Long initialCursor) {
        var finalCredits = hasMoreByLimit ? list.subList(0, (int) applyDataLimit) : list;
        if (!finalCredits.isEmpty() && hasMoreByLimit) {
            var lastIncludedClientId = finalCredits.getLast().getClientId();
            var lastIdx = clientsIds.indexOf(lastIncludedClientId);
            return lastIdx > 0 ? clientsIds.get(lastIdx - 1) : initialCursor;
        }

        return clientsIds.getLast();
    }

    private Flux<Email> fetchEmailsForClient(long clientId, int limit, OffsetDateTime historyDepthDate,
                                             List<String> maskedPatterns) {
        return whmcs.getEmails(clientId, 0L, limit, historyDepthDate)
                .expand(previousResult -> {
                    if (!previousResult.isHasNextPage()) {
                        return Mono.empty();
                    }

                    return whmcs.getEmails(clientId, previousResult.getNextOffset(), limit, historyDepthDate);
                })
                .flatMapIterable(EmailsResult::getElements)
                .map(emailDto -> {
                    var maskedMessageBody = masked(maskedPatterns, emailDto.getMessageBody());
                    return emailMapper.map(emailDto, maskedMessageBody, clientId);
                });
    }

    private String masked(List<String> maskedPatterns, String messageBody) {
        if (messageBody == null || maskedPatterns == null || maskedPatterns.isEmpty()) {
            return messageBody;
        }

        var maskedBody = messageBody;

        for (var patternString : maskedPatterns) {
            var pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            var matcher = pattern.matcher(maskedBody);
            var sb = new StringBuilder();

            while (matcher.find()) {
                var match = matcher.group(0);
                var replacement = "*".repeat(match.length());
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }

            matcher.appendTail(sb);
            maskedBody = sb.toString();
        }

        return maskedBody;
    }

    private OffsetDateTime getMinDate(OffsetDateTime cursorDate, OffsetDateTime historyDepthDate) {
        if (cursorDate == null) {
            return historyDepthDate;
        }

        return historyDepthDate.isAfter(cursorDate) ? historyDepthDate : cursorDate;
    }

    @Override
    public Mono<Boolean> applyActualData(EmailSyncPage page) {
        return emailService.saveAll(page.getEmails());
    }
}