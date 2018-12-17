package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.PrivilegeEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ca.dp3.functional.TestFAIRPlanEndorsement;
import toolkit.datax.TestData;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestFirelineTemplate extends PolicyBaseTest {

	private ErrorTab errorTab = new ErrorTab();
	private ReportsTab reportsTab = new ReportsTab();
	private EndorsementTab endorsementTab = new EndorsementTab();

	private static String firelineWarningMessageSS = "This policy is ineligible due to the FireLine score.";
	private static String firelineWarningMessageCA = "This policy is ineligible due to the FireLine score.";

	protected static String ADDRESS = "ADDRESS";
	protected static String ZIP = "ZIP";

	protected void pas21652_SS_firelineRuleForFirelineTableLookup(String zipCode, String address, int expectedFirelineScore, int firelineLookupTableValue, PrivilegeEnum.Privilege userPrivilege, String levelMatch) {
		if (userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
			mainApp().open();
		} else {
			openAppNonPrivilegedUser(userPrivilege);
		}

		createCustomerIndividual();
		getPolicyType().get().initiate();

		TestData policyTd = getPolicyTD();
		//Override address and ZIP code. This address should return Fireline score 3 or more.
		policyTd.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
				HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), zipCode);
		policyTd.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
				HomeSSMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), address);



		//Override Roof Type to 'Wood shingle/Wood shake'
		policyTd.adjust(TestData.makeKeyPath(HomeSSMetaData.PropertyInfoTab.class.getSimpleName(),
				HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(),
				HomeSSMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel()), "Wood shingle/Wood shake");

		if (!userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
			policyTd.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(),
					HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()));
		}

		getPolicyType().get().getDefaultView().fillUpTo(policyTd, ReportsTab.class, true);

		assertThat(reportsTab.tblFirelineReport.getRow(1)
				.getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel())
				.getValue()).isEqualTo(String.valueOf(expectedFirelineScore));

		if(expectedFirelineScore>firelineLookupTableValue){
			assertThat(reportsTab.lblFirelineMessage.getValue()).isEqualTo(firelineWarningMessageSS);
		}

		reportsTab.submitTab();

		assertThat(new PropertyInfoTab().getFireReportAssetList()
				.getAsset(HomeSSMetaData.PropertyInfoTab.FireReport.PLACEMENT_OR_MATCH_TYPE)
				.getValue()).isEqualTo(levelMatch);



		getPolicyType().get().getDefaultView().fillFromTo(policyTd, PropertyInfoTab.class, PurchaseTab.class,false);

		if (levelMatch == ZIP){
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS14061993);
		}
		if(expectedFirelineScore>2){
			assertThat(errorTab.isVisible()).isTrue();
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS2240042);
			if(expectedFirelineScore>firelineLookupTableValue){
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_Fireline);
			}
			if (userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
				assertThat(errorTab.buttonOverride).isEnabled();
			} else {
				assertThat(errorTab.buttonOverride).isDisabled();
				assertThat(errorTab.buttonApproval).isEnabled();
			}
		} else {
			assertThat(errorTab.isVisible()).isFalse();
		}
	}


	protected void pas18311_CA_firelineRuleForFirelineTableLookup(String zipCode, String address, int expectedFirelineScore,int firelineLookupTableValue, PrivilegeEnum.Privilege userPrivilege, Boolean isFAIRplanAdded,String levelMatch) {
		if (userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
			mainApp().open();
		} else {
			openAppNonPrivilegedUser(userPrivilege);
		}

		createCustomerIndividual();
		getPolicyType().get().initiate();

		TestData policyTd = getPolicyTD();
		//Override address and ZIP code. This address should return Fireline score 3 or more.
		policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
				HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), zipCode);
		policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
				HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), address);

		//Override Roof Type to 'Wood shingle/Wood shake'
		policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(),
				HomeCaMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel()), "Wood shingle/Wood shake");

		if (!userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
			policyTd.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(),
					HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel()));
		}

		getPolicyType().get().getDefaultView()
				.fillUpTo(policyTd, aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab.class, true);

		assertThat(reportsTab.tblFirelineReport.getRow(1)
				.getCell(HomeCaMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel())
				.getValue()).isEqualTo(String.valueOf(expectedFirelineScore));

		if(expectedFirelineScore>firelineLookupTableValue){
			assertThat(reportsTab.lblFirelineMessage.getValue()).isEqualTo(firelineWarningMessageCA);
		}

		reportsTab.submitTab();

		assertThat(new PropertyInfoTab().getFireReportAssetList()
				.getAsset(HomeCaMetaData.PropertyInfoTab.FireReport.PLACEMENT_OR_MATCH_TYPE)
				.getValue()).isEqualTo(String.valueOf(levelMatch));

		getPolicyType().get().getDefaultView()
				.fillFromTo(policyTd, aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab.class,
						EndorsementTab.class, false);

		if (isFAIRplanAdded) {
			if (getPolicyType() == PolicyType.HOME_CA_DP3) {
				//Add FPCECADP endorsement
				endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel()).click();
			} else if (getPolicyType() == PolicyType.HOME_CA_HO3) {
				//Add FPCECA endorsement
				endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
			}
			endorsementTab.btnSaveEndo.click();

			policyTd.adjust(DocumentsTab.class.getSimpleName(), testDataManager.getDefault(TestFAIRPlanEndorsement.class).getTestData("DocumentsTab_SignFairPlanEndorsement"));
		}

		getPolicyType().get().getDefaultView()
				.fillFromTo(policyTd, EndorsementTab.class,
						aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab.class, false);

		if (levelMatch == ZIP){
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA10107077);
		}
		if(expectedFirelineScore>2 && !isFAIRplanAdded){
			assertThat(errorTab.isVisible()).isTrue();
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA1302295);
			if(expectedFirelineScore>firelineLookupTableValue && !isFAIRplanAdded){
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_Fireline_CA02122017);
			}
			if (userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
				assertThat(errorTab.buttonOverride).isEnabled();
			} else {
				assertThat(errorTab.buttonOverride).isDisabled();
				assertThat(errorTab.buttonApproval).isEnabled();
			}
		} else {
			assertThat(errorTab.isVisible()).isFalse();
		}
	}

	protected void pas18296_AE5RuleNotTriggering(String zipCode, String address, int expectedFirelineScore) {
		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().initiate();

		TestData policyTd = getPolicyTD();
		//Add AUTO Other active AAA policies
		policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
				HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()),
				getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO3), "DataGather", "OtherActiveAAAPolicies"));

		//Override address and ZIP code. This address should return Fireline score 5 or 6.
		policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
				HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), zipCode);
		policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(),
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
				HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), address);

		//Override Roof Type to anything but NOT 'Wood shingle/Wood shake'
		policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
				HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(),
				HomeCaMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel()), "Builtup Tar & Gravel");

		getPolicyType().get().getDefaultView()
				.fillUpTo(policyTd, aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab.class, true);

		assertThat(reportsTab.tblFirelineReport.getRow(1)
				.getCell(HomeCaMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel())
				.getValue()).isEqualTo(String.valueOf(expectedFirelineScore));

		reportsTab.submitTab();

		getPolicyType().get().getDefaultView()
				.fillFromTo(policyTd, aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab.class,
						aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab.class, false);

		assertThat(errorTab.isVisible()).isTrue();
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_Fireline_CA02122017);
	}
}
