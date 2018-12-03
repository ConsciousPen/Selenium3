package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInitiateHOQuoteTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)

public class TestInitiateHoPolicyDefaultEndorsementForm extends TestInitiateHOQuoteTemplate {

	private String policyNumber;

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Initiate HO policies and check that Endorsement HO29 is included
	 * @scenario
	 * 1. Create Customer
	 * 2. Create CA Choice Auto Policy
	 * 3. Verify Policy status is 'Policy Active'
	 * 4. Initiate Property Policy from PolicySummary Page
	 * 5. Check that Endorsement HO29 is included for HO property. And is not included for DP3
	 * @details
	 */

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Initiate HO policies and check that Endorsement HO29 is included HO3")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-21410")
	public void pas21410_dataPrepAutoCAChoicePolicyCreation(@Optional("CA") String state) {
		policyNumber = openAppAndCreatePolicy();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Initiate HO policies and check that Endorsement HO29 is included HO3")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-21410")
	public void pas21410_testInitiateHO3PolicyFromAutoCAChoiceHO3(@Optional("CA") String state) {
		searchForPolicy(policyNumber);
		pas21410_testInitiateHOQuoteFromAutoAndHO29IsAdded(PolicyType.HOME_CA_HO3);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Initiate HO policies and check that Endorsement HO29 is included HO4")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-21410")
	public void pas21410_testInitiateHO3PolicyFromAutoCAChoiceHO4(@Optional("CA") String state) {
		searchForPolicy(policyNumber);
		pas21410_testInitiateHOQuoteFromAutoAndHO29IsAdded(PolicyType.HOME_CA_HO4);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Initiate HO policies and check that Endorsement HO29 is included HO6")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-21410")
	public void pas21410_testInitiateHO3PolicyFromAutoCAChoiceHO6(@Optional("CA") String state) {
		searchForPolicy(policyNumber);
		pas21410_testInitiateHOQuoteFromAutoAndHO29IsAdded(PolicyType.HOME_CA_HO6);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Initiate HO policies and check that Endorsement HO29 is not included DP3")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-21410")
	public void pas21410_testInitiateHO3PolicyFromAutoCAChoiceDP3(@Optional("CA") String state) {
		searchForPolicy(policyNumber);
		pas21410_testInitiateHOQuoteFromAutoAndHO29IsAdded(PolicyType.HOME_CA_DP3);
	}
}