package aaa.modules.regression.document_fulfillment.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class TestScenario4 extends AutoSSBaseTest {
	private DocumentsAndBindTab documentsAndBindTab = policy.getDefaultView().getTab(DocumentsAndBindTab.class);
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	
	private String policyNumber;
	
	@Parameters({ "state" })
	@StateList(states = {States.AZ, States.IN, States.OH, States.VA})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();
			String currentHandle = WebDriverHelper.getWindowHandle();
			createCustomerIndividual();

			// Create quote
			String quoteNumber = createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_QuoteCreation").resolveLinks()));

			/* Check Documents in 'Generate on Demand Document' screen for quote */
			policy.quoteDocGen().start();
			switch (getState()) {
				case States.VA:
					docgenActionTab.verify.documentsPresent(softly, Documents.AA11VA, Documents.AA52VA, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03);
					break;
				case States.AZ:
					docgenActionTab.verify.documentsPresent(softly, Documents.AA11AZ, Documents.AA52AZ_UPPERCASE, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03, Documents.AA43AZ);
					break;
				case States.IN:
					docgenActionTab.verify.documentsPresent(softly, Documents.AA11IN, Documents.AA52IN, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03, Documents.AA43IN);
					break;
				case States.OH:
					docgenActionTab.verify.documentsPresent(softly, Documents.AA11OH, Documents.AA52OH, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03, Documents.AA43OH);
					break;
				case States.WV:
					docgenActionTab.verify.documentsPresent(softly, Documents.AA11WV, Documents.AA52WV, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03, Documents.AA43WV);
					break;
			}
			docgenActionTab.verify.documentsPresent(softly, false, Documents.AHPNXX);
			docgenActionTab.cancel();

			/* Generate documents Test */
			policy.dataGather().start();
			NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.BTN_GENERATE_DOCUMENTS).click();
			WebDriverHelper.switchToWindow(currentHandle);
			switch (getState()) {
				case States.AZ:
					DocGenHelper.verifyDocumentsGenerated(softly, quoteNumber, Documents.AA11AZ, Documents.AA43AZ, Documents.AHAPXX);
					break;
				case States.IN:
					DocGenHelper.verifyDocumentsGenerated(softly, quoteNumber, Documents.AA11IN, Documents.AA43IN, Documents.AHAPXX);
					break;
				case States.OH:
					DocGenHelper.verifyDocumentsGenerated(softly, quoteNumber, Documents.AA11OH, Documents.AA43OH, Documents.AHAPXX);
					break;
				case States.VA:
					DocGenHelper.verifyDocumentsGenerated(softly, quoteNumber, Documents.AA11VA, Documents.AHAPXX);
					break;
				case States.WV:
					DocGenHelper.verifyDocumentsGenerated(softly, quoteNumber, Documents.AA11WV, Documents.AA43WV, Documents.AHAPXX);
					break;
			}
			documentsAndBindTab.cancel();

			/* Purchase */
			policy.calculatePremiumAndPurchase(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase").resolveLinks()));
			policyNumber = PolicySummaryPage.getPolicyNumber();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);

			/*
			 * Check Documents in 'Generate on Demand Document' screen for policy
			 */
			policy.policyDocGen().start();
			switch (getState()) {
				case States.VA:
					docgenActionTab.verify
							.documentsEnabled(softly, Documents.AA11VA, Documents.AA52VA, Documents.AA10XX, Documents.AASR22, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605005_SELECT, Documents._605004, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
					docgenActionTab.verify.documentsEnabled(softly, false, Documents.AHFMXX);
					break;
				case States.OH:
					docgenActionTab.verify
							.documentsEnabled(Documents.AA11OH, Documents.AA10XX, Documents.AASR22OH, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605004, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
					docgenActionTab.verify.documentsEnabled(softly, false, Documents.AA52OH);
					break;
				case States.IN:
					docgenActionTab.verify
							.documentsEnabled(softly, Documents.AA11IN, Documents.AA10XX, Documents.AASR22, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605004, Documents._605005_SELECT, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
					docgenActionTab.verify.documentsEnabled(softly, false, Documents.AA52IN, Documents.AHFMXX);
					break;
				case States.AZ:
					docgenActionTab.verify
							.documentsEnabled(softly, Documents.AA11AZ, Documents.AA10XX, Documents.AASR22, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605004, Documents._605005_SELECT, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
					docgenActionTab.verify.documentsEnabled(softly, false, Documents.AA52AZ_UPPERCASE, Documents.AHFMXX);
					break;
				case States.WV:
					docgenActionTab.verify
							.documentsEnabled(softly, Documents.AA11WV, Documents.AASR22, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605004, Documents._605005_SELECT, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
					break;
			}
			docgenActionTab.cancel();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);

			/* Check xml */
			switch (getState()) {
				case States.VA:
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AA02VA, Documents.AHNBXX);
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AASR22);
					break;
				case States.OH:
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AA02OH, Documents.AA43OH, Documents.AHNBXX);
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AASR22);
					break;
				case States.IN:
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AA02IN, Documents.AA43IN, Documents.AHNBXX);
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AASR22);
					break;
				case States.AZ:
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AA02AZ, Documents.AA43AZ, Documents.AHNBXX);
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AASR22);
					break;
				case States.WV:
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AA02WV, Documents.AA43WV, Documents.AHNBXX);
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, Documents.AASR22);
					break;
			}
		});
	}
	
	@Parameters({ "state" })
	@StateList(states = {States.AZ, States.IN, States.OH, States.VA})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_CopyFromPolicy(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();

			SearchPage.openPolicy(policyNumber);
			/* Copy from policy and make some update */
			policy.policyCopy().perform(getPolicyTD("CopyFromPolicy", "TestData"));
			policy.dataGather().start();
			
			TestData td = getTestSpecificTD("TestData_CopyFromPolicy1");
			//order membership report
			NavigationPage.toViewTab(AutoSSTab.RATING_DETAIL_REPORTS.get());
			new RatingDetailReportsTab().fillTab(td);
			
			NavigationPage.toViewTab(AutoSSTab.GENERAL.get());
			new GeneralTab().removeInsured(2);

			//policy.getDefaultView().fillUpTo(td, GeneralTab.class, true);
			//policy.getDefaultView().fillFromTo(td, DriverTab.class, DocumentsAndBindTab.class);
			
			//policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class);
			
			policy.getDefaultView().fillFromTo(td, DriverTab.class, DocumentsAndBindTab.class);
			documentsAndBindTab.saveAndExit();
			String copiedQuoteNumber = PolicySummaryPage.getPolicyNumber();

			/* Generate documents */
			policy.dataGather().start();
			NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.BTN_GENERATE_DOCUMENTS).click();

			/* Check xml */
			switch (getState()) {
				case States.AZ:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedQuoteNumber, Documents.AA11AZ, Documents.AHAPXX);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedQuoteNumber, Documents.AA43AZ, Documents.AASR22, Documents.AAPNXX, Documents.AA02AZ);
					break;
				case States.IN:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedQuoteNumber, Documents.AA11IN, Documents.AHAPXX);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedQuoteNumber, Documents.AA43IN, Documents.AASR22, Documents.AAPNXX, Documents.AA02IN);
					break;
				case States.OH:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedQuoteNumber, Documents.AA11OH, Documents.AHAPXX);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedQuoteNumber, Documents.AA43OH, Documents.AASR22, Documents.AAPNXX, Documents.AA02OH);
					break;
				case States.VA:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedQuoteNumber, Documents.AA11VA, Documents.AHAPXX);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedQuoteNumber, Documents.AASR22, Documents.AAPNXX, Documents.AA02VA);
					break;
				case States.WV:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedQuoteNumber, Documents.AA11WV, Documents.AHAPXX);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedQuoteNumber, Documents.AA43WV, Documents.AASR22, Documents.AAPNXX, Documents.AA02WV);
					break;
			}
		});
	}

	@Parameters({ "state" })
	@StateList(states = {States.AZ, States.IN, States.OH, States.VA})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_CopyFromPolicy(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();

			/* Copy from policy and make some update */
			SearchPage.openPolicy(policyNumber);
			policy.policyCopy().perform(getPolicyTD("CopyFromPolicy", "TestData"));
			policy.dataGather().start();
			policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_CopyFromPolicy2"), PurchaseTab.class, true);
			policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
			String copiedPolicyNumber = PolicySummaryPage.getPolicyNumber();

			/* Do Endorsement action */
			policy.createEndorsement(getPolicyTD("Endorsement", "TestData").adjust(getTestSpecificTD("TestData_Endorsement")));

			/* Check xml */
			switch (getState()) {
				case States.AZ:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedPolicyNumber, Documents.AA02AZ);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedPolicyNumber, Documents.AHAPXX, Documents.AA43AZ, Documents.AASR22, Documents.AAPNXX);
					break;
				case States.IN:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedPolicyNumber, Documents.AA02IN);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedPolicyNumber, Documents.AHAPXX, Documents.AA43IN, Documents.AASR22, Documents.AAPNXX);
					break;
				case States.OH:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedPolicyNumber, Documents.AA02OH);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedPolicyNumber, Documents.AHAPXX, Documents.AA43OH, Documents.AASR22, Documents.AAPNXX);
					break;
				case States.VA:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedPolicyNumber, Documents.AA02VA);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedPolicyNumber, Documents.AHAPXX, Documents.AASR22, Documents.AAPNXX);
					break;
				case States.WV:
					DocGenHelper.verifyDocumentsGenerated(softly, copiedPolicyNumber, Documents.AA02WV);
					DocGenHelper.verifyDocumentsGenerated(softly, false, copiedPolicyNumber, Documents.AHAPXX, Documents.AA43WV, Documents.AASR22, Documents.AAPNXX);
					break;
			}
		});
	}
}
