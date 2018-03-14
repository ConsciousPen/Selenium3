package aaa.modules.regression.conversions.pup.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author S. Sushil
 * @name Test UnderWriter Tab Available for Edit for Conversion PUP
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Create Conversion PUP policy
 * 3. Select RME Action with PUP product
 * 4. Choose Data Gathering Action
 * 5. Fill information up to PremiumAndCoveragesQuoteTab
 * 6. Navigates to Underwriting Tab
 * 7. Verify UW Questions for Fields are available for edit
 *
 */

public class TestPolicyRenewalManualEntryFieldsVerification_UWQuestions extends ConvPUPBaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.PUP, testCaseId = "PAS-9525")
    public void PolicyRenewalActions(@Optional("NJ") String state) {



        UnderwritingAndApprovalTab  underwritingAndApprovalTab = new UnderwritingAndApprovalTab();

        mainApp().open();
        createCustomerIndividual();
        TestData td = getConversionPolicyDefaultTD();
        customer.initiateRenewalEntry().perform(getPolicyTD("InitiateRenewalEntry", "TestData"));
        policy.getDefaultView().fillUpTo(td, UnderwritingAndApprovalTab.class, true);

        //Assert common options present
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.HAS_THE_APPLICANT_BEEN_SUED_FOR_LIBEL_OR_SLANDER)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.DOES_APPLICANT_OWN_ANY_PROPERTY_OUTSIDE_OF_THE_US)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_CREATURE_OR_PERSON)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.ARE_THERE_ANY_OWNED_LEASED_WATERCRAFT_USED_FOR_ANYTHING_OTHER_THAN_PERSONAL_PLEASURE_USE)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.ARE_THERE_ANY_OWNED_LEASED_WATERCRAFT_WITHOUT_LIABILITY_COVERAGE)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.DO_ANY_APPLICANTS_USE_THEIR_PERSONAL_VEHICLES_FOR_WHOLESALE)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.DO_ANY_APPLICANTS_OPERATE_A_COMMERCIAL_VEHICLE)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.DO_EMPLOYEES_OF_ANY_RESIDENT_RESIDE_IN_THE_DWELLING)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.ARE_ANY_APPLICANTS_OR_INSUREDS_A_CELEBRITY_OR_A_PUBLIC_FIGURE)).isEnabled(true);

        //Assert MD Specific Options
        if(state.contains("MD"))        {
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.APPLICANTS_WHO_HAVE_BEEN_CANCELLED)).isEnabled(true);
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES)).isEnabled(true);
        }

        //Assert Options present on SS and OR but not MD
        else if (!state.contains("MD"))
        {
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED)).isEnabled(true);
        }

        //Assert OR Specific Options
        else if(state.contains("OR"))
        {
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES)).isEnabled(true);
        }
        //Assert if not present in OR or MD
        else if(!state.contains("OR") && !state.contains("MD")){
            assertThat(underwritingAndApprovalTab.getAssetList().getAsset(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_HOME_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES)).isEnabled(true);

        }
        }
    }

