/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.modules.regression.sales.template.functional.TestPaperlessPreferencesAbstract;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
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
		return PolicyType.HOME_CA_HO3;
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
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = {"PAS-283", "PAS-1451", "PAS-1453", "PAS-1454", "PAS-1740", "PAS-2564"})
	public void pas283_paperlessPreferencesForAllStatesProducts(@Optional("CA") String state) {
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData tdHome = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO3).getTestData("DataGather"), "TestData")
				.adjust(TestData.makeKeyPath("GeneralTab", HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()), "$<today-25d:MM/dd/yyyy>")
				.adjust(TestData.makeKeyPath("GeneralTab", HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel()), "$<today-25d:MM/dd/yyyy>")
				.adjust(HomeCaMetaData.ErrorTab.class.getSimpleName(), tdError);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(tdHome);

		pas283_paperlessPreferencesForAllStatesProducts();
	}

	/**
	 * * @author Jovita Pukenaite
	 *
	 * PAS-15994
	 *
	 * See detailed steps in template file
	 * {@link TestPaperlessPreferencesAbstract}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = {"PAS-15994"})
	public void pas15994_documentDeliverySectionDuringEndorsement(@Optional("CA") String state) {
		mainApp().open();
		getCopiedPolicy();
		pas12458_documentDeliverySectionDuringEndorsement();
	}

	/**
	 * * @author Jovita Pukenaite
	 *
	 * PAS-15994
	 *
	 * See detailed steps in template file
	 * {@link TestPaperlessPreferencesAbstract}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = {"PAS-15994"})
	public void pas15994_documentDeliverySectionDataGatherMode(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		createQuote();
		pas12458_documentDeliverySectionDataGatherMode();
	}

	@Override
	protected String getDocumentsAndBindTab() {
		return NavigationEnum.HomeCaTab.BIND.get();
	}

	@Override
	protected String getGeneralTab() {
		return NavigationEnum.HomeCaTab.GENERAL.get();
	}

	@Override
	protected String getPremiumAndCoveragesTab() {
		return NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get();
	}

	@Override
	protected InquiryAssetList getInquiryAssetList() {
		return new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), HomeCaMetaData.GeneralTab.class);
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
	protected AssetDescriptor<TextBox> getEnrolledInPaperless() { return HomeCaMetaData.BindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS; }

	@Override
	protected AssetDescriptor<Button> getButtonManagePaperlessPreferences() { return HomeCaMetaData.BindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES; }

	@Override
	protected AssetDescriptor<Button> getEditPaperlessPreferencesButtonDone() { return HomeCaMetaData.BindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE; }

	@Override
	protected CommonErrorTab getErrorTabElement() {
		return new ErrorTab();
	}

	@Override
	public AssetList getPaperlessPreferencesAssetList() {
		return new DocumentsAndBindTab().getAssetList().getAsset(HomeCaMetaData.BindTab.PAPERLESS_PREFERENCES.getLabel(), AssetList.class);
	}

	@Override
	protected AssetDescriptor<ComboBox> getMethodOfDelivery() { return HomeCaMetaData.BindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY; }

	@Override
	protected AssetDescriptor<ComboBox> getIncludeWithEmail() { return HomeCaMetaData.BindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL; }

	@Override
	protected AssetDescriptor<RadioGroup> getApplyeValueDiscount() {
		return null;
	}

	@Override
	protected AssetDescriptor<TextBox> getIssueDate() { return HomeCaMetaData.BindTab.DocumentPrintingDetails.ISSUE_DATE; }

	@Override
	public AssetList getDocumentPrintingDetailsAssetList() {
		return new DocumentsAndBindTab().getAssetList().getAsset(HomeCaMetaData.BindTab.DOCUMENT_PRINTING_DETAILS.getLabel(), AssetList.class);
	}

	@Override
	protected AssetDescriptor<ComboBox> getSuppressPrint() { return HomeCaMetaData.BindTab.SUPPRESS_PRINT; }

}
