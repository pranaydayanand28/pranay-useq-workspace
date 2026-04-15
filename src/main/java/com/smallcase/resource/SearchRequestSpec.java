package com.smallcase.resource;

import commonutils.ConfigRead;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class SearchRequestSpec {

    public static RequestSpecification searchSpec() {

        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("search_url"))
                .addHeader("Content-Type", "application/json").build();
    }
}
