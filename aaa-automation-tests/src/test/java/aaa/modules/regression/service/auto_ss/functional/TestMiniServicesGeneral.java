package aaa.modules.regression.service.auto_ss.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesGeneralHelper;
import toolkit.utils.TestInfo;

public class TestMiniServicesGeneral extends TestMiniServicesGeneralHelper {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Retrieve lookup data service - Payment Methods
	 * @scenario 1. Add State Specific configurations for specific dates, which changes Default configuration's values
	 * 2. Add State Specific configurations for specific dates, which adds new values to it
	 * 3. Retrieve lookup values for the mentioned dates, check value
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9997"})
	public void pas9997_paymentMethodsLookup(@Optional("") String state) {

		pas9997_paymentMethodsLookupBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Retrieve lookup data service - Payment Plans
	 * @scenario 1. Add State Specific configurations for specific dates, which changes Default configuration's values
	 * 2. Add State Specific configurations for specific dates, which adds new values to it
	 * 3. Retrieve lookup values for the mentioned dates, check value
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9997"})
	public void pas9997_paymentPlansLookup(@Optional("") String state) {

		pas9997_paymentPlansLookupBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Validate Meta data Service for Vehicle
	 * Create a va policy
	 * Create an endorsement using DXP
	 * Hit view vehicle service to get OID
	 * Hit Meta data service
	 * Validate the response
	 * Add garage address different than Residential
	 * Hit Meta Data service again.
	 * Validate the garage address response.
	 * @scenario
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12407", "PAS-15058"})
	public void pas12407_BigMetaDataService(@Optional("VA") String state) {
		pas12407_bigDataService();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation of E2E flow in DXP
	 * @scenario 1. see script body
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12866"})
	public void pas12866_e2e(@Optional("AZ") String state) {
		assertSoftly(softly ->
				pas12866_e2eBctBody(state, true, softly)
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation of E2E flow in DXP
	 * @scenario 1. see script body
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12866"})
	public void pas12866_e2eBct(@Optional("ID") String state) {
		assertSoftly(softly ->
				pas12866_e2eBctBody(state, false, softly)
		);
	}
	/**
	 * @author Maris Strazds
	 * @name Validate isRenewalOffered in Renewal summary service
	 * @scenario
	 * 1. Create a policy in PAS
	 * 2. Run all required renewal jobs till Renewal Proposal date (excluding)
	 * 3. Run viewPolicyRenewalSummary service ---> isRenewalOffered is always false for current term and true for renewal term if it is Proposed
	 * 4. Run all required renewal jobs at R-35
	 * 5. Run viewPolicyRenewalSummary service ---> isRenewalOffered is always false for current term and true for renewal term if it is Proposed
	 * 6. Make Revised Renewal by updating Current term ---> isRenewalOffered is always false for current term and true for renewal term if it is Proposed
	 * 7. Make Revised Renewal by updating Renewal term ---> isRenewalOffered is always false for current term and true for renewal term if it is Proposed
	 * 8. Renewa the policy
	 * 9. Generate Renewal image, run viewPolicyRenewalSummary and validate that isRenewalOffered si false for Renewal term
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-22548"})
	public void pas22548_RenewalOfferIndicator(@Optional("VA") String state) {
		pas22548_RenewalOfferIndicatorBody();
	}
}


