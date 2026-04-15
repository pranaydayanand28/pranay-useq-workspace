package com.smallcase.resource.pojos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @Builder(setterPrefix = "set")
public class BrokerLoginRequest {

    private String app;
    private String reqToken;
    private String brokerParams;
    private String broker;
    private String deviceType;
}