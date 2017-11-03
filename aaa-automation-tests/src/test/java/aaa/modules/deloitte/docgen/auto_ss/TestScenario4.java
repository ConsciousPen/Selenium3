package aaa.modules.deloitte.docgen.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.verification.CustomAssert;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.policy.AutoSSBaseTest;

public class TestScenario4 extends AutoSSBaseTest {
	private DocumentsAndBindTab documentsAndBindTab = policy.getDefaultView().getTab(DocumentsAndBindTab.class);
	private DriverTab driverTab = policy.getDefaultView().getTab(DriverTab.class);
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();

		// Create quote
		createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_QuoteCreation").resolveLinks()));

		/*
		 * Verify: In "Documents Available for Printing" section,
		 * "Consumer Information Notice" form is listed, selected and enabled
		 * (radio button="Yes")
		 */
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		verifyConsumerInformationNoticeValue();

		/*
		 * Set Insurance Score = Yes, order reports, insurance score value will
		 * be = 10, Verify: In "Documents Available for Printing" section
		 * "Consumer Information Notice" form is listed, selected and enabled
		 * (radio button="Yes")
		 */
		NavigationPage.toViewTab(AutoSSTab.RATING_DETAIL_REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather1"), RatingDetailReportsTab.class, DocumentsAndBindTab.class);
		verifyConsumerInformationNoticeValue();

		/*
		 * Set Insurance Score = 997 (top level) Verify:
		 * "Consumer Information Notice" form is absent
		 */
		NavigationPage.toViewTab(AutoSSTab.RATING_DETAIL_REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather2"), RatingDetailReportsTab.class, DocumentsAndBindTab.class);
		verifyConsumerInformationNoticeAbsent();

		/*
		 * Add new driver that has incident (MVR) Verify:
		 * "Consumer Information Notice" form is available
		 */
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather3"), DriverTab.class, DocumentsAndBindTab.class);
		if (!getState().equals(States.VA)) {
			NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
			DriverTab.tableDriverList.selectRow(5);
			driverTab.getActivityInformationAssetList().getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER).setValue("Yes");
			policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather4"), DriverTab.class, DocumentsAndBindTab.class);
		}
		verifyConsumerInformationNoticeValue();
		
		/* Update Premium and Coverages tab
		 * To get document: Proof of Prior Insurance document, set 'Bodily Injury Liability' = $50,000/$100,000
		 * To get document: Proof of purchase date (bill of sale) for new vehicle(s), set 'New Car Added Protection' = Yes and 'Purchase Date' = /today-20d for the 4th vehicle
		 * To get document: Proof of equivalent new car added protection with prior carrier for new vehicle(s), set 'New Car Added Protection' = Yes and 'Purchase Date' = /today-40d for the 5th vehicle
		 */
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather5"), PremiumAndCoveragesTab.class, DocumentsAndBindTab.class);
		documentsAndBindTab.saveAndExit();
		
		/* Check Documents in 'Generate on Demand Document' screen for quote */
		policy.quoteDocGen().start();
		switch (getState()) {
		case States.VA:
			docgenActionTab.verify.documentsEnabled(Documents.AA11VA, Documents.AA52VA, Documents.AAIQVA, Documents.AHFMXX, Documents.AU03);
			break;
		}
		docgenActionTab.verify.documentsPresent(false, Documents.AHPNXX);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void verifyConsumerInformationNoticeValue() {
		switch (getState()) {
		case States.VA:
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.ADVERSE_ACTION_UNDERWRITING_DECISION_NOTICE).verify.value("Yes");
			break;
		default:
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.CONSUMER_INFORMATION_NOTICE).verify.value("Yes");
			break;
		}
	}

	private void verifyConsumerInformationNoticeAbsent() {
		switch (getState()) {
		case States.VA:
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.ADVERSE_ACTION_UNDERWRITING_DECISION_NOTICE).verify.present(false);
			break;
		default:
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.CONSUMER_INFORMATION_NOTICE).verify.present(false);
			break;
		}
	}
}
