package aaa.modules.regression.sales.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.admin.metadata.product.MoratoriumMetaData;
import aaa.admin.modules.product.IProduct;
import aaa.admin.modules.product.ProductType;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.PolicyMoratorium;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestPolicyMoratorium extends PolicyMoratorium {

	IProduct moratorium = ProductType.MORATORIUM.get();
	PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	ProductOfferingTab productOfferingTab = new ProductOfferingTab();
	BindTab bindTab = new BindTab();
	PurchaseTab purchaseTab = new PurchaseTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	/**
	 * @author Maija Strazda
	 * @name Soft Stop Moratorium set on New Business Premium Calculation and Hard Stop on New Business Bind - PST-352
	 * @scenario
	 * 1. Add ZIP Code entry in lookupvalue table if not exists.
	 * 2. Set Soft Stop moratorium on Premium Calculation and Hard Stop moratorium on Bind.
	 * 3. Create a customer.
	 * 4. Initiate a Home SS quote and go to Premium and Coverages Product Offering tab.
	 * 5. Press Calculate Premium button on Product Offering tab, validate Soft Stop pop-up dialog and press Cancel.
	 * 6. Press Calculate Premium button on Quote tab, validate Soft Stop pop-up dialog and press OK.
	 * 7. Go to Bind page, press Purchase, validate Hard Stop pop-up dialog and press Cancel.
	 * 8. Expire moratorium.
	 * 9. Create the same policy to make sure moratorium is not triggering anymore.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PST-352: Soft Stop Moratorium set on New Business Premium Calculation and Hard Stop on New Business Bind")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PST-352")
	@StateList(states = Constants.States.AZ)
	public void newBusiness_SoftStopPremiumCalculation_HardStopBind(@Optional("AZ") String state) {

		TestData td = getTestSpecificTD("TestData_Moratorium_Config_1");
		String addressLine = "277 West Street";
		String moratoriumZipCode = getMoratoriumZipCode(td);
		String moratoriumCity = getMoratoriumCity(td);
		String moratoriumName = getMoratoriumName(td);
		String moratoriumDisplayMessage = getExpectedMoratoriumMessage(td);
		String moratoriumCustomerNumber;
		TestData testData = adjustDwellingAddress(moratoriumZipCode, addressLine);
		try {
			//Step 1 -- entry needs to be added to the AAAMoratoriumGeographyLocationInfo lookup in order to be able to select it when creating moratorium in Step 2.
			log.info("Step 1: Add ZIP Code entry in lookupvalue table if not exists.");
			DBService.get().executeUpdate(insertLookupEntry(moratoriumZipCode, moratoriumCity));

			//Step 2
			log.info("Step 2: Set Soft Stop moratorium on Premium Calculation and Hard Stop moratorium on Bind.");
			adminApp().open();
			moratorium.create(td
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel()), moratoriumName)
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", "AddRuleSection", MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel()), moratoriumDisplayMessage));

			//Step 3
			log.info("Step 3: Create a customer.");
			mainApp().open();
			moratoriumCustomerNumber = createCustomer(moratoriumZipCode, addressLine);

			//Step 4
			log.info("Step 4: Initiate a Home SS quote and go to Premium and Coverages Product Offering tab.");
			policy.initiate();
			//when moratorium is set on particular address (state, city, zip code) it will be triggered only if dwelling address contains this geo data
			//dwelling address needs to be adjusted in order to moratorium tests not to affect other tests (moratorium will be set to this zip code and will not affect policies with other zip codes)
			policy.getDefaultView().fillUpTo(testData, ProductOfferingTab.class, false);

			//Step 5
			log.info("Step 5: Press Calculate Premium button on Product Offering tab, validate Soft Stop pop-up dialog and press Cancel.");
			productOfferingTab.btnCalculatePremium.click();
			softStopPopUpCheckAndCancel(moratoriumDisplayMessage);

			//Step 6
			log.info("Step 6: Press Calculate Premium button on Quote tab, validate Soft Stop pop-up dialog and press OK.");
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
			premiumIsNull();
			premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();
			softStopPopUpCheckAndApprove(moratoriumDisplayMessage);
			premiumIsNOTNull();

			//Step 7
			log.info("Step 7: Go to Bind page, press Purchase, validate Hard Stop pop-up dialog and press Cancel.");
			premiumsAndCoveragesQuoteTab.submitTab();
			policy.getDefaultView().fillFromTo(getPolicyTD(), MortgageesTab.class, BindTab.class, true);
			bindTab.btnPurchase.click();
			hardStopPopUpCheck(moratoriumDisplayMessage);

			//Step 9
			log.info("Step 9: Expire moratorium.");
		} finally {
			expireMoratorium(moratoriumName);
		}

		//Step 10
		log.info("Step 10: Create the same policy to make sure moratorium is not triggering anymore.");
		checkMoratoriumIsNotTriggering(moratoriumCustomerNumber, testData);

	}

	/**
	 * @author Maija Strazda
	 * @name Hard Stop (Overridable) Moratorium set on New Business Bind action - PST-352
	 * @scenario
	 * 1. Add ZIP Code entry in lookupvalue table if not exists.
	 * 2. Set Hard Stop (Overridable) level 4 moratorium on Bind action.
	 * 3. Create a customer.
	 * 4. Initiate a Home SS quote, fill it including Bind tab.
	 * 5. Press Purchase button, validate Hard Stop pop-up dialog and override it.
	 * 6. Bind/issue the quote.
	 * 7. Expire moratorium.
	 * 8. Create the same policy to make sure moratorium is not triggering anymore.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PST-352: Hard Stop (Overridable) Moratorium set on New Business Bind action")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PST-352")
	@StateList(states = Constants.States.AZ)
	public void newBusiness_HardStopOverridableBind(@Optional("AZ") String state) {
		TestData td = getTestSpecificTD("TestData_Moratorium_Config_2");
		String addressLine = "278 West Street";
		String moratoriumZipCode = getMoratoriumZipCode(td);
		String moratoriumCity = getMoratoriumCity(td);
		String moratoriumName = getMoratoriumName(td);
		String moratoriumDisplayMessage = getExpectedMoratoriumMessage(td);
		String moratoriumCustomerNumber;
		TestData testData = adjustDwellingAddress(moratoriumZipCode, addressLine);
		try {
			//Step 1
			log.info("Step 1: Add ZIP Code entry in lookupvalue table if not exists.");
			DBService.get().executeUpdate(insertLookupEntry(moratoriumZipCode, moratoriumCity));

			//Step 2
			log.info("Step 2: Set Hard Stop (Overridable) level 4 moratorium on Bind action.");
			adminApp().open();
			moratorium.create(td
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel()), moratoriumName)
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", "AddRuleSection", MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel()), moratoriumDisplayMessage));

			//Step 3
			log.info("Step 3: Create a customer.");
			//user must have 'Moratorium Override' privilege and UW level 4 --> can use qa user
			mainApp().open();
			moratoriumCustomerNumber = createCustomer(moratoriumZipCode, addressLine);

			//Step 4
			log.info("Step 4: Initiate a Home SS quote, fill it including Bind tab.");
			policy.initiate();
			//when moratorium is set on particular address (state, city, zip code) it will be triggered only if dwelling address contains this geo data
			//dwelling address needs to be adjusted in order to moratorium tests not to affect other tests (moratorium will be set to this zip code and will not affect policies with other zip codes)
			policy.getDefaultView().fillUpTo(testData, BindTab.class, true);

			//Step 5
			log.info("Step 5: Press Purchase button, validate Hard Stop pop-up dialog and override it.");
			bindTab.btnPurchase.click();
			hardStopOverridablePopUpCheck(moratoriumDisplayMessage, bindTab);

			//Step 6
			log.info("Step 6:Bind/issue the quote.");
			Page.dialogConfirmation.buttonYes.click();
			policy.getDefaultView().fillFromTo(getPolicyTD(), PurchaseTab.class, PurchaseTab.class, true);
			purchaseTab.submitTab();
			checkPolicyIsActive();

			//Step 7
			log.info("Step 7: Expire moratorium.");
		} finally {
			expireMoratorium(moratoriumName);
		}

		//Step 8
		log.info("Step 8: Create the same policy to make sure moratorium is not triggering anymore.");
		checkMoratoriumIsNotTriggering(moratoriumCustomerNumber, testData);
	}

	private TestData adjustDwellingAddress(String moratoriumZipCode, String dwellingAddressLine) {
		return getPolicyTD()
				.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.DwellingAddress.class
						.getSimpleName(), HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), moratoriumZipCode)
				.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.DwellingAddress.class
						.getSimpleName(), HomeSSMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), dwellingAddressLine);
	}

	private void checkPolicyIsActive() {
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private String getMoratoriumName(TestData td) {
		return td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel());
	}

	private String getMoratoriumZipCode(TestData td) {
		return td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.ZIP_CODES.getLabel());
	}

	private String getMoratoriumCity(TestData td) {
		return td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.CITIES.getLabel());
	}

	private void checkMoratoriumIsNotTriggering(String moratoriumCustomerNumber, TestData testData) {
		mainApp().open();
		SearchPage.openCustomer(moratoriumCustomerNumber);
		createPolicy(testData);
		checkPolicyIsActive();
	}

	private String createCustomer(String moratoriumZipCode, String addressLine) {
		createCustomerIndividual(getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), moratoriumZipCode)
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), addressLine));
		return CustomerSummaryPage.labelCustomerNumber.getValue();
	}

	private String getExpectedMoratoriumMessage(TestData td) {
		return td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getTestData(MoratoriumMetaData.AddMoratoriumTab.ADD_RULE_SECTION.getLabel())
				.getValue(MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel());
	}

	private void softStopPopUpCheckAndApprove(String expectedMessage) {
		assertThat(Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()).isTrue();
		assertThat(Page.dialogConfirmation.labelMessage.getValue()).contains(expectedMessage);
		assertThat(Page.dialogConfirmation.labelHeader.getValue()).isEqualTo("Warning");
		Page.dialogConfirmation.buttonOk.click();
	}

	private void softStopPopUpCheckAndCancel(String expectedMessage) {
		assertThat(Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()).isTrue();
		assertThat(Page.dialogConfirmation.labelMessage.getValue()).contains(expectedMessage);
		assertThat(Page.dialogConfirmation.labelHeader.getValue()).isEqualTo("Warning");
		Page.dialogConfirmation.buttonCancel.click();
	}

	private void hardStopPopUpCheck(String expectedMessage) {
		assertThat(Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()).isTrue();
		assertThat(Page.dialogConfirmation.labelMessage.getValue()).contains(expectedMessage);
		assertThat(Page.dialogConfirmation.labelHeader.getValue()).isEqualTo("Error");
		assertThat(Page.dialogConfirmation.buttonOk.isPresent()).isFalse();
		Page.dialogConfirmation.buttonCancel.click();
		assertThat(bindTab.confirmPurchase.isPresent()).isFalse();
	}

	private void hardStopOverridablePopUpCheck(String expectedMessage, Tab tab) {
		assertThat(tab.moratoriumOverrideDialog.isPresent() && tab.moratoriumOverrideDialog.isVisible()).isTrue();
		assertThat(tab.moratoriumOverrideDialog.getAsset(DialogsMetaData.MoratoriumOverrideDialog.MESSAGE).getValue()).contains(expectedMessage);
		tab.moratoriumOverrideDialog.getAsset(DialogsMetaData.MoratoriumOverrideDialog.MORATORIUM_OVERRIDE_REASON).setValue("Business Retention");
		tab.moratoriumOverrideDialog.getAsset(DialogsMetaData.MoratoriumOverrideDialog.OK).click();
	}

	private void premiumIsNOTNull() {
		assertThat(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().toString()).isNotEqualTo("$0.00");
	}

	private void premiumIsNull() {
		assertThat(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().toString()).isEqualTo("$0.00");
	}

}