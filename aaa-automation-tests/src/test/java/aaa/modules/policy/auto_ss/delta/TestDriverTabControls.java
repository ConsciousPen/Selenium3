/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss.delta;

import java.util.Arrays;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

public class TestDriverTabControls extends AutoSSBaseTest {

	/**
	 * @author Dmitry Chubkov
	 * @name Driver tab controls check for AutoSS product, CO state [TC02]
	 * @scenario
	 * 1. Create customer
	 * 2. Initiate AutoSS quote creation
	 * 3. Move to Driver tab
	 * 4. Add second drive
	 * 5. Verify following Marital Statuses available for CO: "Registered Domestic Partner/Civil Union", "Common Law"
	 * 6. Verify Dropdown Values in Driver tab
	 *
	 * @details
	 */
	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testDriverInformationControlsOnDriverTab() {
		DriverTab dTab = new DriverTab();

		mainApp().open();

		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_TC02"), DriverTab.class, true);

		CustomAssert.enableSoftMode();
		dTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Available for Rating", "Not Available for Rating", "Excluded"));

		dTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("First Named Insured", "Spouse", "Child", "Parent", "Sibling", "Other Resident Relative", "Employee", "Other", "Registered Domestic Partner/Civil Union"));

		dTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.GENDER.getLabel(), ComboBox.class).verify.options(Arrays.asList("Male", "Female"));

		dTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Married", "Single", "Divorced", "Widowed", "Separated", "Registered Domestic Partner/Civil Union", "Common Law"));

		dTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Licensed (US)", "Licensed (Canadian)", "Foreign", "Not Licensed", "Learner's Permit"));

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name Driver tab controls check for AutoSS product, CO state [TC03]
	 * @scenario
	 * 1. Create customer
	 * 2. Initiate AutoSS quote creation
	 * 3. Move to Driver tab
	 * 4. Click on the 'Add Activity', select Type: 'Principally At-Fault Accident', Description: 'Principally At-Fault Accident (Property Damage Only)'
	 * 5. Review the dropdown values for 'Apply Waiver' field, check that 'Insurance Incident Waiver' not applicable for CO
	 * 6. Remove activity information
	 * 7. Click on the 'Add Activity', select Type 'Minor Violation', Description: 'Improper Passing'
	 * 8. Set Occurrence Date = '01/10/2012', verify 'Conviction Date' field appears as enabled and empty by default
	 * 9. Leave 'Conviction Date' empty, verify that no 'Violation points' are calculated (equals to '0')
	 * 10. Enter a 'Conviction Date', verify that 'Violation points' are calculated (not 0)
	 * 11. Enter a 'Conviction Date' that is later than the current date and click Continue button
	 * 12. Verify that error message appears: 'Conviction Date later than current date' is displayed. User should stay in Driver tab
	 * 13. Remove activity information, verify that 'List of Activity Information' table gets empty
	 *
	 * @details
	 */
	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testActivityInformationControlsOnDriverTab() {
		MultiInstanceBeforeAssetList aiAssetList = new DriverTab().getActivityInformationAssetList();

		mainApp().open();

		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_TC03"), DriverTab.class, true);

		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Principally At-Fault Accident");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Principally At-Fault Accident (Property Damage Only)");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2012");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).setValue("1500");
		//Skipped step: Review the dropdown values for 'Apply Waiver' field, check that 'Insurance Incident Waiver' not applicable for CO
		DriverTab.tableActivityInformationList.removeRow(1);

		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Minor Violation");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Improper Passing");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2012");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).verify.present();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).verify.enabled();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).verify.value("");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.VIOLATION_POINTS).verify.value("0");

		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).setValue("01/01/2015");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.VIOLATION_POINTS).verify.valueByRegex("^[^0]+$");

		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().plusDays(5).format(DateTimeUtils.MM_DD_YYYY));
		aiAssetList.getWarning(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).verify.value("Conviction Date later than current date");
		Tab.buttonNext.click();
		NavigationPage.Verify.viewTabSelected(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab.tableActivityInformationList.isPresent();

		DriverTab.tableActivityInformationList.removeRow(1);
		DriverTab.tableActivityInformationList.verify.empty();
	}
}
