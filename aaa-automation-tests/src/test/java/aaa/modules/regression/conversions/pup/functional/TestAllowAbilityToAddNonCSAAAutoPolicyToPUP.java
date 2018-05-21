package aaa.modules.regression.conversions.pup.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Parth Varmora
 * @name Test Policy Renewal for PUP
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Create Conversion PUP policy
 * 3. Select RME Action with PUP product
 * 4. Choose Data Gathering Action
 * 5. Fill information up to PremiumAndCoveragesQuoteTab
 * 6. Navigates to GeneralTab
 * 7. Verify fields: CONVERSION_DATE, LEAD_SOURCE, COMMISSION_TYPE,
 * APPLICATION_TYPE, LEAD_SOURCE and checks pupPolicyNumber sequence
 */

public class TestAllowAbilityToAddNonCSAAAutoPolicyToPUP extends ConvPUPBaseTest {
    private UnderlyingRisksAutoTab underlyingRisksAutoTab = policy.getDefaultView().getTab(UnderlyingRisksAutoTab.class);

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.PUP, testCaseId = "PAS-11831")
    public void pas6951_PolicyRenewalActions(@Optional("AZ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Gather test data
        TestData tdPUP = getConversionPolicyDefaultTD();

        // Initiate Conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(tdPUP, UnderlyingRisksAutoTab.class);

        // Fill Underlying Risks Tabs
        fillUnderlyingRisksAutoTab(tdPUP);
    }

    private void fillUnderlyingRisksAutoTab(TestData tdPUP) {
        TestData tdAutoTabDrivers = getTestSpecificTD("TestData_Drivers");

        tdPUP.adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel()), tdAutoTabDrivers);

       underlyingRisksAutoTab.fillTab(tdPUP);
    }
}
