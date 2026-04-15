package com.smallcase.resource.pojos.createdSmallcase.Request;

import lombok.Builder;
import lombok.Data;

@Data @Builder(setterPrefix = "set")
public class Stats {
    public int initialValue;
}
