package aaa.modules.regression.document_fulfillment.home_ca.dp3.functional.PasDoc;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPasDocWURFICA extends HomeCaDP3BaseTest {
	private PolicyDocGenActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(PolicyDocGenActionTab.class);

	/**
	 * @author Rokas Lazdauskas
	 * @name Test WURFICA document generation in 'Generate On Demand Document' on Policy
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Quote
	 * 3. Do 'On Demand Document' action
	 * 4. Check that WURFICA document is available
	 * 5. Generate Document
	 * 6. Check that document is generated
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-30936")
	public void testWURFICA_policy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyDefaultTD().adjust(TestData.makeKeyPath(DocumentsTab.class.getSimpleName(),
				HomeCaMetaData.DocumentsTab.DOCUMENTS_TO_ISSUE.getLabel(),
				HomeCaMetaData.DocumentsTab.DocumentsToIssue.CALIFORNIA_RESIDENTIAL_PROPERTY_INSURANCE_DISCLOSURE.getLabel()
				), "Not Signed"));

		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsPresent(DocGenEnum.Documents.WURFICA);
		//		docgenActionTab.verify.documentsEnabled(false, DocGenEnum.Documents.WURFICA);

		docgenActionTab.generateDocuments(DocGenEnum.Documents.WURFICA);

		PasDocImpl.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.WURFICA);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Test WURFICA document generation in 'Generate On Demand Document' on Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Quote
	 * 3. Do 'On Demand Document' action
	 * 4. Check that WURFICA document is available
	 * 5. Generate Document
	 * 6. Check that document is generated
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-30936")
	public void testWURFICA_quote(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		String quoteNumber = createQuote(getPolicyDefaultTD().adjust(TestData.makeKeyPath(DocumentsTab.class.getSimpleName(),
				HomeCaMetaData.DocumentsTab.DOCUMENTS_TO_ISSUE.getLabel(),
				HomeCaMetaData.DocumentsTab.DocumentsToIssue.CALIFORNIA_RESIDENTIAL_PROPERTY_INSURANCE_DISCLOSURE.getLabel()
		), "Not Signed"));

		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsPresent(DocGenEnum.Documents.WURFICA);
		//		docgenActionTab.verify.documentsEnabled(false, DocGenEnum.Documents.WURFICA);

		docgenActionTab.generateDocuments(DocGenEnum.Documents.WURFICA);

		PasDocImpl.verifyDocumentsGenerated(quoteNumber, DocGenEnum.Documents.WURFICA);
	}
}
