package aaa.modules.regression.sales.auto_ca.select;

import static aaa.main.metadata.policy.AutoCaMetaData.DocumentsAndBindTab.*;
import static aaa.main.metadata.policy.AutoCaMetaData.GeneralTab.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.AbstractEditableStringElement;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

/**
 * <p> Created by lkazarnovskiy on 8/29/2017.
 * <p> <p>
 * <p> Objectives: Validate GeneralTab for auto_ca product.
 * <p> <p>
 * <p> TC Steps:
 * <p> <p>
 * <p> 1.The Account is created successfully
 * <p> 2.The quote is initiated
 * <p> 3.Page should be navigated to General Page successfully.
 * <p> Policy Information Section should have the following:
 * <p> - Source of Business (dropdown, mandatory, enabled)
 * <p> - Policy State (dropdown, disabled)
 * <p> - Policy Type (dropdown, mandatory, enabled)
 * <p> - Effective Date (enabled, mandatory, calendar pick)
 * <p> - Expiration Date (disabled, calculated)
 * <p> - Channel Type (dropdown, mandatory, enabled/disabled rules in 020-220CL)
 * <p> - Agency(dropdown, mandatory, enabled/disabled rules in 020-220CL)
 * <p> - Agency of Record (disabled)
 * <p> - Agency Location
 * <p> - Agent (dropdown, mandatory, enabled/disabled rules in 020-220CL)
 * <p> - Agent of Record  (disabled, values display both agent name and number)
 * <p> - Agent Number
 * <p> - Commission Type
 * <p> - Authorized by (not mandatory, enabled)
 * <p> - Toll Free Number
 * <p> - Language Preference (enabled, radio button, default is English)
 * <p> - Suppress Print (enabled, drop down)
 * <p> 4.The following default values should be displayed:
 * <p> - Source of Business = "New Business"
 * <p> - Policy State = default to Policy state selected on the Prefill.
 * <p> - Policy Type = Standard
 * <p> - Effective Date = Current System Date
 * <p> - Channel Type = "Location Type" field from Agency/Vendor screen
 * <p> - Location = defaults to location of record for user
 * <p> - Agency =  defaulted to Agency Name and Agency Code
 * <p> - Agency of Record (defaults to AAA NCNU for all users, including Contact Center)
 * <p> - Agent and Agent Number =default to user sign on profile
 * <p> - Agent of Record = For branch office employees, should default to agent's name based on user sign on profile. For Contact Center, should default to House Agent.
 * <p> - Authorized by = blank
 * <p> - Toll Free Number = blank
 * <p> - Language Preference (enabled, radio button, default is English)
 * <p> - Suppress Print = Print Declaration
 * <p> 5.The following fields in the Policy Information section should  have values in a drop down list that have to be validated using the Lookup Table:
 * <p> - Source of Business
 * <p> - Policy Type
 * <p> - Channel Type
 * <p> - Location
 * <p> - Agency
 * <p> - Agent (includes Agent Number)
 * <p> - Suppress Print
 * <p> 6. Select "Yes" radio buttons for "Existing AAA Policy" under "AAA Products Owned" section in the General page:
 * <p> Motorcycle  Life  Home  Renters _ Condo
 * <p> 7. Verify that Phone fields and PolicyNumber field are displayed on Bind Tab
 */

public class TestQuoteGeneralTab extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testQuoteGeneralTab(@Optional("CA") String state) {

		GeneralTab generalTab = new GeneralTab();
		TestData td = getPolicyTD();
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		policy.getDefaultView().fillUpTo(td, GeneralTab.class, false);

		verifyFieldProperties(PolicyInformation.SOURCE_OF_BUSINESS, true, "New Business");
		assertThat(generalTab.getPolicyInfoAssetList().getAsset(PolicyInformation.SOURCE_OF_BUSINESS)).
				containsAllOptions("New Business", "Non-pay Rewrite", "Spin-Off", "Split", "Rewrite", "Continuation");
		verifyFieldProperties(PolicyInformation.POLICY_STATE, false, getState());
		verifyFieldProperties(PolicyInformation.POLICY_TYPE, true, "Standard");
		assertThat(generalTab.getPolicyInfoAssetList().getAsset(PolicyInformation.POLICY_TYPE)).
				containsAllOptions("Standard", "TRUST", "Doing Business As", "Named Non Owner");
		verifyFieldProperties(PolicyInformation.EFFECTIVE_DATE, true);
		verifyFieldProperties(PolicyInformation.EXPIRATION_DATE, false);
		verifyFieldProperties(PolicyInformation.CHANNEL_TYPE, true);
		assertThat(generalTab.getPolicyInfoAssetList().getAsset(PolicyInformation.CHANNEL_TYPE)).
				containsAllOptions("Phone (Direct Sales Unit)", "AAA Agent", "Sub Producer");
		verifyFieldProperties(PolicyInformation.AGENCY, true);
		verifyFieldProperties(PolicyInformation.AGENCY_OF_RECORD, false);
		verifyFieldProperties(PolicyInformation.AGENT, true);
		verifyFieldProperties(PolicyInformation.AGENT_OF_RECORD, false);


		assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(PolicyInformation.COMISSION_TYPE)).isEnabled();
		assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(PolicyInformation.AUTHORIZED_BY)).isEnabled();
		assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(PolicyInformation.TOLLFREE_NUMBER)).hasValue("");
		assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(PolicyInformation.LANGUAGE_PREFERENCE)).hasValue("English");
		assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(PolicyInformation.SUPPRESS_PRINT)).hasValue("Print Declaration");

		generalTab.fillTab(td);
		assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(PolicyInformation.AGENT_NUMBER).getValue()).isNotEqualTo(""); //verification that field is not blank
		generalTab.submitTab();

		policy.getDefaultView().fillFromTo(td, DriverTab.class, DocumentsAndBindTab.class);
		assertThat(documentsAndBindTab.getAssetList().getAsset(WORK_PHONE_NUM)).isEnabled();
		assertThat(documentsAndBindTab.getAssetList().getAsset(MOBILE_PHONE_NUM)).isEnabled();
		documentsAndBindTab.getAssetList().getAsset(WORK_PHONE_NUM).setValue("1234567890");
		documentsAndBindTab.fillTab(td).submitTab();
		new PurchaseTab().fillTab(td).submitTab();

		log.info("General tab for Auto CA policy works properly. Test is passed for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}

	private void verifyFieldProperties(AssetDescriptor<? extends AbstractEditableStringElement> fieldDescriptor, boolean isEnabled) {
		GeneralTab generalTab = new GeneralTab();
		if (isEnabled) {
			//verification that field is enabled
			assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(fieldDescriptor)).isEnabled();
			//verification that field is mandatory
			assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(fieldDescriptor).getAttribute("class").contains("required")).isTrue();
		} else {
			assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(fieldDescriptor)).isEnabled(false);
		}
	}

	private void verifyFieldProperties(AssetDescriptor<? extends AbstractEditableStringElement> fieldDescriptor, boolean isEnabled, String expectedFieldValue) {
		GeneralTab generalTab = new GeneralTab();
		this.verifyFieldProperties(fieldDescriptor, isEnabled);
		assertThat(generalTab.getAssetList().getAsset(POLICY_INFORMATION).getAsset(fieldDescriptor)).hasValue(expectedFieldValue);
	}
}
