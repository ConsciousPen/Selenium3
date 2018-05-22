/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestInstallmentFeesPopUpAndSavingsMsgAbstract;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestInstallmentFeesPopUpAndSavingsMsg extends TestInstallmentFeesPopUpAndSavingsMsgAbstract {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = {"PAS-246", "PAS-237"})
	public void pas246_InstallmentFeesPopUpAndSavingsMsg(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		createQuote();
		policy.dataGather().start();

		pas246_InstallmentFeesPopUpAndSavingsMsg();
	}

	@Override
	protected void openInstallmentFeeTable() {
		PremiumAndCoveragesTab.linkPaymentPlan.click();
		PremiumAndCoveragesTab.linkViewApplicableFeeSchedule.click();
	}

	@Override
	protected void navigateAndRate() {
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		premiumAndCoveragesTab.calculatePremium();
	}

	@Override
	protected String getGeneralTab() {
		return NavigationEnum.AutoCaTab.GENERAL.get();
	}


	@Override
	protected String getPremiumAndCoverageTab() {
		return NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get();
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
	protected Tab getGeneralTabElement() {
		return new GeneralTab();
	}

	@Override
	protected Tab getPremiumAndCoverageTabElement() {
		return new PremiumAndCoveragesTab();
	}


	@Override
	protected Tab getDocumentsAndBindElement() {
		return new DocumentsAndBindTab();
	}

	@Override
	protected Tab getPurchaseTabElement() {
		return new PurchaseTab();
	}

	@Override
	protected AssetDescriptor<ComboBox> getPaymentPlanComboBox() {
		return AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN;
	}

	@Override
	protected AssetDescriptor<FillableTable> getInstallmentFeesDetailsTable() {
		return AutoCaMetaData.PremiumAndCoveragesTab.INSTALLMENT_FEES_DETAILS_TABLE;
	}

}
