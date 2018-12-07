package aaa.modules.regression.sales.home_ss.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestEndorsementsTabTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestIncreasedInsuranceLimitAndSupplementalLossEndorsements extends TestEndorsementsTabTemplate {

	private String insuranceLimitFormId = EndorsementForms.HomeSSEndorsementForms.DS_04_20.getFormId();
	private String supplementLossFormId = EndorsementForms.HomeSSEndorsementForms.DS_04_63.getFormId();

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_SS_DP3; }

	/**
	 * @author Dominykas Razgunas
	 * @name Test Increased Insurance Limit And Supplemental Loss Endorsements NB
	 * @scenario 1. Login app with user who has 'Add/Remove Endorsement' privilege
	 * 2. Create Customer
	 * 3. Initiate Quote creation
	 * 4. Fill everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 5. Check that DS 04 20 endorsement is available in 'Optional Endorsements'
	 * 6. Add DS 04 20 endorsement
	 * 7. Check that Edit link is available
	 * 8. Change something in endorsement and verify that it is saved
	 * 9. Check that DS 04 63 endorsement is available in 'Optional Endorsements'
	 * 10. Add DS 04 63 endorsement
	 * 11. Check that Edit link is available
	 * 12. Change something in endorsement and verify that it is saved
	 * 13. Check that Remove link is available
	 * 14. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Increased Insurance Limit And Supplemental Loss Endorsements")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-18111")
	public void pas18111_Privileged_NewBusiness(@Optional("") String state) {
		newBusinessTx_privileged(insuranceLimitFormId, supplementLossFormId);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Increased Insurance Limit And Supplemental Loss Endorsements Endorsement
	 * @scenario 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Endorsement
	 * 5. Fill everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 6. Check that DS 04 20 endorsement is available in 'Optional Endorsements'
	 * 7. Add DS 04 20 endorsement
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that DS 04 63 endorsement is available in 'Optional Endorsements'
	 * 11. Add DS 04 63 endorsement
	 * 12. Check that Edit link is available
	 * 13. Change something in endorsement and verify that it is saved
	 * 14. Check that Remove link is available
	 * 15. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Increased Insurance Limit And Supplemental Loss Endorsements")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-18111")
	public void pas18111_Privileged_Endorsement(@Optional("") String state) {
		endorsementTx_privileged(insuranceLimitFormId, supplementLossFormId);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Increased Insurance Limit And Supplemental Loss Endorsements Renewal
	 * @scenario 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Renewal
	 * 5. Fill everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 6. Check that DS 04 20 endorsement is available in 'Optional Endorsements'
	 * 7. Add DS 04 20 endorsement
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that DS 04 63 endorsement is available in 'Optional Endorsements'
	 * 11. Add DS 04 63 endorsement
	 * 12. Check that Edit link is available
	 * 13. Change something in endorsement and verify that it is saved
	 * 14. Check that Remove link is available
	 * 15. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Increased Insurance Limit And Supplemental Loss Endorsements")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-18111")
	public void pas18111_Privileged_Renewal(@Optional("") String state) {
		renewalTx_privileged(insuranceLimitFormId, supplementLossFormId);
	}
}