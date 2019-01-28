/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import aaa.toolkit.webdriver.customcontrols.NoSectionsMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

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

	public GeneralTab() {
		super(AutoSSMetaData.GeneralTab.class);
	}

	public MultiAssetList getNamedInsuredInfoAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), MultiInstanceAfterAssetList.class);
	}

	public AddressValidationDialog getValidateAddressDialogAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_DIALOG);
	}

	public MultiInstanceBeforeAssetList getAAAMembershipAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), MultiInstanceBeforeAssetList.class);
	}

	public NoSectionsMultiAssetList getOtherAAAProductOwnedAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED.getLabel(), NoSectionsMultiAssetList.class);
	}

	public AssetList getSearchOtherAAAProductsAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.SEARCH_OTHER_AAA_PRODUCTS.getLabel(), AssetList.class);
	}

	public AssetList getListOfProductRowsAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.LIST_OF_PRODUCT_ROWS.getLabel(), AssetList.class);
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

    public void mpd_SearchAndAddManually(String policyType, String policyNumber){
        mpd_SearchByPolicyNumber(policyType, policyNumber);
        mpd_ManuallyAddPolicyAfterNoResultsFound(policyType);
    }

	/**
	 * Simply conducts a basic search using the input String as a policy number.
	 * @param inputPolicyNumber
	 */
	public void mpd_SearchByPolicyNumber(String policyType, String inputPolicyNumber){
		getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY_POLICY_NUMBER_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY_POLICY_NUMBER_BTN.getControlClass()).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_TYPE.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_TYPE.getControlClass()).setValue(policyType);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getControlClass()).setValue(inputPolicyNumber);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
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
		getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), Button.class).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY_CUSTOMER_DETAILS_BTN.getLabel(), Button.class).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.FIRST_NAME.getLabel(), TextBox.class).setValue(searchFieldValue);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.LAST_NAME.getLabel(), TextBox.class).setValue("JunkDataLastName");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.DATE_OF_BIRTH.getLabel(), TextBox.class).setValue("01/01/1980");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADDRESS_LINE_1.getLabel(), TextBox.class).setValue("JunkDataAddress");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.CITY.getLabel(), TextBox.class).setValue("JunkDataCity");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ZIP_CODE.getLabel(), TextBox.class).setValue("JunkDataZip");
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), Button.class).click();
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
	public void mpd_SearchCustomerDetails(String firstName, String lastName, String dateOfBirth, String address, String city, String State, String zipCode){
		getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), Button.class).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BY_CUSTOMER_DETAILS_BTN.getLabel(), Button.class).click();
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.FIRST_NAME.getLabel(), TextBox.class).setValue(firstName);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.LAST_NAME.getLabel(), TextBox.class).setValue(lastName);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.DATE_OF_BIRTH.getLabel(), TextBox.class).setValue(dateOfBirth);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADDRESS_LINE_1.getLabel(), TextBox.class).setValue(address);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.CITY.getLabel(), TextBox.class).setValue(city);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.STATE.getLabel(), ComboBox.class).setValue(State);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ZIP_CODE.getLabel(), TextBox.class).setValue(zipCode);
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), Button.class).click();
	}

	public void mpd_EditPolicyInMPDTable(int index, String newPolicyType, String newPolicyNumber){
		mpdTable_getEditLinkByIndex(index).click();
		getListOfProductRowsAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE_EDIT.getLabel(),
				AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE_EDIT.getControlClass()).setValue(newPolicyType);
		getListOfProductRowsAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.QUOTE_POLICY_NUMBER_EDIT.getLabel(),
				AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.QUOTE_POLICY_NUMBER_EDIT.getControlClass()).setValue(newPolicyNumber);
		getListOfProductRowsAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.SAVE_BTN.getLabel(),
				AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.SAVE_BTN.getControlClass()).click();
	}

	public void mpd_ClickRefresh() {
		getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH.getLabel(), Button.class).click();
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
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_SELECTED_BUTTON.getLabel(), Button.class).click();
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
		getSearchOtherAAAProductsAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.ADD_SELECTED_BUTTON.getLabel(), Button.class).click();
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
}
