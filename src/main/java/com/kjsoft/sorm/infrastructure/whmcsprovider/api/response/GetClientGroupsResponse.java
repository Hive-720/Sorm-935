package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
public class GetClientGroupsResponse extends Response {
    @JsonProperty("groups")
    private Groups groups;

    public List<Groups.Group> getClientCroups() {
        return groups != null && groups.group != null ? groups.group : List.of();
    }

    @Getter
    @Setter
    public static class Groups {
        private List<Group> group;

        @Getter
        @Setter
        public static class Group {
            @JsonProperty("id")
            private Long id;
            @JsonProperty("groupName")
            private String groupName;
            @JsonProperty("groupColour")
            private String groupColour;
            @JsonProperty("discountpercent")
            private BigDecimal discountPercent;
            @JsonProperty("susptermexempt")
            private String suspTermExempt;
            @JsonProperty("separateinvoices")
            private String separateInvoices;
        }
    }
}