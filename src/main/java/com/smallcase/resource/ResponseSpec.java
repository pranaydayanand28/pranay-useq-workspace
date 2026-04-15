package com.smallcase.resource;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;

public class ResponseSpec {
    private static ResponseSpecification responseSpecification;

    public static ResponseSpecification responseSpecificationForSuccess(){
        responseSpecification  = new ResponseSpecBuilder()
                .expectStatusCode(ApiConstants.success)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(ApiConstants.API_LOAD_TIME))
                .build();
        return responseSpecification;
    }

    public static ResponseSpecification responseSpecificationForCreated(){
        responseSpecification  = new ResponseSpecBuilder()
                .expectStatusCode(ApiConstants.created)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(ApiConstants.API_LOAD_TIME))
                .build();
        return responseSpecification;
    }

    public static ResponseSpecification responseSpecificationForUpdated(){
        responseSpecification  = new ResponseSpecBuilder()
                .expectStatusCode(ApiConstants.updated)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(ApiConstants.API_LOAD_TIME))
                .build();
        return responseSpecification;
    }

    public static ResponseSpecification responseSpecificationForBadRequest(){
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(ApiConstants.badRequest)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(ApiConstants.API_LOAD_TIME))
                .build();
        return responseSpecification;
    }

    public static ResponseSpecification responseSpecificationForUnauthorized(){
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(ApiConstants.unAuthorized)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(ApiConstants.API_LOAD_TIME))
                .build();
        return responseSpecification;
    }

    public static ResponseSpecification responseSpecificationForForbidden(){
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(ApiConstants.forbidden)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(ApiConstants.API_LOAD_TIME))
                .build();
        return responseSpecification;
    }

    public static ResponseSpecification responseSpecificationForNotFound(){
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(ApiConstants.notFound)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(ApiConstants.API_LOAD_TIME))
                .build();
        return responseSpecification;
    }
}
