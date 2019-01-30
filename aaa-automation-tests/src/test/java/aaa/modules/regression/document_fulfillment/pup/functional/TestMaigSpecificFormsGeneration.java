package aaa.modules.regression.document_fulfillment.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigSpecificFormsGenerationTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMaigSpecificFormsGeneration extends TestMaigSpecificFormsGenerationTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	/**
	 * @author Viktor Petrenko, Rokas Lazdauskas
	 * PAS-9607
	 * PAS-9651
	 * PAS-2674
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@StateList(states = {Constants.States.NJ, Constants.States.DE, Constants.States.VA, Constants.States.MD, Constants.States.PA, Constants.States.NV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2674", "PAS-20250"})
	public void pas2674_ConversionPacket(@Optional("NV") String state) throws NoSuchFieldException {
		TestData policyCreationTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "Conversion", "TestData");
		verifyConversionFormsSequence(policyCreationTD);
	}

	/**
	 * @author Viktor Petrenko, Rokas Lazdauskas
	 * PAS-9816
	 * PAS-9650
	 * PAS-9607
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@StateList(states = {Constants.States.NJ, Constants.States.DE, Constants.States.VA, Constants.States.MD, Constants.States.PA, Constants.States.NV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-9816", "PAS-20250"})
	public void pas9816_BillingPacketGeneration_autopay(@Optional("NV") String state) throws NoSuchFieldException {
		// CW, DE, VA
		TestData policyCreationTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "Conversion", "TestData").resolveLinks();
		verifyBillingFormsSequence(policyCreationTD.adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","Payment Plan"),"Monthly (Renewal)"), true);
	}

	/**
	 * @author Rokas Lazdauskas
	 * PAS-9816
	 * PAS-9650
	 * PAS-9607
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@StateList(states = {Constants.States.NJ, Constants.States.DE, Constants.States.VA, Constants.States.MD, Constants.States.PA, Constants.States.NV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-9816", "PAS-20250"})
	public void pas9816_BillingPacketGeneration_nonAutopay(@Optional("NV") String state) throws NoSuchFieldException {
		// CW, DE, VA
		TestData policyCreationTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "Conversion", "TestData").resolveLinks();
		verifyBillingFormsSequence(policyCreationTD.adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","Payment Plan"),"Monthly (Renewal)"), false);
	}

}
