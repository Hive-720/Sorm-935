package com.kjsoft.sorm.presentation.graphql.query.records;

import java.util.List;


public record User(String id, String name, List<Comment> comments) {
}