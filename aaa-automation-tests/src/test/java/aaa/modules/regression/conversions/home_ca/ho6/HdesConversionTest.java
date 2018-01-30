package aaa.modules.regression.conversions.home_ca.ho6;

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
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest5(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/5.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest6(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion("HO6/6.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewWithLapse1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewWithLapse2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewWithLapse3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewWithLapse4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewWithLapse5(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/5.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewWithLapse6(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewWithLapse("HO6/6.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewAfterPayment1(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/1.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewAfterPayment2(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/2.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewAfterPayment3(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/3.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewAfterPayment4(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/4.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewAfterPayment5(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/5.xml", context);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_CA_HO6)
	public void hdesCAHO6ConversionTest_renewAfterPayment6(@Optional("CA") String state, ITestContext context) {
		hdesCAConversion_renewAfterPayment("HO6/6.xml", context);
	}
}
