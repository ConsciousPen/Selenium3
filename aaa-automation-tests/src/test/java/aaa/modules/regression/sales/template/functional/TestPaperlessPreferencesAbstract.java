/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.rest.wiremock.HelperWireMockStub;
import aaa.helpers.rest.wiremock.dto.PaperlessPreferencesTemplateData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import org.testng.annotations.AfterSuite;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.LinkedList;
import java.util.List;

import static aaa.helpers.rest.wiremock.dto.PaperlessPreferencesTemplateData.*;
import static aaa.main.enums.PolicyConstants.PolicyErrorsTable.MESSAGE;
import static toolkit.verification.CustomAssertions.assertThat;

public abstract class TestPaperlessPreferencesAbstract extends PolicyBaseTest {

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoveragesTab();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract Tab getPremiumAndCoveragesTabElement();

	protected abstract InquiryAssetList getInquiryAssetList();

	protected abstract AssetDescriptor<TextBox> getEnrolledInPaperless();

	protected abstract AssetDescriptor<Button> getButtonManagePaperlessPreferences();

	protected abstract AssetDescriptor<Button> getEditPaperlessPreferencesButtonDone();

	protected abstract CommonErrorTab getErrorTabElement();

	protected abstract AssetList getPaperlessPreferencesAssetList();

	protected abstract AssetDescriptor<TextBox> getIssueDate();

	protected abstract AssetDescriptor<ComboBox> getMethodOfDelivery();

	protected abstract AssetDescriptor<ComboBox> getIncludeWithEmail();

	protected abstract AssetDescriptor<RadioGroup> getApplyeValueDiscount();

	protected abstract AssetList getDocumentPrintingDetailsAssetList();

	protected abstract AssetDescriptor<ComboBox> getSuppressPrint();

	private static final String EV100003 = "The customer must choose to Opt In to Paperless Billing and Policy Documents ...";
	private static List<HelperWireMockStub> stubList = new LinkedList<>();

	/**
	 * @author Oleg Stasyuk
	 * @name Test Paperless Preferences properties and Inquiry mode
	 * @scenario 1. Create new Paperless Preferences eligible quote but for the not eligible state (PA)
	 * 2. Check Enrolled in Paperless? field and Manage Paperless Preferences button are shown in Documents tab
	 * 3. Check Manage Paperless Preferences button is enabled
	 * 4. Check Documents Delivery section fields are present
	 * 5. Check Help text for Paperless option
	 * 4. Check Paperless Preferences Popup can be opened and closed
	 * @details
	 */

