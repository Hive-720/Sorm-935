package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ActivityLogResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.dto.ActivityLogSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.activityLogResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActivityLogResultMapperTest {

    private ActivityLogResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = activityLogResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog();
        first.setId(1L);
        first.setDescription("first");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog();
        second.setId(2L);
        second.setDescription("second");

        ActivityLogResult result = mapper.map(new ActivityLogSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals("1", result.result().get(0).id());
        assertEquals("first", result.result().get(0).description());
        assertEquals("2", result.result().get(1).id());
        assertEquals("second", result.result().get(1).description());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
