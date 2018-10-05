package aaa.modules.financials;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.testng.annotations.*;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.*;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class FinancialsBaseTest extends PolicyBaseTest {

	private static final List<String> ALL_POLICIES = Collections.synchronizedList(new ArrayList<>());

	private static final String UNEARNED_INCOME_1015 = "1015";
	private static final String CHANGE_IN_UNEARNED_INCOME_1021 = "1021";
	private static final String CA_SELECT = "AutoCA";
	private static final String CA_CHOICE = "AutoCAC";
	private static final String AUTO_SS = "AutoSS";
	private static final String HOME_SS_HO3 = "HomeSS_HO3";
	private static final String HOME_SS_HO4 = "HomeSS_HO4";
	private static final String HOME_SS_HO6 = "HomeSS_HO6";
	private static final String HOME_SS_DP3 = "HomeSS_DP3";
	private static final String HOME_CA_HO3 = "HomeCA_HO3";
	private static final String HOME_CA_HO4 = "HomeCA_HO4";
	private static final String HOME_CA_HO6 = "HomeCA_HO6";
	private static final String HOME_CA_DP3 = "HomeCA_DP3";
	private static final String PUP = "PUP";

//	@BeforeSuite(alwaysRun = true)
//	public void beforeFinancialSuite() {
//		validateAccounts();
//	}

	@AfterSuite(alwaysRun = true)
	public void afterFinancialSuite() {
		// ********* For debugging only ************
		for (String policy : ALL_POLICIES) {
			log.info(policy);
		}
		// *****************************************

		//validateAccounts();

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
		ALL_POLICIES.add(policyNum);
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
		return getEndorsementTD(TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected TestData getEndorsementTD(LocalDateTime effDate) {
		TestData td =  getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Endorsement"), "TestData");
		String type = getPolicyType().getShortName();
		String date = effDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		switch (type) {
			case CA_SELECT:
			case CA_CHOICE:
				td.adjust(TestData.makeKeyPath(AutoCaMetaData.EndorsementActionTab.class.getSimpleName(), AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
				break;
			case AUTO_SS:
				td.adjust(TestData.makeKeyPath(AutoSSMetaData.EndorsementActionTab.class.getSimpleName(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
				break;
			case HOME_SS_HO3:
			case HOME_SS_HO4:
			case HOME_SS_HO6:
			case HOME_SS_DP3:
				td.adjust(TestData.makeKeyPath(HomeSSMetaData.EndorsementActionTab.class.getSimpleName(), HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
				break;
			case HOME_CA_HO3:
			case HOME_CA_HO4:
			case HOME_CA_HO6:
			case HOME_CA_DP3:
				td.adjust(TestData.makeKeyPath(HomeCaMetaData.EndorsementActionTab.class.getSimpleName(), HomeCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
				break;
			case PUP:
				td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.EndorsementActionTab.class.getSimpleName(), PersonalUmbrellaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
				break;
			default:
				return td;
		}
		return td;
	}

	protected TestData getCancellationTD() {
		return getCancellationTD(TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected TestData getCancellationTD(LocalDateTime effDate) {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Cancellation"), "TestData");
		String type = getPolicyType().getShortName();
		String date = effDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		switch (type) {
			case CA_SELECT:
			case CA_CHOICE:
				td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(),
						AutoCaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(),
						AutoCaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
				break;
			case AUTO_SS:
				td.adjust(TestData.makeKeyPath(AutoSSMetaData.CancellationActionTab.class.getSimpleName(),
						AutoSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				td.adjust(TestData.makeKeyPath(AutoSSMetaData.CancellationActionTab.class.getSimpleName(),
						AutoSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
				break;
			case HOME_SS_HO3:
			case HOME_SS_HO4:
			case HOME_SS_HO6:
			case HOME_SS_DP3:
				td.adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(),
						HomeSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				td.adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(),
						HomeSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
				break;
			case HOME_CA_HO3:
			case HOME_CA_HO4:
			case HOME_CA_HO6:
			case HOME_CA_DP3:
				td.adjust(TestData.makeKeyPath(HomeCaMetaData.CancelActionTab.class.getSimpleName(),
						HomeCaMetaData.CancelActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				td.adjust(TestData.makeKeyPath(HomeCaMetaData.CancelActionTab.class.getSimpleName(),
						HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
				break;
			case PUP:
				td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
						PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
				td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
						PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
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

	protected TestData adjustTdPolicyEffDate(TestData td, LocalDateTime date) {
		String type = getPolicyType().getShortName();
		String sDate = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		switch (type) {
			case CA_SELECT:
			case CA_CHOICE:
				td.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), sDate);
				break;
			case AUTO_SS:
				td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), sDate);
				break;
			case HOME_SS_HO3:
			case HOME_SS_HO4:
			case HOME_SS_HO6:
			case HOME_SS_DP3:
				td.adjust(TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()), sDate);
				break;
			case HOME_CA_HO3:
			case HOME_CA_HO4:
			case HOME_CA_HO6:
			case HOME_CA_DP3:
				td.adjust(TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(),
						HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()), sDate);
				break;
			case PUP:
				td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
						PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()), sDate);
				break;
			default:
				return td;
		}
		return td;
	}

	protected TestData adjustTdWithEmpBenefit(TestData td) {
		String type = getPolicyType().getShortName();
		switch (type) {
			case CA_SELECT:
			case CA_CHOICE:
				td.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.EMPLOYEE_BENEFIT_TYPE.getLabel()), "Active employee");
				break;
			case AUTO_SS:
				td.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel()), "AAA Employee");
				break;
			case HOME_SS_HO3:
			case HOME_SS_HO4:
			case HOME_SS_HO6:
			case HOME_SS_DP3:
				td.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel()), "Yes");
				break;
			case HOME_CA_HO3:
			case HOME_CA_HO4:
			case HOME_CA_HO6:
			case HOME_CA_DP3:
				td.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel()), "Yes");
				break;
			case PUP:
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
			ALL_POLICIES.add(hoPolicy);
			typeAuto.get().createPolicy(getStateTestData(testDataManager.policy.get(typeAuto), "DataGather", "TestData"));
			autoPolicy = PolicySummaryPage.getPolicyNumber();
			policies.put("Primary_Auto", autoPolicy);
			ALL_POLICIES.add(autoPolicy);
		}
		return policies;
	}

	private void validateAccounts() {
		if (TimeSetterUtil.getInstance().getCurrentTime().getDayOfMonth() != 1) {
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).plusMonths(1));
		}
		JobUtils.executeJob(Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		assertSoftly(softly -> softly.assertThat(DBService.get().getValue(FinancialsSQL.getTotalEntryAmtForAcct(UNEARNED_INCOME_1015)).get())
				.isEqualTo(DBService.get().getValue(FinancialsSQL.getTotalEntryAmtForAcct(CHANGE_IN_UNEARNED_INCOME_1021)).get()));
	}

}
