package aaa.modules.regression.conversions.home_ca.ho3;

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
	public void hdesCAHO3ConversionTest(@Optional("CA") String state, @Optional("") String file) {
		hdesCAConversion(file, context);
	}

	@Parameters({"state", "file"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewWithLapse(@Optional("CA") String state, String file) {
		hdesCAConversion_renewWithLapse(file, context);
	}

	@Parameters({"state", "file"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewAfterPayment(@Optional("CA") String state, String file) {
		hdesCAConversion_renewAfterPayment(file, context);
	}
}
