package com.kjsoft.sorm.service.whmcsdata.dataservice.user.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.User;

import java.util.List;


public record UserSearchResult(String cursor, boolean hasNextPage, List<User> userList) {
}