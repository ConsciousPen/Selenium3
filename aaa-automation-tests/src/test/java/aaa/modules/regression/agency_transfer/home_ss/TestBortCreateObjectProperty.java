package aaa.modules.regression.agency_transfer.home_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.admin.metadata.agencyvendor.AgencyTransferMetaData;
import aaa.admin.modules.agencyvendor.AgencyTransfer.defaulttabs.AgencyTransferTab;
import aaa.admin.pages.agencyvendor.AgencyTransferPage;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * @author S. Sivaram
 * @name Test Create Bort object
 * @scenario 1. Create Policy
 * 2. Create Bort Object
 * 3. Run Bort Job
 * 4. Validate transfer Completed
 * @details
 */
public class TestBortCreateObjectProperty extends HomeSSHO3BaseTest {

    private ApplicantTab applicantTab = new ApplicantTab();
    private String policyNumber;

    @Parameters({"state"})
    @StateList(states = Constants.States.AZ)
    @Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)


    public void testBortCreateObject(@Optional("AZ") String state) {
        policyNumber = createPolicyNumber();
        String sourceAgent = getSourceAgentDetails();
        createBortObject();
		JobUtils.executeJob(BatchJob.policyBORTransferJob);
        String agentPostTransfer = getTargetAgentDetails(policyNumber);
        assertAndLogResults(sourceAgent, agentPostTransfer);
    }

    public String createPolicyNumber() {
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
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        return applicantTab.getInquiryAssetList().getInquiryAssetList(HomeSSMetaData.ApplicantTab.AGENT_INFORMATION).getStaticElement(HomeSSMetaData.ApplicantTab.AgentInfo.AGENT).getValue();
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
		log.info("No of transfer ID's: " + AgencyTransferPage.tableTransfers.getRowsCount());
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
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        String sourceAgent = applicantTab.getInquiryAssetList().getInquiryAssetList(HomeSSMetaData.ApplicantTab.AGENT_INFORMATION).getStaticElement(HomeSSMetaData.ApplicantTab.AgentInfo.AGENT).getValue();
        assertThat(sourceAgent).contains("Foster Bottenberg");
        log.info("Source Agent: " + sourceAgent);
        mainApp().close();
        return sourceAgent;
    }
}
