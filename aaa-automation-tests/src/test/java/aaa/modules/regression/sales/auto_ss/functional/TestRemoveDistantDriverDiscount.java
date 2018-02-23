package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import javax.xml.datatype.DatatypeConfigurationException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.soap.GetAutoPolicyDetailsHelper;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.soap.autopolicy.models.wsdl.ErrorInfo;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetailResponse;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestRemoveDistantDriverDiscount extends AutoSSBaseTest {

	private DriverTab driverTab = new DriverTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	/**
	* @author Dominykas Razgunas
	* @name Remove Distant Driver Discaount during mid term endorsement
	* @scenario
	* 1. Create quote
	* 2. Add Driver with Distant driver discount
	* 3. Issue Policy
	 * 4. Endorse Policy
	 * 5. Remove Distant Driver
	 * 6. Calculate Premium
	 * 7. Check if Distant Driver Discount is applied
	* @details
	*/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-8915")
	public void pas8915_removeDistantDriverDiscount(@Optional("NJ") String state) {

		TestData testData = getPolicyDefaultTD();

		//  Create Customer
		mainApp().open();
		createCustomerIndividual();

		//  Create Quote and fill up to Drivers Tab
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, DriverTab.class, true);

		// Add a driver with discount
		driverTab.fillTab(getTestSpecificTD("TestData_Driver"));
		driverTab.submitTab();

		// Fill quote up to Premium and Coverages Tab Check if discount is applied
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue().contains("Distant Student Discount(Angel FromEarth)");
		premiumAndCoveragesTab.submitTab();

		// Issue Policy
		policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, PurchaseTab.class,true);
		purchaseTab.submitTab();

		// Endorse Policy and Remove the eligibility for DSD
		policy.endorse().perform(getTestSpecificTD("TestData_Endorse"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab.viewDriver(2);
		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DISTANT_STUDENT.getLabel()).setValue("No");

		// Calculate Premium and check that there is no Discount applied
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue().contains("Distant Student Discount(Angel FromEarth)")).isFalse();



	}
}
