package aaa.modules.regression.conversions.home_ca.ho3;

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
import aaa.helpers.conversion.HdesConversionData;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class HdesImportSmokeTest extends HomeCaHO3BaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.SMOKE, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ImportTest(@Optional("CA") String state) {
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		ConversionPolicyData data = new HdesConversionData("HO3/20170808_045542_CNV_B_HDESPROP_EXGPAS_8435_D_20170908_30000506_XE65055.xml", effDate);
		String policyNum = ConversionUtils.importPolicy(data, context, false);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}
}