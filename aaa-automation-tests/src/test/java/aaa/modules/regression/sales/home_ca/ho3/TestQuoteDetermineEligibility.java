package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum.Errors;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;

public class TestQuoteDetermineEligibility extends HomeCaHO3BaseTest {

	PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
	BindTab bindTab = new BindTab();
	ErrorTab errorTab = new ErrorTab();
	MortgageesTab mortgageesTab = new MortgageesTab();

	/**
	  * @author Jurij Kuznecov
	  * @name Test CAH Quote Determine Eligibility
	  *     rules for:
	  *     - Detached Structures
	  *     - Roof Type/Condition
	  *     - Stoves
	  * @scenario 
	  * 1.  Create new or open existent Customer
	  * 2.  Create a new HO3 quote and fill mandatory fields
	  * 3.  Verify an eligibility error if 4 not rented detached structures are added
	  * 4.  Verify an eligibility error if 3 rented detached structures are added
	  * 5.  Verify an eligibility error if Limit of Liabilities + CovB > CovA
	  * 6.  Verify an eligibility error if Limit of Liabilities >= 50% * CovA
	  * 7.  Verify an eligibility error if Roof renovation = "3+ layers"  
	  * 8.  Verify an eligibility error if Roof shape = "Flat"
	  * 9.  Verify an eligibility error if Wood Stove Is Sole Source Of Heat
	  * 10. Verify an eligibility error if there is a Wood Stove but no fire alarm
	  * 11. Verify an eligibility error if Wood Stove wasn't installed professionally    
	  */
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
	public void testQuoteDetermineEligibilitySC1(@Optional("CA") String state) {
		String expected_ER0908 = "Wood burning stoves as the sole source of heat are ineligible.";
		String expected_ER0522 = "Dwellings with a wood burning stove without at least one smoke detector installed per floor are ineligible.";
		String expected_ER0909 = "Wood burning stoves are ineligible unless professionally installed by a licensed contractor.";

		mainApp().open();
		// TODO
		// change create to getCopied
		createCustomerIndividual();
		createQuote();
		// getCopiedQuote();

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());

