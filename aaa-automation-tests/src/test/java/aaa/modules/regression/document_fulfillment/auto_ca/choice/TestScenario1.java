package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestScenario1 extends AutoCaChoiceBaseTest {
	private PolicyDocGenActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(PolicyDocGenActionTab.class);
	private PremiumAndCoveragesTab premiumTab = policy.dataGather().getView().getTab(PremiumAndCoveragesTab.class);

	/**
	 * 1. Create CA Choice Quote
	 *    To get AA59XX (Existing Damage Endorsement) document: add Damage to Vehicle (not GOOD)
	 * 2. Check Documents on GODD: displayed, enable/disable
	 * 3. Call DataGather and update the quote
	 * 	  To get AA74CAA (Rating Information Disclosure NB) document: Always generated at NB - US 19824
	 *    To get AADDCA (Discounts - Private Passenger) document (not GODD): Always generated at NB - US 21213
	 *    To get AA43CA +AA43CAB (Named Driver(s) Exclusion) document: Add Excluded Driver - US 21222
	 *    To get WUAECA (Amendatory Endorsement) document: Always generated at NB (not GODD) - US 19851
	 *    To get AA10XX (Insurance Identification Card) document: at least one private passenger vehicle and/or Motorhome with liability coverage is included - US 21197
	 *    To get AA09CA (Special Equipment Endorsement) document: Set Special Equipment Coverage - US 21196
	 *    To get AA47CA (Towing and Labor Coverage Endorsement) document: set Towing and Labor Coverage (not GODD) - US 21228
	 *    To get AA49CA (Rental Car Benefit): set Rental Reimbursement Coverage (not GOOD) - US 21231
	 *    To get AA02CA (Declaration page) document: Always generated at NB - US 21194
	 *    To get AA59XX (Existing Damage Endorsement) document: Damage to Vehicle was added (not GOOD) - US 21219
	 * 4. Check Documents on GODD: documents are enabled
	 * 5. Issue CA Choice Quote
	 * 6. Check xml file
	 * 7. Check Documents on GODD for Policy: displayed, enable/disable
	 */
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPolicyDocuments(@Optional("") String state) {

		mainApp().open();

		// 1
		createCustomerIndividual();
		createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_QuoteCreation")));

		// 2
		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		docgenActionTab.verify.documentsEnabled(softly,
				Documents.AA11CA,
				Documents.AHAPXX_CA,
				Documents.AA53CA,
				Documents.AHFMXX,
				Documents.AAIQCA
		);
		docgenActionTab.verify.documentsEnabled(softly, false,
				Documents.AA41CA,
				Documents.AA52CA,
				Documents.CAU01,
				Documents.CAU04,
				Documents.CAU08,
				Documents.CAU09
		);
		docgenActionTab.cancel();

		// 3
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoCaTab.DRIVER.get());
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_QuoteUpdate"), PremiumAndCoveragesTab.class, true);
		Tab.buttonSaveAndExit.click();

		// 4
		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsEnabled(softly,
				Documents.AA11CA,
				Documents.AA43CA,
				Documents.AHAPXX_CA,
				Documents.AHFMXX,
				Documents.AAIQCA
		);
		docgenActionTab.verify.documentsEnabled(softly, false,
				Documents.AA41CA,
				Documents.AA52CA,
				Documents.CAU01,
				Documents.CAU04,
				Documents.CAU08,
				Documents.CAU09
		);
		docgenActionTab.verify.documentsPresent(softly, false,
				Documents.AA09CA,
				Documents.AA47CA,
				Documents.AA49CA,
				Documents.AA59CA,
				Documents.AADDCA);
		docgenActionTab.cancel();

		// 5
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
		policy.dataGather().getView().fillFromTo(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase").resolveLinks()), DriverActivityReportsTab.class, PurchaseTab.class, true);
		policy.dataGather().getView().getTab(PurchaseTab.class).submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		mainApp().open();
		// 6
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				Documents.AA74CAA,
				Documents.AADDCA,
				Documents.AA43CA,
				Documents.AA43CAB,
				Documents.WUAECA,
				Documents.AA10XX,
				Documents.AA09CA,
				Documents.AA47CA,
				Documents.AA49CA,
				Documents.AA02CA,
				Documents.AA59XX,
				Documents.AARFIXX
		);

		// 7
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.policyDocGen().start();
		docgenActionTab.verify.documentsEnabled(softly, Documents.AA11CA);
		docgenActionTab.verify.documentsEnabled(softly, Documents.AA43CA);
		docgenActionTab.verify.documentsEnabled(softly, Documents.AHRCTXXPUP);
		docgenActionTab.verify.documentsEnabled(softly, Documents.CAU01);
		docgenActionTab.verify.documentsEnabled(softly, Documents.CAU02);
		docgenActionTab.verify.documentsEnabled(softly, Documents.CAU04);
		docgenActionTab.verify.documentsEnabled(softly, Documents.CAU07);
		docgenActionTab.verify.documentsEnabled(softly, Documents.CAU08);
		docgenActionTab.verify.documentsEnabled(softly, Documents.CAU09);
		docgenActionTab.verify.documentsEnabled(softly, Documents.SR22SR1P);
		docgenActionTab.verify.documentsEnabled(softly, Documents._605005);
		docgenActionTab.verify.documentsEnabled(softly, Documents.AA06XX);
		docgenActionTab.verify.documentsEnabled(softly, Documents.AA10XX);

		docgenActionTab.verify.documentsEnabled(softly, false,
				Documents.AA41CA,
				Documents.AA52CA,
				Documents.AA53CA
		);
		docgenActionTab.verify.documentsPresent(softly, false,
				Documents.AA09CA,
				Documents.AA47CA,
				Documents.AA49CA,
				Documents.AADDCA);
		docgenActionTab.cancel();

		// Check AA52CA for Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
		checkAA52CA(softly);
		Tab.buttonCancel.click();
		Page.dialogConfirmation.buttonDeleteEndorsement.click();

		// Check AA52CA for Renew
		policy.renew().start();
		checkAA52CA(softly);
		Tab.buttonSaveAndExit.click();
		softly.close();
	}

	private void checkAA52CA(ETCSCoreSoftAssertions softly) {
		NavigationPage.toViewTab(AutoCaTab.FORMS.get());
		softly.assertThat(FormsTab.tableSelectedPolicyForms.getRow("Name", "AA52CA")).isPresent(false);
		NavigationPage.toViewTab(AutoCaTab.PREMIUM_AND_COVERAGES.get());
		premiumTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY).setValue("contains=No Coverage");
		NavigationPage.toViewTab(AutoCaTab.FORMS.get());
		softly.assertThat(FormsTab.tableSelectedPolicyForms.getRow("Name", "AA52CA")).isPresent();
	}
}
