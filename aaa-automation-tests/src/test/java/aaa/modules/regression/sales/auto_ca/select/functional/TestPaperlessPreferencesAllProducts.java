/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select.functional;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.PAPERLESS_PREFERENCES_ELIGIBILITY_CHECK_FOR_PRODUCT;
import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.PAPERLESS_PREFERENCES_ELIGIBILITY_INSERT_FOR_PRODUCT;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.regression.sales.template.functional.TestPaperlessPreferencesAbstract;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestPaperlessPreferencesAllProducts extends TestPaperlessPreferencesAbstract {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-283", "PAS-1451", "PAS-1453", "PAS-1454", "PAS-1740", "PAS-2564"})
	public void pas283_paperlessPreferencesForAllStatesProducts(@Optional("CA") String state) {
		if(DBService.get().getValue(String.format(PAPERLESS_PREFERENCES_ELIGIBILITY_CHECK_FOR_PRODUCT, "AAA_CSA", state)).orElse("").equals("")) {
			DBService.get().executeUpdate(String.format(PAPERLESS_PREFERENCES_ELIGIBILITY_INSERT_FOR_PRODUCT, "AAA_CSA", state));
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		}

		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData td = getPolicyTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath("GeneralTab",
						AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), "$<today-25d:MM/dd/yyyy>")
				.adjust(AutoCaMetaData.ErrorTab.class.getSimpleName(), tdError);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(td);
		pas283_paperlessPreferencesForAllStatesProducts();
	}

	@Override
	protected String getDocumentsAndBindTab() {
		return NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get();
	}

	@Override
	protected InquiryAssetList getInquiryAssetList() {
		return new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), AutoCaMetaData.GeneralTab.class);
	}

	@Override
	protected Tab getDocumentsAndBindTabElement() {
		return new DocumentsAndBindTab();
	}

	@Override
	protected AssetDescriptor<TextBox> getEnrolledInPaperless() { return AutoCaMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS; }

	@Override
	protected AssetDescriptor<Button> getButtonManagePaperlessPreferences() { return AutoCaMetaData.DocumentsAndBindTab.PaperlessPreferences.BTN_MANAGE_PAPERLESS_PREFERENCES; }

	@Override
	protected AssetDescriptor<Button> getEditPaperlessPreferencesButtonDone() { return AutoCaMetaData.DocumentsAndBindTab.PaperlessPreferences.EDIT_PAPERLESS_PREFERENCES_BTN_DONE; }

	@Override
	public AssetList getPaperlessPreferencesAssetList() {
		return new DocumentsAndBindTab().getAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.PAPERLESS_PREFERENCES.getLabel(), AssetList.class);
	}

	@Override
	protected AssetDescriptor<ComboBox> getMethodOfDelivery() { return AutoCaMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY; }

	@Override
	protected AssetDescriptor<ComboBox> getIncludeWithEmail() { return AutoCaMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL; }

	@Override
	protected AssetDescriptor<TextBox> getIssueDate() { return AutoCaMetaData.DocumentsAndBindTab.DocumentPrintingDetails.ISSUE_DATE; }

	@Override
	public AssetList getDocumentPrintingDetailsAssetList() {
		return new DocumentsAndBindTab().getAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.DOCUMENT_PRINTING_DETAILS.getLabel(), AssetList.class);
	}

	@Override
	protected AssetDescriptor<ComboBox> getSuppressPrint() { return AutoCaMetaData.DocumentsAndBindTab.SUPPRESS_PRINT; }

}