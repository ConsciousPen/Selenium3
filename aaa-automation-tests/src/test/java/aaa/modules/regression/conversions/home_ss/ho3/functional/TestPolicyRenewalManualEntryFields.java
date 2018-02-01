package aaa.modules.regression.conversions.home_ss.ho3.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.regression.conversions.ConvHomeSsHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

/**
 * @author S. Jaraminas
 * @name Test Policy Renewal
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Choose Data Gathering Action
 * 4. Verify fields: CONVERSION_DATE, LEAD_SOURCE, COMMISSION_TYPE,
 * IMMEDIATE_PRIOR_CARRIER, PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG
 * 5. Fill information up to UnderwritingAndApprovalTab
 * 6. Verify if all fields are enabled in UnderwritingAndApprovalTab page
 * 7. Navigates to GeneralTab page and verify if CONVERSION_DATE field is correct
 */

public class TestPolicyRenewalManualEntryFields extends ConvHomeSsHO3BaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3, testCaseId = "PAS-6663")
    public void pas6663_PolicyRenewal(@Optional("") String state) {

        GeneralTab generalTab = new GeneralTab();
        UnderwritingAndApprovalTab underwritingAndApprovalTab = new UnderwritingAndApprovalTab();

        TestData td = getConversionPolicyDefaultTD();
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        String inceptionDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(10).format(DateTimeUtils.MM_DD_YYYY);
        String effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY);

        mainApp().open();

        createCustomerIndividual();

        initiateManualConversion(getManualConversionInitiationTd()
                .adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
                CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel()), effectiveDate)
                .adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
                CustomerMetaData.InitiateRenewalEntryActionTab.INCEPTION_DATE.getLabel()), inceptionDate));

        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONVERSION_DATE.getLabel()).getValue().toString().isEmpty());
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel()).isEnabled()).isFalse();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel()).getValue()).isEqualTo("Hybrid Conversion");
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.COMMISSION_TYPE.getLabel()).isEnabled()).isFalse();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.COMMISSION_TYPE.getLabel()).getValue()).isEqualTo("Renewal");
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel()).isEnabled()).isTrue();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()).getValue()).isEqualTo(inceptionDate);
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).getAllValues().
                containsAll(Arrays.asList("CSAA Mid-Atlantic Insurance Company of New Jersey", "CSAA Affinity Insurance Company", "AAA Insurance"))).isTrue();

        policy.getDefaultView().fillUpTo(td, UnderwritingAndApprovalTab.class, true);

        assertThat(underwritingAndApprovalTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED_IN_THE_PAST_3_YEARS.getLabel()).isEnabled()).isTrue();
        assertThat(underwritingAndApprovalTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_PERSON.getLabel()).isEnabled()).isTrue();
        assertThat(underwritingAndApprovalTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.HAS_THE_PROPERTY_BEEN_IN_FORECLOSURE_PROCEEDINGS_WITHIN_THE_PAST_18_MONTHS.getLabel()).isEnabled()).isTrue();
        assertThat(underwritingAndApprovalTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.DO_EMPLOYEES_OF_ANY_RESIDENT_RESIDE.getLabel()).isEnabled()).isTrue();
        assertThat(underwritingAndApprovalTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_CONDUCTED_ON_THE_PREMISES_FOR_WHICH_AN_ENDORSEMENT_IS_NOT_ATTACHED_TO_THE_POLICY.getLabel()).isEnabled()).isTrue();

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());

        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONVERSION_DATE.getLabel()).getValue()).isEqualTo(currentDate);

    }
}