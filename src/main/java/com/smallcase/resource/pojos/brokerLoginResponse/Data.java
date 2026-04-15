package com.smallcase.resource.pojos.brokerLoginResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Data {
    private String jwt_trd;
    private Flags flags;
    @JsonProperty("x-csrf-token")
    private String csrf;

}