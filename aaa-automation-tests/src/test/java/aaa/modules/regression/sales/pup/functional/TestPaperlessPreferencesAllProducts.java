/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.pup.functional;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.PAPERLESS_PREFERENCES_ELIGIBILITY_CHECK_FOR_PRODUCT;
import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.PAPERLESS_PREFERENCES_ELIGIBILITY_INSERT_FOR_PRODUCT;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestPaperlessPreferencesAbstract;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestPaperlessPreferencesAllProducts extends TestPaperlessPreferencesAbstract {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	/** Disabled because functionality was synced only with HO SS and Auto SS
	 * * @author Oleg Stasyuk
	 *
	 * PAS-283
	 *
	 * See detailed steps in template file
	 * {@link TestPaperlessPreferencesAbstract}
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = {"PAS-283", "PAS-1451", "PAS-1453", "PAS-1454", "PAS-1740", "PAS-2564"})
	public void pas283_paperlessPreferencesForAllStatesProducts(@Optional("VA") String state) {
		if ("".equals(DBService.get().getValue(String.format(PAPERLESS_PREFERENCES_ELIGIBILITY_CHECK_FOR_PRODUCT, "AAA_PUP_SS", state)).orElse(""))) {
			DBService.get().executeUpdate(String.format(PAPERLESS_PREFERENCES_ELIGIBILITY_INSERT_FOR_PRODUCT, "AAA_PUP_SS", state));
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		}

		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData tdHome = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather"), "TestData_" + state)
				.adjust(TestData.makeKeyPath("GeneralTab", HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()), "$<today-25d:MM/dd/yyyy>")
				.adjust(TestData.makeKeyPath("GeneralTab", HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()), "$<today-25d:MM/dd/yyyy>")
				.adjust(HomeSSMetaData.ErrorTab.class.getSimpleName(), tdError);

		mainApp().open();
		createCustomerIndividual();
		PolicyType.HOME_SS_HO3.get().createPolicy(tdHome);
		//SearchPage.openPolicy("VAH3952918744");
		String hoPolicyNumber = PolicySummaryPage.getPolicyNumber();
		//		 Create Test Data
		TestData tdPUP = getPolicyTD()
				.adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(),
						PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel() + "[0]",
						PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(),
						PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_NUMBER.getLabel()),
						hoPolicyNumber)
				.adjust(TestData.makeKeyPath("GeneralTab",
						PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
						PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()), "$<today-25d:MM/dd/yyyy>")
				.adjust(PersonalUmbrellaMetaData.ErrorTab.class.getSimpleName(), tdError);

		createPolicy(tdPUP);

		pas283_paperlessPreferencesForAllStatesProducts();
	}

	@Override
	protected String getDocumentsAndBindTab() {
		return NavigationEnum.PersonalUmbrellaTab.BIND.get();
	}

	@Override
	protected String getGeneralTab() {
		return NavigationEnum.PersonalUmbrellaTab.GENERAL.get();
	}

	@Override
	protected String getPremiumAndCoveragesTab() {
		return NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get();
	}

	@Override
	protected InquiryAssetList getInquiryAssetList() {
		return new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), PersonalUmbrellaMetaData.GeneralTab.class);
	}

	@Override
	protected Tab getDocumentsAndBindTabElement() {
		return new DocumentsAndBindTab();
	}

	@Override
	protected Tab getPremiumAndCoveragesTabElement() {
		return null;
	}

	@Override
	protected AssetDescriptor<TextBox> getEnrolledInPaperless() { return PersonalUmbrellaMetaData.BindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS; }

	@Override
	protected AssetDescriptor<Button> getButtonManagePaperlessPreferences() { return PersonalUmbrellaMetaData.BindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES; }

	@Override
	protected AssetDescriptor<Button> getEditPaperlessPreferencesButtonDone() { return PersonalUmbrellaMetaData.BindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE; }

	@Override
	protected CommonErrorTab getErrorTabElement() {
		return new ErrorTab();
	}

	@Override
	public AssetList getPaperlessPreferencesAssetList() {
		return new DocumentsAndBindTab().getAssetList().getAsset(PersonalUmbrellaMetaData.BindTab.PAPERLESS_PREFERENCES.getLabel(), AssetList.class);
	}

	@Override
	protected AssetDescriptor<ComboBox> getMethodOfDelivery() { return PersonalUmbrellaMetaData.BindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY; }

	@Override
	protected AssetDescriptor<ComboBox> getIncludeWithEmail() { return PersonalUmbrellaMetaData.BindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL; }

	@Override
	protected AssetDescriptor<RadioGroup> getApplyeValueDiscount() {
		return null;
	}

	@Override
	protected AssetDescriptor<TextBox> getIssueDate() { return PersonalUmbrellaMetaData.BindTab.DocumentPrintingDetails.ISSUE_DATE; }

	@Override
	public AssetList getDocumentPrintingDetailsAssetList() {
		return new DocumentsAndBindTab().getAssetList().getAsset(PersonalUmbrellaMetaData.BindTab.DOCUMENT_PRINTING_DETAILS.getLabel(), AssetList.class);
	}

	@Override
	protected AssetDescriptor<ComboBox> getSuppressPrint() { return PersonalUmbrellaMetaData.BindTab.SUPPRESS_PRINT; }

}
