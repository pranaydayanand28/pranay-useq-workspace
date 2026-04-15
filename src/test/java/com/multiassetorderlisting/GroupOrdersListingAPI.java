package com.multiassetorderlisting;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
import com.smallcase.resource.ApiConstants;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.MultiAssetResource;
import commonutils.DataToShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GroupOrdersListingAPI extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GroupOrdersListingAPI.class);

    @Test(testName = "Verify Group Orders listing API with valid Sam JWT and invalid CSRF", description = "Group Orders listing API with valid Sam JWT and invalid CSRF")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithValidSamJWTAndInvalidCSRF(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec("invalidCSRF"))
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.inValidCSRF);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with invalid Sam JWT and valid CSRF", description = "Group Orders listing API with invalid Sam JWT and valid CSRF")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithInvalidSamJWTAndValidCSRF(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec("invalidSamJWT"))
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.inValidSamJWT);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with invalid Sam JWT and invalid CSRF", description = "Group Orders listing API with invalid Sam JWT and invalid CSRF")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithInvalidSamJWTAndInvalidCSRF(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec("invalid"))
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.inValidSamJWT);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with valid Sam JWT and valid CSRF", description = "Group Orders Listing API with valid Sam JWT and valid CSRF")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithValidSamJWTAndValidCSRF(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .validateResponseDataArray("isNotEmpty", "data.orders") //isEmpty, isNotEmpty
                .validatePagination();

        logger.info("Test passed");

        JsonPath jsonPath = new JsonPath(response.asString());
        String key = assetType.equalsIgnoreCase("mutualFund")? "mfId" : "sid";

        logger.info("Saving "+key+"s from response to DataToShare");
        List<String> value = jsonPath.getList("data.orders."+key);
        DataToShare.setValue(key, value); //Saving mfIds/sids from response to DataToShare
    }

    @Test(testName = "Verify Group Orders listing of user without orders history", description = "Group Orders Listing API of user without orders history")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingForUserWithoutOrders(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .validateResponseDataArray("isEmpty", "data.orders"); //isEmpty, isNotEmpty

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with invalid AssetType param", description = "Group Orders listing API with invalid AssetType param")
    public void VerifyGroupOrdersListingWithInvalidAssetType() throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", "assetType") //smallcase, mutualFund, stock
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.inValidAssetType);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API without AssetType param", description = "Group Orders listing API without AssetType param")
    public void VerifyGroupOrdersListingWithoutAssetType() throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .spec(RequestSpec.paginationSpec())
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.noAssetType);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with invalid PageNo param", description = "Group Orders listing API with invalid PageNo param")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithInvalidPageNo(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", "aa")
                .queryParam("pageSize", 10)
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.invalidPageNo);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API without PageNo param", description = "Group Orders listing API without PageNo param")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithoutPageNo(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageSize", 10)
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.noPageNo);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with PageNo as 0", description = "Group Orders listing API with PageNo as 0")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithPageNoZero(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 0)
                .queryParam("pageSize", 10)
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.pageNoZero);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with PageNo as 2", description = "Group Orders listing API with PageNo as 2")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithPageNoTwo(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 2)
                .queryParam("pageSize", 10)
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .validateResponseDataArray("isNotEmpty", "data.orders") //isEmpty, isNotEmpty
                .validatePagination(2, 10);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with invalid PageSize param", description = "Group Orders listing API with invalid PageSize param")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithInvalidPageSize(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .queryParam("pageSize", "bb")
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.invalidPageSize);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API without PageSize param", description = "Group Orders listing API without PageSize param")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithoutPageSize(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.noPageSize);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with PageSize as 0", description = "Group Orders listing API with PageSize as 0")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithPageSizeZero(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .queryParam("pageSize", 0)
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.pageSizeZero);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Group Orders listing API with PageSize greater than 10", description = "Group Orders listing API with PageSize greater than 10")
    @Parameters("assetType")
    public void VerifyGroupOrdersListingWithPageSizeGreaterThanTen(String assetType) throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .queryParam("pageSize", 15)
                .when().get(MultiAssetResource.groupOrderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Group orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .validateResponseDataArray("isNotEmpty", "data.orders") //isEmpty, isNotEmpty
                .validatePagination(1, 15);

        logger.info("Test passed");
    }
}
