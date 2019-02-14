/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import aaa.common.components.MPDSearchTableElement;
import aaa.common.components.MPDTableElement;
import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.SelectSearchDialog;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.collection.Links;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class GeneralTab extends Tab {

	public static Table tblInsuredList = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_Insured_body']//table"));

	public static Table tblOtherAAAProducts_GeneralTabTable = new Table(By.id("policyDataGatherForm:otherAAAProductsTable"));

	public static Table tblOtherAAAProducts_SearchResults = new Table(By. id("autoOtherPolicySearchForm:elasticSearchResponseTable"));

	public static List<String> _listOfMPDTableColumnNames = Arrays.asList("Policy Number / Address", "Policy Type", "Customer Name/DOB", "Expiration Date", "Status", "MPD");

	public static List<String> _listOfMPDSearchResultsTableColumnNames = Arrays.asList("Customer Name/Address", "Date of Birth", "Policy Type", "Other AAA Products/Policy Address", "Status", "Select");

	public GeneralTab() {
		super(AutoSSMetaData.GeneralTab.class);
	}

	public MultiAssetList getNamedInsuredInfoAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), MultiInstanceAfterAssetList.class);
	}

	public AddressValidationDialog getValidateAddressDialogAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_DIALOG);
	}

	public AssetList getAAAMembershipAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AssetList.class);
	}

	public AssetList getOtherAAAProductOwnedAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED.getLabel(), AssetList.class);
	}

	public Table getOtherAAAProductTable() {
		return getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIST_OF_PRODUCTS_OWNED.getLabel(), FillableTable.class).getTable();
	}

	public AssetList getListOfProductsRowsAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.LIST_OF_PRODUCT_ROWS.getLabel(), AutoSSMetaData.GeneralTab.LIST_OF_PRODUCT_ROWS.getControlClass());
	}

	public Table getManualSearchResultTable() {
		return getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.RESULT_TABLE.getLabel(), FillableTable.class).getTable();
	}

	public SelectSearchDialog getSearchOtherAAAProducts() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.SEARCH_OTHER_AAA_PRODUCTS.getLabel(), SelectSearchDialog.class);
	}

	public AssetList getSearchOtherAAAProductsAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.SEARCH_OTHER_AAA_PRODUCTS.getLabel(), AssetList.class);
	}

	public AssetList getCurrentCarrierInfoAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), AssetList.class);
	}

	public AssetList getPolicyInfoAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AssetList.class);
	}

	public AssetList getContactInfoAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.CONTACT_INFORMATION.getLabel(), AssetList.class);
	}

	public InquiryAssetList getPolicyInfoInquiryAssetList() {
		return getInquiryAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), InquiryAssetList.class);
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}

	public void removeInsured(int index) {
		if (tblInsuredList.isPresent() && tblInsuredList.getRow(index).isPresent()) {
			tblInsuredList.getRow(index).getCell(4).controls.links.get("Remove").click(Waiters.AJAX);
			Page.dialogConfirmation.confirm();
		}
	}

	public void viewInsured(int index) {
		if (tblInsuredList.isPresent() && tblInsuredList.getRow(index).isPresent()) {
			tblInsuredList.getRow(index).getCell(4).controls.links.get("View/Edit").click(Waiters.AJAX);
		}
	}

	/**
	 * Adds another named insured and fills out required data.
	 * @param firstName is named insured's first name.
	 * @param lastName is named insured's last name.
	 * @param dateOfBirth is named insured's date of birth in mm/dd/yyyy format
	 * @param livedHereLessThan3Years is "Yes" or "No" if named insured has lived at location for less than 3 years.
	 * @param residence can be any option in the Residence drop down.
	 */
	public void addAnotherNamedInsured(String firstName, String lastName, String dateOfBirth, String livedHereLessThan3Years, String residence){
		GeneralTab generalTab = new GeneralTab();

		// Click Add Insured Button
		generalTab.getNamedInsuredInfoAssetList()
				.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getLabel(),
						AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getControlClass()).click(Waiters.AJAX);

		// Click cancel on the Named Insured Popup
		generalTab.getNamedInsuredInfoAssetList()
				.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_SEARCH_DIALOG.getLabel(),
						AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_SEARCH_DIALOG.getControlClass()).cancel();

		// First Name
		generalTab.getNamedInsuredInfoAssetList().
				getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(),
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getControlClass()).setValue(firstName);

		// Last Name
		generalTab.getNamedInsuredInfoAssetList().
				getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(),
						AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getControlClass()).setValue(lastName);

		// Date of Birth
		generalTab.getNamedInsuredInfoAssetList().
				getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH.getLabel(),
						AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH.getControlClass()).setValue(dateOfBirth);

		// Lived here less than 3 years
		generalTab.getNamedInsuredInfoAssetList().
				getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS.getLabel(),
						AutoSSMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS.getControlClass()).setValue(livedHereLessThan3Years);

		// Residence
		generalTab.getNamedInsuredInfoAssetList().
				getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(),
						AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getControlClass()).setValue(residence);
	}

    public void mpd_SearchAndAddManually(String policyType, String policyNumber){
        mpd_SearchByPolicyNumber(policyType, policyNumber);
        mpd_ManuallyAddPolicyAfterNoResultsFound(policyType);
    }

	/**
	 * Removes all policies from the Other AAA Products Owned table.
	 */
	public void removeAllOtherAAAProductsOwnedTablePolicies(){
		List<Row> rows = this.getOtherAAAProductTable().getRows();

		int zeroBasedRowIterator = rows.size() - 1;

		// Start at end of list since table gets smaller
		for (int i = zeroBasedRowIterator; i >= 0; i-- ){
			// Uses cell index due to column not labelled
			rows.get(i).getCell(7).controls.links.get("Remove").click(Waiters.AJAX);
		}
	}

	/**
	 * Simply conducts a basic search using the input String as a policy number.
	 * @param inputPolicyNumber
	 */
	public void mpd_SearchByPolicyNumber(String policyType, String inputPolicyNumber){
		getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Policy Number");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_TYPE.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_TYPE.getControlClass()).setValue(policyType);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getControlClass()).setValue(inputPolicyNumber);

		if (!policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
			getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
		}

	}

	/**
	 * This method is used when viewing the Search Other AAA Products popup after searching via Policy Number. <br>
	 * Will simply click 'Add' button, unless provided instruction to change data.
	 */
	public void mpd_ManuallyAddPolicyAfterNoResultsFound(String policyType){
	    if(policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME.getLabel()) || policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS.getLabel()) ||
                policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO.getLabel())){

			getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_HOME_RENTERS_CONDO_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_HOME_RENTERS_CONDO_BTN.getControlClass()).click();

        }else{
			getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_MOTOR_OR_LIFE_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_MOTOR_OR_LIFE_BTN.getControlClass()).click();
        }
	}

	/**
	 * This method is used when viewing the Search Other AAA Products popup after searching via Policy Number. <br>
	 * Will simply click 'Add' button after changing policy data. <br>
	 * @param policyType The type of policy being entered.
	 * @param inputPolicyNumber The policy number to search for. This field also manipulates mockwire response results, if given a mapped string.
	 */
	public void mpd_ManuallyAddPolicyAfterNoResultsFound(String policyType, String inputPolicyNumber){
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_TYPE.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_TYPE.getControlClass()).setValue(policyType);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getControlClass()).setValue(inputPolicyNumber);

        mpd_ManuallyAddPolicyAfterNoResultsFound(policyType);
	}

	/**
	 * Used to search an MPD policy, via Customer Details. Applies provided string over 'First Name' <br>
	 * @param searchFieldValue This variable is applied to the First Name field of the Customer Details Search and can manipulate response results. <br>
	 */
	public void mpd_SearchCustomerDetails(String searchFieldValue){
		getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Customer Details");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.FIRST_NAME.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.FIRST_NAME.getControlClass()).setValue(searchFieldValue);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.LAST_NAME.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.LAST_NAME.getControlClass()).setValue("JunkDataLastName");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.DATE_OF_BIRTH.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.DATE_OF_BIRTH.getControlClass()).setValue("01/01/1980");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADDRESS_LINE_1.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADDRESS_LINE_1.getControlClass()).setValue("JunkDataAddress");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.CITY.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.CITY.getControlClass()).setValue("JunkDataCity");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ZIP_CODE.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ZIP_CODE.getControlClass()).setValue("JunkDataZip");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
	}

	/**
	 * Used to search an MPD policy, via Customer Details. <br>
	 * @param firstName This parameter has been chosen to drive the search results/response. Edit this field with mapped MPD search string to manipulate which response comes back. <br>
	 * @param lastName Customer Last Name. <br>
	 * @param dateOfBirth Customer Date of Birth in 'mm/dd/yyyy' format. <br>
	 * @param address Customer Street Address. <br>
	 * @param city Customer City. <br>
	 * @param zipCode Customer Zip Code. <br>
	 */
	public void mpd_SearchCustomerDetails(String firstName, String lastName, String dateOfBirth, String address, String city, String state, String zipCode){
		getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Customer Details");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.FIRST_NAME.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.FIRST_NAME.getControlClass()).setValue(firstName);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.LAST_NAME.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.LAST_NAME.getControlClass()).setValue(lastName);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.DATE_OF_BIRTH.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.DATE_OF_BIRTH.getControlClass()).setValue(dateOfBirth);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADDRESS_LINE_1.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADDRESS_LINE_1.getControlClass()).setValue(address);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.CITY.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.CITY.getControlClass()).setValue(city);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.STATE.getLabel(), (AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.STATE.getControlClass())).setValue(state);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ZIP_CODE.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ZIP_CODE.getControlClass()).setValue(zipCode);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
	}

	public void mpd_EditPolicyInMPDTable(int index, String newPolicyType, String newPolicyNumber){
		mpdTable_getEditLinkByIndex(index).click();
		getListOfProductsRowsAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE_EDIT.getLabel(),
				AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE_EDIT.getControlClass()).setValue(newPolicyType);
		getListOfProductsRowsAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.QUOTE_POLICY_NUMBER_EDIT.getLabel(),
				AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.QUOTE_POLICY_NUMBER_EDIT.getControlClass()).setValue(newPolicyNumber);
		getListOfProductsRowsAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.SAVE_BTN.getLabel(),
				AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.SAVE_BTN.getControlClass()).click();
	}

	public void mpd_ClickRefresh() {
		getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH.getControlClass()).click();
	}

	/**
	 * Returns the 'Remove' link object, given an index. <br>
	 * @param index Index represents desired Row, where the edit link is contained.
	 * @return
	 */
	public Link mpdTable_getRemoveLinkByIndex(int index) {
		return new Link(By.id("policyDataGatherForm:otherAAAProductsTable:" + String.valueOf(index) + ":removeMPDPolicyLink"));
	}

	/**
	 * Returns the 'Edit' link object, given an index. <br>
	 * @param index Index represents desired Row, where the edit link is contained.
	 * @return
	 */
	public Link mpdTable_getEditLinkByIndex(int index){
		return new Link(By.id("policyDataGatherForm:otherAAAProductsTable:" + String.valueOf(index) + ":editMPDPolicyLink"));
	}

	/**
	 * Used to access the selectable checkbox directly.
	 * @param index
	 * @return
	 */
	public CheckBox mpdSearchTable_getSelectBoxByIndex(int index){
		index = mpdIndexWatchDog(index);
		return new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + String.valueOf(index) + ":customerSelected"));
	}

	/**
	 * Given an index beginning from 0, this will select and add the chosen system returned policy.
	 * @param index
	 */
	public void mpdSearchTable_addSelected(int index){
		new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + String.valueOf(index) + ":customerSelected")).setValue(true);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_SELECTED_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_SELECTED_BTN.getControlClass()).click();
	}

	/**
	 * Given a list of indexes, this will iterate through the list, select each index as true, then click the add selected button.
	 * @param indexList
	 */
	public void mpdSearchTable_addSelected(int[] indexList){
		for(int index : indexList)
		{
			new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + String.valueOf(index) + ":customerSelected")).setValue(true);
		}
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_SELECTED_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_SELECTED_BTN.getControlClass()).click();
	}

	/**
	 * Returns 'Policy Number / Address', 'Date of Birth', etc. data via the given row index.
	 * @param index First row of data begins at index 1, not index 0.
	 * @return
	 */
	public String mpdTable_viewData(String columnName, int index) {
		index = mpdIndexWatchDog(index);
		return tblOtherAAAProducts_GeneralTabTable.getColumn(columnName).getCell(index).getValue();
	}

	/**
	 * Returns 'Policy Number / Address', 'Date of Birth', etc. data via the given row index.
	 * @param index First row of data begins at index 1, not index 0.
	 * @return
	 */
	public String mpdSearchTable_viewData(String columnName, int index) {
		index = mpdIndexWatchDog(index);
		return tblOtherAAAProducts_SearchResults.getColumn(columnName).getCell(index).getValue();
	}

	public ArrayList<String> mpdTable_viewAllRowDataByColumn(String columnName) {
		ArrayList<String> myStringArray = new ArrayList<>();
		for(int i = 1; i <= tblOtherAAAProducts_GeneralTabTable.getRowsCount(); i++) {
			myStringArray.add(mpdTable_viewData(columnName, i));
		}
		return myStringArray;
	}

	/**
	 *
	 * @param index_RowToGet Index begins at 1.
	 * @return
	 */
	public ArrayList<String> mpdTable_viewAllColumnDataByRow(int index_RowToGet) {
		index_RowToGet = mpdIndexWatchDog(index_RowToGet);
		ArrayList<String> myStringArray = new ArrayList<>();

		for(String columnName: _listOfMPDTableColumnNames){
			myStringArray.add(mpdTable_viewData(columnName, index_RowToGet));
		}
		return myStringArray;
	}

	/**
	 *
	 * @param index_RowToGet Index begins at 1.
	 * @return
	 */
	public ArrayList<String> mpdSearchTable_viewAllColumnDataByRow(int index_RowToGet) {
		index_RowToGet = mpdIndexWatchDog(index_RowToGet);
		ArrayList<String> myStringArray = new ArrayList<>();

		for(String columnName: _listOfMPDSearchResultsTableColumnNames){
			myStringArray.add(mpdSearchTable_viewData(columnName, index_RowToGet));
		}
		return myStringArray;
	}

	/**
	 *
	 * @param index_RowToGet Index begins at 1.
	 * @return
	 */
	public MPDTableElement mpdTable_getTableRowAsObject(int index_RowToGet) {
		index_RowToGet = mpdIndexWatchDog(index_RowToGet);
		ArrayList<String> dataAsArray = mpdTable_viewAllColumnDataByRow(index_RowToGet);

		MPDTableElement _rowAsObject = new MPDTableElement(
				dataAsArray.get(0), dataAsArray.get(1), dataAsArray.get(2), dataAsArray.get(3), dataAsArray.get(4), dataAsArray.get(5)
		);
		return _rowAsObject;
	}

	/**
	 *
	 * @param index_RowToGet Index begins at 1.
	 * @return
	 */
	public MPDSearchTableElement mpdSearchResultsTable_getTableRowAsObject(int index_RowToGet){
		index_RowToGet = mpdIndexWatchDog(index_RowToGet);
		ArrayList<String> dataAsArray = mpdSearchTable_viewAllColumnDataByRow(index_RowToGet);

		MPDSearchTableElement _rowAsObject = new MPDSearchTableElement(
				dataAsArray.get(0), dataAsArray.get(1), dataAsArray.get(2), dataAsArray.get(3), dataAsArray.get(4)
		);

		return _rowAsObject;
	}

	/**
	 * Used to silently correct improper index input to mpd methods. Some methods that involve MPD tables do not begin with index 0. <br>
	 *     This method will catch an input of 0 and silently convert it to 1, which should be the row the user intended to access.
	 * @param i The input integer.
	 * @return If i = 0, returns 1.
	 */
	protected int mpdIndexWatchDog(int i){
		if(i==0){
			i = 1;
		}
		return i;
	}

	/**
	 * Returns Unquoted Checkbox control based on passed in data.
	 * @param assetDescriptor AssetDescriptor for each checkbox.
	 * @return Checkbox representing requested control.
	 */
	public CheckBox getUnquotedCheckBox(AssetDescriptor<CheckBox> assetDescriptor){
		return this.getOtherAAAProductOwnedAssetList().getAsset(assetDescriptor.getLabel(),
				assetDescriptor.getControlClass());
	}
}
