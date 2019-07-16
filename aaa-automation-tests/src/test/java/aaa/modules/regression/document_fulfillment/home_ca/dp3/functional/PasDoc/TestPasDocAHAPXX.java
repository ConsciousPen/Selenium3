package aaa.modules.regression.document_fulfillment.home_ca.dp3.functional.PasDoc;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestPasDocAHAPXX extends HomeCaDP3BaseTest {
	private PolicyDocGenActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(PolicyDocGenActionTab.class);

	/**
	 * @author Rokas Lazdauskas
	 * @name Test AHAPXX document generation in 'Documents' tab
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Quote creation
	 * 3. Fill to 'Documents' tab
	 * 4. Check that 'AutoPay Authorization Form' (AHAPXX) is available in 'Documents Available for Printing' section
	 * 5. Generate Document
	 * 6. Check that document is generated
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-30933")
	public void testAHAPXX_documentsTab(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyDefaultTD(), DocumentsTab.class);
		DocumentsTab documentsTab = new DocumentsTab();

		documentsTab.getAssetList().getAsset(HomeCaMetaData.DocumentsTab.DOCUMENTS_FOR_PRINTING.getLabel(), AssetList.class)
				.getAsset(HomeCaMetaData.DocumentsTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM).setValue("Yes");

		documentsTab.btnGenerateDocuments.click();

		String quoteNumber = documentsTab.getPolicyNumber();

		PasDocImpl.verifyDocumentsGenerated(quoteNumber,DocGenEnum.Documents.AHAPXX_CA);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Test AHAPXX document generation in 'Generate On Demand Document' on Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Quote
	 * 3. Do 'On Demand Document' action
	 * 4. Check that AHAPXX document is available
	 * 5. Generate Document
	 * 6. Check that document is generated
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-30933")
	public void testAHAPXX_GODDPage_Quote(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String quoteNumber = createQuote();
		policy.quoteDocGen().start();

		docgenActionTab.verify.documentsPresent(DocGenEnum.Documents.AHAPXX_CA);
		docgenActionTab.verify.documentsEnabled(DocGenEnum.Documents.AHAPXX_CA);

		docgenActionTab.generateDocuments(DocGenEnum.Documents.AHAPXX_CA);

		PasDocImpl.verifyDocumentsGenerated(quoteNumber, DocGenEnum.Documents.AHAPXX_CA);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Test AHAPXX document generation in 'Generate On Demand Document' on Policy
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 3. Do 'On Demand Document' action
	 * 4. Check that AHAPXX document is available
	 * 5. Generate Document
	 * 6. Check that document is generated
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = {"PAS-30933", "PAS-30936"})
	public void testAHAPXX_GODDPage_Policy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyDefaultTD().adjust(TestData.makeKeyPath(
				PurchaseMetaData.PurchaseTab.class.getSimpleName()), "@home_ss_ho3@DataGather@PurchaseTab_WithAutopay"));

		policy.quoteDocGen().start();

		docgenActionTab.verify.documentsPresent(DocGenEnum.Documents.AHAPXX_CA);
		docgenActionTab.verify.documentsEnabled(DocGenEnum.Documents.AHAPXX_CA);

		docgenActionTab.verify.documentsPresent(false, DocGenEnum.Documents.WURFICA);

		docgenActionTab.generateDocuments(DocGenEnum.Documents.AHAPXX_CA);

		PasDocImpl.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.AHAPXX_CA);
	}
}