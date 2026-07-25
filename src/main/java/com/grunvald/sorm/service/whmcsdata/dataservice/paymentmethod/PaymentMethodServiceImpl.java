package com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod;

import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import com.grunvald.sorm.util.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;


@Service
@AllArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository repository;
    private final DateUtils date;


    @Override
    public Mono<PaymentMethodSearchResult> search(PaymentMethodFilter filter, String cursor,
                                                  Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAll(@NonNull List<PaymentMethod> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        return repository.findAll()
                .collectMap(PaymentMethod::getModule)
                .flatMap(existingMap -> {

                    var toAdd = new ArrayList<PaymentMethod>();
                    var toUpdate = new ArrayList<PaymentMethod>();

                    for (var actual : incomingList) {
                        var local = existingMap.remove(actual.getModule());

                        if (local == null) {
                            actual.setUpdatedAt(date.now());
                            toAdd.add(actual);
                        } else {
                            if (local.update(actual)) {
                                local.setUpdatedAt(date.now());
                                toUpdate.add(local);
                            }
                        }
                    }

                    if (!existingMap.isEmpty()) {
                        existingMap.values()
                                .forEach(paymentMethod -> {
                                    paymentMethod.setDeleted(true);
                                    toUpdate.add(paymentMethod);
                                });
                    }

                    return (toAdd.isEmpty() ? Mono.just(true) : repository.addAll(toAdd))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAll(toUpdate))
                            .then(Mono.just(true));
                });
    }
}