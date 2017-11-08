/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select.functional;

import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.auto_ca.HelperAutoCA;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;

public class TestMiniServicesNonPremiumBearing extends PolicyBaseTest {


	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Email change through service
	 * @scenario 1. Create customer
	 * 2. Create a policy
	 * 3. Run a request in Swagger-UI
	 * 4. Check Transaction history contains "Email Updated - External System"  for a new endorsement
	 * 5. Check quote in Inquiry mode has new eMail address in General Tab and in Documents Tab
	 * 6. Do one more endorsement, issue
	 * Check there is no extra Actions for the product
	 * Check that number of document records in the DB before running the eMail update is equal to the number of document records after update
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void pas1441_emailChangeOutOfPas(@Optional("CA") String state) {
		HelperCommon helperCommon = new HelperCommon();
		HelperAutoCA helperAutoCA = new HelperAutoCA();

		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		//BUG PAS-5815 There is an extra Endorse action available for product
		NavigationPage.comboBoxListAction.verify.noOption("Endorse");

		//PAS-343 start
		String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", "%%");
		int numberOfDocumentsRecordsInDb = Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get());
		//PAS-343 end

		String emailAddressChanged = "osi.test@email.com";
		helperCommon.emailUpdateSwaggerUi(policyNumber, emailAddressChanged);

		helperCommon.emailUpdateTransactionHistoryCheck(policyNumber);
		helperAutoCA.emailAddressChangedInEndorsementCheck(emailAddressChanged);

		//PAS-343 start
		CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get()), numberOfDocumentsRecordsInDb);
		//PAS-343 end

		helperAutoCA.secondEndorsementIssueCheck();
	}








}