		// 3. Verify an eligibility error if 4 not rented detached structures are added
		propertyInfoTab.fillTab(getTestSpecificTD("DetachedStructures_4_NotRented"));
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA9230000);
		removeAllDetachedStructures();

		// 4. Verify an eligibility error if 3 rented detached structures are added
		propertyInfoTab.fillTab(getTestSpecificTD("DetachedStructures_3_Rented"));
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA12260015);
		removeAllDetachedStructures();

		// 5. Verify an eligibility error if Limit of Liabilities + CovB > CovA
		propertyInfoTab.fillTab(getTestSpecificTD("DetachedStructures_CovBGreaterCovA"));
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA12240000);
		removeAllDetachedStructures();

		// 6. Verify an eligibility error if Limit of Liabilities >= 50% * CovA
		propertyInfoTab.fillTab(getTestSpecificTD("DetachedStructures_TotalGreaterHalfCovA"));
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA12240080);
		removeAllDetachedStructures();

		// 7. Verify an eligibility error if Roof renovation = "3+ layers"
		propertyInfoTab.fillTab(getTestSpecificTD("RoofRenovation_3_PlusLayers"));
		propertyInfoTab.submitTab();
		assertThat(propertyInfoTab.getHomeRenovationAssetList().getWarning(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION)).valueContains(Errors.ERROR_AAA_HO_CA7220432.getMessage());
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA7220432);
		propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION).setValue("");

		// 8. Verify an eligibility error if Roof shape = "Flat"
		String roofShape = propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.ROOF_SHAPE).getValue();
		propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.ROOF_SHAPE.getLabel(), ComboBox.class).setValue("Flat");
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.submitTab();
		assertThat(new PurchaseTab().btnApplyPayment).isPresent();
		PurchaseTab.buttonCancel.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.ROOF_SHAPE.getLabel(), ComboBox.class).setValue(roofShape);

		// 9. Verify an eligibility error if Wood Stove Is Sole Source Of Heat
		propertyInfoTab.fillTab(getTestSpecificTD("Stove_SoleSourceOfHeat"));
		assertThat(propertyInfoTab.getStovesAssetList().getWarning(HomeCaMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT)).valueContains(expected_ER0908);
		propertyInfoTab.fillTab(getTestSpecificTD("Stove_SoleSourceOfHeat2"));
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, expected_ER0908)).isPresent();
		errorTab.cancel();

		// 10. Verify an eligibility error if there is a Wood Stove but no fire alarm
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(getTestSpecificTD("Stove_NoFireAlarm"));
		assertThat(propertyInfoTab.getStovesAssetList().getWarning(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY)).valueContains(expected_ER0522);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, expected_ER0522)).isPresent();
		errorTab.cancel();

		// 11. Verify an eligibility error if Wood Stove wasn't installed professionally
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(getTestSpecificTD("Stove_NotInstalledProfessionally"));
		//propertyInfoTab.submitTab();
		assertThat(propertyInfoTab.getStovesAssetList().getWarning(HomeCaMetaData.PropertyInfoTab.Stoves.WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR)).valueContains(expected_ER0909);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, expected_ER0909)).isPresent();
		errorTab.cancel();
		//propertyInfoTab.getStovesAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_PROPERTY_HAVE_A_WOOD_BURNING_STOVE.getLabel(), RadioGroup.class).setValue("No");
	}

	/**
	  * @author Jurij Kuznecov
	  * @name Test CAH Quote Determine Eligibility SC2
	  *     rules for:
	  *     - Year Built
	  *     - Loss Info
	  *     - Animal Info
	  * @scenario 
	  * 1.  Create new or open existent Customer
	  * 2.  Create a new HO3 quote and fill mandatory fields
	  * 3.  Verify an eligibility error if Year built < 1900
	  * 4.  Verify an eligibility error if there is one liability loss claim
	  * 5.  Verify an eligibility error if Claim is more than 25000 in 3 years
	  * 6.  Verify error if animal count is > 100
	  */

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
	public void testQuoteDetermineEligibilitySC2(@Optional("CA") String state) {

		mainApp().open();
		// TODO
		// change create to getCopied
		createCustomerIndividual();
		createQuote();
		// getCopiedQuote();

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());

		// 3. Verify an eligibility error if Year built < 1900
		String yearBuilt = propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), TextBox.class).getValue();
		propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), TextBox.class).setValue("1899");
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA7220104);
		propertyInfoTab.getConstructionAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), TextBox.class).setValue(yearBuilt);

		// 4. Verify an eligibility error if there is one liability loss claim
		propertyInfoTab.fillTab(getTestSpecificTD("ClaimHistory_LossClaim"));
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA1219040y);

		// 5. Verify an eligibility error if Claim is more than 25000 in 3 years
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel(), TextBox.class).setValue("26000");
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA12190315);
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.BTN_REMOVE).click();
		Page.dialogConfirmation.confirm();

		// 6. Verify error if animal count is > 100
		propertyInfoTab.fillTab(getTestSpecificTD("AnimalCount_MoreThanHundred"));
		propertyInfoTab.getPetsOrAnimalsAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.PetsOrAnimals.BTN_ADD.getLabel(), Button.class).click();
		errorTab.verify.errorsPresent(Errors.ERROR_AAA_HO_CA7220704);
		errorTab.cancel();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.getPetsOrAnimalsAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.PetsOrAnimals.ARE_ANY_PETS_OR_ANIMALS_KEPT_ON_THE_PROPERTY.getLabel(), RadioGroup.class).setValue("No");
		Page.dialogConfirmation.confirm();
	}

	/**
	 * @author Jurij Kuznecov
	 * @name Test CAH Quote Determine Eligibility SC3
	 *     rules for:
	 *     - Coverage - HO3
	 *     - Additional Insured Count
	 *     - Home Renovation
	 * @scenario 
	 * 1.  Create new or open existent Customer
	 * 2.  Create a new HO3 quote and fill mandatory fields
	 * 3.  Verify an eligibility error if  CovA  is more than 20% ABOVE the home replacement cost from ISO
	 * 4.  Verify an eligibility error if there are > 2 Additional Insured
	 * 5.  Verify an eligibility error if electrical renovation is not eligible
	 * 6.  Verify an eligibility error if roof renovation is not eligible
	 * 7.  Verify an eligibility error if heat renovation is not eligible
	 * 8.  Verify an eligibility error if plumbing renovation is not eligible
	 */
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
	public void testQuoteDetermineEligibilitySC3(@Optional("CA") String state) {

		mainApp().open();
		// TODO
		// change create to getCopied
		createCustomerIndividual();
		createQuote();
		// getCopiedQuote();

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());

		// 3. Verify an eligibility error if CovA is more than 20% ABOVE the home replacement cost from ISO
		/*propertyInfoTab.fillTab(getTestSpecificTD("CovA_MoreThan20PercentsOfReplacementCosts"));  // rule disabled according to PPS-371
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CACovAReplacementCost);
		String coverageA = getTestSpecificTD("CovA_MoreThan20PercentsOfReplacementCosts").getTestData(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(),
			HomeCaMetaData.PropertyInfoTab.PropertyValue.class.getSimpleName()).getValue(HomeCaMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST.getLabel());
		propertyInfoTab.getPropertyValueAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), TextBox.class).setValue(coverageA); */

		// 4. Verify an eligibility error if there are > 2 Additional Insured
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
		mortgageesTab.fillTab(getTestSpecificTD("AdditionalInsured_CountMoreThan2"));
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA7231530);
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
		mortgageesTab.getAssetList().getAsset(HomeCaMetaData.MortgageesTab.IS_THERE_ADDITIONA_INSURED.getLabel(), RadioGroup.class).setValue("No");
		Page.dialogConfirmation.confirm();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(getTestSpecificTD("HomeRenovation_SetCorrectValues"));

		// 5. Verify an eligibility error if electrical renovation is not eligible
		// Other
		propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ELECTRICAL_RENOVATION.getLabel(), ComboBox.class).setValue("Other");
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		setCorrectValueOfComboBox(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ELECTRICAL_RENOVATION.getLabel());

		// 6. Verify an eligibility error if roof renovation is not eligible
		// Partial replace
		propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION.getLabel(), ComboBox.class).setValue("Partial replace");
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		// Spot repair
		propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION.getLabel(), ComboBox.class).setValue("Spot repair");
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		setCorrectValueOfComboBox(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION.getLabel());
		// > than 25 years ago
		// propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_YEAR_OF_COMPLECTION.getLabel(),
		// TextBox.class).setValue("1980");
		// goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		// setCorrectValueOfTextBox(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_YEAR_OF_COMPLECTION.getLabel());
		// percent of completion < 100%
		propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_PERCENT_COMPLETE.getLabel(), TextBox.class).setValue("80");
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		setCorrectValueOfTextBox(HomeCaMetaData.PropertyInfoTab.HomeRenovation.ROOF_PERCENT_COMPLETE.getLabel());

		// 7. Verify an eligibility error if heat renovation is not eligible
		// Space Heater
		propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_RENOVATION.getLabel(), ComboBox.class).setValue("Space Heater");
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		setCorrectValueOfComboBox(HomeCaMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_RENOVATION.getLabel());
		// > than 25 years ago
		// propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_YEAR_OF_COMPLECTION.getLabel(),
		// TextBox.class).setValue("1980");
		// goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		// setCorrectValueOfTextBox(HomeCaMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_YEAR_OF_COMPLECTION.getLabel());

		// 8. Verify an eligibility error if plumbing renovation is not eligible
		// Other
		propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_RENOVATION.getLabel(), ComboBox.class).setValue("Other");
		goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		setCorrectValueOfComboBox(HomeCaMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_RENOVATION.getLabel());
		// > than 25 years ago
		// propertyInfoTab.getHomeRenovationAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_YEAR_OF_COMPLECTION.getLabel(),
		// TextBox.class).setValue("1980");
		// goToBindAndVerifyError(Errors.ERROR_AAA_HO_CA_15011_1);
		// setCorrectValueOfTextBox(HomeCaMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_YEAR_OF_COMPLECTION.getLabel());
	}

	private void goToBindAndVerifyError(Errors errorCode) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.btnPurchase.click();
		errorTab.verify.errorsPresent(errorCode);
		errorTab.cancel();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
	}

	private void removeAllDetachedStructures() {
		propertyInfoTab.getDetachedStructuresAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.DetachedStructures.ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY.getLabel(), RadioGroup.class)
			.setValue("No");
		Page.dialogConfirmation.confirm();
	}

	private void setCorrectValueOfTextBox(String field) {
		propertyInfoTab.getHomeRenovationAssetList().getAsset(field, TextBox.class).setValue(
			getTestSpecificTD("HomeRenovation_SetCorrectValues").getTestData(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(), HomeCaMetaData.PropertyInfoTab.HomeRenovation.class.getSimpleName())
				.getValue(field));
	}

	private void setCorrectValueOfComboBox(String field) {
		propertyInfoTab.getHomeRenovationAssetList().getAsset(field, ComboBox.class).setValue(
			getTestSpecificTD("HomeRenovation_SetCorrectValues").getTestData(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(), HomeCaMetaData.PropertyInfoTab.HomeRenovation.class.getSimpleName())
				.getValue(field));
	}
}
