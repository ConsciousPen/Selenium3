package aaa.modules.e2e.templates;

import java.time.LocalDate;
import java.util.List;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.billing.BillingHelper;
import aaa.main.modules.policy.IPolicy;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTime;

public class Scenario1 extends BaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected String policyNumber;
	protected DateTime policyEffectiveDate;
	protected DateTime policyExpirationDate;
	protected List<LocalDate> installmentDueDates;
	
	public void step1() {
		policy = getPolicyType().get();
		tdPolicy = testDataManager.policy.get(getPolicyType());
		
		policyNumber = createPolicy(getStateTestData(tdPolicy, "DataGather", "TestData_Scenario1"));
		
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		
		
		
	}

}
