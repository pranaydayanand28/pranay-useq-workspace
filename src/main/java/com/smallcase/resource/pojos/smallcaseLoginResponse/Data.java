package com.smallcase.resource.pojos.smallcaseLoginResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Data {

    private String message;
    @JsonProperty("x-csrf-token")
    private String csrf;
    private String jwt;
    private String broker;
    private Flags flags;

}