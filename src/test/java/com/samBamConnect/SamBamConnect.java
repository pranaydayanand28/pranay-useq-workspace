package com.samBamConnect;

import com.smallcaseapi.BaseTest;
import com.smallcaseapi.SmallcaseLoginAPI;
import com.smallcaseapi.brokerAuthCookies.BrokerAuthCookies;
import com.smallcaseapi.samflow.SamLoginHelper;
import commonutils.PhoneNoTestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Hashtable;

public class SamBamConnect extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SamBamConnect.class);

    private SamLoginHelper samLoginHelper = new SamLoginHelper();
    private ConnectHelper connectHelper = new ConnectHelper();
    private SmallcaseLoginAPI smallcaseLoginHelper = new SmallcaseLoginAPI();

    //Connect New SAM with New BAM
    @Test(dataProvider = "data", description = "Perform SAM BAM Connect")
    public void newSamBamConnect(Hashtable<String, String> arguments) {
        
        String testFlow = "SAM BAM Connect - " + arguments.get("broker");
        logger.info("--------------- Test "+ testFlow +" started ---------------");

        String phoneNumber = PhoneNoTestUser.generatePhoneNumber(); //Generate Phone Number
        samLoginHelper.processLogin(phoneNumber, true, testFlow); //Performing SAM Login
        connectHelper.generateGatewaySdkToken(testFlow); //Generate Gateway SDK token
        connectHelper.initiateGatewaySession(testFlow); //Initiate Gateway Session
        connectHelper.initiateConnectTransaction(testFlow); //Initiate Connect Transaction
        connectHelper.updateTransactionAtGateway(testFlow); //Update Transaction at Gateway
        BrokerAuthCookies.brokerLogin(arguments); //Performing BAM Login
        BrokerAuthCookies.getRequestToken(arguments); //Generate Request Token
        smallcaseLoginHelper.loginWithValidJWTAndRequestToken(arguments.get("broker")); //Perform smallcase Login for Broker
        connectHelper.gatewayConnect(testFlow); //Initiate Gateway Connect
        connectHelper.gatewaySessionRefresh(testFlow); //Gateway Session Refresh
        connectHelper.gatewayIntent(testFlow); //Generate Gateway Intent
        connectHelper.updateTransactionAtGateway(testFlow); //Update Transaction at Gateway
        connectHelper.connect(testFlow); //Connect SAM with BAM
        connectHelper.checkSession(testFlow); //Check smallcase Session

        logger.info("--------------- Test "+ testFlow +" ended ---------------");
    }
}
