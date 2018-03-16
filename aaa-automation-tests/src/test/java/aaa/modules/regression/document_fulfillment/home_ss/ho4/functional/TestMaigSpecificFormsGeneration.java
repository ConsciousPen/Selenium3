package aaa.modules.regression.document_fulfillment.home_ss.ho4.functional;

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
		return PolicyType.HOME_SS_HO4;
	}

	/**
	 * Specific Conversion Packet Generation for DE, VA , PA , CW with mortgagee payment plan
	 * @author Viktor Petrenko
	 * PAS-9607
	 * PAS-2674
	 * PAS-8777
	 * PAS-8766
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-2674"})
	public void pas2674_ConversionPacket(@Optional("NJ") String state) throws NoSuchFieldException {
		verifyConversionFormsSequence(getConversionPolicyDefaultTD());
	}

	/**
	 * Specific Conversion Packet Generation for DE, VA , PA , CW with mortgagee payment plan
	 * @author Viktor Petrenko
	 * PAS-9607
	 * PAS-2674
	 * PAS-8777
	 * PAS-8766
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-2674"})
	public void pas2674_ConversionPacketMortgagee(@Optional("DE") String state) throws NoSuchFieldException {
		// CW, DE, VA
		verifyConversionFormsSequence(getConversionPolicyDefaultTD());
	}

	/**
	 * Specific Billing Packet Generation For CW, DE, VA
	 * @author Viktor Petrenko
	 * PAS-9816
	 * PAS-9607
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-9816"})
	public void pas9816_BillingPacketGeneration(@Optional("DE") String state) throws NoSuchFieldException {
		// CW, DE, VA
		verifyBillingFormsSequence(getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab","Payment plan"),"Monthly (Renewal)").resolveLinks());
	}
}
