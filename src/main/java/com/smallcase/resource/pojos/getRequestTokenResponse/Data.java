package com.smallcase.resource.pojos.getRequestTokenResponse;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Data {

    private String reqToken;
    private Long expire;
}