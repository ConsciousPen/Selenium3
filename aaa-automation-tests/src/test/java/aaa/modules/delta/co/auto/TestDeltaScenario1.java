/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.delta.co.auto;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.*;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.FillableDocumentsTable;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry Chubkov
 * @name General tab controls check for AutoSS product, CO state [TC01]
 * @scenario 1. Create customer
 * 2. Initiate AutoSS quote creation
 * 3. Go to General Tab
 * 4. Verify Dropdown Values on General tab
 * 5. Verify that there is no Motorcycle option in 'AAA Products Owned' section
 * 6. Select option "Yes" For all available products owned - Life, Home, Renters, Condo.
 * 7. Do not provide policy numbers for any of those current AAA policies.
 * 8. Verify field TollFree Number visible
 * 9. Select any option other than "None" for 'Adversely Impacted' field.
 * 10. Verify dropdown visible
 * @details
 */
@Test(groups = {Groups.DELTA, Groups.HIGH})
public class TestDeltaScenario1 extends AutoSSBaseTest {
	private String quoteNumber;

	private DriverTab driverTab = new DriverTab();
	private PremiumAndCoveragesTab pacTab = new PremiumAndCoveragesTab();
	private GeneralTab gTab = new GeneralTab();
	private MultiInstanceBeforeAssetList aiAssetList = driverTab.getActivityInformationAssetList();
	private ErrorTab errorTab = new ErrorTab();

