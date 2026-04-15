package com.smallcase.resource.pojos.createdSmallcase.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data @Builder(setterPrefix = "set")
@AllArgsConstructor
public class Root {
    public String did;
    public String scid;
    public String source;
    public ArrayList<Constituent> constituents;
    public ArrayList<Segment> segments;
    public String compositionScheme;
    public Info info;
    public Stats stats;
}
