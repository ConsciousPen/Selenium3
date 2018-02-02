package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.ADHOC_DOC_ON_DEMAND_GENERATE;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.SELECT;
import static aaa.main.enums.DocGenEnum.Documents.AA11NY;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.DataElementChoice;
import aaa.helpers.xml.model.DocumentDataSection;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;

public class TestSymbolsPresenceDeclarationPage extends AutoSSBaseTest {

	private final Tab generalTab = new GeneralTab();
	private final Tab premiumCovTab = new PremiumAndCoveragesTab();
	private final Tab driverReportTab = new DriverActivityReportsTab();
	private final Tab docAndBind = new DocumentsAndBindTab();
	private final GenerateOnDemandDocumentActionTab generateOnDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();

	/**
	 * @author Viktor Petrenko
	 * @name PAS-706, PAS-2704 NY doc gen check for AA02NY
	 * @scenario
	 * 1. Issue NY policy
	 * 2. Get DeclarationPage from db
	 * 3. Check comp and coll symbols presence
	 * 4. Liability symbols presence
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-2704")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2704,PAS-706")
	public void pas2704_DeclarationPage(@Optional("NY") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		String query = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(Arrays.asList(DocGenEnum.Documents.AA02NY.getId()));

		docsToCheck.forEach(docID -> {
			// Start PAS-2713 Scenario 1: all states except CA/NY stat code = N/A
			List<DocumentDataSection> VehStatCd = DocGenHelper.getDocumentDataElemByName("VehStatCd", docID, query);
			assertSoftly(softly -> softly.assertThat(VehStatCd).isNotEmpty().isNotNull());
			// Start PAS-2713 Scenario 1: all states except CA/NY stat code = N/A

			// Start PAS-2704 Scenario
			verifyLiabilitySymbolsPresence(query, docID);
			// End PAS-2704 Scenario

			// Start PAS-706 Comp and coll symbols presence
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
	 * 4. liability symbols presence
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-2713")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2713")
	// All states except CA / NY document is generated with "N/A" in the current STAT field
	public void pas2713_ApplicationFormNYStatCodeNotNA(@Optional("NY") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		policy.policyDocGen().start();
		generateOnDemandDocumentActionTab.verify.documentsPresent(AA11NY);
		generateOnDemandDocumentActionTab.getDocumentsControl().getTable().getRow(DOCUMENT_NUM, AA11NY.getId()).getCell(SELECT).controls.checkBoxes.getFirst().verify.enabled(true);
		generateOnDemandDocumentActionTab.generateDocuments(AA11NY);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, DocGenEnum.Documents.AA11NY.getId(), ADHOC_DOC_ON_DEMAND_GENERATE);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(Arrays.asList(DocGenEnum.Documents.AA11NY.getId()));

		docsToCheck.forEach(docID -> {
			// Start PAS-2713 Scenario 1: all states except CA/NY  stat code != N/A
			List<DocumentDataSection> documentDataSection = DocGenHelper.getDocumentDataElemByName("VehStsCd", docID, query);
			DataElementChoice actualNode = documentDataSection.get(0).getDocumentDataElements().get(0).getDataElementChoice();
			assertSoftly(softly -> softly.assertThat(actualNode).isNotEqualTo(new DataElementChoice().setTextField("N/A")).isNotNull());
			// Start PAS-2713 Scenario 1: all states except CA/NY

			// Start PAS-2713 Scenario 2
			verifyLiabilitySymbolsPresence(query, docID);
			// End PAS-2713 Scenario 2

			// Start PAS-532
			verifyCompCollSymbolsPresence(query, docID);
			// End PAS-532
		});

	}

	/**
	 * @author Viktor Petrenko
	 * @name Application form stat code and liability symbols check
	 * @scenario
	 * 1. Issue All states except CA / NY
	 * 2. Generate on demand document
	 * 3. Check Comp and Coll symbols presence
	 * 4. Stat Code equal to NA
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-2713")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2713, PAS-532")
	// All states except CA / NY document is generated with "N/A" in the current STAT field
	public void pas2713_ApplicationForm(@Optional("") String state) {
		String documentId = String.format("AA11%s", state);
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

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getId(), ADHOC_DOC_ON_DEMAND_GENERATE);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(Arrays.asList(document.getId()));
		docsToCheck.forEach(docID -> {
			// Start PAS-2713 Scenario 1: all states except CA/NY stat code = N/A
			List<DocumentDataSection> documentDataSection = DocGenHelper.getDocumentDataElemByName("VehStsCd", docID, query);
			DataElementChoice actualNode = documentDataSection.get(0).getDocumentDataElements().get(0).getDataElementChoice();
			assertSoftly(softly -> softly.assertThat(actualNode).isEqualTo(new DataElementChoice().setTextField("N/A")).isNotNull());
			// End PAS-2713 Scenario 1

			// Start PAS-532 All States except NY
			verifyCompCollSymbolsPresence(query, docID);
			// End PAS-532
		});
	}

	private void verifyLiabilitySymbolsPresence(String query, DocGenEnum.Documents docID) {
		Arrays.asList("BdyInjSymbl", "MPSymbl", "PdSymbl", "UmSymbl").forEach(v ->
				assertThat(DocGenHelper.getDocumentDataElemByName(v, docID, query)).isNotEmpty().isNotNull());
	}

	private void verifyCompCollSymbolsPresence(String query, DocGenEnum.Documents docID) {
		Arrays.asList("CompDmgSymbl", "CollDmgSymbl").forEach(v ->
				assertThat(DocGenHelper.getDocumentDataElemByName(v, docID, query)).isNotEmpty().isNotNull());
	}

	private List<DocGenEnum.Documents> getEnumList(List<String> valuesList) {
		return valuesList.stream().map(DocGenEnum.Documents::valueOf).collect(Collectors.toList());
	}

}
