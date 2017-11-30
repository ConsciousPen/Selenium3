/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.DialogSearchEnum;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.models.DocumentDataSection;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.RadioGroup;

import java.time.LocalDateTime;
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
	 * 3. Check that 'Customer Info Notice' file is generated.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6341")
	public void testCinNewBusiness(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();

		TestData testData = getPolicyTD();
		testData.adjust(TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName()), getTestSpecificTD("PrefillTab"))
				.adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName()), getTestSpecificTD("PremiumAndCoveragesTab"));
		createPolicy(testData);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//DB check that CIN document (AHAUXX) is generated
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Test CIN Document generation
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 3. Change time to R-35
	 * 4. Create Manual Renewal for Policy (add driver which has chargeable violation)
	 * 3. Check that 'Customer Info Notice' file is generated.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6341")
	public void testCinRenewal(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();

		createPolicy();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate); //-35

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.createRenewal(getTestSpecificTD("TestData_Renewal_" + state));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//DB check that CIN document (AHAUXX) is generated
	}
}