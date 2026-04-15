package com.smallcase.resource.pojos.brokerLoginResponse;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BrokerLogin {

    private Boolean success;
    private String[] errors;
    private Data data;

}