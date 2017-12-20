package aaa.modules.regression.sales.home_ss.dp3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSDP3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author S. Jaraminas
 * @name Test Policy Renewal
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Choose Data Gathering Action
 * 4. Verify fields: CONVERSION_DATE, LEAD_SOURCE, COMMISSION_TYPE,
 * IMMEDIATE_PRIOR_CARRIER, PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG
 * 5. Fill information up to PremiumsAndCoveragesQuoteTab
 * 6. Verify if CONVERSION_DATE field is correct
 */

public class TestPolicyRenewalManualEntryFields extends HomeSSDP3BaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3)
    public void testPolicyRenewal(@Optional("") String state) {

        GeneralTab generalTab = new GeneralTab();
        TestData td = getTestSpecificTD("TestData");
        String currentDate = LocalDateTime.now().format(DateTimeUtils.MM_DD_YYYY);
        String inceptionDate = getTestSpecificTD("TD_Renewal_Actions").getTestData("InitiateRenewalEntryActionTab").getValue("Inception Date");

        mainApp().open();

        createCustomerIndividual();

        customer.initiateRenewalEntry().perform(getTestSpecificTD("TD_Renewal_Actions"));

        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONVERSION_DATE.getLabel()).getValue().toString().isEmpty());
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel()).isEnabled()).isFalse();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel()).getValue()).isEqualTo("Hybrid Conversion");
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.COMMISSION_TYPE.getLabel()).isEnabled()).isFalse();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.COMMISSION_TYPE.getLabel()).getValue()).isEqualTo("Renewal");
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel()).isEnabled()).isTrue();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()).getValue()).isEqualTo(inceptionDate);
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).getAllValues().
                containsAll(Arrays.asList("CSAA Mid-Atlantic Insurance Company of New Jersey", "CSAA Affinity Insurance Company", "AAA Insurance"))).isTrue();

        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());

        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONVERSION_DATE.getLabel()).getValue()).isEqualTo(currentDate);

    }
}