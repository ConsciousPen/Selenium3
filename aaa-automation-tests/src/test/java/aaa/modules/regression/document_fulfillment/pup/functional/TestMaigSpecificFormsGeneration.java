package aaa.modules.regression.document_fulfillment.pup.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigSpecificFormsGenerationTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMaigSpecificFormsGeneration extends TestMaigSpecificFormsGenerationTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	private TestData testDataPolicy = testDataManager.policy.get(getPolicyType());

	
	/**
	 * @author Viktor Petrenko
	 * PAS-9607
	 * PAS-9651
	 * PAS-2674
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2674"})
	public void pas2674_ConversionPacket(@Optional("MD") String state) throws NoSuchFieldException {
		TestData policyCreationTD = getStateTestData(testDataPolicy, "Conversion", "TestData");
		verifyConversionFormsSequence(policyCreationTD);
	}

	/**
	 * @author Viktor Petrenko
	 * PAS-9816
	 * PAS-9650
	 * PAS-9607
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-9816"})
	public void pas9816_BillingPacketGeneration(@Optional("DE") String state) throws NoSuchFieldException {
		// CW, DE, VA
		TestData policyCreationTD = getStateTestData(testDataPolicy, "Conversion", "TestData").resolveLinks();
		verifyBillingFormsSequence(policyCreationTD.adjust(TestData.makeKeyPath("PremiumAndCoveragesQuoteTab","Payment Plan"),"Monthly (Renewal)"));
	}

}
