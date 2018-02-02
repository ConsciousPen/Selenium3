package aaa.modules.regression.conversions.home_ca.ho4;

import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.conversions.home_ca.HdesConversionTestTemplate;
import toolkit.utils.TestInfo;

public class HdesConversionTest extends HdesConversionTestTemplate {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO4/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO4/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO4/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest_renewWithLapse1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO4/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest_renewWithLapse2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO4/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest_renewWithLapse3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO4/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest_renewAfterPayment1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO4/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest_renewAfterPayment2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO4/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO4)
	public void hdesCAHO4ConversionTest_renewAfterPayment3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO4/3.xml", context);
	}
}
