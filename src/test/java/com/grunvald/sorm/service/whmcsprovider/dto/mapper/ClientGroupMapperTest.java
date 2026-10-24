package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientGroupMapperTest {

    private final ClientGroupMapper mapper = new ClientGroupMapperImpl();

    @Test
    void map_fullDto_mapsAllFields() {
        var dto = new ClientGroupProviderDto();
        dto.setId(1L);
        dto.setGroupName("VIP");
        dto.setDiscountPercent(new BigDecimal("10.5"));

        ClientGroup result = mapper.map(dto);

        assertEquals(1L, result.getId());
        assertEquals("VIP", result.getName());
        assertEquals(new BigDecimal("10.5"), result.getDiscountPercent());
        assertNull(result.getUpdatedAt());
        assertFalse(result.isDeleted());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
