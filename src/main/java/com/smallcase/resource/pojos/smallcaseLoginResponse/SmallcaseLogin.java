package com.smallcase.resource.pojos.smallcaseLoginResponse;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class SmallcaseLogin {

    private Boolean success;
    private String[] errors;
    private Data data;
}