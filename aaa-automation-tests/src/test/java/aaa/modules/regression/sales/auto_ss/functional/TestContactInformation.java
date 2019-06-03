/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;

public class TestContactInformation extends AutoSSBaseTest {

	private final ErrorTab errorTab = new ErrorTab();
	private final GeneralTab generalTab = new GeneralTab();
	private final DriverTab driverTab = new DriverTab();
	private final DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
	private final PurchaseTab purchaseTab = new PurchaseTab();

	/**
	 * @author Viktoriia Lutsenko
	 * <b> Absence of contact information for other named insureds than FNI. </b>
	 * <p> Steps:
	 * <p> 1.Initiate quote creation.
	 * <p> 2.Add FNI with only email as contact information.
	 * <p> 3.Add 2nd named insured.
	 * <p> 4.Add 3rd named insured.
	 * <p> 5.Verify that FNI has Contact Information section, 2nd and 3rd named insureds don't have Contact Information section.
	 * <p> 6.Bind policy.
	 * <p> 7.Verify that system shows error, because rule for mandatory phone number is triggered for FNI only (AAA_SS10240324).
	 * <p> 8.Fill phone number for FNI.
	 * <p> 9.Change 2nd named insured to FNI, don't fill contact information.
	 * <p> 10.FNI has contact information section, others don't have.
	 * <p> 11.Bind policy.
	 * <p> 12.Verify that system shows error, because rule for mandatory phone number is triggered (AAA_SS10240324).
	 * <p> 13.Fill contact information for the FNI.
	 * <p> 14.Bind policy.
	 * <p> 15.Verify that policy is bound.
	 *
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.UT)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-270", "PAS-267"})
	public void pas270_contactInformation(@Optional("UT") String state) {
		initiateQuote();

		verifyContactInformationSection();
		changeFNIAndVerifyContactInformationSection();
		bindPolicy();
		verifyPolicyStatus();
	}

	/**
	 * <p> Steps: #1-#4
	 */
	private void initiateQuote() {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData"), DocumentsAndBindTab.class, true);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
	}

	/**
	 * <p> Steps: #5-#7
	 */
	private void verifyContactInformationSection() {
		assertContactsInformationPresent(true, false, false);
		verificationOfMandatoryPhoneNumber();
	}

	/**
	 * <p> Steps: #8-#12
	 */
	private void changeFNIAndVerifyContactInformationSection() {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		addPhoneNumberToInsured(1, "1112223334");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValueByIndex(1);
		Page.dialogConfirmation.confirm();
		assertContactsInformationPresent(false, true, false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		setRelationshipToNI(1, 2);
		setRelationshipToNI(3, 3);
		verificationOfMandatoryPhoneNumber();
	}

	/**
	 * <p> Steps: #13-#14
	 */
	private void bindPolicy() {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		addPhoneNumberToInsured(1, "2223334445");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		policy.bind().submit();
		purchaseTab.fillTab(getTestSpecificTD("TestData")).submitTab();
	}

	/**
	 * <p> Steps: #15
	 */
	private void verifyPolicyStatus() {
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNum = PolicySummaryPage.getPolicyNumber();
		log.info("policyNum: {}", policyNum);
	}

	private void assertContactsInformationPresent(boolean firstNIPresent, boolean secondNIPresent, boolean thirdNiPresent) {
		presenceOfContactInformationSection(1, firstNIPresent);
		presenceOfContactInformationSection(2, secondNIPresent);
		presenceOfContactInformationSection(3, thirdNiPresent);
	}

	private void presenceOfContactInformationSection(int insuredNumber, boolean isPresent) {
		generalTab.viewInsured(insuredNumber);
		assertThat(generalTab.isSectionPresent("Contact Information")).as("'Contact Information' section is present").isEqualTo(isPresent);
	}

	private void setRelationshipToNI(int driverNumber, int relationship) {
		DriverTab.viewDriver(driverNumber);
		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).setValueByIndex(relationship);
	}

	private void verificationOfMandatoryPhoneNumber() {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonNext.click();
		if (driverActivityReportsTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).isPresent() && driverActivityReportsTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).isEnabled()) {
			driverActivityReportsTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
		}
		driverActivityReportsTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
		DriverActivityReportsTab.buttonNext.click();
		DocumentsAndBindTab.btnPurchase.click();
		assertThat(errorTab.tableErrors.getRowContains("Message", ErrorEnum.Errors.ERROR_AAA_SS10240324.getMessage())).isPresent();
		assertThat(errorTab.getErrorsControl().getTable()).as("Error with code 'AAA_SS10240324' should be displayed only for first named insured").hasRows(1);
		errorTab.cancel();
	}

	private void addPhoneNumberToInsured(int insuredNumber, String phoneNumber) {
		generalTab.viewInsured(insuredNumber);
		generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.HOME_PHONE_NUMBER).setValue(phoneNumber);
	}
}
