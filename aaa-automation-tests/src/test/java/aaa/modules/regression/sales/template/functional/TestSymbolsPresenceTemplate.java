package aaa.modules.regression.sales.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.ADHOC_DOC_ON_DEMAND_GENERATE;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.SELECT;
import static aaa.main.enums.DocGenEnum.Documents._554000;
import static aaa.main.enums.DocGenEnum.Documents._55_4000;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.DataElementChoice;
import aaa.helpers.xml.model.DocumentDataSection;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.auto_ca.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestSymbolsPresenceTemplate extends PolicyBaseTest {
	private final GenerateOnDemandDocumentActionTab generateOnDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();

	public void verifySymbolsPresence() {
		//Adjust default Data with modified VehicleTab Data
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		// Start of PAS-535 Modifies View Rating Details to display separate comp and coll symbols.
		List<String> pas535Fields = Arrays.asList("Coll Symbol", "Comp Symbol");
		pas535Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).isPresent()).isEqualTo(true));

		pas535Fields.forEach(f ->assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue().isEmpty() &&
						PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue().isEmpty()).isEqualTo(false));
		// End of PAS-535 Modifies View Rating Details to display separate comp and coll symbols.
		//For the second vehicle with VIN that did not match we should validate if Comp and Coll symbols are equals (if VIN matches they could be different)
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue())
				.isEqualTo(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue());
		// Start of PAS-2712 Update UI (View Rating Details)
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "PD Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).isPresent()).isEqualTo(true));
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Stat Code").isPresent()).isEqualTo(false);
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Vehicle Type").isPresent()).isEqualTo(false);
		// End of PAS-2712 Update UI (View Rating Details)
	}

	public void verifySymbolsPresenceInDocs(){
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());
		/*String policyNumber = "CAAS926232191";
		mainApp().open();
		SearchPage.openPolicy(policyNumber);*/

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
			// Start Check that changes doesn't affect CA PAS-532
			List<DocumentDataSection> compDmgSymbl = DocGenHelper.getDocumentDataElemByName("CompDmgSymbl", docID, query);
			assertSoftly(softly -> softly.assertThat(compDmgSymbl).isNullOrEmpty());

			List<DocumentDataSection> collDmgSymbl = DocGenHelper.getDocumentDataElemByName("CollDmgSymbl", docID, query);
			assertSoftly(softly -> softly.assertThat(collDmgSymbl).isNullOrEmpty());
			// End Check that changes doesn't affect CA PAS-532
		});
	}


	private List<DocGenEnum.Documents> getEnumList(List<String> valuesList) {
		return valuesList.stream().map(DocGenEnum.Documents::valueOf).collect(Collectors.toList());
	}
}
