package aaa.modules.regression.agency_transfer.auto_ss;

import aaa.admin.metadata.agencyvendor.AgencyTransferMetaData;
import aaa.admin.modules.agencyvendor.AgencyTransfer.defaulttabs.AgencyTransferTab;
import aaa.admin.pages.agencyvendor.AgencyTransferPage;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author S. Sivaram
 * @name Test Create Bort object
 * @scenario 1. Create Policy
 * 2. Create Bort Object
 * 3. Run Bort Job
 * 4. Validate transfer Completed
 * @details
 */
public class TestBortCreateObjectAutoSS extends AutoSSBaseTest {

    private GeneralTab generalTab = new GeneralTab();
    private String policyNumber;

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)


    public void testBortCreateObject(@Optional("AZ") String state) {
        policyNumber = createPolicyNumber();
        String sourceAgent = getSourceAgentDetails();
        createBortObject();
        JobUtils.executeJob(Jobs.policyBORTransferJob);
        String agentPostTransfer = getTargetAgentDetails(policyNumber);
        assertAndLogResults(sourceAgent, agentPostTransfer);
    }

    private String createPolicyNumber() {
        String policyNumber = openAppAndCreatePolicy();
        log.info("policy created: " + policyNumber);
        return policyNumber;
    }

    private void assertAndLogResults(String sourceAgent, String agentPostTransfer) {
        assertThat(agentPostTransfer).contains("House Agent AAA Az Ins Agcy Az");
        log.info("Test Passed " + "Agent Prior Transfer : " + sourceAgent + "" + "Agent Post Transfer : " + agentPostTransfer);
    }

    private String getTargetAgentDetails(String policyNumber) {
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policy.policyInquiry().start();
        return generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).getValue();
    }

    private void createBortObject() {
        adminApp().open();
        AgencyTransferTab agencyTransferTab = new AgencyTransferTab();
        agencyTransferTab.createBortObject();
        TestData td = getTestSpecificTD("TestData");
        agencyTransferTab.getAssetList().fill(td);
        agencyTransferTab.getAssetList().getAsset(AgencyTransferMetaData.AgencyTransferTab.TRANSFER_TARGET_SECTION)
                .getAsset(AgencyTransferMetaData.AgencyTransferTab.TransferTargetSection.TARGET_INSURANCE_AGENT).setValue("House Agent AAA Az Ins Agcy Az");

        agencyTransferTab.submitTab();


        AgencyTransferPage.buttonSearchTransfer.click();
        log.info("No of transfer ID's: " + Integer.toString(AgencyTransferPage.tableTransfers.getRowsCount()));
        log.info("*************** " + AgencyTransferPage.tableTransfers.getRow(AgencyTransferPage.tableTransfers.getRowsCount()).getCell(3).getValue());
        log.info("Transfer ID:  " + AgencyTransferMetaData.AgencyTransferTab.TRANSFER_ID.getLabel());
        log.info("Waiting for Object to be Submitted");

        int counter = 0;
        do {
            AgencyTransferPage.buttonSearchTransfer.click();
            Waiters.SLEEP(3000).go();
            counter++;
            if (counter == 200) {
                log.info("Timed out after waiting for 10 minutes for status to become Submitted to Batch");
            }
        }
        while (!AgencyTransferPage.tableTransfers.getRow(AgencyTransferPage.tableTransfers.getRowsCount()).getCell("Status").getValue().equalsIgnoreCase("Submitted to Batch") && counter < 200);
        adminApp().close();
    }

    private String getSourceAgentDetails() {
        policy.policyInquiry().start();
        String sourceAgent = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getStaticElement(AutoSSMetaData.GeneralTab.PolicyInformation.AGENT).getValue();
        assertThat(sourceAgent).contains("Foster Bottenberg");
        log.info("Source Agent: " + sourceAgent);
        mainApp().close();
        return sourceAgent;
    }
}
