package com.smallcase.resource.pojos.createdSmallcase.Request;

import lombok.Builder;
import lombok.Data;

@Data @Builder(setterPrefix = "set")
public class Info {
    public String name;
    public String shortDescription;
    public String tier;
}
