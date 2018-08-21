package aaa.modules.regression.conversions.home_ca.ho4;

import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.conversions.home_ca.HdesConversionTestTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class HdesConversionTest extends HdesConversionTestTemplate {

	@Parameters({"state", "file"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO4ConversionTest(@Optional("CA") String state, String file, ITestContext context) {
		hdesCAConversion(file, context);
	}

	@Parameters({"state", "file"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO4ConversionTest_renewWithLapse(@Optional("CA") String state, String file, ITestContext context) {
		hdesCAConversion_renewWithLapse(file, context);
	}

	@Parameters({"state", "file"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO4ConversionTest_renewAfterPayment(@Optional("CA") String state, String file, ITestContext context) {
		hdesCAConversion_renewAfterPayment(file, context);
	}
}
