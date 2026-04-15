package com.smallcase.resource.pojos.PlaceOrder.Request;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data @Builder(setterPrefix = "set")
public class Root {
    public ArrayList<Order> orders;
    public String scid;
    public String variety;
    public String source;
    public String label;
    public String smallcaseName;
    public String did;
    public String clientType;
    public boolean consent;
    public String iscid;
}
