package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.ADHOC_DOC_ON_DEMAND_GENERATE;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.SELECT;
import static aaa.main.enums.DocGenEnum.Documents.AA11NY;
import static aaa.main.enums.DocGenEnum.Documents.valueOf;
import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.*;
import static aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LICENSE_STATE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.POLICY_INFORMATION;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.*;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM;
import static aaa.main.metadata.policy.AutoSSMetaData.PremiumAndCoveragesTab.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.DataElementChoice;
import aaa.helpers.xml.model.Document;
import aaa.helpers.xml.model.DocumentDataElement;
import aaa.helpers.xml.model.DocumentDataSection;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;

public class TestNyDocGen extends AutoSSBaseTest {

	private final Tab generalTab = new GeneralTab();
	private final Tab premiumCovTab = new PremiumAndCoveragesTab();
	private final Tab driverReportTab = new DriverActivityReportsTab();
	private final Tab docAndBind = new DocumentsAndBindTab();
	private final GenerateOnDemandDocumentActionTab generateOnDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();

	/**
	 * @author Igor Garkusha
	 * @name NY doc gen check for AADNNY1, AAINXX1, AAMTNY, AASANY, AAOANY, AAACNY
	 * @scenario
	 * 1. Initiate a manual entry conversion for SIS - NY from PAS..
	 * 2. Navigate through the application and calculate the premium.
	 * 3. Run the renewal process till Renewal Offer stage.
	 * 4. Complete the renewal process on the policy.
	 * 5. Make sure the docgen xml is generated with the template id with proper Policy Number
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "include PAS-2832 ,PAS-2448 ,PAS-2829 ,PAS-2830 ,PAS-2833 ,PAS-2831")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2832")
	public void pas2832_IdentificationCardNoticeAADNNY1(@Optional("NY") String state) {
		TestData policyTd = prepareConvTD(getPolicyTD(), state);
		String policyNumber = conversionPolicyPreconditions(policyTd);
		String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		List<DocGenEnum.Documents> docsToCheck = getEnumList(getStateTestData(testDataManager.getDefault(this.getClass()), "DocToCheck").
				getList("DocumentsNames"));
		docsToCheck.forEach(docID -> {
			//Select doc from DB
			List<DocumentDataSection> docData = DocGenHelper.getDocumentDataElemByName("PlcyNum", docID, getDataSql);
			assertThat(docData).isNotEmpty();

			DataElementChoice actualNode = docData.get(0).getDocumentDataElements().get(0).getDataElementChoice();
			//Check that doc contains expected node
			assertSoftly(softly -> softly.assertThat(actualNode).isEqualTo(new DataElementChoice().setTextField(policyNumber)));
		});

	}

	/**
	 * @author Igor Garkusha
	 * @name Test Supplementary UM UIM Reject/Elect Lower Limits
	 * @scenario
	 * 1. Initiate a manual entry conversion for SIS - NY from PAS.
	 * 2. Navigate through the application and calculate the premium with BI Limit as the very lowest option selected on P&C page.
	 * 3. Run the renewal process till Renewal Offer stage.
	 * 4. Complete the renewal process on the policy.
	 * 5. Make sure the docgen xml is generated with the template id AA52NY and Physically Signed data in XML
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "include PAS-2834,PAS-2835")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2834")
	public void pas2832_docAA52PhysicallySigned(@Optional("NY") String state) {

		DocumentDataElement expectedElem = new DocumentDataElement().setName("SgnReqYN").
				setDataElementChoice(new DataElementChoice().setTextField("Y"));

		aa52TestBody("Physically Signed", "SgnReqYN", state, expectedElem);
	}

	/**
	 * * @author Igor Garkusha
	 * @name Test Supplementary UM UIM Reject/Elect Lower Limits
	 * @scenario 1. Initiate a manual entry conversion for SIS - NY from PAS.
	 * 2.Navigate through the application and calculate the premium with BI Limit as the very lowest option selected on P&C page.
	 * 3. Run the renewal process till Renewal Offer stage.
	 * 4.  Complete the renewal process on the policy.
	 * 5. Make sure the docgen xml is generated with the template id AA52NY and Electronically Signed data in XML
	 * @details include
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "include PAS-2834,PAS-2835")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2834")
	public void pas2832_docAA52ElectronicallySigned(@Optional("NY") String state) {

		DocumentDataElement expectedElem = new DocumentDataElement().setName("SgntrOnFile").
				setDataElementChoice(new DataElementChoice().setTextField("SIGNATURE ON FILE"));

		aa52TestBody("Electronically Signed", "SgntrOnFile", state, expectedElem);
	}

	/**
	 * @author Igor Garkusha
	 * @name Test Sequencing for NY Conversion Docs
	 * @scenario
	 * 1. Initiate a manual entry conversion for SIS - NY from PAS.
	 * 2. Navigate through all tabs, enter required information, calculate premium and bind the policy.
	 * 3. Check if Conversion Renewal Packet is generated.
	 * 4. Check the forms sequence on XML.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4172")
	public void pas4172_sequencingForNYConversionDocs(@Optional("NY") String state) {
		TestData policyTd = prepareConvTD(getPolicyTD(), state);
		policyTd.
				adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), SUPPLEMENTAL_SPOUSAL_LIABILITY.getLabel()), "Yes").
				adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), RENTAL_REIMBURSEMENT.getLabel()), "index=1");
		String policyNumber = conversionPolicyPreconditions(policyTd);
		String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(getStateTestData(testDataManager.getDefault(this.getClass()), "DocToCheck").
				getList("DocumentsNamesInOrder"));

		Document privDoc = null;

		for (DocGenEnum.Documents doc : docsToCheck) {

			Document currentDoc = DocGenHelper.getDocument(doc, getDataSql);
			assertThat(currentDoc).isNotNull();

			if (privDoc != null) {
				assertThat(Integer.parseInt(currentDoc.getSequence())).
						isGreaterThan(Integer.parseInt(privDoc.getSequence()));
			}
			privDoc = currentDoc;
		}
	}

	/**
	 * @author Viktor Petrenko
	 * @name PAS-706, PAS-2704 NY doc gen check for AA02NY
	 * @scenario
	 * 1. Issue NY policy
	 * 2. Get DeclarationPage from db
	 * 3. Check comp and coll symbols presence
	 * 4. Stat Code
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-2704")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2704,PAS-706")
	public void pas2704_LiabilitySymbolsPresenceDeclarationPage(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		String query = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(Arrays.asList(DocGenEnum.Documents.AA02NY.getId()));

		docsToCheck.forEach(docID -> {
			// Start PAS-2713 Scenario 1
			List<DocumentDataSection> documentDataSection = DocGenHelper.getDocumentDataElemByName("VehStatCd", docID, query);
			DataElementChoice actualNode = documentDataSection.get(0).getDocumentDataElements().get(0).getDataElementChoice();
			assertSoftly(softly -> softly.assertThat(actualNode).isNotEqualTo(new DataElementChoice().setTextField("N/A")).isNotNull());
			// End PAS-2713 Scenario 1

			// Start PAS-706
			verifyCompCollSymbolsPresence(query, docID);
			// Start PAS-706
		});
	}

	/**
	 * @author Viktor Petrenko
	 * @name NY doc gen check for AA11NY
	 * @scenario
	 * 1. Issue NY policy
	 * 2. Generate on demand document
	 * 3. Check symbols presence and statcode != NA and not empty
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-2713")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2713")
	// All states except CA / NY document is generated with "N/A" in the current STAT field
	public void pas2713_ApplicationFormStatCodeNotNA(@Optional("NY") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		policy.policyDocGen().start();
		generateOnDemandDocumentActionTab.verify.documentsPresent(AA11NY);
		generateOnDemandDocumentActionTab.getDocumentsControl().getTable().getRow(DOCUMENT_NUM, AA11NY.getId()).getCell(SELECT).controls.checkBoxes.getFirst().verify.enabled(true);
		generateOnDemandDocumentActionTab.generateDocuments(AA11NY);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, DocGenEnum.Documents.AA11NY.getId(),ADHOC_DOC_ON_DEMAND_GENERATE);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(Arrays.asList(DocGenEnum.Documents.AA11NY.getId()));
		// todo add check for liability symbols PAS-2713 Scenario 2: NY
		docsToCheck.forEach(docID -> {
			//Select doc from DB
			List<DocumentDataSection> documentDataSection = DocGenHelper.getDocumentDataElemByName("VehStsCd", docID, query);
			DataElementChoice actualNode = documentDataSection.get(0).getDocumentDataElements().get(0).getDataElementChoice();
			//Check that doc contains expected node
			assertSoftly(softly -> softly.assertThat(actualNode).isNotEqualTo(new DataElementChoice().setTextField("N/A")).isNotNull());
		});
	}

	/**
	 * @author Viktor Petrenko
	 * @name Application form stat code and liability symbols check
	 * @scenario
	 * 1. Issue All states except CA / NY
	 * 2. Generate on demand document
	 * 3. Check symbols presence and statcode = NA and not empty
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-2713")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2713")
	// All states except CA / NY document is generated with "N/A" in the current STAT field
	public void pas2713_ApplicationFormStatCodeNA(@Optional("") String state) {
		String documentId = String.format("AA11%s",state);
		DocGenEnum.Documents document = DocGenEnum.Documents.valueOf(documentId);
		// "AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, OH, OK, OR, PA, SD, UT, VA, WV, WY"
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		policy.policyDocGen().start();

		generateOnDemandDocumentActionTab.verify.documentsPresent(document);
		generateOnDemandDocumentActionTab.getDocumentsControl().getTable().getRow(DOCUMENT_NUM, document.getId()).getCell(SELECT).controls.checkBoxes.getFirst().verify.enabled(true);
		generateOnDemandDocumentActionTab.generateDocuments(document);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getId(),ADHOC_DOC_ON_DEMAND_GENERATE);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(Arrays.asList(document.getId()));
		// todo add check for liability symbols
		docsToCheck.forEach(docID -> {
			// Start PAS-2713
			List<DocumentDataSection> documentDataSection = DocGenHelper.getDocumentDataElemByName("VehStsCd", docID, query);
			DataElementChoice actualNode = documentDataSection.get(0).getDocumentDataElements().get(0).getDataElementChoice();
			//Check that doc contains expected node
			//assertSoftly(softly -> softly.assertThat(actualNode).isEqualTo(new DataElementChoice().setTextField("N/A")).isNotNull());
			// End PAS-2713
			// Start PAS-532
			verifyCompCollSymbolsPresence(query, docID);
			// End PAS-532
		});
	}


	private void verifyCompCollSymbolsPresence(String getDataSql, DocGenEnum.Documents docID) {
		List<DocumentDataSection> compDmgSymbl = DocGenHelper.getDocumentDataElemByName("CompDmgSymbl", docID, getDataSql);
		assertSoftly(softly -> softly.assertThat(compDmgSymbl).isNotEmpty());

		List<DocumentDataSection> collDmgSymbl = DocGenHelper.getDocumentDataElemByName("CollDmgSymbl", docID, getDataSql);
		assertSoftly(softly -> softly.assertThat(collDmgSymbl).isNotEmpty());
	}


	private String getVehicleInfo(int rowNum) {
		String yearVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(YEAR).getValue();
		String makeVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(MAKE).getValue();
		String modelVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(MODEL).getValue();
		return yearVeh + " " + makeVeh + " " + modelVeh;
	}

	private void aa52TestBody(String signeType, String xmlTag, String state, DocumentDataElement expectedValue) {
		//Preconditions

		TestData policyTd = prepareConvTD(getPolicyTD().
						adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), BODILY_INJURY_LIABILITY.getLabel()), "index=0"),
				state);

		policyTd.adjust(TestData.makeKeyPath(docAndBind.getMetaKey(), "RequiredToBind", SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION.getLabel()),
				signeType);
		String policyNumber = conversionPolicyPreconditions(policyTd);

		//Select data from DB
		String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);

		//Get actual value
		DocumentDataSection docData = DocGenHelper.getDocumentDataElemByName(xmlTag, valueOf("AA52" + state), getDataSql).get(0);

		//Compare with actual value
		assertSoftly(softly -> softly.assertThat(docData.getDocumentDataElements()).contains(expectedValue));
	}

	private String conversionPolicyPreconditions(TestData policyTd) {
		mainApp().open();
		initiateManualConversionR35();

		policy.getDefaultView().fillUpTo(policyTd, DriverActivityReportsTab.class);
		driverReportTab.getAssetList().getAsset(VALIDATE_DRIVING_HISTORY.getLabel(), Button.class).click();
		driverReportTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
		driverReportTab.submitTab();
		docAndBind.fillTab(policyTd);
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();
		JobUtils.executeJob(Jobs.renewalJob);
		return PolicySummaryPage.linkPolicy.getValue();

	}

	private TestData prepareConvTD(TestData policyTd, String state) {
		return policyTd.mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), EFFECTIVE_DATE.getLabel())).
				mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), LEAD_SOURCE.getLabel())).
				mask(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_PRODUCT_OWNED.getLabel(), LAST_NAME.getLabel())).
				adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_PRODUCT_OWNED.getLabel(), CURRENT_AAA_MEMBER.getLabel()), "No").
				mask(TestData.makeKeyPath(premiumCovTab.getMetaKey(), POLICY_TERM.getLabel())).
				adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), PAYMENT_PLAN.getLabel()), "Annual (Renewal)").
				adjust(TestData.makeKeyPath(new DriverTab().getMetaKey(), LICENSE_STATE.getLabel()), state).
				adjust(TestData.makeKeyPath(new VehicleTab().getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Private Passenger Auto").
				mask(TestData.makeKeyPath(docAndBind.getMetaKey(), "Agreement"));

	}

	private List<DocGenEnum.Documents> getEnumList(List<String> valuesList) {
		return valuesList.stream().map(DocGenEnum.Documents::valueOf).collect(Collectors.toList());
	}

}
