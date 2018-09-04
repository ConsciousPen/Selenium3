/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.conversions.home_ss.ho4.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestSpecialNonRenewalLetterKYTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.KY})
public class TestSpecialNonRenewalLetterKY extends TestSpecialNonRenewalLetterKYTemplate {

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_SS_HO4; }

	/**
	 * @author Rokas Lazdauskas
	 * @name Check that Conversion Specific Special Non-renewal letter is triggered on R-80
	 * @scenario
	 * 1. Create Conversion Policy at R-81 (Have underlying PUP policy: ApplicantTab -> Other Active AAA Policies)
	 * 2. Try to generate Conversion Specific Special Non-renewal letter (document should not be generated)
	 * 3. Check that document is not generated
	 * 4. Check that organic letter (HSRNKY) is suppressed on conversion renewal (1st)
	 * 5. Change time to R-80 (document should be generated)
	 * 6. Try to generate Conversion Specific Special Non-renewal letter (FORM# HSSNRKYXX)
	 * 7. Check that document is generated
	 * 8. Check that transaction code is MCON
	 * 9. Check that 'PupCvrgYN' tag has 'Y' value (depends on step 1.)
	 * 10. Check that organic letter (HSRNKY) is suppressed on conversion renewal (1st)
	 * 11. Switch time to R
	 * 12. Propose Conversion Renewal (1st)
	 * 13. Purchase Conversion Renewal (1st)
	 * 14. Change time to second pre renewal letter generation date (policy expiration date + 1 years - 80 days)
	 * 15. Try generating second pre renewal letter
	 * 16. Check that document is not generated (Conversion Specific Special Non-renewal letter is not sent on organic Renewal)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-18121")
	public void pas18121_specialNonRenewalLetterBeforeR80_withEndorsement(@Optional("KY") String state) throws NoSuchFieldException {
		specialNonRenewalLetterBeforeR80Generated(true);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Check that Conversion Specific Special Non-renewal letter is triggered on R-80
	 * @scenario
	 * 1. Create Conversion Policy at R-81 (Policy without underlying PUP policy: ApplicantTab -> Other Active AAA Policies)
	 * 2. Try to generate Conversion Specific Special Non-renewal letter (document should not be generated)
	 * 3. Check that document is not generated
	 * 4. Check that organic letter (HSRNKY) is suppressed on conversion renewal (1st)
	 * 5. Change time to R-80 (document should be generated)
	 * 6. Try to generate Conversion Specific Special Non-renewal letter (FORM# HSSNRKYXX)
	 * 7. Check that document is generated
	 * 8. Check that transaction code is MCON
	 * 9. Check that 'PupCvrgYN' tag has 'N' value (depends on step 1.)
	 * 10. Check that organic letter (HSRNKY) is suppressed on conversion renewal (1st)
	 * 11. Switch time to R
	 * 12. Propose Conversion Renewal (1st)
	 * 13. Purchase Conversion Renewal (1st)
	 * 14. Change time to second pre renewal letter generation date (policy expiration date + 1 years - 80 days)
	 * 15. Try generating second pre renewal letter
	 * 16. Check that document is not generated (Conversion Specific Special Non-renewal letter is not sent on organic Renewal)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-18121")
	public void pas18121_specialNonRenewalLetterBeforeR80_withoutEndorsement(@Optional("KY") String state) throws NoSuchFieldException {
		specialNonRenewalLetterBeforeR80Generated(false);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Check that Conversion Specific Special Non-renewal letter is triggered on R-80
	 * @scenario
	 * 1. Create Conversion Policy at R-81
	 * 2. Change time to R-79 (document should not be generated)
	 * 3. Try to generate Conversion Specific Special Non-renewal letter
	 * 4. Check that document is not generated - special conversion non renewal letter for KY (HSSNRKY)
	 * 5. Check that organic letter is supressed on conversion policy creation (HSRNKY)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-18121")
	public void pas18121_specialNonRenewalLetterAfterR80(@Optional("KY") String state) {
		specialNonRenewalLetterAfterR80NotGenerated();
	}
}