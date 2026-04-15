package com.smallcase.resource.pojos.getRequestTokenResponse;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetRequestToken {

    private Boolean success;
    private String[] errors;
    private Data data;
}