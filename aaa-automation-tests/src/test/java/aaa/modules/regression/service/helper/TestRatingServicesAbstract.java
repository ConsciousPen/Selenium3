package aaa.modules.regression.service.helper;

import aaa.common.Tab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestRatingServicesAbstract extends PolicyBaseTest {

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getVehicleTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract Tab getVehicleTabElement();

	protected abstract AssetDescriptor<Button> getCalculatePremium();

	protected void pas10302_discountPercentageRetrieveBody(String lob, String usState, String coverageCd, String expectedValue) {
		HelperCommon.executeDiscountPercentageRetrieveRequest(lob, usState, coverageCd, expectedValue);
	}
}
