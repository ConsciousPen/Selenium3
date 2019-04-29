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
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.HelperMiniServices;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;

public class TestMiniServicesDiscounts extends AutoCaSelectBaseTest {
	private static final String DISCOUNT_CODE_GSD = "GSD";
	private static final String DISCOUNT_CODE_NDD = "NDD";
	private static final String DISCOUNT_CODE_MDD = "MDD";
	private final HelperMiniServices helperMiniServices = new HelperMiniServices();

	/**
	 * @author Maris Strazds
	 * @name View Driver Service - CA Select and Driver Related Discounts - Good Student Discount, New Driver Discount, does not have Mature Driver Discount
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create Endorsement through service
	 * 3. Add drivers and check that they have/have not Good Student Discount, New Driver Discount, does not have Mature Driver Discount
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-27988"})
	public void pas27988_miniServicesDiscounts(@Optional("CA") String state) {
		String policyNumber = getCopiedPolicy();

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
				16, "CA", "CH", "S", true);
		HelperCommon.updateDriver(policyNumber, addDriverResponse6.oid, updateDriverRequest);

		DiscountSummary endorsementDiscountsResponse = HelperCommon.viewDiscounts(policyNumber, "endorsement", 200);
		//Driver 1 - Check that have Good Student Discount, New Driver Discount, does not have Mature Driver Discount
		driverLevelDiscountsCheck(endorsementDiscountsResponse, DISCOUNT_CODE_GSD, "Good Student Discount", addDriverResponse1.oid);
		driverLevelDiscountsCheck(endorsementDiscountsResponse, DISCOUNT_CODE_NDD, "New Driver Discount", addDriverResponse1.oid);
		driverHasNotDiscount(endorsementDiscountsResponse, DISCOUNT_CODE_MDD, addDriverResponse1.oid);

		//Driver 2 - Check that does not have Good Student Discount, New Driver Discount, Mature Driver Discount
		checkThatDriverDoesNotHaveTheseDiscounts(addDriverResponse2, endorsementDiscountsResponse);

		//Driver 3 - Check that does not have Good Student Discount, New Driver Discount, Mature Driver Discount
		checkThatDriverDoesNotHaveTheseDiscounts(addDriverResponse3, endorsementDiscountsResponse);

		//Driver 4 - Check that does not have Good Student Discount, New Driver Discount, Mature Driver Discount
		checkThatDriverDoesNotHaveTheseDiscounts(addDriverResponse4, endorsementDiscountsResponse);

		//Driver 5 - Check that does not have Good Student Discount, New Driver Discount, Mature Driver Discount
		checkThatDriverDoesNotHaveTheseDiscounts(addDriverResponse5, endorsementDiscountsResponse);

		//Driver 5 - Check that does not have Good Student Discount, New Driver Discount. Has Mature Driver Discount
		driverHasNotDiscount(endorsementDiscountsResponse, DISCOUNT_CODE_GSD, addDriverResponse6.oid);
		driverHasNotDiscount(endorsementDiscountsResponse, DISCOUNT_CODE_NDD, addDriverResponse6.oid);
		driverLevelDiscountsCheck(endorsementDiscountsResponse, DISCOUNT_CODE_MDD, "Mature Driver Discount", addDriverResponse6.oid);

		//TODO-mstrazds: works - if no, bind manually and check
		helperMiniServices.endorsementRateAndBind(policyNumber);
		DiscountSummary policyDiscountsResponse = HelperCommon.viewDiscounts(policyNumber, "policy", 200);
		assertThat(policyDiscountsResponse).isEqualTo(endorsementDiscountsResponse);

	}

	private void driverLevelDiscountsCheck(DiscountSummary policyDiscountsResponse, String discountCode, String discountName, String oid) {
		DiscountInfo discount = policyDiscountsResponse.driverDiscounts.stream().filter(disc -> discountCode.equals(disc.discountCd)).filter(disc -> oid.equals(disc.oid)).findFirst().orElseThrow(() -> new IstfException("no such discount"));
		assertThat(discount.discountCd).isEqualTo(discountCode);
		assertThat(discount.discountName).isEqualTo(discountName);
		assertThat(discount.oid.equals(oid)).isTrue();
	}

	private void driverHasNotDiscount(DiscountSummary policyDiscountsResponse, String discountCode, String oid) {
		assertThat(policyDiscountsResponse.driverDiscounts.stream().filter(disc -> discountCode.equals(disc.discountCd)).filter(driver -> oid.equals(driver.oid)).findFirst().orElse(null))
				.as("Discount with code " + discountCode + "is not expected for driver with oid " + oid + ".").isNull();
	}

	private void checkThatDriverDoesNotHaveTheseDiscounts(DriversDto driver, DiscountSummary policyDiscountsResponse) {
		driverHasNotDiscount(policyDiscountsResponse, DISCOUNT_CODE_GSD, driver.oid);
		driverHasNotDiscount(policyDiscountsResponse, DISCOUNT_CODE_NDD, driver.oid);
		driverHasNotDiscount(policyDiscountsResponse, DISCOUNT_CODE_MDD, driver.oid);
	}
}
