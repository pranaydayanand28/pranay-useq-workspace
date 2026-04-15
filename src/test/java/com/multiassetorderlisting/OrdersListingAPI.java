package com.multiassetorderlisting;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
import com.smallcase.resource.ApiConstants;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.MultiAssetResource;
import commonutils.DataToShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OrdersListingAPI extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(OrdersListingAPI.class);

    @Test(testName = "Verify Orders listing API with valid Sam JWT and invalid CSRF", description = "Orders listing API with valid Sam JWT and invalid CSRF")
    @Parameters("assetType")
    public void VerifyOrdersListingWithValidSamJWTAndInvalidCSRF(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec("invalidCSRF"))
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.inValidCSRF);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with invalid Sam JWT and valid CSRF", description = "Orders listing API with invalid Sam JWT and valid CSRF")
    @Parameters("assetType")
    public void VerifyOrdersListingWithInvalidSamJWTAndValidCSRF(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec("invalidSamJWT"))
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.inValidSamJWT);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with invalid Sam JWT and invalid CSRF", description = "Orders listing API with invalid Sam JWT and invalid CSRF")
    @Parameters("assetType")
    public void VerifyOrdersListingWithInvalidSamJWTAndInvalidCSRF(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec("invalid"))
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .queryParams("pageSize", 10)
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.inValidSamJWT);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with valid Sam JWT and valid CSRF", description = "Orders Listing API with valid Sam JWT and valid CSRF")
    @Parameters("assetType")
    public void VerifyOrdersListingWithValidSamJWTAndValidCSRF(String assetType) throws IOException {
        String key = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";
        List<String> ids = (List<String>) DataToShare.getValue(key);
        for (String id: ids) {
            System.out.println(id);
            logger.info("Generating request for "+key+": "+id);
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.samFlowRequestSpec())
                    .spec(RequestSpec.paginationSpec())
                    .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                    .queryParam(key, id) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                    .when().get(MultiAssetResource.orderListing);

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

            logger.info("Validating response");
            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit()
                    .validateResponseDataArray("isNotEmpty", "data.orders") //isEmpty, isNotEmpty
                    .validatePagination();
        }
        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing of user without orders history", description = "Orders Listing API of user without orders history")
    @Parameters("assetType")
    public void VerifyOrdersListingForUserWithoutOrders(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam(id, "id") //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .validateResponseDataArray("isEmpty", "data.orders");

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with invalid AssetType param", description = "Orders listing API with invalid AssetType param")
    public void VerifyOrdersListingWithInvalidAssetType() throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", "assetType") //smallcase, mutualFund, stock
                .queryParam("id", DataToShare.getValue("Id"))
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.inValidOrderListingAssetType);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API without AssetType param", description = "Orders listing API without AssetType param")
    public void VerifyOrdersListingWithoutAssetType() throws IOException {
        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .spec(RequestSpec.paginationSpec())
                .queryParam("id", DataToShare.getValue("Id"))
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.noAssetType);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with invalid PageNo param", description = "Orders listing API with invalid PageNo param")
    @Parameters("assetType")
    public void VerifyOrdersListingWithInvalidPageNo(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", "aa")
                .queryParam("pageSize", 10)
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.invalidPageNo);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API without PageNo param", description = "Orders listing API without PageNo param")
    @Parameters("assetType")
    public void VerifyOrdersListingWithoutPageNo(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageSize", 10)
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.noPageNo);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with PageNo as 0", description = "Orders listing API with PageNo as 0")
    @Parameters("assetType")
    public void VerifyOrdersListingWithPageNoZero(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 0)
                .queryParam("pageSize", 10)
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.pageNoZero);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with invalid PageSize param", description = "Orders listing API with invalid PageSize param")
    @Parameters("assetType")
    public void VerifyOrdersListingWithInvalidPageSize(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .queryParam("pageSize", "bb")
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.invalidPageSize);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API without PageSize param", description = "Orders listing API without PageSize param")
    @Parameters("assetType")
    public void VerifyOrdersListingWithoutPageSize(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.noPageSize);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with PageSize as 0", description = "Orders listing API with PageSize as 0")
    @Parameters("assetType")
    public void VerifyOrdersListingWithPageSizeZero(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .queryParam("pageSize", 0)
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.pageSizeZero);

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing API with PageSize greater than 10", description = "Orders listing API with PageSize greater than 10")
    @Parameters("assetType")
    public void VerifyOrdersListingWithPageSizeGreaterThanTen(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .queryParam("pageNo", 1)
                .queryParams("pageSize", 15)
                .queryParam(id, ((List<String>) DataToShare.getValue(id)).get(0)) //mfId/sId will be set into DataToShare from Test method: GroupOrdersAPI.VerifyGroupOrdersListing()
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .validateResponseDataArray("isNotEmpty", "data.orders") //isEmpty, isNotEmpty
                .validatePagination(1, 15);;

        logger.info("Test passed");
    }

    @Test(testName = "Verify Orders listing without AssetId param", description = "Orders Listing API without AssetId param")
    @Parameters("assetType")
    public void VerifyOrdersListingWithoutAssetId(String assetType) throws IOException {
        String id = assetType.equalsIgnoreCase("mutualFund") ? "mfId" : "sid";

        logger.info("Generating request");
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .spec(RequestSpec.paginationSpec())
                .queryParam("assetType", assetType) //smallcase, mutualFund, stock
                .when().get(MultiAssetResource.orderListing);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), "Orders listing");

        logger.info("Validating response");
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .returnsValidErrorMessage(ApiConstants.noAssetId.replace("id", id));

        logger.info("Test passed");
    }
}
