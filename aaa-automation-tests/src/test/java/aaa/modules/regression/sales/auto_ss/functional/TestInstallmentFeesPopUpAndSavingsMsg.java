/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestInstallmentFeesPopUpAndSavingsMsgAbstract;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestInstallmentFeesPopUpAndSavingsMsg extends TestInstallmentFeesPopUpAndSavingsMsgAbstract {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}


	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-246", "PAS-237"})
	public void pas246_InstallmentFeesPopUpAndSavingsMsg(@Optional("VA") String state) {
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
		return NavigationEnum.AutoSSTab.GENERAL.get();
	}

	@Override
	protected String getPremiumAndCoverageTab() {
		return NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get();
	}

	@Override
	protected String getDocumentsAndBindTab() {
		return NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get();
	}

	@Override
	protected InquiryAssetList getInquiryAssetList() {
		return new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), AutoSSMetaData.GeneralTab.class);
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
		return AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN;
	}

	@Override
	protected AssetDescriptor<FillableTable> getInstallmentFeesDetailsTable() {
		return AutoSSMetaData.PremiumAndCoveragesTab.INSTALLMENT_FEES_DETAILS_TABLE;
	}

}
