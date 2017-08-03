/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss.delta;

import java.util.Arrays;
import org.testng.annotations.Test;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

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
public class TestDriverTabControls extends AutoSSBaseTest {

	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testDriverTabControls() {
		DriverTab dTab = new DriverTab();

		mainApp().open();

		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), DriverTab.class);
		new DriverTab().fillTab(getTestSpecificTD("TestData_CO"));

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
}
