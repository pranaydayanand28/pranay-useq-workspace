package com.smallcaseapi.samflow.multiassetwatchlist;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import com.smallcaseapi.payload.RemoveWatchlistV2;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class RemoveWatchlist  extends BaseTest {


    private static final Logger logger = LoggerFactory.getLogger(AddWatchtlist.class.getName());

    @Test(testName = "Remove from Watchllist v2", description = "Remove SCID from Watchlist v2")
    @Parameters({"PhoneNo", "Flow"})
    public void RemoveFromWatchlist(String PhoneNo, String Flow) {
        logger.info("removing Smallcase from Watchlist" + PhoneNo);
        Response RemoveFromWatchlist = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .body(RemoveWatchlistV2.RemoveSCIDFromWatchlist())
                .log().all()
                .when()
                .delete(SmallcaseResource.RemoveWatchlistV2)
                .then()
                .extract().response();

        System.out.println("Remove Stock : "+RemoveFromWatchlist.asString());

        ResponseAssert.assertThat(RemoveFromWatchlist)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        writeRequestAndResponseInReport(writer.toString(), RemoveFromWatchlist.prettyPrint(), Flow);

    }

    @Test(testName = "Remove  Stocks to wathlist v2", description = "Remove Stock from Watchlist v2")
    @Parameters({"PhoneNo", "Flow"})
    public void AddStockToWatchlsit(String PhoneNo, String Flow) {
        logger.info("Remove Stock from Watchlist" + PhoneNo);
        Response RemoveStock = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .body(RemoveWatchlistV2.RemoveStockFromWatchlist())
                .log().all()
                .when()
                .delete(SmallcaseResource.RemoveWatchlistV2)
                .then()
                .extract().response();

        ResponseAssert.assertThat(RemoveStock)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        writeRequestAndResponseInReport(writer.toString(), RemoveStock.prettyPrint(), Flow);
    }

    @Test(testName = "Remove  MF from wathlist v2", description = "Remove MutualFund from Watchlist v2")
    @Parameters({"PhoneNo", "Flow"})
    public void AddMFToWatchlsit(String PhoneNo, String Flow) {
        logger.info("remove MF from Watchlist" + PhoneNo);
        Response RemoveMF = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .body(RemoveWatchlistV2.RemoveMFfromWatchlist())
                .log().all()
                .when()
                .delete(SmallcaseResource.RemoveWatchlistV2)
                .then()
                .extract().response();

        ResponseAssert.assertThat(RemoveMF)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        writeRequestAndResponseInReport(writer.toString(), RemoveMF.prettyPrint(), Flow);
    }
}
