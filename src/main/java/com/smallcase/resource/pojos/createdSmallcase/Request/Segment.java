package com.smallcase.resource.pojos.createdSmallcase.Request;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data @Builder(setterPrefix = "set")
public class Segment {
    public String label;
    public ArrayList<String> constituents;
}
