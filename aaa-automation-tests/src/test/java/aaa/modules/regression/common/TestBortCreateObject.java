/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.common;

import aaa.admin.metadata.agencyvendor.AgencyTransferMetaData;
import aaa.admin.modules.agencyvendor.AgencyTransfer.defaulttabs.AgencyTransferTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;


/**
 * @author S. Sivaram
 * @name Test Create Bort object
 * @scenario
 * 1. Create Policy
 * 2. Create Bort Object
 * 3. Add second Customer to Account
 * 4. Update Account
 * @details
 */
public class TestBortCreateObject extends AutoSSBaseTest {
    GeneralTab gTab = new GeneralTab();
    @Parameters({"state"})
    @Test(groups = { Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testBortCreateObject(@Optional("AZ") String state) {


        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        log.info("policy created: "+policyNumber);
        log.info("Get Source Policy Agency Details...");
        policy.policyInquiry().start();

        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        String sourceChannelType =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE).getValue();
        String sourceAgency =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY).getValue();
        String sourceAgencyOfRecord =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY_OF_RECORD).getValue();
        String sourceSalesChannel =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.SALES_CHANNEL).getValue();
        String sourceAgencyLocation =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY_LOCATION).getValue();
        String sourceAgent =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).getValue();
        String sourceAgentOfRecord =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT_OF_RECORD).getValue();
        String sourceAgentNumber =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT_NUMBER).getValue();

        log.info("Source Channel type: "+sourceChannelType);
        log.info("Source Agency: "+sourceAgency);
        log.info("Source Agency Of Record: "+sourceAgencyOfRecord);
        log.info("Source Sales Channel: "+sourceSalesChannel);
        log.info("Source Agency Location: "+sourceAgencyLocation);
        log.info("Source Agent: "+sourceAgent);
        log.info("Source Agent Of Record: "+sourceAgentOfRecord);
        log.info("Source Agenct Number: "+sourceAgentNumber);


        //loging out and logging in to Admin

        adminApp().open();
        AgencyTransferTab agencyTransferTab = new AgencyTransferTab();
        agencyTransferTab.createBortObject();

        TestData td = getTestSpecificTD("TestData");
        agencyTransferTab.getAssetList().fill(td);





        /*
        AgencyTransferPage.buttonSearchTransfer.click();
        log.info( "No of transfer ID's: "+Integer.toString(AgencyTransferPage.tableTransfers.getRowsCount()));
        log.info("*************** "+AgencyTransferPage.tableTransfers.getRow(AgencyTransferPage.tableTransfers.getRowsCount()).getCell(3).getValue());
*/


        log.info("Transfer ID:  "+AgencyTransferMetaData.AgencyTransferTab.TRANSFER_ID.getLabel());
    }
}
