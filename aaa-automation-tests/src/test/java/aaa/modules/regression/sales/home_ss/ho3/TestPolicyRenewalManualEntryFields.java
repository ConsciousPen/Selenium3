package aaa.modules.regression.sales.home_ss.ho3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.preconditions.SetTodayDate;
import com.exigen.istf.timesetter.client.TimeSetter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.ComboBox;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author S. Jaraminas
 * @name Test Policy Renewal
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Choose Data Gathering Action
 * 5. Verify fields
 * 6. Fill information up to PremiumsAndCoveragesQuoteTab
 * 7. Verify if CONVERSION_DATE field is correct date
 */

public class TestPolicyRenewalManualEntryFields extends HomeSSHO3BaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
    public void testPolicyRenewal(@Optional("") String state) {

        GeneralTab generalTab = new GeneralTab();
        TestData td = getTestSpecificTD("TestData");
        String currentDate = LocalDateTime.now().format(DateTimeUtils.MM_DD_YYYY);

        mainApp().open();

        createCustomerIndividual();

        customer.initiateRenewalEntry().perform(getTestSpecificTD("TD_Renewal_Actions"));

        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONVERSION_DATE.getLabel()).getValue().toString().isEmpty());
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel()).isEnabled()).isFalse();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel()).getValue()).isEqualTo("Hybrid Conversion");
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.COMMISSION_TYPE.getLabel()).isEnabled()).isFalse();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.COMMISSION_TYPE.getLabel()).getValue()).isEqualTo("Renewal");
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel()).isEnabled()).isTrue();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).getAllValues().
                containsAll(Arrays.asList("CSAA Mid-Atlantic Insurance Company of New Jersey", "CSAA Affinity Insurance Company", "AAA Insurance"))).isTrue();

        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());

        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONVERSION_DATE.getLabel()).getValue()).isEqualTo(currentDate);

    }
}