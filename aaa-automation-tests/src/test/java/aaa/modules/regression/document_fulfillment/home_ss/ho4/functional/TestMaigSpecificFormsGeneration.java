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
	 * Specific Conversion Packet Generation for CW, DE, VA , MD, PA  with mortgagee payment plan
	 *
	 * @throws NoSuchFieldException See detailed steps in template file
	 * @author Viktor Petrenko
	 * PAS-9607
	 * PAS-2674
	 * PAS-8777
	 * PAS-8766
	 * PAS-9651
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-2674"})
	public void pas2674_ConversionPacket(@Optional("PA") String state) throws NoSuchFieldException {
		verifyConversionFormsSequence(getConversionPolicyDefaultTD());
	}

	/**
	 * Specific Conversion Packet Generation for CW, DE, VA , MD, PA  with mortgagee payment plan
	 *
	 * @throws NoSuchFieldException See detailed steps in template file
	 * @author Viktor Petrenko
	 * PAS-9607
	 * PAS-2674
	 * PAS-8777
	 * PAS-8766
	 * PAS-9651
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-2674"})
	public void pas2674_ConversionPacketMortgagee(@Optional("DE") String state) throws NoSuchFieldException {
		verifyConversionFormsSequence(getConversionPolicyDefaultTD());
	}

	/**
	 * Specific Billing Packet Generation for CW, DE, VA , MD, PA
	 *
	 * @throws NoSuchFieldException See detailed steps in template file
	 * @author Viktor Petrenko
	 * PAS-9816
	 * PAS-9607
	 * PAS-9650
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-9816"})
	public void pas9816_BillingPacketGeneration_autopay(@Optional("DE") String state) throws NoSuchFieldException {
		verifyBillingFormsSequence(getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab", "Payment plan"), "Monthly (Renewal)").resolveLinks(), true);
	}

	/**
	 * Specific Billing Packet Generation for CW, DE, VA , MD, PA
	 *
	 * @throws NoSuchFieldException See detailed steps in template file
	 * @author Rokas Lazdauskas
	 * PAS-9816
	 * PAS-9607
	 * PAS-9650
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO4, testCaseId = {"PAS-9816"})
	public void pas9816_BillingPacketGeneration_nonAutopay(@Optional("DE") String state) throws NoSuchFieldException {
		verifyBillingFormsSequence(getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab", "Payment plan"), "Monthly (Renewal)").resolveLinks(), false);
	}
}
