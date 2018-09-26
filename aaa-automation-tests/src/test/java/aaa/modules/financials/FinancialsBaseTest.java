package aaa.modules.financials;

import java.util.*;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.*;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class FinancialsBaseTest extends PolicyBaseTest {

	private static final List<String> POLICIES = Collections.synchronizedList(new ArrayList<>());

	@BeforeSuite
	public void beforeFinancialSuite() {
		//implement this
	}

	@Override
	protected TestData getPolicyTD() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPupUnderlyingPolicies());
		}
		return td;
	}

	protected String createFinancialPolicy() {
		return createFinancialPolicy(getPolicyTD());
	}

	protected String createFinancialPolicy(TestData td) {
		String policyNum = createPolicy(td);
		POLICIES.add(policyNum);
		return policyNum;
	}

	protected void payAmountDue(){
		// Open Billing account and Pay min due for the renewal
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar minDue = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		// Open Policy Summary Page
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
	}

	protected TestData getEndorsementTD() {
		return getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Endorsement"), "TestData");
	}

	protected TestData getCancellationTD() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Cancellation"), "TestData");
		String type = getPolicyType().getShortName();
		switch (type) {
			case "AutoCA":
			case "AutoCAC":
				td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(),
						AutoCaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				break;
			case "AutoSS":
				td.adjust(TestData.makeKeyPath(AutoSSMetaData.CancellationActionTab.class.getSimpleName(),
						AutoSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				break;
			case "HomeSS_HO3":
			case "HomeSS_HO4":
			case "HomeSS_HO6":
			case "HomeSS_DP3":
				td.adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(),
						HomeSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				break;
			case "HomeCA_HO3":
			case "HomeCA_HO4":
			case "HomeCA_HO6":
			case "HomeCA_DP3":
				td.adjust(TestData.makeKeyPath(HomeCaMetaData.CancelActionTab.class.getSimpleName(),
						HomeCaMetaData.CancelActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				break;
			case "PUP":
				td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
						PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				break;
			default:
				return td;
		}
		return td;
	}

	protected TestData getReinstatementTD() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Reinstatement"), "TestData");
		if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
			return td.adjust(TestData.makeKeyPath(AutoCaMetaData.ReinstatementActionTab.class.getSimpleName(),
					AutoCaMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel()), "$<today:MM/dd/yyyy>");
		}
		return td;
	}

	/**
	 * Adjusts the effective date of the policy for the given test data
	 * @param td TestData to be adjusted
	 * @param date String representation of new effective date in the format 'MM/dd/yyyy'
	 * @return TestData object with effective date adjustment
	 */
	protected TestData adjustTdEffectiveDate(TestData td, String date) {
		String type = getPolicyType().getShortName();
		switch (type) {
			case "AutoCA":
			case "AutoCAC":
				td.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), date);
				break;
			case "AutoSS":
				td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), date);
				break;
			case "HomeSS_HO3":
			case "HomeSS_HO4":
			case "HomeSS_HO6":
			case "HomeSS_DP3":
				td.adjust(TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()), date);
				break;
			case "HomeCA_HO3":
			case "HomeCA_HO4":
			case "HomeCA_HO6":
			case "HomeCA_DP3":
				td.adjust(TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(),
						HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()), date);
				break;
			case "PUP":
				td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
						PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()), date);
				break;
			default:
				return td;
		}
		return td;
	}

	protected TestData adjustTdWithEmpBenefit(TestData td) {
		String type = getPolicyType().getShortName();
		switch (type) {
			case "AutoCA":
			case "AutoCAC":
				td.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.EMPLOYEE_BENEFIT_TYPE.getLabel()), "Active employee");
				break;
			case "AutoSS":
				td.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel()), "AAA Employee");
				break;
			case "HomeSS_HO3":
			case "HomeSS_HO4":
			case "HomeSS_HO6":
			case "HomeSS_DP3":
				td.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel()), "Yes");
				break;
			case "HomeCA_HO3":
			case "HomeCA_HO4":
			case "HomeCA_HO6":
			case "HomeCA_DP3":
				td.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel()), "Yes");
				break;
			case "PUP":
				td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel(),
						PersonalUmbrellaMetaData.PrefillTab.NamedInsured.AAA_EMPLOYEE.getLabel()), "Yes");
		}
		return td;
	}

	private Map<String, String> getPupUnderlyingPolicies() {
		Map<String, String> policies = new LinkedHashMap<>();
		PolicyType type;
		PolicyType typeAuto;
		String hoPolicy;
		String autoPolicy;
		String state = getState().intern();
		synchronized (state) {
			if (getState().equals(Constants.States.CA)) {
				type = PolicyType.HOME_CA_HO3;
				typeAuto = PolicyType.AUTO_CA_SELECT;
			} else {
				type = PolicyType.HOME_SS_HO3;
				typeAuto = PolicyType.AUTO_SS;
			}
			type.get().createPolicy(getStateTestData(testDataManager.policy.get(type), "DataGather", "TestData"));
			hoPolicy = PolicySummaryPage.getPolicyNumber();
			policies.put("Primary_HO3", hoPolicy);
			POLICIES.add(hoPolicy);
			typeAuto.get().createPolicy(getStateTestData(testDataManager.policy.get(typeAuto), "DataGather", "TestData"));
			autoPolicy = PolicySummaryPage.getPolicyNumber();
			policies.put("Primary_Auto", autoPolicy);
			POLICIES.add(autoPolicy);
		}
		return policies;
	}

	@AfterSuite(alwaysRun = true)
	public void testPolicyLogging() {
		for (String policy : POLICIES) {
			log.info(policy);
		}
	}

}
