package com.smallcase.resource.pojos.createdSmallcase.Request;

import lombok.Builder;
import lombok.Data;

@Data @Builder(setterPrefix = "set")
public class Constituent {
    public String sid;
    public double shares;
    public boolean locked;
    public double weight;
    public SidInfo sidInfo;
}
