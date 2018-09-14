package aaa.modules.financials;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.AfterSuite;
import aaa.main.metadata.policy.*;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class FinancialsBaseTest extends PolicyBaseTest {

	protected static final List<String> POLICIES = new ArrayList<>();

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
