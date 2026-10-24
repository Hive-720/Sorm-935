package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientGroupsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientGroupProviderMapperTest {

    private final ClientGroupProviderMapper mapper = new ClientGroupProviderMapperImpl();

    @Test
    void map_fullGroup_mapsAllFields() {
        var source = new GetClientGroupsResponse.Groups.Group();
        source.setId(1L);
        source.setGroupName("VIP");
        source.setGroupColour("#ff0000");
        source.setDiscountPercent(new BigDecimal("10.5"));
        source.setSuspTermExempt("on");
        source.setSeparateInvoices("on");

        ClientGroupProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals("VIP", result.getGroupName());
        assertEquals(new BigDecimal("10.5"), result.getDiscountPercent());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
