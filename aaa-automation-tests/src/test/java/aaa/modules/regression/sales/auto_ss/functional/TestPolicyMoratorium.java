package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.admin.metadata.product.MoratoriumMetaData;
import aaa.admin.modules.product.IProduct;
import aaa.admin.modules.product.ProductType;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.PolicyMoratorium;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestPolicyMoratorium extends PolicyMoratorium {
	IProduct moratorium = ProductType.MORATORIUM.get();
	PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	PurchaseTab purchaseTab = new PurchaseTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Maija Strazda
	 * @name Soft Stop Moratorium set on New Business Premium Calculation and Hard Stop moratorium on New Business Bind - PST-352
	 * @scenario
	 * 1. Add Zip Code entry in lookupvalue table if not exists.
	 * 2. Set Soft Stop moratorium on Premium Calculation and Hard Stop moratorium on Bind.
	 * 3. Create a customer.
	 * 4. Initiate an Auto SS quote and go to Premium and Coverages tab.
	 * 5. Validate that premium is not calculated and warning message in Moratorium Information section is present.
	 * 6. Press Calculate Premium button, validate Soft Stop pop-up dialog and press OK.
	 * 7. Validate that premium is calculated and Moratorium Information section is not displayed anymore.
	 * 8. Go to Documents and Bind page, press Purchase, validate Hard Stop pop-up dialog and press Cancel.
	 * 9. Expire moratorium.
	 * 10. Create the same policy to make sure moratorium is not triggering anymore.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PST-352: Soft Stop Moratorium set on New Business Premium Calculation and Hard Stop moratorium on New Business Bind")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PST-352")
	@StateList(states = Constants.States.AZ)
	public void newBusiness_SoftStopPremiumCalculation_HardStopBind(@Optional("AZ") String state) {

		TestData td = getTestSpecificTD("TestData_Moratorium_Config_1");
		String moratoriumZipCode = getMoratoriumZipCode(td);
		String moratoriumCity = getMoratoriumCity(td);
		String moratoriumName = getMoratoriumName(td);
		String moratoriumDisplayMessage = getExpectedMoratoriumMessage(td);
		String moratoriumCustomerNumber;
		try {
			//Step 1 -- Zip code entry needs to be added to the AAAMoratoriumGeographyLocationInfo lookup in order to be able to select it when creating moratorium in Step 2.
			log.info("Step 1: Add Zip Code entry in lookupvalue table if not exists.");
			DBService.get().executeUpdate(insertLookupEntry(moratoriumZipCode, moratoriumCity, "AZ"));

			//Step 2
			log.info("Step 2: Set Soft Stop moratorium on Premium Calculation and Hard Stop moratorium on Bind.");
			adminApp().open();
			moratorium.create(td
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel()), moratoriumName)
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", "AddRuleSection", MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel()), moratoriumDisplayMessage));

			//Step 3
			log.info("Step 3: Create a customer.");
			mainApp().open();
			//when moratorium is set on particular address (state, city, zip code) it will be triggered only if garaging address contains this geo data
			//customer address (which will be used in policy also as garaging address) needs to be adjusted in order to moratorium tests not to affect other tests (moratorium will be set to this zip code and will not affect policies with other zip codes)
			moratoriumCustomerNumber = createCustomer(moratoriumZipCode, "275 West Street");

			//Step 4
			log.info("Step 4: Initiate an Auto SS quote and go to Premium and Coverages tab.");
			policy.initiate();
			policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumAndCoveragesTab.class, false);

			//Step 5
			log.info("Step 5: Validate that premium is not calculated and warning message in Moratorium Information section is present.");
			totalTermPremiumIsNull();
			moratoriumInformationWarningCheck();

			//Step 6
			log.info("Step 6: Press Calculate Premium button, validate Soft Stop pop-up dialog and press OK.");
			premiumAndCoveragesTab.btnCalculatePremium().click();
			softStopPopUpCheck(moratoriumDisplayMessage);

			//Step 7
			log.info("Step 7: Validate that premium is calculated and Moratorium Information section is not displayed anymore.");
			totalTermPremiumIsNOTNull();
			moratoriumInformationWarningNotPresentCheck();

			//Step 8
			log.info("Step 8: Go to Documents and Bind page, press Purchase, validate Hard Stop pop-up dialog and press Cancel.");
			premiumAndCoveragesTab.submitTab();
			policy.getDefaultView().fillFromTo(getPolicyTD(), DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
			DocumentsAndBindTab.btnPurchase.click();
			hardStopPopUpCheck(moratoriumDisplayMessage);

			//Step 9
			log.info("Step 9: Expire moratorium.");
		} finally {
			expireMoratorium(moratoriumName);
		}

		//Step 10
		log.info("Step 10: Create the same policy to make sure moratorium is not triggering anymore.");
		checkMoratoriumIsNotTriggering(moratoriumCustomerNumber);
	}

	/**
	 * @author Maija Strazda
	 * @name Hard Stop (Overridable) Moratorium set on New Business Bind action - PST-352
	 * @scenario
	 * 1. Add ZIP Code entry in lookupvalue table if not exists.
	 * 2. Set Hard Stop (Overridable) level 4 moratorium on Bind action.
	 * 3. Create a customer.
	 * 4. Initiate an Auto SS quote, fill it including Documents and Bind tab.
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
		String moratoriumZipCode = getMoratoriumZipCode(td);
		String moratoriumCity = getMoratoriumCity(td);
		String moratoriumName = getMoratoriumName(td);
		String moratoriumDisplayMessage = getExpectedMoratoriumMessage(td);
		String moratoriumCustomerNumber;
		try {
			//Step 1 -- entry needs to be added to the AAAMoratoriumGeographyLocationInfo lookup in order to be able to select it when creating moratorium in Step 2.
			log.info("Step 1: Add Zip Code entry in lookupvalue table if not exists.");
			DBService.get().executeUpdate(insertLookupEntry(moratoriumZipCode, moratoriumCity, "AZ"));

			//Step 2
			log.info("Step 2: Set Hard Stop (Overridable) level 4 moratorium on Bind action.");
			adminApp().open();
			//moratorium name and moratorium display message needs to be adjusted because they change value of randomly generated numeric part each time when gets called
			moratorium.create(td
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel()), moratoriumName)
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", "AddRuleSection", MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel()), moratoriumDisplayMessage));

			//Step 3
			log.info("Step 3: Create a customer.");
			//user must have 'Moratorium Override' privilege and UW level 4 in order to override moratorium --> can use qa user
			mainApp().open();
			//customer address needs to be adjusted in order to moratorium tests not to affect other tests (moratorium will be set to this zip code and will not affect policies with other zip codes)
			moratoriumCustomerNumber = createCustomer(moratoriumZipCode, "276 West Street");

			//Step 4
			log.info("Step 4: Initiate an Auto SS quote, fill it including Documents and Bind tab.");
			policy.initiate();
			policy.getDefaultView().fillUpTo(getPolicyTD(), DocumentsAndBindTab.class, true);

			//Step 5
			log.info("Step 5: Press Purchase button, validate Hard Stop pop-up dialog and override it.");
			DocumentsAndBindTab.btnPurchase.click();
			hardStopOverridablePopUpCheck(moratoriumDisplayMessage, documentsAndBindTab);

			//Step 6
			log.info("Step 6: Bind/issue the quote.");
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
		checkMoratoriumIsNotTriggering(moratoriumCustomerNumber);
	}

	private void checkPolicyIsActive() {
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private String getMoratoriumZipCode(TestData td) {
		return td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.ZIP_CODES.getLabel());
	}

	private String getMoratoriumCity(TestData td) {
		return td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.CITIES.getLabel());
	}

	private String getMoratoriumName(TestData td) {
		return td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel());
	}

	private String getExpectedMoratoriumMessage(TestData td) {
		return td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getTestData(MoratoriumMetaData.AddMoratoriumTab.ADD_RULE_SECTION.getLabel())
				.getValue(MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel());
	}

	private void checkMoratoriumIsNotTriggering(String moratoriumCustomerNumber) {
		mainApp().open();
		SearchPage.openCustomer(moratoriumCustomerNumber);
		createPolicy();
		checkPolicyIsActive();
	}

	private String createCustomer(String moratoriumZipCode, String addressLine) {
		createCustomerIndividual(getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), moratoriumZipCode)
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), addressLine));
		return CustomerSummaryPage.labelCustomerNumber.getValue();
	}

	private void softStopPopUpCheck(String expectedMessage) {
		assertThat(Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()).isTrue();
		assertThat(Page.dialogConfirmation.labelMessage.getValue()).contains(expectedMessage);
		assertThat(Page.dialogConfirmation.labelHeader.getValue()).isEqualTo("Warning");
		Page.dialogConfirmation.buttonOk.click();
	}

	private void hardStopPopUpCheck(String expectedMessage) {
		assertThat(Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()).isTrue();
		assertThat(Page.dialogConfirmation.labelMessage.getValue()).contains(expectedMessage);
		assertThat(Page.dialogConfirmation.labelHeader.getValue()).isEqualTo("Error");
		assertThat(Page.dialogConfirmation.buttonOk.isPresent()).isFalse();
		Page.dialogConfirmation.buttonCancel.click();
		assertThat(DocumentsAndBindTab.confirmPurchase.isPresent()).isFalse();
	}

	private void hardStopOverridablePopUpCheck(String expectedMessage, Tab tab) {
		assertThat(tab.moratoriumOverrideDialog.isPresent() && tab.moratoriumOverrideDialog.isVisible()).isTrue();
		assertThat(tab.moratoriumOverrideDialog.getAsset(DialogsMetaData.MoratoriumOverrideDialog.MESSAGE).getValue()).contains(expectedMessage);
		tab.moratoriumOverrideDialog.getAsset(DialogsMetaData.MoratoriumOverrideDialog.MORATORIUM_OVERRIDE_REASON).setValue("Business Retention");
		tab.moratoriumOverrideDialog.getAsset(DialogsMetaData.MoratoriumOverrideDialog.OK).click();
	}

	private void moratoriumInformationWarningCheck() {
		assertThat(premiumAndCoveragesTab.getMoratoriumInformationAssetList().getAsset(AutoSSMetaData.MoratoriumInformationSection.MORATORIUM_INFORMATION_MESSAGE).getValue())
				.contains("Premium was not calculated automatically due to a moratorium");
	}

	private void moratoriumInformationWarningNotPresentCheck() {
		assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.MORATORIUM_INFORMATION)).isPresent(false);
	}

	private void totalTermPremiumIsNull() {
		assertThat(PremiumAndCoveragesTab.totalTermPremium.getValue()).isEqualTo("$0.00");
	}

	private void totalTermPremiumIsNOTNull() {
		assertThat(PremiumAndCoveragesTab.totalTermPremium.getValue()).isNotEqualTo("$0.00");
	}

}
