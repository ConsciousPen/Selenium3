package aaa.modules.regression.sales.pup.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestOffCycleBillNoInstallmentDateAbstract;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestOffCycleInvoiceNoInstallmentDate extends TestOffCycleBillNoInstallmentDateAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Override
	protected PurchaseTab getPurchaseTab() {
		return new PurchaseTab();
	}

	@Override
	protected BindTab getBindTab() {
		return new BindTab();
	}

	@Override
	protected PremiumAndCoveragesQuoteTab getPremiumAndCoveragesTab() {
		return new PremiumAndCoveragesQuoteTab();
	}

	@Override
	protected void navigateToPremiumAndCoveragesTab() {
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
	}

	@Override
	protected void navigateToBindTab() {
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
	}

	@Override
	protected void adjustPremiumBearingValue() {

	}

	@Override
	protected AssetDescriptor<JavaScriptButton> getCalculatePremiumButton() {
		return null;
	}

}
