/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.*;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PolicyBaseTest extends BaseTest {

	protected IPolicy policy;
	private TestData tdPolicy;

	public PolicyBaseTest() {
		PolicyType type = getPolicyType();
		if (type != null) {
			policy = type.get();
		}
		tdPolicy = testDataManager.policy.get(type);
	}

	protected TestData getPolicyTD() {
		return getPolicyTD("DataGather", "TestData");
	}

	protected TestData getCopyFromPolicyTD() {
		return getPolicyTD("CopyFromPolicy", "TestData");
	}

	protected TestData getPolicyTD(String fileName, String tdName) {
		return getStateTestData(tdPolicy, fileName, tdName);
	}

	protected TestData getBackDatedPolicyTD() {
		return getBackDatedPolicyTD(getPolicyType(), DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
	}

	protected TestData getBackDatedPolicyTD(String date) {
		return getBackDatedPolicyTD(getPolicyType(), date);
	}

	protected TestData getBackDatedPolicyTD(PolicyType type) {
		return getBackDatedPolicyTD(type, DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
	}

	protected TestData getBackDatedPolicyTD(PolicyType type, String date) {
		TestData returnValue = getPolicyTD();
		switch (type.getName()) {
			case "Personal Umbrella Policy":
				String pupKey = TestData.makeKeyPath(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(), PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel());
				TestData tdPup = returnValue.resolveLinks().adjust(pupKey, date);
				//String pupAutoKey = TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
				TestData tdPupAuto = DataProviderFactory.emptyData();
				if (getState().equals(Constants.States.CA)) {
					String pupHomeCaKey = TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel());
					String pupHomeCaDateKey = TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel());
					TestData tdPupHomeCa = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO3), "DataGather", "TestData").resolveLinks().adjust(pupHomeCaDateKey, date).adjust(pupHomeCaKey, date);
					//tdPupAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT), "DataGather", "TestData").resolveLinks().adjust(pupAutoKey, date);
					tdPup = new PrefillTab().adjustWithRealPolicies(tdPup, getPrimaryPoliciesForPup(tdPupHomeCa, tdPupAuto));
				} else {
					String pupHomeKey = TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel());
					String pupHomeDateKey = TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel());
					TestData tdPupHome = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData").resolveLinks().adjust(pupHomeDateKey, date).adjust(pupHomeKey, date);
					tdPup = new PrefillTab().adjustWithRealPolicies(tdPup, getPrimaryPoliciesForPup(tdPupHome, tdPupAuto));
				}
				return tdPup.resolveLinks().adjust(pupKey, date);

			case "Homeowners Signature Series":
				String homeKey = TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel());
				String propertyDateKey = TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel());
				return returnValue.resolveLinks().adjust(homeKey, date).adjust(propertyDateKey, date);

			case "California Homeowners":
				String homeCaDateKey = TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel());
				String baseDateKey = TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel());
				return returnValue.resolveLinks().adjust(homeCaDateKey, date).adjust(baseDateKey, date);

			case "California Auto":
				String autoCaKey = TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
				return getPolicyTD().resolveLinks().adjust(autoCaKey, date);

			case "Auto Signature Series":
				String autoKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
				return returnValue.resolveLinks().adjust(autoKey, date);

			default:
				return returnValue;
		}
	}

	/**
	 * Check that documents exist in XML in aaaDocGenEntity event (XML is sent to DCS to create actual .pdf documents)
	 *
	 * @param policyNumber - policy number
	 * @param eventName - event name on which documents are suppose to be generated
	 * @param docGenIds - document ids
	 */
	protected void checkDocGenTriggered(String policyNumber, AaaDocGenEntityQueries.EventNames eventName, String... docGenIds) {
		List<Document> policyDocuments = DocGenHelper.getDocumentsList(policyNumber, eventName);
		Object[] documentTemplate = policyDocuments.stream().map(Document::getTemplateId).toArray();
		for (String docGenId : docGenIds) {
			assertThat(documentTemplate).contains(docGenId);
		}
	}

	/**
	 * Check that documents DOES NOT exist in XML in aaaDocGenEntity event (XML is sent to DCS to create actual .pdf documents)
	 *
	 * @param policyNumber - policy number
	 * @param eventName - event name on which documents are suppose to be generated
	 * @param docGenIds - document ids
	 */
	protected void checkDocGenIsNotTriggered(String policyNumber, AaaDocGenEntityQueries.EventNames eventName, String... docGenIds) {
		List<Document> policyDocuments = DocGenHelper.getDocumentsList(policyNumber, eventName);
		Object[] documentTemplate = policyDocuments.stream().map(Document::getTemplateId).toArray();
		for (String docGenId : docGenIds) {
			assertThat(documentTemplate).doesNotContain(docGenId);
		}
	}

	/**
	 * Sets the DONOTRENEWIND value in DB to "1" and exempt it from being renewed.
	 * Can be used when renewal jobs running in parallel are creating a renewal for a policy that should not be there for another test.
	 * NOTE:  You must use this in conjunction with policy.removeDoNotRenew() if/when you wish to create a renewal.
	 * @param policyNumber String value representing the policy number
	 */
	protected void setDoNotRenewFlag(String policyNumber) {
		setDoNotRenewFlag(policyNumber, "1");
	}

	/**
	 * See description above.
	 * @param policyNumber String value representing the policy number
	 * @param flagValue value you wish to set the DONOTRENEWIND value in DB to.  Possible values include 1, 0, null
	 *
	 */
	protected void setDoNotRenewFlag(String policyNumber, String flagValue) {
		DBService.get().executeUpdate("update POLICYSUMMARY set DONOTRENEWIND = " + flagValue + " where policyNumber = '" + policyNumber + "'");
	}

	protected String openAppAndCreatePolicy(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		return createPolicy(testData);
	}

	protected String openAppAndCreatePolicy() {
		return openAppAndCreatePolicy(getPolicyTD());
	}

	protected String openAppAndCreateConversionPolicy(TestData tdManualConversionInitiation, TestData tdPolicy) {
		mainApp().open();
		createCustomerIndividual();
		return createConversionPolicy(tdManualConversionInitiation, tdPolicy);
	}

	protected String openAppAndCreateConversionPolicy(TestData tdPolicy) {
		return openAppAndCreateConversionPolicy(getManualConversionInitiationTd(), tdPolicy);
	}

	protected String openAppAndCreateConversionPolicy() {
		return openAppAndCreateConversionPolicy(getManualConversionInitiationTd(), getConversionPolicyDefaultTD());
	}

    protected void createQuoteAndFillUpTo(TestData testData, Class<? extends Tab> tab, boolean isFillTab) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, tab, isFillTab);
    }

	protected void createQuoteAndFillUpTo(TestData testData, Class<? extends Tab> tab) {
        createQuoteAndFillUpTo(testData, tab, true);
	}

	protected void createQuoteAndFillUpTo(Class<? extends Tab> tab) {
		createQuoteAndFillUpTo(getPolicyTD(), tab);
	}

    protected void createConversionQuoteAndFillUpTo(TestData testData, Class<? extends Tab> tab, boolean isFillTab) {
        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(testData, tab, isFillTab);
    }

	protected void createConversionQuoteAndFillUpTo(TestData testData, Class<? extends Tab> tab) {
        createConversionQuoteAndFillUpTo(testData, tab, true);
	}

	protected void createConversionQuoteAndFillUpTo(Class<? extends Tab> tab) {
		createConversionQuoteAndFillUpTo(getConversionPolicyDefaultTD(), tab);
	}

	protected void moveTimeAndRunRenewJobs(LocalDateTime nextPhaseDate) {
		TimeSetterUtil.getInstance().nextPhase(nextPhaseDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	public void searchForPolicy(String policyNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	protected void openAppNonPrivilegedUser(PrivilegeEnum.Privilege privilege) {
		mainApp().open(initiateLoginTD()
				.adjust("User","qa_roles")
				.adjust("Groups", privilege.getName())
				.adjust("UW_AuthLevel", "01")
				.adjust("Billing_AuthLevel", "01")
		);
	}

	protected void payTotalAmtDue(LocalDateTime renewalEffectiveDate, String policyNumber){
		//Move time to Policy Expiration Date
		TimeSetterUtil.getInstance().nextPhase(renewalEffectiveDate);
		payTotalAmtDue(policyNumber);

	}

	protected void payTotalAmtDue(String policyNumber) {
		// Open Billing account and Pay min due for the renewal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar minDue = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		// Open Policy (Renewal)
		SearchPage.openPolicy(policyNumber);

	}
}
