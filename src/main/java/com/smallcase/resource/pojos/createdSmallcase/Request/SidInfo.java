package com.smallcase.resource.pojos.createdSmallcase.Request;

import lombok.Builder;
import lombok.Data;

@Data @Builder(setterPrefix = "set")
public class SidInfo {
    public String name;
    public String sector;
    public String ticker;
}
