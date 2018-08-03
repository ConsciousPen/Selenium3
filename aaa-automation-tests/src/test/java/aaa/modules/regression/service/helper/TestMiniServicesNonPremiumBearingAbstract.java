package aaa.modules.regression.service.helper;

import aaa.common.Tab;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesNonPremiumBearingAbstract extends PolicyBaseTest {


	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getVehicleTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract Tab getVehicleTabElement();

	protected abstract AssetDescriptor<JavaScriptButton> getCalculatePremium();

}
