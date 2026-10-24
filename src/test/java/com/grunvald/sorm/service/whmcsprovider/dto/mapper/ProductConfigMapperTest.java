package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.grunvald.sorm.service.whmcsprovider.dto.ProductProviderDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductConfigMapperTest {

    private final ProductConfigMapper mapper = new ProductConfigMapperImpl();

    @Test
    void map_fullConfig_mapsAllFields() {
        var config = new ProductProviderDto.Config();
        config.setId(1L);
        config.setName("cfgName");
        config.setType("cfgType");

        ProductConfig result = mapper.map(config);

        assertEquals(1L, result.getId());
        assertEquals("cfgName", result.getName());
        assertEquals("cfgType", result.getType());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
