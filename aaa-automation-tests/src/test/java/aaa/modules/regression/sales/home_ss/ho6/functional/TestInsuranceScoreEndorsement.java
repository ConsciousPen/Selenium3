package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInsuranceScoreEndorsementTemplate;
import aaa.modules.regression.sales.template.functional.TestInsuranceScoreRenewalTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestInsuranceScoreEndorsement extends TestInsuranceScoreEndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
	}

	//	-------------------- Order insurance score at renewal when Qualified named insured is added on midterm endorsement ----------------------------
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA, Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-24663"})
	public void pas24663_Endorsement(@Optional("") String state) {
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("TestData_EndorsementWithQualifiedNamedInsured",
				500, 650, 800, 600,
				true, true, true);
	}

}