package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

@StateList(statesExcept = Constants.States.CA)
public class TestProofOfNewCarAddedProtection extends AutoSSBaseTest {


    /**
     *@author Dominykas Razgunas
     *@name Test Proof of equivalent new car added protection with prior carrier for new vehicle(s) after endorsement
     *@scenario
    1. Initiate a SS auto quote.
    2. Navigate to Vehicle tab.
    3. Add a 2019 vehicle.
    4. Navigate to Premium & Coverage tab.
    5. Add new car added protection with purchase date in last 30 days.
    6. Calculate premium.
    7. Navigate to Documents & Bind tab.
    8. Select 'YES' for 'Proof of purchase date (bill of sale) for new vehicle(s)'.
    9. Bind the quote.
    10. Initiate an endorsement on SS auto policy after 30 days of effective date.
    11. Navigate to Documents & Bind tab.
    12. Review 'Require to Issue' section.
    13. 'Proof of equivalent new car added protection with prior carrier for new vehicle(s)' should not be shown.
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27186")
    public void pas27186_testProofOfNewCarAddedProtectionEndorsement(@Optional("AZ") String state) {

        String today = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        String endorsementDay = TimeSetterUtil.getInstance().getCurrentTime().plusDays(31).format(DateTimeUtils.MM_DD_YYYY);

        // Adjust policy with preconditions. IMPORTANT!! Car should be new (2018 year atm fits)
        TestData tdPolicy = getPolicyTD().adjust(TestData.makeKeyPath(AutoSSMetaData.VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "JN8AZ2NF9J9660512")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
                        AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES.getLabel()), "Yes")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel()), new SimpleDataProvider())
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(),
                        AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.NEW_CAR_ADDED_PROTECTION.getLabel()), "Yes")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(),
                        AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.PURCHASE_DATE.getLabel()), today);

        // Endorsement day should be after 30 days (exactly 30days have different requirements)
        TestData tdEndorsement = getPolicyTD("Endorsement", "TestData").adjust(TestData.makeKeyPath(AutoSSMetaData.EndorsementActionTab.class.getSimpleName(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), endorsementDay);

        // Create Policy with preconditions
        openAppAndCreatePolicy(tdPolicy);

        // endorse policy
        policy.endorse().perform(tdEndorsement);
        // Navigate to D&B tab
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        // Assert that 'Proof of equivalent new car added protection with prior carrier for new vehicle(s)' is set to yes
        assertThat(new DocumentsAndBindTab().getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES)).isAbsent();

    }
}