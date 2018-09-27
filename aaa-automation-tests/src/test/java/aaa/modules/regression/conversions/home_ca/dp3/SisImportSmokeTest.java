package aaa.modules.regression.conversions.home_ca.dp3;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.SisConversionData;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class SisImportSmokeTest extends HomeCaDP3BaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.SMOKE, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_DP3)
	public void sisCADP3ImportTest(@Optional("CA") String state) {

		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new SisConversionData("20170525_012421_CNV_B_CADP3_EXGPAS_8435_D_20170523_30000471_3669264.xml", effDate);
		String policyNum = ConversionUtils.importPolicy(data, context, false);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}
}
