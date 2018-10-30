/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.common;

import aaa.admin.metadata.agencyvendor.AgencyTransferMetaData;
import aaa.admin.modules.agencyvendor.AgencyTransfer.defaulttabs.AgencyTransferTab;
import aaa.admin.pages.agencyvendor.AgencyTransferPage;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.waiters.Waiters;

import static toolkit.verification.CustomAssertions.assertThat;

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
    private String policyNumber;

    @Parameters({"state"})
    @Test(groups = { Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    @BeforeClass(alwaysRun = true)


    public void testBortCreateObject(@Optional("AZ") String state) {
        policyNumber = createPolicyNumber();
        String sourceAgent = getSourceAgentDetails();
        createBortObject();
        JobUtils.executeJob(Jobs.policyBORTransferJob);
        String agentPostTransfer = gettargetAgentDetails(policyNumber);
        assertAndLogResults(sourceAgent, agentPostTransfer);
      }

    private String createPolicyNumber() {
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();
        log.info("policy created: "+policyNumber);
        return policyNumber;
    }

    private void assertAndLogResults(String sourceAgent, String agentPostTransfer) {
        assertThat(agentPostTransfer).contains("House Agent AAA Az Ins Agcy Az");
        log.info("Test Passed " + "Agent Prior Transfer : " + sourceAgent + "" + "Agent Post Transfer : " + agentPostTransfer);
    }

    private String gettargetAgentDetails(String policyNumber) {
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        policy.policyInquiry().start();
        return gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).getValue();
    }

    private void createBortObject() {
        adminApp().open();
        AgencyTransferTab agencyTransferTab = new AgencyTransferTab();
        agencyTransferTab.createBortObject();
        TestData td = getTestSpecificTD("TestData");
        agencyTransferTab.getAssetList().fill(td);
        AgencyTransferTab.targetInsuranceAgent.setValue("House Agent AAA Az Ins Agcy Az");
        AgencyTransferTab.submit.click();

        AgencyTransferPage.buttonSearchTransfer.click();
        log.info( "No of transfer ID's: "+Integer.toString(AgencyTransferPage.tableTransfers.getRowsCount()));
        log.info("*************** "+AgencyTransferPage.tableTransfers.getRow(AgencyTransferPage.tableTransfers.getRowsCount()).getCell(3).getValue());
        log.info("Transfer ID:  "+ AgencyTransferMetaData.AgencyTransferTab.TRANSFER_ID.getLabel());

        log.info("Waiting for Object to be Submitted");
        int counter =0;
        do {AgencyTransferPage.buttonSearchTransfer.click();
            Waiters.SLEEP(3000).go();
            counter++;
            if(counter==200){
                log.info("Timed out after waiting for 10 minutes for status to become Submitted to Batch");
            }
        } while (!AgencyTransferPage.tableTransfers.getRow(AgencyTransferPage.tableTransfers.getRowsCount()).getCell(3).getValue().equalsIgnoreCase("Submitted to Batch") && counter<200);
        adminApp().close();
    }

    private String getSourceAgentDetails() {
        policy.policyInquiry().start();
        String sourceAgent =  gTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).getValue();
        assertThat(sourceAgent).contains("Foster Bottenberg");
        log.info("Source Agent: "+sourceAgent);
        mainApp().close();
        return sourceAgent;
    }







}
