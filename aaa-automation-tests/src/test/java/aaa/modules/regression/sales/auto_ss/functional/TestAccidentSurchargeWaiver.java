package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestAccidentSurchargeWaiver extends AutoSSBaseTest {

    private DriverTab driverTab = new DriverTab();
    private PurchaseTab purchaseTab = new PurchaseTab();

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
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
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
     * 1. Initiate Auto SS policy with base date > 4 years ago
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
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
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
     * 1. Initiate Auto SS policy with base date > 4 years ago and AF accident in the past 33 months
     * 2. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * 3. Bind policy
     * 4. Initiate endorsement with trans. eff. date 5 days from policy eff. date
     * 5. Add another AF claim in the past 33 months
     * 6. Calculate premium and validate the 'Included in Points and/or Tier' value and reason code on the Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
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
     * @author Josh Carpenter
     * @name Test that a new at-fault accident is waived if the ASW conditions are met during renewal
     * @scenario
     * 1. Create Auto SS policy with base date > 4 years ago
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
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiverRenewal(@Optional("") String state) {

        TestData td = adjustTdBaseDate(getPolicyTD());
        openAppAndCreatePolicy(td);
        policy.renew().perform();
        validateAFW(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }

    private TestData adjustTdBaseDate(TestData td) {

        return td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel()), "$<today-5y>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel()), "$<today-5y>")
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
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), DataProviderFactory.emptyData())
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

}
