package aaa.modules.regression.conversions.pup.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

/**
 * @author S. Jaraminas
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

public class TestPolicyRenewalManualEntryFieldsVerification extends ConvPUPBaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.PUP, testCaseId = "PAS-6951, PAS-6831, PAS-6838")
    public void pas6951_PolicyRenewalActions(@Optional("DE") String state) {

        GeneralTab generalTab = new GeneralTab();
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

        mainApp().open();

        createCustomerIndividual();

        TestData td = getConversionPolicyDefaultTD();

        customer.initiateRenewalEntry().perform(getPolicyTD("InitiateRenewalEntry", "TestData"));
        
        policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesQuoteTab.class, true);

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());

        assertThat(generalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.CONVERSION_DATE)).hasValue(currentDate);
        assertThat(generalTab.getPolicyInfoAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.COMMISSION_TYPE)).hasValue("Renewal");
        assertThat(generalTab.getPolicyInfoAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.APPLICATION_TYPE)).hasValue("Hybrid Conversion");
        assertThat(generalTab.getPolicyInfoAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.LEAD_SOURCE)).hasValue("Hybrid Conversion");

        String policyState = String.valueOf(generalTab.getPolicyInfoAssetList().getAsset(PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.STATE).getValue());
        String pupPolicyNumberSuffix = policyState + "PU109";

	    assertThat(generalTab.getPolicyNumberForConversion().startsWith(pupPolicyNumberSuffix)).isTrue();
	    assertThat(generalTab.getPolicyNumberForConversion().substring(4).matches("^\\d+$")).isTrue();
	    assertThat(generalTab.getPolicyNumberForConversion().length()).isEqualTo(13);

    }
}