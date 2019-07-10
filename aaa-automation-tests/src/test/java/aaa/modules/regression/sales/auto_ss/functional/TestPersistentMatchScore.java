package aaa.modules.regression.sales.auto_ss.functional;

import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.CheckBox;

public class TestPersistentMatchScore extends AutoSSBaseTest {

	GeneralTab _generalTab = new GeneralTab();

	/**
	 * This test will evaluate if the Match Score Value associated with customer searches is populated in the DB after adding a companion policy.
	 * @implNote Team: CIO
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.CIO, Groups.FUNCTIONAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void pas29112_TestMatchScoreValuePersistsInDB(@Optional("UT") String state) {
		initiateTestAndNavigateToDriverTab();
		addCompanionPolicy("CUSTOMER_E");
		assertDBPersistsMatchScoreValue();
	}

	/**
	 * Used to drive the test through test-setup and up to the General Tab.
	 */
	public void initiateTestAndNavigateToDriverTab() {
		mainApp().open();
		createCustomerIndividual();
		createQuoteAndFillUpTo(getPolicyDefaultTD(), GeneralTab.class, true);
	}

	/**
	 * Used to retrieve the Match Score Value from the DB, via query. <br>
	 *     Then asserts value returned is not NULL, which would indicate data did not populate correctly.
	 */
	public void assertDBPersistsMatchScoreValue() {
		String policyNumber = Tab.labelPolicyNumber.getValue();
		Tab.buttonSaveAndExit.click();
		String matchScoreValue = AAAMembershipQueries.getMatchScoreValue(policyNumber);
		CustomAssertions.assertThat(matchScoreValue).isNotNull();
	}

	/**
	 * Ripped from TestMultiPolicyDiscount.java <br>
	 *     TODO: Replace this method with a static class reference to an MPD Test Class method.
	 * @param in_policyNumber
	 */
	public void addCompanionPolicy(String in_policyNumber) {

		otherAAAProducts_SearchCustomerDetails(in_policyNumber, "junk", "01/01/2000", "123 Fake St.", "Goodyear", "AZ", "85395");
		otherAAAProductsSearchTable_addSelected(0);
	}

	/**
	 * Used to search an MPD policy, via Customer Details. <br>
	 *     TODO: Replace this method with a static class reference to an MPD Test Class method.
	 * @param firstName This parameter has been chosen to drive the search results/response. Edit this field with mapped MPD search string to manipulate which response comes back. <br>
	 * @param lastName Customer Last Name. <br>
	 * @param dateOfBirth Customer Date of Birth in 'mm/dd/yyyy' format. <br>
	 * @param address Customer Street Address. <br>
	 * @param city Customer City. <br>
	 * @param zipCode Customer Zip Code. <br>
	 */
	public void otherAAAProducts_SearchCustomerDetails(String firstName, String lastName, String dateOfBirth, String address, String city, String state, String zipCode) {
		_generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Customer Details");
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ZIP_CODE.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ZIP_CODE.getControlClass()).setValue(zipCode);
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.FIRST_NAME.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.FIRST_NAME.getControlClass()).setValue(firstName);
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.LAST_NAME.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.LAST_NAME.getControlClass()).setValue(lastName);
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.DATE_OF_BIRTH.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.DATE_OF_BIRTH.getControlClass()).setValue(dateOfBirth);
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADDRESS_LINE_1.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADDRESS_LINE_1.getControlClass()).setValue(address);
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.CITY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.CITY.getControlClass()).setValue(city);
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.STATE.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.STATE.getControlClass()).setValue(state);
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
	}

	/**
	 * Ripped from TestMultiPolicyDiscount.java <br>
	 * TODO: Replace this method with a static class reference to an MPD Test Class method.
	 * @param index
	 */
	public void otherAAAProductsSearchTable_addSelected(int index) {
		new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + index + ":customerSelected")).setValue(true);
		_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN.getControlClass()).click();
	}

}