	@Test(groups = {Groups.DELTA, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC01() {
		preconditions(NavigationEnum.AutoSSTab.GENERAL);

		CustomAssert.enableSoftMode();
		//Verify Dropdown Values on General tab
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Own Home", "Own Condo", "Own Mobile Home", "Rents Multi-Family Dwelling", "Rents Single-Family Dwelling", "Lives with Parent", "Other"));

		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Yes", "No", "Membership Pending"));

		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.SOURCE_OF_BUSINESS.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("New Business", "Spin", "Split", "Rewrite", "Book Roll"));

		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Standard", "Named Non Owner"));

		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Annual", "Semi-annual"));

		//Verify that there is no Motorcycle option in 'AAA Products Owned' section
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE).verify.present(false);

		//Select option "Yes" For all available products owned - Life, Home, Renters, Condo.
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.LIFE).setValue("Yes");
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.HOME).setValue("Yes");
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.RENTERS).setValue("Yes");
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CONDO).setValue("Yes");

		//Verify field TollFree Number visible
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.TOLLFREE_NUMBER).verify.present();

		//Select any option other than "None" for 'Adversely Impacted' field.
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).setAnyValueExcept("None");

		//Verify dropdown visible
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).verify.present();
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();

		Tab.buttonSaveAndExit.click();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name Driver tab controls check for AutoSS product, CO state [TC02]
	 * @scenario 1. Create customer
	 * 2. Initiate AutoSS quote creation
	 * 3. Move to Driver tab
	 * 4. Add second drive
	 * 5. Verify following Marital Statuses available for CO: "Registered Domestic Partner/Civil Union", "Common Law"
	 * 6. Verify Dropdown Values in Driver tab
	 * @details
	 */
	@Test
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC02() {
		preconditions(NavigationEnum.AutoSSTab.DRIVER);

		driverTab.fillTab(getTestSpecificTD("TestData"));

		CustomAssert.enableSoftMode();
		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Available for Rating", "Not Available for Rating", "Excluded"));

		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("First Named Insured", "Spouse", "Child", "Parent", "Sibling", "Other Resident Relative", "Employee", "Other", "Registered Domestic Partner/Civil Union"));

		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.GENDER.getLabel(), ComboBox.class).verify.options(Arrays.asList("Male", "Female"));

		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Married", "Single", "Divorced", "Widowed", "Separated", "Registered Domestic Partner/Civil Union", "Common Law"));

		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Licensed (US)", "Licensed (Canadian)", "Foreign", "Not Licensed", "Learner's Permit"));

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();

		Tab.buttonSaveAndExit.click();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name Driver tab controls check for AutoSS product, CO state [TC03]
	 * @scenario 1. Create customer
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
	 * @details
	 */
	@Test
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC03() {
		preconditions(NavigationEnum.AutoSSTab.DRIVER);
		driverTab.fillTab(getTestSpecificTD("TestData"));

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

		Tab.buttonSaveAndExit.click();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name CO_SC1_TC04
	 */
	@Test
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC04() {
		VehicleTab vTab = new VehicleTab();

		preconditions(NavigationEnum.AutoSSTab.DRIVER);
		TestData adjustedData = getTestSpecificTD("TestData").adjust(driverTab.getMetaKey(), Collections.singletonList(getTestSpecificTD("DriverTab_TC04")));
		policy.getDefaultView().fillFromTo(adjustedData, DriverTab.class, VehicleTab.class, true);

		vTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.TYPE).verify.options(Arrays.asList("Private Passenger Auto", "Limited Production/Antique", "Trailer", "Motor Home", "Conversion Van", "Trailer"));
		vTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE).verify.options(Arrays.asList("Pleasure", "Commute", "Business", "Artisan", "Farm"));
		vTab.submitTab();

		Tab.buttonNext.click();
		PremiumAndCoveragesTab.buttonCalculatePremium.click();
		//in old test this verification was skipped with comment 'PAS12:AS per RSG, commenting premium rating verifications'
		//PremiumAndCoveragesTab.totalTermPremium.verify.value(new Dollar("3,409.00").toString());

		Tab.buttonSaveAndExit.click();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name CO_SC1_TC05
	 */
	@Test
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC05() {
		preconditions(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES);

		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).verify.value("$100,000/$300,000 (+$0.00)");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY).verify.value("$50,000  (+$0.00)");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY).verify.value("$100,000/$300,000 (+$0.00)");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS).verify.value("$5,000  (+$0.00)");

		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE).verify.value("$250  (+$0.00)");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).verify.value("$500  (+$0.00)");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE).verify.value("$1,500.00");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS).verify.value("No Coverage");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT).verify.value("No Coverage (+$0.00)");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE).verify.value("No Coverage (+$0.00)");
		Tab.buttonSaveAndExit.click();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name CO_SC1_TC06
	 */
	@Test
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC06() {
		preconditions(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES);
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		//CO DELTA - No full safety glass
		//Update: 080-006CO_VA_V3.0 is updated to add Full safety glass coverage
		CustomAssert.assertTrue(pacTab.getRatingDetailsVehiclesData().stream().allMatch(td -> td.containsKey("Full Safety Glass")));
		CustomAssert.assertEquals(pacTab.getRatingDetailsQuoteInfoData().getValue("Adversely Impacted Applied"), "Yes");
		pacTab.submitTab();
		//PAS 11 fix application change #35
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_200103);
		//PAS11 CR fix
		errorTab.overrideErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.TEMPORARY_ISSUE, ErrorEnum.Errors.ERROR_200103);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		StaticElement warningMessage = new StaticElement(By.id("policyDataGatherForm:warningMessage"));
		warningMessage.verify.value(String.format("Adversely Impacted was applied to the policy effective %s.", QuoteDataGatherPage.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY)));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).setValue("None");
		gTab.submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonCalculatePremium.click();
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		CustomAssert.assertEquals(pacTab.getRatingDetailsQuoteInfoData().getValue("Adversely Impacted Applied"), "No");
		pacTab.submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		warningMessage.verify.present(false);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).setAnyValueExcept("None");
		gTab.submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonCalculatePremium.click();

		Tab.buttonSaveAndExit.click();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name CO_SC1_TC07
	 */
	@Test
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC07() {
		preconditions(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES);
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS).verify.value("No Coverage");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE).verify.value(new Dollar(1500).toString());

		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueByRegex("No Coverage.*");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE).verify.value("$250  (+$0.00)");

		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueByRegex("\\$250.*");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE).verify.present(false);

		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS).setValueByRegex("No Coverage.*");
		PremiumAndCoveragesTab.buttonCalculatePremium.click();
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS).setValue("Yes");
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.MOTORCYCLE).verify.present(false);
		pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS).setValue("No");

		Tab.buttonSaveAndExit.click();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name CO_SC1_TC08
	 */
	@Test
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC08() {
		DocumentsAndBindTab dabTab = new DocumentsAndBindTab();

		preconditions(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS);
		policy.getDefaultView().fillFromTo(getPolicyTD(), DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
		//policy.getDefaultView().fillFromTo(getPolicyTD().mask("DriverActivityReportsTab|Has the customer expressed interest in purchasing the quote?"), DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
		dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.MEDICAL_PAYMENTS_REJECTION_OF_COVERAGE).verify.present();
		dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.MEDICAL_PAYMENTS_REJECTION_OF_COVERAGE).verify.value("Yes");

		dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.MEDICAL_PAYMENTS_REJECTION_OF_COVERAGE).verify.present();

		dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EXISTING_AAA_LIFE_POLICY_NUMBER).verify.present();
		dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EXISTING_AAA_HOME_POLICY_NUMBER).verify.present();
		dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EXISTING_AAA_RENTERS_POLICY_NUMBER).verify.present();
		dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EXISTING_AAA_CONDO_POLICY_NUMBER).verify.present();

		dabTab.submitTab();
		errorTab.verify.errorsPresent(
				ErrorEnum.Errors.ERROR_200060_CO,
				ErrorEnum.Errors.ERROR_200401,
				ErrorEnum.Errors.ERROR_AAA_CSA3080819,
				ErrorEnum.Errors.ERROR_AAA_CSA3082394,
				ErrorEnum.Errors.ERROR_AAA_CSA3083444,
				ErrorEnum.Errors.ERROR_AAA_CSA3080903);

		errorTab.overrideErrors(ErrorEnum.Errors.ERROR_200060_CO, ErrorEnum.Errors.ERROR_200401);

		Tab.buttonSaveAndExit.click();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name CO_SC1_TC09
	 */
	@Test
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testSC1_TC09() {
		GenerateOnDemandDocumentActionTab goddTab = new GenerateOnDemandDocumentActionTab();

		mainApp().open();
		SearchPage.openQuote(getQuoteNumber());
		policy.quoteDocGen().start();

		List<TestData> expectedData = new ArrayList<>(8);
		expectedData.add(DataProviderFactory.dataOf("Document #", "AA11CO", "Document Name", "Colorado Auto Insurance Application"));
		expectedData.add(DataProviderFactory.dataOf("Document #", "AA43CO", "Document Name", "Named Driver Exclusion Endorsement"));
		expectedData.add(DataProviderFactory.dataOf("Document #", "AAIQCO", "Document Name", "Auto Insurance Quote"));
		expectedData.add(DataProviderFactory.dataOf("Document #", "AHFMXX", "Document Name", "Fax Memorandum"));
		expectedData.add(DataProviderFactory.dataOf("Document #", "AU03", "Document Name", "Notice of Declination"));
		expectedData.add(DataProviderFactory.dataOf("Document #", "AA16CO", "Document Name", "MEDICAL PAYMENTS REJECTION OF COVERAGE"));
		expectedData.add(DataProviderFactory.dataOf("Document #", "AADNCO", "Document Name", "Colorado Private Passenger Automobile Insurance Summary Disclosure Form"));
		expectedData.add(DataProviderFactory.dataOf("Document #", "AHAUXX", "Document Name", "Consumer Information Notice")); //missed in original TC
		expectedData.forEach(e -> e.adjust("Select", ""));

		FillableDocumentsTable documents = goddTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.ON_DEMAND_DOCUMENTS);
		documents.getTable().verify.value(expectedData);
		documents.getTable().getRow("Document #", "AA43CO").getCell("Select").controls.checkBoxes.getFirst().verify.enabled(false);

		expectedData.forEach(e -> e.adjust("Select", "true"));
		expectedData.add(DataProviderFactory.dataOf("Free Form Text", "Free Text"));
		documents.setValue(expectedData);

		goddTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD).setValue("Central Print");
		policy.quoteDocGen().submit();
		NavigationPage.Verify.mainTabSelected(NavigationEnum.AppMainTabs.QUOTE.get());
	}

	private void preconditions(NavigationEnum.AutoSSTab navigateTo) {
		mainApp().open();
		String quote = getQuoteNumber();
		MainPage.QuickSearch.buttonSearchPlus.click();
		if (Page.dialogConfirmation.isPresent()) { //happens if previous test in queue was failed
			Page.dialogConfirmation.confirm();
		}
		SearchPage.openQuote(quote);
		policy.dataGather().start();
		NavigationPage.toViewTab(navigateTo.get());
	}

	private String getQuoteNumber() {
		if (quoteNumber == null) {
			mainApp().open();
			createCustomerIndividual();
			policy.initiate();
			policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData"), GeneralTab.class, true);
			Tab.buttonSaveAndExit.click();
			quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
			log.info("DELTA CO SC1: ASS Quote created with #" + quoteNumber);
		}
		return quoteNumber;
	}
}
