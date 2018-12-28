/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Sushil Sivaram
 * @name Test Create SS Home Policy
 * @scenario 1. Create new or open existed customer.
 * 2. Initiate HSS quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium.
 * 4. Purchase policy.
 * 5. Verify policy status is Active on Consolidated policy view.
 * 6. Move policy to first renewal
 * 7. Assert Fireline and PPC reports are not ordered at renewal batch
 * 8. move policy to second renewal
 * 9.Assert Fireline is not ordered and PPC is ordered
 * @details
 */
public class TestPolicyFirelineReportsOnRenewal extends HomeSSHO3BaseTest {

	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	private String FirelineOrderDateAtNewBusiness;
	ReportsTab reportsTab = new ReportsTab();
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.CO, States.ID,States.NV, States.OR,States.UT})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3)
	public void testPolicyCreation(@Optional("AZ") String state) {

		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		/*policyNumber = "AZH3952918546";
		mainApp().open();
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);*/



		policyNumber = PolicySummaryPage.getPolicyNumber();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.getExpirationDate()).isEqualTo(PolicySummaryPage.getEffectiveDate().plusYears(1));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//R-(35-28) = R-63
		LocalDateTime timePoint1 = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate).minusDays(28);


		HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_ISO_BATCH);
		Waiters.SLEEP(5000).go();

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.REPORTS.get());
		FirelineOrderDateAtNewBusiness = reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.ORDER_DATE.getLabel()).getValue();
		mainApp().close();

		TimeSetterUtil.getInstance().nextPhase(timePoint1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();

		String firelineOrderDateAtRMinus63 = firelineOrderDatePostRenewal();
		assertThat(FirelineOrderDateAtNewBusiness.equals(firelineOrderDateAtRMinus63));


		//renewalImageGeneration
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
		BindTab bindTab = new BindTab();
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		//NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();
		assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();

		//update PolicyStatus
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		String firelineOrderDateAfterRenewal = firelineOrderDatePostRenewal();
		mainApp().close();


		assertThat(FirelineOrderDateAtNewBusiness.equals(firelineOrderDateAfterRenewal));

	}

	private String firelineOrderDatePostRenewal() {
		mainApp().reopen();
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.REPORTS.get());
		return reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.ORDER_DATE.getLabel()).getValue();
	}

}

