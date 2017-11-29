package aaa.modules.regression.sales.auto_ss.functional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestAutoPolicyLockPreConditions;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestAutoPoliciesLock extends AutoSSBaseTest implements TestAutoPolicyLockPreConditions {

	private static final String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ISO_DATE);
	private static final String lookUpId = "(SELECT ll.id FROM lookupList ll WHERE ll.lookupName LIKE '%AAAFactorsLockLookup')";
	private static final String toCurrentDate = "to_date('" + currentDate + "', 'YYYY-MM-DD')";

	/**
	 * @author Lev Kazarnovskiy
	 * <p>
	 * PAS-2247, PAS-2248 - Lock Membership and Auto Ins Persistency, Not at Fault and Comp Claims
	 * @name Test VINupload 'Update VIN' scenario.
	 * @scenario 0. Create customer
	 * 1. Configure lock for AIP and NAF  in DB
	 * 2. Initiate Auto SS quote creation
	 * 3. Note the values for CC, NAF and AIP in VRD on Premium&Coverages screen
	 * 4. Initiate Renewal for quote
	 * 5. Verify that NAF and AIP values are locked (does not incremented) and CC value is increased
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2247")
	public void pas2247_pas2248_AipAndNafLock(@Optional("CT") String state) {

		TestData testData = getAdjustedTD();

		setLockForTheElement("numberNAFAccident");
		setLockForTheElement("autoInsurancePersistency");

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		//Save the values of listed items to compare them with values on Renewal Later
		String previousCCValue = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
				.getRow(4, "Number of Comprehensive Claims").getCell(5).getValue();

		String previousNAFValue = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
				.getRow(4, "Number of Not-At-Fault Accidents").getCell(5).getValue();

		String previousAIPValue = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
				.getRow(1, "AAA Insurance Persistency").getCell("Value").getValue();

		//Close rating details pop-up, issue the policy, initiate renewal and verify items values in VRD
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		new PremiumAndCoveragesTab().submitTab();

		overrideErrorsAndBind(testData);
		policy.renew().start();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		new DriverTab().fillTab(testData);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		//Verify that values of NAF and AIP are locked and not changed in VRD. Verify that CC values is increased
		CustomAssert.enableSoftMode();

		PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Number of Not-At-Fault Accidents").getCell(5)
				.verify.value("Number of Not-At-Fault Accidents is not locked on Renewal", previousNAFValue);
		PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, "AAA Insurance Persistency").getCell("Value")
				.verify.value("Value for AAA Insurance Persistency is not locked on Renewal", previousAIPValue);
		CustomAssert.assertFalse("Number of Comprehensive Claims is locked on Renewal, but shouldn't",
				previousCCValue.equals(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Number of Comprehensive Claims").getCell(5).getValue()));
		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();

		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}

	@AfterMethod(alwaysRun = true)
	private void cleanDB() {
		//Restore lock parameters in DB to default values
		deleteLockForTheElement("numberNAFAccident");
		deleteLockForTheElement("autoInsurancePersistency");
	}

	private TestData getAdjustedTD() {
		String driverTabSimpleName = new DriverTab().getMetaKey();
		String generalTabSimpleName = new GeneralTab().getMetaKey();
		String namedInsuredInformationSection = AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel();

		//Adjust data for DriverTab.
		List<TestData> driverTabAdjustment = new ArrayList<>();
		driverTabAdjustment.add(getPolicyTD().getTestData(driverTabSimpleName)
				.adjust(getTestSpecificTD("TestData").resolveLinks()));

		//Adjust data for Base Date field on General Tab
		List<TestData> baseDateAdjustment = new ArrayList<>();
		baseDateAdjustment.add(getPolicyTD().getTestData(generalTabSimpleName).getTestDataList(namedInsuredInformationSection).get(0)
				.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel(), "/today-1y")
				.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(), "Derek")
				.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(), "Martin"));

		return getPolicyTD().adjust(TestData.makeKeyPath(generalTabSimpleName, namedInsuredInformationSection), baseDateAdjustment)
				.adjust(driverTabSimpleName, driverTabAdjustment);
	}

	private void overrideErrorsAndBind(TestData testData) {
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		PurchaseTab purchaseTab = new PurchaseTab();

		policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
		documentsAndBindTab.submitTab();
		new ErrorTab().overrideAllErrors();
		documentsAndBindTab.submitTab();
		purchaseTab.fillTab(testData);
		purchaseTab.submitTab();
	}

	private void setLockForTheElement(String elementName) {
		int i = DBService.get().executeUpdate(String.format(INSERT_QUERY, lookUpId, elementName, toCurrentDate, getState()));
		CustomAssert.assertTrue("values should be inserted ", i > 0);
	}

	private void deleteLockForTheElement(String elementName) {
		DBService.get().executeUpdate(String.format(DELETE_QUERY, lookUpId, elementName, toCurrentDate, getState()));
	}
}

