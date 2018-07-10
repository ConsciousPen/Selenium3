package aaa.modules.regression.document_fulfillment.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.MAKE;
import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.MODEL;
import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.YEAR;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestDecPlcyVehInfoHelper extends BaseTest {
	private VehicleTab vehicleTab = new VehicleTab();

	public void pas14156_DecPagePlcyVehInfoBody(PolicyType policyType, TestData policyTdAdjusted, TestData endorsementTd, String templateId, DocGenEnum.Documents documents) {
		mainApp().open();
		createCustomerIndividual();

		policyType.get().createPolicy(policyTdAdjusted);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vehicle1 = getVehicleInfo(1);
		String vehicle2 = getVehicleInfo(2);
		checkDecPlcyVehInfo(policyNumber, templateId, documents, "POLICY_ISSUE", 1, vehicle1, vehicle2);

		policyType.get().endorse().perform(endorsementTd);
		new PremiumAndCoveragesTab().calculatePremium();
		vehicleTab.saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		Waiters.SLEEP(3000).go();
		checkDecPlcyVehInfo(policyNumber, templateId, documents, "ENDORSEMENT_ISSUE", 1, vehicle1, vehicle2);
	}

	private String getVehicleInfo(int rowNum) {
		String yearVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(YEAR).getValue();
		String makeVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(MAKE).getValue();
		String modelVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(MODEL).getValue();
		return yearVeh + " " + makeVeh + " " + modelVeh;
	}

	private void checkDecPlcyVehInfo(String policyNum, String templateId, DocGenEnum.Documents documents, String eventName, int numberOfDocuments, String... vehicleInfos) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, templateId, eventName);

		assertThat(DocGenHelper.getDocumentDataElemByName("PlcyVehInfo", documents, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).isEqualTo(vehicleInfos[0]);

		for (int index = 0; index < vehicleInfos.length; index++) {
			assertThat(DocGenHelper.getDocumentDataElemByName("PlcyVehInfo", documents, query).get(0).getDocumentDataElements().
							get(index).getDataElementChoice().getTextField()).isEqualTo(vehicleInfos[index++]);
			++index;
		}

		String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, templateId, eventName);
		assertThat(DBService.get().getValue(query2).map(Integer::parseInt)).hasValue(numberOfDocuments);
	}
}
