package com.kjsoft.sorm.presentation.graphql.query.result;

import com.kjsoft.sorm.presentation.graphql.query.records.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class CommentResult {
    private boolean hasNextPage;
    private List<Comment> result;
}