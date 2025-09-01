import com.kjsoft.sorm.SormApplication;
import com.kjsoft.sorm.presentation.graphqlapi.operation.QueryResolver;
import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.AffiliateRepository;
import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.PaymentMethodRepository;
import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.kjsoft.sorm.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;


@SpringBootTest(classes = SormApplication.class)
public class SormApplicationTests {

    @Autowired
    AffiliateRepository affiliateRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    DateUtils date;

    @MockitoBean //TODO: это временно, мокируем проблемный компонент
    private QueryResolver queryResolver;
    @MockitoBean
    private GraphQlSource graphQlSource;


    @Test
    public void affiliate() {

        var affiliate = new Affiliate();
        affiliate.setId(1L);
        affiliate.setBalance(new BigDecimal(100));
        affiliate.setClientId(3L);
        affiliate.setVisitors(1040L);
        affiliate.setPayType("Test1");
        affiliate.setPayAmount(new BigDecimal(23342L));
        affiliate.setOneTime(true);
        affiliate.setWithdrawn(new BigDecimal(28444));
        affiliate.setDate(date.now());
        affiliate.setCreatedAt(date.now());
        affiliate.setUpdatedAt(date.now());

        var affiliate2 = new Affiliate();
        affiliate2.setId(2L);
        affiliate2.setClientId(4L);
        affiliate2.setVisitors(14040L);
        affiliate2.setPayType("Test2");
        affiliate2.setPayAmount(new BigDecimal(2432L));
        affiliate2.setOneTime(true);
        affiliate2.setBalance(new BigDecimal(12456));
        affiliate2.setWithdrawn(new BigDecimal(21444));
        affiliate2.setDate(date.now());
        affiliate2.setCreatedAt(date.now());
        affiliate2.setUpdatedAt(date.now());

        affiliateRepository.addAll(List.of(affiliate, affiliate2)).block();

        //TODO: проверка
        var listAfterAdd = affiliateRepository.findAllByIdIn(Set.of(1L, 2L)).collectList().block();

        var affiliateUpdated1 = new Affiliate();
        affiliateUpdated1.setId(1L);
        affiliateUpdated1.setBalance(new BigDecimal(0));
        affiliateUpdated1.setClientId(30L);
        affiliateUpdated1.setVisitors(0L);
        affiliateUpdated1.setPayType("Test111111");
        affiliateUpdated1.setPayAmount(new BigDecimal(0L));
        affiliateUpdated1.setOneTime(false);
        affiliateUpdated1.setWithdrawn(new BigDecimal(0));
        affiliateUpdated1.setDate(OffsetDateTime.now());
        affiliateUpdated1.setCreatedAt(OffsetDateTime.now());
        affiliateUpdated1.setUpdatedAt(OffsetDateTime.now().minusDays(1));

        var affiliateUpdated2 = new Affiliate();
        affiliateUpdated2.setId(2L);
        affiliateUpdated2.setClientId(40L);
        affiliateUpdated2.setVisitors(1L);
        affiliateUpdated2.setPayType("Test22222");
        affiliateUpdated2.setPayAmount(new BigDecimal(10L));
        affiliateUpdated2.setOneTime(false);
        affiliateUpdated2.setBalance(new BigDecimal(10));
        affiliateUpdated2.setWithdrawn(new BigDecimal(10));
        affiliateUpdated2.setDate(OffsetDateTime.now());
        affiliateUpdated2.setCreatedAt(OffsetDateTime.now());
        affiliateUpdated2.setUpdatedAt(OffsetDateTime.now().minusDays(1));

        affiliate.update(affiliateUpdated1);
        affiliate2.update(affiliateUpdated2);


        affiliateRepository.updateAll(List.of(affiliate, affiliate2)).block();


        System.out.println();
    }

    @Test
    public void paymentMethod() {


        var paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setModule("test11");
        paymentMethod.setDisplayName("test-display11");
        paymentMethod.setUpdatedAt(date.now());
        paymentMethod.setDeleted(false);

        var paymentMethod1 = new PaymentMethod();
        paymentMethod1.setId(2L);
        paymentMethod1.setModule("test22");
        paymentMethod1.setDisplayName("test-display22");
        paymentMethod1.setUpdatedAt(date.now());
        paymentMethod1.setDeleted(false);

        paymentMethodRepository.addAll(List.of(paymentMethod, paymentMethod1)).block();

        //TODO: проверка
        var list = paymentMethodRepository.findAll().collectList().block();


        var paymentMethodUpdated = new PaymentMethod();
        paymentMethodUpdated.setId(1L);
        paymentMethodUpdated.setModule("te");
        paymentMethodUpdated.setDisplayName("te");
        paymentMethodUpdated.setUpdatedAt(date.now().minusDays(5));
        paymentMethodUpdated.setDeleted(false);

        var paymentMethodUpdated1 = new PaymentMethod();
        paymentMethodUpdated1.setId(2L);
        paymentMethodUpdated1.setModule("t");
        paymentMethodUpdated1.setDisplayName("t");
        paymentMethodUpdated1.setDeleted(true);

        paymentMethod.update(paymentMethodUpdated);
        paymentMethod.setId(1L);
        paymentMethod.setUpdatedAt(date.now().minusDays(1));

        paymentMethod1.update(paymentMethodUpdated1);
        paymentMethod1.setId(2L);
        paymentMethod1.setUpdatedAt(date.now().minusDays(5));



        paymentMethodRepository.updateAll(List.of(paymentMethod, paymentMethod1)).block();

    }


}