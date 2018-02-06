package aaa.modules.regression.conversions.home_ca.ho3;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.conversions.home_ca.HdesConversionTestTemplate;
import toolkit.utils.TestInfo;
import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class HdesConversionTest extends HdesConversionTestTemplate {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO3/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO3/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO3/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO3/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewWithLapse1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO3/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewWithLapse2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO3/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewWithLapse3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO3/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewWithLapse4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO3/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewAfterPayment1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO3/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewAfterPayment2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO3/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewAfterPayment3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO3/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO3)
	public void hdesCAHO3ConversionTest_renewAfterPayment4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO3/4.xml", context);
	}
}
