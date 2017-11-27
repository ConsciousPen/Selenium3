/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.models.DocumentDataSection;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.RadioGroup;

import java.util.List;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;

public class TestCINDocuments extends AutoCaSelectBaseTest {

	/**
	 * @author Rokas Lazdauskas
	 * @name Test CIN Document generation
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with Driver which has chargeable violation
	 * 3. Check that 'Customer Informatio Notice' file is generated.
	 * @detailss
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.FUNCTIONAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-1267")
	public void testCINNewBusiness(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();

		TestData testData = getPolicyTD();
		testData.adjust(TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName()), getTestSpecificTD("PrefillTab"))
				.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName()), getTestSpecificTD("DriverTab"))
				.adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName()), getTestSpecificTD("PremiumAndCoveragesTab"))
				.adjust(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName()), getTestSpecificTD("DocumentsAndBindTab"));
		createPolicy(testData);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String policyNumber = PolicySummaryPage.getPolicyNumber();




	}

    private void documentPaymentMethodCheckInDb(String policyNumber) {
//		assertThat(docData).isNotEmpty();
//        String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AH35XX", "POLICY_ISSUE");
//        CustomAssert.assertTrue(DbAwaitHelper.waitForQueryResult(query, 5));
//        CustomAssert.assertTrue(DocGenHelper.getDocumentDataElemByName("AcctNum", DocGenEnum.Documents.AH35XX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()
//                .contains(visaNumberScreened));
//
//        String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, "AH35XX", "AUTO_PAY_METNOD_CHANGED");
//        CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(query2).get()), numberOfDocuments);
    }
}
