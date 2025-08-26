package com.kjsoft.sorm.presentation.graphql.query.result;

import com.kjsoft.sorm.presentation.graphql.query.records.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class UserResult {
    private boolean hasNextPage;
    private List<User> result;
}