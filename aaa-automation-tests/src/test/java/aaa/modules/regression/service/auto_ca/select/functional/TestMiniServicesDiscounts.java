package aaa.modules.regression.service.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.format.DateTimeFormatter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.HelperMiniServices;
import aaa.modules.regression.service.helper.TestMiniServicesDriversHelper;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;

public class TestMiniServicesDiscounts extends AutoCaSelectBaseTest {
	private static final String DISCOUNT_CODE_GSD = "GSD";
	private static final String DISCOUNT_CODE_NDD = "NDD";
	private static final String DISCOUNT_CODE_MDD = "MDD";
	private final HelperMiniServices helperMiniServices = new HelperMiniServices();
	private final static TestMiniServicesDriversHelper DRIVERS_HELPER = new TestMiniServicesDriversHelper();

	/**
	 * @author Maris Strazds
	 * @name View Driver Service - CA Select and Driver Related Discounts - Good Student Discount, New Driver Discount, does not have Mature Driver Discount
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create Endorsement through service
	 * 3. Add drivers and check that they have/have not Good Student Discount, New Driver Discount, does not have Mature Driver Discount
	 * 4. Check that for drivers that already had these discounts applied at NB, they are not returned as Available
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-27988"})
	public void pas27988_miniServicesDiscounts(@Optional("CA") String state) {
		TestData td = getPolicyDefaultTD();
		TestData testData = td.
				adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Discounts").getTestDataList("DriverTab")).
				adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_Discounts").getTestDataList("VehicleTab")).
				adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_Discounts").getTestData("AssignmentTab")).
				adjust(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(),
						AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
						AutoCaMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_NEW_DRIVER_COURSE_COMPLETION.getLabel()),
						"Yes").
				adjust(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(),
						AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
						AutoCaMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_MATURE_DRIVER_COURSE_COMPLETION.getLabel()),
						"Yes").
				resolveLinks();

		String policyNumber = openAppAndCreatePolicy(testData);
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add driver LESS THAN 26y old and Single
		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("Jarred", "", "Benjami", TimeSetterUtil.getInstance().getCurrentTime().minusYears(27).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "I");
		DriversDto addDriverResponse1 = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "B1234560",
				16, "CA", "CH", "S", true);
		HelperCommon.updateDriver(policyNumber, addDriverResponse1.oid, updateDriverRequest);

		//Add driver LESS THAN 26y old and other than Single
		addDriverRequest = DXPRequestFactory.createAddDriverRequest("Tom", "", "Ao", TimeSetterUtil.getInstance().getCurrentTime().minusYears(27).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "I");
		DriversDto addDriverResponse2 = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "B1234561",
				16, "CA", "CH", "M", true);
		HelperCommon.updateDriver(policyNumber, addDriverResponse2.oid, updateDriverRequest);

		//Add driver MORE THAN 26y old and Single
		addDriverRequest = DXPRequestFactory.createAddDriverRequest("Tim", "", "Bo", TimeSetterUtil.getInstance().getCurrentTime().minusYears(25).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "I");
		DriversDto addDriverResponse3 = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "B1234562",
				16, "CA", "CH", "S", true);
		HelperCommon.updateDriver(policyNumber, addDriverResponse3.oid, updateDriverRequest);

		//Add driver MORE THAN 26y and less than 50y old and Single
		addDriverRequest = DXPRequestFactory.createAddDriverRequest("Tim", "", "Co", TimeSetterUtil.getInstance().getCurrentTime().minusYears(49).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "I");
		DriversDto addDriverResponse4 = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "B1234563",
				16, "CA", "CH", "S", true);
		HelperCommon.updateDriver(policyNumber, addDriverResponse4.oid, updateDriverRequest);

		//Add driver MORE THAN 26y and LESS THAN 50y old and OTHER THAN Single
		addDriverRequest = DXPRequestFactory.createAddDriverRequest("Paul", "", "Do", TimeSetterUtil.getInstance().getCurrentTime().minusYears(49).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "I");
		DriversDto addDriverResponse5 = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "B1234564",
				16, "CA", "CH", "M", true);
		HelperCommon.updateDriver(policyNumber, addDriverResponse5.oid, updateDriverRequest);

		//Add driver 50y old or older
		addDriverRequest = DXPRequestFactory.createAddDriverRequest("James", "", "Fo", TimeSetterUtil.getInstance().getCurrentTime().minusYears(50).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "I");
		DriversDto addDriverResponse6 = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "B1234565",
				16, "CA", "CH", "S", false);
		HelperCommon.updateDriver(policyNumber, addDriverResponse6.oid, updateDriverRequest);

		ViewDriversResponse viewEndorsementDrivers = HelperCommon.viewEndorsementDrivers(policyNumber);
		//Driver 1 - Check that have Good Student Discount, New Driver Discount, does not have Mature Driver Discount
		driverAvailableDiscountsCheck(viewEndorsementDrivers, DISCOUNT_CODE_GSD, "Good Student Discount", addDriverResponse1.oid);
		driverAvailableDiscountsCheck(viewEndorsementDrivers, DISCOUNT_CODE_NDD, "New Driver Discount", addDriverResponse1.oid);
		assertThat(helperMiniServices.findDriver(viewEndorsementDrivers, addDriverResponse1.oid).availableDiscounts.size()).isEqualTo(2);

		//Driver 2 - Check that does not have Good Student Discount, New Driver Discount, Mature Driver Discount
		checkThatDriverDoesNotHaveAvailableDiscounts(addDriverResponse2.oid, viewEndorsementDrivers);

		//Driver 3 - Check that does not have Good Student Discount, New Driver Discount, Mature Driver Discount
		checkThatDriverDoesNotHaveAvailableDiscounts(addDriverResponse3.oid, viewEndorsementDrivers);

		//Driver 4 - Check that does not have Good Student Discount, New Driver Discount, Mature Driver Discount
		checkThatDriverDoesNotHaveAvailableDiscounts(addDriverResponse4.oid, viewEndorsementDrivers);

		//Driver 5 - Check that does not have Good Student Discount, New Driver Discount, Mature Driver Discount
		checkThatDriverDoesNotHaveAvailableDiscounts(addDriverResponse5.oid, viewEndorsementDrivers);

		//Driver 6 - Check that does not have Good Student Discount, New Driver Discount. Has Mature Driver Discount
		driverAvailableDiscountsCheck(viewEndorsementDrivers, DISCOUNT_CODE_MDD, "Mature Driver Discount", addDriverResponse6.oid);
		assertThat(helperMiniServices.findDriver(viewEndorsementDrivers, addDriverResponse1.oid).availableDiscounts.size()).isEqualTo(1);

		//Check that for drivers that had discounts already applied at NB, there are no availableDiscounts for them
		DriversDto driverWithDiscounts1 = DRIVERS_HELPER.findDriverByLicenseNumber(viewEndorsementDrivers, "C1234569");//license number from TD
		DriversDto driverWithDiscounts2 = DRIVERS_HELPER.findDriverByLicenseNumber(viewEndorsementDrivers, "C1234568");//license number from TD
		checkThatDriverDoesNotHaveAvailableDiscounts(driverWithDiscounts1.oid, viewEndorsementDrivers);
		checkThatDriverDoesNotHaveAvailableDiscounts(driverWithDiscounts2.oid, viewEndorsementDrivers);

		//TODO-mstrazds: works - if no, bind manually and check. Should work when assignments are done - when?
		helperMiniServices.endorsementRateAndBind(policyNumber);
		DiscountSummary policyDiscountsResponse = HelperCommon.viewDiscounts(policyNumber, "policy", 200);
		assertThat(policyDiscountsResponse).isEqualTo(viewEndorsementDrivers);
	}

	private void driverAvailableDiscountsCheck(ViewDriversResponse viewDriversResponse, String discountCode, String discountName, String driverOid) {
		DriversDto driver = helperMiniServices.findDriver(viewDriversResponse, driverOid);
		DiscountInfo discount = driver.availableDiscounts.stream().filter(disc -> discountCode.equals(disc.discountCd)).filter(disc -> driverOid.equals(disc.oid)).findFirst().orElseThrow(() -> new IstfException("no such discount"));
		assertThat(discount.discountCd).isEqualTo(discountCode);
		assertThat(discount.discountName).isEqualTo(discountName);
		assertThat(discount.oid.equals(driverOid)).isTrue();
	}

	private void checkThatDriverDoesNotHaveAvailableDiscounts(String driverOid, ViewDriversResponse viewEndorsementDrivers) {
		DriversDto driver = helperMiniServices.findDriver(viewEndorsementDrivers, driverOid);
		assertThat(driver.availableDiscounts).as("Driver with this oid should not have any available discounts: " + driverOid).isEmpty();
	}
}