	protected void pas283_paperlessPreferencesForAllStatesProducts() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		//pas283_eValuePaperlessPreferences should be put here for all states and products
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());

		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Paperless Preferences")).as("'Paperless Preferences' section should be present").isTrue();
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery Details")).as("'Document Delivery Details' section should be absent").isFalse();
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).isPresent();
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).isDisabled();
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("Yes");

		assertThat(getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences())).isPresent();
		assertThat(getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences())).isEnabled();

		//left overs of previous functionality. Showing Hiding rules will change with new story
		//getDocumentsAndBindTabElement().getAssetList().getAsset(getSuppressPrint()).verify.present(); //TODO not for PUP, if uncomment, then  add "if ProductType"
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isPresent(false);
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getIncludeWithEmail())).isPresent(false);
		//left overs of previous functionality. Lookup list will change with new story

		//PAS-3097 remove the Issue Date field from Bind tab (VA state)
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getIssueDate())).isPresent(false);
		//PAS-3097 end

		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isPresent(false);
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getIncludeWithEmail())).isPresent(false);

		//PAS-266 start
		getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences()).click();
		Waiters.SLEEP(5000).go();
		while(getPaperlessPreferencesAssetList().getAsset(getEditPaperlessPreferencesButtonDone()).isPresent())
		getPaperlessPreferencesAssetList().getAsset(getEditPaperlessPreferencesButtonDone()).click();
		//PAS-282, PAS-268, PAS-266 end

		//PAS-287 start
		String helpMessageHT10001 = "Indicates the customer's paperless notifications enrollment status. If \"Pending\", advise the customer to accept the terms and conditions. During mid-term, you may not be able to complete the endorsement until the status has changed to \"Yes\".";
		assertThat(DocumentsAndBindTab.helpIconPaperlessPreferences.getAttribute("title")).isEqualTo(helpMessageHT10001);
		//PAS-287 end

		//PAS-277 start
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery Details")).as("'Document Delivery Details' section should be absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("Send To")).as("Field 'Send To' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("Country")).as("Field 'Country' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("Zip/Postal Code")).as("Field 'Zip/Postal Code' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("Address Line 1")).as("Field 'Address Line 1' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("Address Line 2")).as("Field 'Address Line 2' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("Address Line 3")).as("Field 'Address Line 3' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("City")).as("Field 'City' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("State / Province")).as("Field 'State / Province' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("Notes")).as("Field 'Notes' is absent").isFalse();
		assertThat(getDocumentsAndBindTabElement().isFieldThatIsNotInAssetListIsPresent("Issue Date")).as("Field 'Issue Date' is absent").isFalse();
		//PAS-277 end

		getDocumentsAndBindTabElement().saveAndExit();
		policy.quoteInquiry().start();

		//PAS-269 start
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Paperless Preferences")).as("'Paperless Preferences' section should be present").isTrue();
		assertThat(getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences())).isPresent();
		assertThat(getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences())).isDisabled();

		InquiryAssetList PaperlessPreferencesInquiryAssetList = new InquiryAssetList(getDocumentsAndBindTabElement().getAssetList().getLocator(), AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.class);
		InquiryAssetList docPrintingDetailsInquiryAssetList = new InquiryAssetList(getDocumentsAndBindTabElement().getAssetList().getLocator(), AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.class);
		assertThat(PaperlessPreferencesInquiryAssetList.getStaticElement(getEnrolledInPaperless())).isPresent();
		assertThat(docPrintingDetailsInquiryAssetList.getStaticElement(getIssueDate())).isPresent(false);
		assertThat(docPrintingDetailsInquiryAssetList.getStaticElement(getMethodOfDelivery())).isPresent(false);
		assertThat(docPrintingDetailsInquiryAssetList.getStaticElement(getIncludeWithEmail())).isPresent(false);//will change based on View/Hide rules
		getDocumentsAndBindTabElement().cancel();
		//PAS-269 end
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Test Document Delivery section when paperless preferences OPT_OUT/ OPT_IN during endorsement.
	 * @scenario
	 * 1. Create policy.
	 * 2. Set policy paperless preferences to opt_out.
	 * 3. Start endorsement.
	 * 4. Don't change paperless preferences and go to the bind tab.
	 * 5. Check document delivery section. Should be displaying.
	 * 6. Set policy paperless preferences to opt_in.
	 * 7. Start endorsement again.
	 * 8. Check document delivery section. Should not be displaying.
	 * 9. Repeat all checks for renewal.
	 * @details
	 */
	protected void pas12458_documentDeliverySectionDuringEndorsement() {
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());

		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("No");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).as("'Document Delivery' section should be present").isTrue();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isPresent();
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).setValue("Email");
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getIncludeWithEmail())).isPresent();
		getDocumentsAndBindTabElement().saveAndExit();
		deleteSinglePaperlessPreferenceRequest(stub);

		HelperWireMockStub stub1 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("Pending");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).isFalse();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isAbsent();
		getDocumentsAndBindTabElement().saveAndExit();
		deleteSinglePaperlessPreferenceRequest(stub1);

		HelperWireMockStub stub2 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("Yes");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).isFalse();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isAbsent();
		getDocumentsAndBindTabElement().saveAndExit();

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.deletePendedTransaction().start().submit();

		//renewal
		policy.renew().start().submit();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("Yes");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).as("'Document Delivery' section should be absent").isFalse();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isAbsent();
		deleteSinglePaperlessPreferenceRequest(stub2);
		getDocumentsAndBindTabElement().saveAndExit();

		HelperWireMockStub stub3 = createPaperlessPreferencesRequestId(policyNumber, OPT_IN_PENDING);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("Pending");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).isFalse();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isAbsent();
		getDocumentsAndBindTabElement().saveAndExit();
		deleteSinglePaperlessPreferenceRequest(stub3);

		HelperWireMockStub stub4 = createPaperlessPreferencesRequestId(policyNumber, OPT_OUT);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("No");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).isTrue();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isPresent();
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).setValue("Mail");
		deleteSinglePaperlessPreferenceRequest(stub4);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Test Document Delivery section when paperless preferences OPT_IN_PENDING/IN/OUT
	 * @scenario
	 * 1. Create quote.
	 * 2. Set policy preferences opt_in_pending.
	 * 3. Go to data gather mode, bind tab.
	 * 4. Check document delivery section. Should not be displaying.
	 * 5. Set policy preferences opt_in.
	 * 6. Go to data gather mode, bind tab.
	 * 7. Check document delivery section. Should not be displaying.
	 * 8. Set policy preferences opt_out.
	 * 9. Go to data gather mode, bind tab.
	 * 10. Check document delivery section. Should not be displaying.
	 * @details
	 */
	protected void pas12458_documentDeliverySectionDataGatherMode() {
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		HelperWireMockStub stub = createPaperlessPreferencesRequestId(quoteNumber, OPT_IN_PENDING);

		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("Pending");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).as("'Document Delivery' section should be absent").isFalse();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isPresent(false);
		deleteSinglePaperlessPreferenceRequest(stub);
		getDocumentsAndBindTabElement().cancel(true);

		HelperWireMockStub stub2 = createPaperlessPreferencesRequestId(quoteNumber, OPT_IN);
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("Yes");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).as("'Document Delivery' section should be absent").isFalse();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isPresent(false);
		deleteSinglePaperlessPreferenceRequest(stub2);
		getDocumentsAndBindTabElement().cancel(true);

		HelperWireMockStub stub3 = createPaperlessPreferencesRequestId(quoteNumber, OPT_OUT);
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue("No");
		assertThat(getDocumentsAndBindTabElement().isSectionPresent("Document Delivery")).as("'Document Delivery' section should be absent").isFalse();
		assertThat(getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery())).isPresent(false);
		deleteSinglePaperlessPreferenceRequest(stub3);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check all combination of Policy/Billing paperless preferences OPT_IN_PENDING/IN/OUT
	 * @scenario
	 * 1. Create quote.
	 * 2. Check all combination for Policy/Billing Paperless Preferences result in expected Status
	 * @details
	 */
	protected void pas266_PaperlessPreferencesAllTransactionsBody(String policyNumber) {
		checkPaperlessPreferencesStatus(policyNumber, OPT_IN, OPT_IN, "Yes");

		checkPaperlessPreferencesStatus(policyNumber, OPT_IN, OPT_OUT, "Yes (Policy Only)");

		checkPaperlessPreferencesStatus(policyNumber, OPT_IN, OPT_IN_PENDING, "Pending (Billing)");

		checkPaperlessPreferencesStatus(policyNumber, OPT_OUT, OPT_IN, "Yes (Billing Only)");

		checkPaperlessPreferencesStatus(policyNumber, OPT_OUT, OPT_OUT, "No");

		checkPaperlessPreferencesStatus(policyNumber, OPT_OUT, OPT_IN_PENDING, "Pending (Billing Only)");

		checkPaperlessPreferencesStatus(policyNumber, OPT_IN_PENDING, OPT_IN, "Pending (Policy)");

		checkPaperlessPreferencesStatus(policyNumber, OPT_IN_PENDING, OPT_OUT, "Pending (Policy Only)");

		checkPaperlessPreferencesStatus(policyNumber, OPT_IN_PENDING, OPT_IN_PENDING, "Pending");

		checkPaperlessPreferencesStatus(policyNumber, "empty-paperless-preferences-200", null, null, "No");

		checkPaperlessPreferencesStatus(policyNumber, "paperless-preferences-error", null, null, "Error");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check all combination of Policy/Billing paperless preferences not equal to OPT_IN result in error
	 * @scenario
	 * 1. Create a policy.
	 * 2. Check all combination for Policy/Billing Paperless Preferences result in error
	 * 3. Create a renewal
	 * 4. Check all combination for Policy/Billing Paperless Preferences result in error
	 * @details
	 */
	protected void pas285_PaperlessPreferencesErrorMsgBody(String quoteNumber) {
		NavigationPage.toViewSubTab(getPremiumAndCoveragesTab());
		getPremiumAndCoveragesTabElement().getAssetList().getAsset(getApplyeValueDiscount()).setValue("Yes");
		getPremiumAndCoveragesTabElement().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM).click();

		checkPaperlessPreferencesError(quoteNumber, OPT_IN, OPT_OUT, "Yes (Policy Only)");

		checkPaperlessPreferencesError(quoteNumber, OPT_IN, OPT_IN_PENDING, "Pending (Billing)");

		checkPaperlessPreferencesError(quoteNumber, OPT_OUT, OPT_IN, "Yes (Billing Only)");

		checkPaperlessPreferencesError(quoteNumber, OPT_OUT, OPT_OUT, "No");

		checkPaperlessPreferencesError(quoteNumber, OPT_OUT, OPT_IN_PENDING, "Pending (Billing Only)");

		checkPaperlessPreferencesError(quoteNumber, OPT_IN_PENDING, OPT_IN, "Pending (Policy)");

		checkPaperlessPreferencesError(quoteNumber, OPT_IN_PENDING, OPT_OUT, "Pending (Policy Only)");

		checkPaperlessPreferencesError(quoteNumber, OPT_IN_PENDING, OPT_IN_PENDING, "Pending");

		checkPaperlessPreferencesError(quoteNumber, "paperless-preferences-200", OPT_IN, OPT_IN, "Yes", false);

		checkPaperlessPreferencesError(quoteNumber, "empty-paperless-preferences-200", OPT_OUT, OPT_OUT, "No", true);
	}

	private void checkPaperlessPreferencesStatus(String quoteNumber, String policyAction, String billingAction, String paperlessStatus) {
		checkPaperlessPreferencesStatus(quoteNumber, "paperless-preferences-200", policyAction, billingAction, paperlessStatus);
	}

	private void checkPaperlessPreferencesStatus(String quoteNumber, String templateName,
												 String policyAction, String billingAction,
												 String paperlessStatus) {
		HelperWireMockStub stub = createPaperlessPolicyBillingPreferencesRequestId(quoteNumber, templateName, policyAction, billingAction);
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue(paperlessStatus);
		NavigationPage.toViewSubTab(getGeneralTab());
		deleteSinglePaperlessPreferenceRequest(stub);
	}

	private void checkPaperlessPreferencesError(String quoteNumber, String policyAction, String billingAction, String paperlessStatus) {
		checkPaperlessPreferencesError(quoteNumber, "paperless-preferences-200", policyAction, billingAction, paperlessStatus, true);
	}

	private void checkPaperlessPreferencesError(String quoteNumber, String templateName,
												String policyAction, String billingAction,
												String paperlessStatus, boolean expectError) {
		HelperWireMockStub stub = createPaperlessPolicyBillingPreferencesRequestId(quoteNumber, templateName, policyAction, billingAction);
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		assertThat(getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless())).hasValue(paperlessStatus);
		getDocumentsAndBindTabElement().submitTab();
		if(expectError) {
			assertThat(getErrorTabElement().getErrorsControl().getTable().getRowContains(MESSAGE, EV100003)).isPresent();
		} else {
			assertThat(getErrorTabElement().getErrorsControl().getTable().getRowContains(MESSAGE, EV100003)).isAbsent();
		}
		Tab.buttonCancel.click();
		deleteSinglePaperlessPreferenceRequest(stub);
	}

	@AfterSuite()
	public void deleteAllPaperlessPreferencesRequests() {
		deleteMultiplePaperlessPreferencesRequests();
		printToLog("ALL REQUEST DELETION WAS EXECUTED");
	}

	private HelperWireMockStub createPaperlessPreferencesRequestId(String policyNumber, String paperlessAction) {
		PaperlessPreferencesTemplateData template = create(policyNumber, paperlessAction);
		HelperWireMockStub stub = HelperWireMockStub.create("paperless-preferences-200", template).mock();
		stubList.add(stub);
		printToLog("THE REQUEST ID WAS CREATED " + stub.getId());
		return stub;
	}

	private HelperWireMockStub createPaperlessPolicyBillingPreferencesRequestId(String policyNumber, String templateName,
																				String policyAction, String billingAction) {
		PaperlessPreferencesTemplateData template = createPolicyBillingActions(policyNumber, policyAction, billingAction);
		HelperWireMockStub stub = HelperWireMockStub.create(templateName, template).mock();
		stubList.add(stub);
		printToLog("THE REQUEST ID WAS CREATED " + stub.getId());
		return stub;
	}

	private void deleteMultiplePaperlessPreferencesRequests() {
		for (HelperWireMockStub stub : stubList) {
			stub.cleanUp();
			printToLog("MULTIPLE REQUEST DELETION WAS EXECUTED for " + stub.getId());
		}
		stubList.clear();
	}

	private void deleteSinglePaperlessPreferenceRequest(HelperWireMockStub stub) {
		stub.cleanUp();
		printToLog("DELETE SINGLE REQUEST WAS EXECUTED for " + stub.getId());
		stubList.remove(stub);
	}
}
