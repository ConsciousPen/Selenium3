/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import aaa.modules.regression.service.helper.HelperMiniServices;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.regression.service.helper.TestMiniServicesBillingAbstract;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesBilling extends TestMiniServicesBillingAbstract {

	public HelperMiniServices helperMiniServices = new HelperMiniServices();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Current Bill Service for non-Annual
	 * @scenario See inner method
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-1441", "PAS-5986, PAS-343"})
	public void pas13663_CurrentBillServiceMonthly(@Optional("VA") String state) {
		String paymentPlan = "contains=Eleven";
		String premiumCoverageTabMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
		TestData policyTdAdjusted = getPolicyTD().adjust(premiumCoverageTabMetaKey, paymentPlan);

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(policyTdAdjusted);

		assertSoftly(softly ->
				pas13663_CurrentBillServiceBody(softly, policyNumber)
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Current Bill Service for non-Annual
	 * @scenario See inner method
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13663"})
	public void pas13663_CurrentBillServiceQuarterly(@Optional("VA") String state) {
		String paymentPlan = "contains=Quarte";
		String premiumCoverageTabMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
		TestData policyTdAdjusted = getPolicyTD().adjust(premiumCoverageTabMetaKey, paymentPlan);

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(policyTdAdjusted);

		assertSoftly(softly ->
				pas13663_CurrentBillServiceBody(softly, policyNumber)
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Current Bill Service for Annual
	 * @scenario 1. Create a policy
	 * 2. run the current bill service
	 * 3. check zero balances
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13663"})
	public void pas13663_CurrentBillServiceAnnual(@Optional("VA") String state) {
		String paymentPlan = "contains=Annual";
		String premiumCoverageTabMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
		TestData policyTdAdjusted = getPolicyTD().adjust(premiumCoverageTabMetaKey, paymentPlan);

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(policyTdAdjusted);

		assertSoftly(softly ->
				currentBillServiceCheck(softly, policyNumber)
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name View Installment Schedule Service
	 * @scenario 1. Create policy.
	 * 2. Hit Customer service, check info in the service and in UI.
	 * 3. Hit Installments service, check info.
	 * 4. Create endorsement outside of PAS.
	 * 5. Add new vehicle and new driver. Update them.
	 * 6. Bind endorsement.
	 * 7. Hit Customer service, check info in the service and in UI.
	 * 8. Hit Installments service, check info.
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16982"})
	public void pas16982_ViewInstallmentScheduleService(@Optional("VA") String state) {
		TestData policyTd = getPolicyTD().adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
				AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Quarterly");

		mainApp().open();
		//createCustomerIndividual();
		String policyNumber = createPolicy(policyTd);
				// "VASS952918979";



		//Hit account service, check all info
		assertSoftly(softly -> {
			String lastDueDate = installmentsServiceCheck(softly, policyNumber);
			currentAccountInfoServiceCheck(softly, policyNumber, lastDueDate);
		});

	}

	@Override
	protected String getGeneralTab() {
		return NavigationEnum.AutoSSTab.GENERAL.get();
	}

	@Override
	protected String getPremiumAndCoverageTab() {
		return NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get();
	}

	@Override
	protected String getDocumentsAndBindTab() {
		return NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get();
	}

	@Override
	protected String getVehicleTab() {
		return NavigationEnum.AutoSSTab.VEHICLE.get();
	}

	@Override
	protected Tab getGeneralTabElement() {
		return new GeneralTab();
	}

	@Override
	protected Tab getPremiumAndCoverageTabElement() {
		return new PremiumAndCoveragesTab();
	}

	@Override
	protected Tab getDocumentsAndBindTabElement() {
		return new DocumentsAndBindTab();
	}

	@Override
	protected AssetDescriptor<JavaScriptButton> getCalculatePremium() {
		return AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM;
	}

}
