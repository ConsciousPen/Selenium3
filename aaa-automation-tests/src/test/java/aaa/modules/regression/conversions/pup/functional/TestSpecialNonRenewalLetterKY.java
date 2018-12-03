/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.conversions.pup.functional;

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
	protected PolicyType getPolicyType() { return PolicyType.PUP; }

	/**
	 * @author Rokas Lazdauskas
	 * @name Check that Conversion Specific Special Non-renewal letter is triggered on R-80
	 * @scenario
	 * 1. Create Conversion Policy at R-81
	 * 2. Change time to R-80 (document should not be generated)
	 * 3. Try to generate Conversion Specific Special Non-renewal letter
	 * 4. Check that document is not generated - special conversion non renewal letter for KY (HSSNRKY) (not generated for PUP)
	 * 5. Check that organic letter is supressed on conversion policy creation (HSRNKY)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-18121")
	public void pas18121_specialNonRenewalLetterBeforeR80(@Optional("KY") String state) throws NoSuchFieldException {
		specialNonRenewalLetterR80PUPNotGenerated();
	}
}