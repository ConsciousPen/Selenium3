package aaa.modules.financials;

import java.util.*;
import org.testng.annotations.AfterSuite;
import aaa.common.enums.Constants;
import aaa.main.metadata.policy.*;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class FinancialsBaseTest extends PolicyBaseTest {

	protected static final List<String> POLICIES = Collections.synchronizedList(new ArrayList<>());

	@Override
	protected TestData getPolicyTD() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPupUnderlyingPolicies());
		}
		return td;
	}

	protected Map<String, String> getPupUnderlyingPolicies() {
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

	@AfterSuite
	public void testPolicyLogging() {
		for (String policy : POLICIES) {
			log.info(policy);
		}
	}

}
