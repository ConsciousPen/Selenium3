package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.util.Files.contentOf;
import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.nio.charset.Charset;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.claim.datamodel.claim.CASClaimResponse;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestAccidentSurchargeWaiver extends TestOfflineClaimsTemplate {

    private PurchaseTab purchaseTab = new PurchaseTab();
    File claimResponseFile;

    /**
     * @author Josh Carpenter
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during conversion
     * @scenario
     * 1. Initiate Auto SS conversion policy with base date > 4 years ago
     * 2. Fill policy up to P & C tab with AF accident in the past 33 months
     * 3. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 4. Add a second AF accident in the past 33 months
     * 5. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 6. Remove the second claim
     * 7. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 8. Change the prior carrier to non-AAA (Progressive)
     * 9. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverConversion(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getConversionPolicyDefaultTD());
        createConversionQuoteAndFillUpTo(td, DocumentsAndBindTab.class);
        validateAFW(td);
        assertThat(PolicySummaryPage.tableRenewals).isPresent();

    }

    /**
     * @author Josh Carpenter
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during NB
     * @scenario
     * 1. Initiate Auto SS policy with base date = 2 years ago and time with previous carrier = 2 years
     * 2. Fill policy up to P & C tab with AF accident in the past 33 months
     * 3. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 4. Add a second AF accident in the past 33 months
     * 5. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 6. Remove the second claim
     * 7. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 8. Change the prior carrier to non-AAA (Progressive)
     * 9. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverNB(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getPolicyTD());
        createQuoteAndFillUpTo(td, DocumentsAndBindTab.class);
        validateAFW(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }

    /**
     * @author Josh Carpenter
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during endorsement
     * @scenario
     * 1. Initiate Auto SS policy with base date = 2 years ago and time with previous carrier = 2 years and AF accident in the past 33 months
     * 2. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 3. Bind policy
     * 4. Initiate endorsement with trans. eff. date 5 days from policy eff. date
     * 5. Add another AF claim in the past 33 months
     * 6. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverEndorsement(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getPolicyTD());
        createQuoteAndFillUpTo(td, RatingDetailReportsTab.class);
        addActivityDriverTab(getAccidentInfoTd());
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        policy.getDefaultView().fillFromTo(td, VehicleTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
        addActivityDriverTab(getAccidentInfoTd().adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-8M>"));
        validateIncludedInPoints("No", "Yes");

    }

    /**
     * @author Josh Carpenter, Sreekanth Kopparapu
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during renewal
     * @scenario
     * 1. Create Auto SS policy with base date = 2 years ago and time with previous carrier = 2 years
     * 2. Create renewal image and add AF accident in the past 33 months
     * 3. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 4. Add a second AF accident in the past 33 months
     * 5. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 6. Remove the second claim
     * 7. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 8. Change the prior carrier to non-AAA (Progressive)
     * 9. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverRenewal(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getPolicyTD());
        openAppAndCreatePolicy(td);
        policy.renew().perform();
        validateAFW(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }

    /**
     * @author Josh Carpenter
     * @name Test that ASW is given properly during an aged renewal (ASW not eligible in NB policy)
     * @scenario
     * 1.  Create Auto SS policy with the following attributes:
     *       a. Base date = 1 year ago
     *       b. Time with previous carrier = 2 years
     *       c. Previous carrier = 1 year, AAA provider
     * 2.  Advance time to renewal reports order date (R-63)
     * 3.  Run renewalOfferGenerationPart1 and renewalClaimOrderAsyncJob
     * 4.  Create CAS response file and upload to VDM with micro service automation
     * 5.  Run renewalOfferGenerationPart2 and renewalClaimReceiveAsyncJob
     * 6.  Open policy and validate CAS claim is on the driver tab and 'Included in Rating' = 'Yes'
     * 7.  Bind policy
     * 8.  Advance time to renewal effective date and run renewal jobs again
     * 9.  Pay amount due for the renewal
     * 10. Advance time to next renewal reports order date (2R-63)
     * 11. Run renewalOfferGenerationPart1
     * 12. Update claim loss amount, create CAS response, and upload to VDM
     * 13. Run renewalOfferGenerationPart2 and renewalClaimReceiveAsyncJob
     * 14. Open policy, navigate to Driver tab, validate.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-23995")
    public void pas23995_testAccidentSurchargeWaiverAgedRenewalCAS(@Optional("") String state) {

        TestData tdCustomer = getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "ASW")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "CASTest");

        TestData tdPolicy = adjustTdBaseDate(getPolicyTD())
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel()), "$<today>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "D99155622");

        // Create policy
        mainApp().open();
        createCustomerIndividual(tdCustomer);
        createPolicy(tdPolicy);
        policyNumber = PolicySummaryPage.getPolicyNumber();
        policyExpirationDate = PolicySummaryPage.getExpirationDate();

        // Advance time to renewal reports order date and run jobs
        runRenewalAndOrderJobs();

        // Generate CAS response file, upload to VDM, and run renewal receive jobs
        downloadClaimRequest();
        createCasResponseAndUpload();
        runRenewalClaimReceiveJob();

        // Open policy and validate the AF accident is included in rating
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        validateIncludedInPoints("Yes");
        assertThat(driverTab.getActivityInformationAssetList().getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).getValue()).contains("5000");
        new PremiumAndCoveragesTab().calculatePremium();

        // Bind policy and make active
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        payTotalAmtDue(policyNumber);
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        // Open policy and create second renewal image
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        assertThat(PolicySummaryPage.getEffectiveDate()).isEqualToIgnoringHours(policyExpirationDate);
        policyExpirationDate = PolicySummaryPage.getExpirationDate();
        runRenewalAndOrderJobs();

        // Upload file into inbound folder and run receive jobs
        createCasResponseUpdateLossAmtAndUpload();
        runRenewalClaimReceiveJob();

        // Open policy and validate claim is still included in rating and claim amount has been updated
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        validateIncludedInPoints("Yes");
        assertThat(driverTab.getActivityInformationAssetList().getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).getValue()).contains("4000");

    }

    private TestData adjustTdBaseDate(TestData td) {

        return td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel()), "$<today-2y>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel()), "$<today-3y>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel()), "$<today-1y>");
    }

    private void validateAFW(TestData policyTd) {

        // Add AF accident
        addActivityDriverTab(getAccidentInfoTd());

        // Validate AFW is given
        validateIncludedInPoints("No");

        // Add a second AF accident in past 33 months and validate
        addActivityDriverTab(getAccidentInfoTd().adjust(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-8M>"));
        validateIncludedInPoints("Yes", "Yes");

        // Remove second claim and validate AFW is given
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        DriverTab.tableActivityInformationList.removeRow(2);
        validateIncludedInPoints("No");

        // Change Prior Carrier to non-AAA (Progressive) and validate no AFW for both
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        new GeneralTab().getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER).setValue("Progressive");
        validateIncludedInPoints("Yes");
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

        if (PurchaseTab.remainingBalanceDueToday.isPresent()) {
            purchaseTab.fillTab(policyTd).submitTab();
        }

    }

    private void validateIncludedInPoints(String... expectedValues) {
        new PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        for (int i = 1; i <= expectedValues.length; i++) {
            String value = expectedValues[i - 1];
            assertThat(DriverTab.tableActivityInformationList.getRow(i).getCell(PolicyConstants.ActivityInformationTable.INCLUDE_IN_POINTS_TIER).getValue())
                    .contains(value);
            if ("No".equals(value)) {
                assertThat(DriverTab.tableActivityInformationList.getRow(i).getCell(PolicyConstants.ActivityInformationTable.NOT_INCLUDED_REASON_CODES).getValue())
                        .contains(PolicyConstants.ActivityInformationTable.REASON_CODE_ASW);
            } else {
                assertThat(DriverTab.tableActivityInformationList.getRow(i).getCell(PolicyConstants.ActivityInformationTable.NOT_INCLUDED_REASON_CODES).getValue()).isEmpty();
            }
        }
    }

    private void addActivityDriverTab(TestData td) {
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        new DriverTab().fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel()), td));
    }

    private TestData getAccidentInfoTd() {
        return DataProviderFactory.dataOf(
                AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY.getLabel(), "Click",
                AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), "At-Fault Accident",
                AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), "Accident (Property Damage Only)",
                AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-10M>",
                AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), "3000");
    }

    private void runRenewalAndOrderJobs() {
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(policyExpirationDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalClaimOrderAsyncJob);
    }

    private void createCasResponseAndUpload() {
        String casResponseFileName = getCasResponseFileName();
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper("af_accident_claim_data_model.yaml", casResponseFileName);
        claimResponseFile = batchClaimHelper.processClaimTemplate(response -> setPolicyNumber(policyNumber, response));
        assertThat(claimResponseFile).isFile().isNotNull();
        String content = contentOf(claimResponseFile, Charset.defaultCharset());
        log.info("Generated CAS claim response filename {} content {}", casResponseFileName, content);
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(), Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());
    }

    private void createCasResponseUpdateLossAmtAndUpload() {
        String casResponseFileName = getCasResponseFileName();
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper("af_accident_claim_data_model.yaml", casResponseFileName);
        claimResponseFile = batchClaimHelper.processClaimTemplate(response -> {
            setPolicyNumber(policyNumber, response);
            setClaimLossAmount("4000.000000000", response);
        });
        assertThat(claimResponseFile).isFile().isNotNull();
        String content = contentOf(claimResponseFile, Charset.defaultCharset());
        log.info("Updated CAS claim total loss amount in response filename {} content {}", casResponseFileName, content);
        RemoteHelper.get().uploadFile(claimResponseFile.getAbsolutePath(), Jobs.getClaimReceiveJobFolder() + File.separator + claimResponseFile.getName());
    }

    private void setClaimLossAmount(String totalAmountPaid, CASClaimResponse response) {
        response.getClaimLineItemList().forEach(claimLineItem -> {
            claimLineItem.getClaimList().forEach(claim -> claim.setTotalAmountPaid(totalAmountPaid));
        });
    }

}
