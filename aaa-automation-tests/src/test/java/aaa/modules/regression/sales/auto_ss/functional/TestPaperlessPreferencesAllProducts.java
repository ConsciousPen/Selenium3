/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.PAPERLESS_PREFERENCES_ELIGIBILITY_CHECK_FOR_PRODUCT;
import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.PAPERLESS_PREFERENCES_ELIGIBILITY_INSERT_FOR_PRODUCT;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
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
		return PolicyType.AUTO_SS;
	}

	/**
	 * * @author Oleg Stasyuk
	 *
	 * PAS-283
	 *
	 * See detailed steps in template file
	 * {@link TestPaperlessPreferencesAbstract}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-283", "PAS-1451", "PAS-1453", "PAS-1454", "PAS-1740", "PAS-2564"})
	public void pas283_paperlessPreferencesForAllStatesProducts(@Optional("VA") String state) {
		if ("".equals(DBService.get().getValue(String.format(PAPERLESS_PREFERENCES_ELIGIBILITY_CHECK_FOR_PRODUCT, "AAA_SS", state)).orElse(""))) {
			DBService.get().executeUpdate(String.format(PAPERLESS_PREFERENCES_ELIGIBILITY_INSERT_FOR_PRODUCT, "AAA_SS", state));
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		}

		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData td = getPolicyTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath("GeneralTab",
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), "$<today-25d:MM/dd/yyyy>")
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(td);
		pas283_paperlessPreferencesForAllStatesProducts();
	}

	/**
	 * * @author Jovita Pukenaite
	 *
	 * PAS-12458
	 *
	 * See detailed steps in template file
	 * {@link TestPaperlessPreferencesAbstract}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-12458"})
	public void pas12458_documentDeliverySectionDuringEndorsement(@Optional("VA") String state) {
		mainApp().open();
		getCopiedPolicy();
		pas12458_documentDeliverySectionDuringEndorsement();
	}

	/**
	 * * @author Jovita Pukenaite
	 *
	 * PAS-12458
	 *
	 * See detailed steps in template file
	 * {@link TestPaperlessPreferencesAbstract}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-12458"})
	public void pas12458_documentDeliverySectionDataGatherMode(@Optional("VA") String state) {
		mainApp().open();
		getCopiedQuote();
		pas12458_documentDeliverySectionDataGatherMode();
	}

	/**
	 * * @author Oleg Stasyuk
	 *
	 * PAS-266
	 *
	 * See detailed steps in template file
	 * {@link TestPaperlessPreferencesAbstract}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-266")
	public void pas266_PaperlessPreferencesAllTransactions(@Optional("VA") String state) {
		mainApp().open();
		String quoteNumber = getCopiedQuote();
		policy.dataGather().start();
		pas266_PaperlessPreferencesAllTransactionsBody(quoteNumber);
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-266"})
	public void pas266_PaperlessPreferencesAllTransactionsPolicy(@Optional("VA") String state) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		pas266_PaperlessPreferencesAllTransactionsBody(policyNumber);
		Tab.buttonSaveAndExit.click();

		SearchPage.openPolicy(policyNumber);
		policy.renew().start();
		pas266_PaperlessPreferencesAllTransactionsBody(policyNumber);
	}

	/**
	 * * @author Oleg Stasyuk
	 *
	 * PAS-285, PAS-298
	 *
	 * See detailed steps in template file
	 * {@link TestPaperlessPreferencesAbstract}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-285", "PAS-298"})
	public void pas285_PaperlessPreferencesErrorMsg(@Optional("VA") String state) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		pas285_PaperlessPreferencesErrorMsgBody(policyNumber);
		Tab.buttonSaveAndExit.click();
		//PAS-298 Start
		SearchPage.openPolicy(policyNumber);
		policy.renew().start();
		pas285_PaperlessPreferencesErrorMsgBody(policyNumber);
		//PAS-298 end
	}


	@Override
	protected String getDocumentsAndBindTab() {
		return NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get();
	}

	@Override
	protected String getGeneralTab() {
		return NavigationEnum.AutoSSTab.GENERAL.get();
	}

	@Override
	protected String getPremiumAndCoveragesTab() {
		return NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get();
	}

	@Override
	protected InquiryAssetList getInquiryAssetList() {
		return new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), AutoSSMetaData.GeneralTab.class);
	}

	@Override
	protected Tab getDocumentsAndBindTabElement() {
		return new DocumentsAndBindTab();
	}

	@Override
	protected Tab getPremiumAndCoveragesTabElement() {
		return new PremiumAndCoveragesTab();
	}

	@Override
	protected AssetDescriptor<TextBox> getEnrolledInPaperless() { return AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS; }

	@Override
	protected AssetDescriptor<Button> getButtonManagePaperlessPreferences() { return AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES; }

	@Override
	protected AssetDescriptor<Button> getEditPaperlessPreferencesButtonDone() { return AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE; }

	@Override
	protected CommonErrorTab getErrorTabElement() {
		return new ErrorTab();
	}

	@Override
	public AssetList getPaperlessPreferencesAssetList() {
		return new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PAPERLESS_PREFERENCES.getLabel(), AssetList.class);
	}

	@Override
	protected AssetDescriptor<ComboBox> getMethodOfDelivery() { return AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY; }

	@Override
	protected AssetDescriptor<ComboBox> getIncludeWithEmail() { return AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL; }

	@Override
	protected AssetDescriptor<RadioGroup> getApplyeValueDiscount() {
		return AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT;
	}

	@Override
	protected AssetDescriptor<TextBox> getIssueDate() { return AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE; }

	@Override
	public AssetList getDocumentPrintingDetailsAssetList() {
		return new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENT_PRINTING_DETAILS.getLabel(), AssetList.class);
	}

	@Override
	protected AssetDescriptor<ComboBox> getSuppressPrint() { return AutoSSMetaData.DocumentsAndBindTab.SUPPRESS_PRINT; }

}
