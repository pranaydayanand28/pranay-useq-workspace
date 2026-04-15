package com.smallcase.resource.pojos.PlaceOrder.Request;

import lombok.Builder;
import lombok.Data;

@Data @Builder(setterPrefix = "set")
public class Order {
    public String sid;
    public int quantity;
    public String transactionType;
}
