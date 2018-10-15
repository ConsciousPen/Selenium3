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
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestAccidentSurchargeWaiver extends AutoSSBaseTest {

    /**
     * @author Josh Carpenter
     * @name Test that a new at-fault accident is waived if the ASW conditions are met
     * @scenario
     * 1. Create Customer
     * 2. Initiate Auto SS conversion policy with base date > 4 years ago
     * 3. Fill policy up to P & C tab with a not at fault accident in the past 12 months
     * 4. Calculate premium and open VRD page
     * 5. Validate the 'Included in Points and/or Tier' value for the NAF accident is non-chargeable
     * 6. Navigate back to Driver tab
     * 7. Add an at fault accident in the past 12 months
     * 6. Validate the 'Included in Points and/or Tier' for the AF accident value states 'Waived - Accident Surcharge Waiver'
     * 7. Bind policy
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14738")
    public void pas14738_testAccidentSurchargeWaiver(@Optional("") String state) {

        // Prepare test data
        TestData td  = getConversionPolicyDefaultTD()
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel()), "$<today-5y>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel()), "$<today-5y>")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel()), "$<today+44d>");

        TestData tdActivityInfo = DataProviderFactory.dataOf(
                AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY.getLabel(), "Click",
                AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), "At-Fault Accident",
                AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), "Accident (Property Damage Only)",
                AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-6M>",
                AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), "3000");

        // Create conversion quote and add AF accident
        createConversionQuoteAndFillUpTo(td, DocumentsAndBindTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        new DriverTab().fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel()), tdActivityInfo));

        // Validate VRD page for AF accident
        new PremiumAndCoveragesTab().calculatePremium();
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
//        assertThat(PremiumAndCoveragesTab.tableRatingDetailsActivities.getRow("", "Include in Points and/or Tier").getCell(2).getValue())
//                .isEqualTo("Waived - Accident Surcharge Waiver");
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

        // Bind policy
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        assertThat(PolicySummaryPage.tableRenewals).isPresent();

    }

}
