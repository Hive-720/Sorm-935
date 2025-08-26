package com.kjsoft.sorm.presentation.graphql.query.filters;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


//TODO record?
@Data
@AllArgsConstructor
public class UserFilterInput {
    private List<UserFilterInput> and;
    private List<UserFilterInput> or;
    private UserFilterInput not;
    private String name;
    private Integer age;
}