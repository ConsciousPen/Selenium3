package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.PrivilegeEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestFirelineTemplate extends PolicyBaseTest {

	private ErrorTab errorTab = new ErrorTab();
	private ReportsTab reportsTab = new ReportsTab();

	protected void pas18302_SS_firelineRuleForWoodShingleRoof(String zipCode, String address, int expectedFirelineScore, PrivilegeEnum.Privilege userPrivilege) {
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

		//Then you order PUBLIC_PROTECTION_CLASS new popup shows up if Address returns something, this method clicks popup OK.
		TestData ppcReportDialog = new SimpleDataProvider()
				.adjust(HomeSSMetaData.ReportsTab.PPCReportDialog.BTN_OK.getLabel(), "click");
		TestData ppcReports = new SimpleDataProvider()
				.adjust(HomeSSMetaData.ReportsTab.PublicProtectionClassRow.REPORT.getLabel(), "Report")
				.adjust(HomeSSMetaData.ReportsTab.PublicProtectionClassRow.PPC_REPORT_DIALOG.getLabel(), ppcReportDialog);
		TestData reportTab = policyTd.getTestData(HomeSSMetaData.ReportsTab.class.getSimpleName())
				.adjust(HomeSSMetaData.ReportsTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcReports);
		policyTd.adjust(HomeSSMetaData.ReportsTab.class.getSimpleName(), reportTab).resolveLinks();

		//Override Roof Type to 'Wood shingle/Wood shake'
		policyTd.adjust(TestData.makeKeyPath(HomeSSMetaData.PropertyInfoTab.class.getSimpleName(),
				HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(),
				HomeSSMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel()), "Wood shingle/Wood shake");

		if (!userPrivilege.equals(PrivilegeEnum.Privilege.L41)) {
			policyTd.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()));
		}

		getPolicyType().get().getDefaultView().fillUpTo(policyTd, ReportsTab.class, true);

		assertThat(reportsTab.tblFirelineReport.getRow(1)
				.getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue()).isEqualTo(String.valueOf(expectedFirelineScore));

		reportsTab.submitTab();
		getPolicyType().get().getDefaultView().fillFromTo(policyTd, PropertyInfoTab.class, PurchaseTab.class, false);

		if (expectedFirelineScore > 2) {
			assertThat(errorTab.isVisible()).isTrue();
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS2240042);
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

	protected void pas18914_CA_firelineRuleForWoodShingleRoof(String zipCode, String address, int expectedFirelineScore, PrivilegeEnum.Privilege userPrivilege) {
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

		getPolicyType().get().getDefaultView().fillUpTo(policyTd, aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab.class, true);

		assertThat(reportsTab.tblFirelineReport.getRow(1)
				.getCell(HomeCaMetaData.ReportsTab.FirelineReportRow.WILDFIRE_SCORE.getLabel()).getValue()).isEqualTo(String.valueOf(expectedFirelineScore));

		reportsTab.submitTab();
		getPolicyType().get().getDefaultView().fillFromTo(policyTd, aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab.class,
				aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab.class, false);

		if (expectedFirelineScore > 2) {
			assertThat(errorTab.isVisible()).isTrue();
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA1302295);
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
}
