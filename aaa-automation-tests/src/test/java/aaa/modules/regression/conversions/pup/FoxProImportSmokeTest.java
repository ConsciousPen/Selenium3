package aaa.modules.regression.conversions.pup;

import java.time.LocalDateTime;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.FoxProConversionData;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import toolkit.utils.TestInfo;

public class FoxProImportSmokeTest extends FoxProConversionTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.PUP)
	public void foxProCAPUPImportTest(@Optional("CA") String state) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		mainApp().open();
		createCustomerIndividual();
		Map<String, String> policies = getPrimaryPoliciesForPup();
		ConversionPolicyData data = new FoxProConversionData("1.xml", effDate, policies.get("Primary_HO3"), policies.get("Primary_Auto"));
		String policyNum = ConversionUtils.importPolicy(data, context, false);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		fillPolicy();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}
}
