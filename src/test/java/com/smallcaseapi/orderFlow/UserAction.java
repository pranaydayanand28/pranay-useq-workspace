package com.smallcaseapi.orderFlow;

import com.smallcaseapi.BaseTest;
import com.smallcaseapi.GetUser;
import commonutils.DataToShare;
import commonutils.WaitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.reports.LogStatus;

import java.io.IOException;

public class UserAction extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(UserAction.class);
    private static int counterUser = 0;
    private static boolean ifRepairIsRequired;

    public static boolean userAction(String broker) throws IOException, InterruptedException {

        logger.info("The request was successful, checking if repair action is needed....");

        WaitMethod.wait(10);
        //GetUser.ifRepairRequired(broker);
        ifRepairIsRequired = GetUser.ifRepairRequired(broker, (String)DataToShare.getValue("iscid"));
        //int fixActionSize = (Integer) DataToShare.getValue("fixSize");
        logger.info("Repair required for the current order ? " + ifRepairIsRequired);

        while (ifRepairIsRequired && counterUser < 10) {
            logger.info("Repair required, trying to repair....");
            WaitMethod.wait(3); //wait method here is required because if within 2 seconds another transaction takes place for a scid, it'll lock the scid for transaction
            RepairAction.fixBatch(broker);

            WaitMethod.wait(3);
            //GetUser.ifRepairRequired(broker);
            ifRepairIsRequired = GetUser.ifRepairRequired(broker, (String)DataToShare.getValue("iscid"));
            //fixActionSize = (Integer) DataToShare.getValue("fixSize");
            counterUser++;
            logger.info("Repair required after performing repair action ? " + ifRepairIsRequired);
        }

        counterUser = 0;
        if (ifRepairIsRequired) {
            logger.error("Unable to repair the smallcase, check for errors.....");
            LogStatus.info("Unable to repair the smallcase, check for errors.....");
            return false;
        }
        else {
            logger.info("Successfully repaired/ No repair needed, Successful");
            LogStatus.info("Successfully repaired/ No repair needed, Successful");
            return true;
        }
    }
}