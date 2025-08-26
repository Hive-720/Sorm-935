package com.kjsoft.sorm.presentation.graphql.query.filters;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class CommentFilterInput {
    private List<CommentFilterInput> and;
    private List<CommentFilterInput> or;
    private CommentFilterInput not;
    private String userId;
    private String text;
}