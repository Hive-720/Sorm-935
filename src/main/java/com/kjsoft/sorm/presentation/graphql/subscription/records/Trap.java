package com.kjsoft.sorm.presentation.graphql.subscription.records;

import com.kjsoft.sorm.presentation.graphql.subscription.enums.TrapType;


public record Trap(TrapType type, String message) {}