package aaa.modules.regression.sales.auto_ca.select.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.ADHOC_DOC_ON_DEMAND_GENERATE;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.SELECT;
import static aaa.main.enums.DocGenEnum.Documents._554000;
import static aaa.main.enums.DocGenEnum.Documents._55_4000;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.DataElementChoice;
import aaa.helpers.xml.model.DocumentDataSection;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.auto_ca.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

public class TestNyDocGen extends AutoCaSelectBaseTest {
	private final GenerateOnDemandDocumentActionTab generateOnDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();

	/**
	 * @author Viktor Petrenko
	 * @name NY doc gen check for AA11NY
	 * @scenario
	 * 1. Issue NY policy
	 * 2. Get DeclarationPage from db
	 * 3. Check symbols presence
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-2713")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2713")
	// All states except CA / NY document is generated with "N/A" in the current STAT field
	public void pas2713_ApplicationFormStatCodeNotNA(@Optional("CA") String state) {
		/*mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());*/
		String policyNumber = "CAAS926232191";
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		policy.policyDocGen().start();
		generateOnDemandDocumentActionTab.verify.documentsPresent(_554000);
		generateOnDemandDocumentActionTab.getDocumentsControl().getTable().getRow(DOCUMENT_NUM, _554000.getId()).getCell(SELECT).controls.checkBoxes.getFirst().verify.enabled(true);
		generateOnDemandDocumentActionTab.generateDocuments(_554000);

		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber,_55_4000.getId(),ADHOC_DOC_ON_DEMAND_GENERATE);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(Arrays.asList("_55_4000"));

		docsToCheck.forEach(docID -> {
			//Select doc from DB
			List<DocumentDataSection> documentDataSection = DocGenHelper.getDocumentDataElemByName("VehStAbrv", docID, query);
			DataElementChoice actualNode = documentDataSection.get(0).getDocumentDataElements().get(0).getDataElementChoice();
			//Check that doc contains expected node
			assertSoftly(softly -> softly.assertThat(actualNode).isNotEqualTo(new DataElementChoice().setTextField("N/A")).isNotNull());
		});
	}

	private List<DocGenEnum.Documents> getEnumList(List<String> valuesList) {
		return valuesList.stream().map(DocGenEnum.Documents::valueOf).collect(Collectors.toList());
	}

}
