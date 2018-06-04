/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ca.ho6.functional;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeCaHO6BaseTest;
import aaa.modules.regression.sales.template.functional.InitiateHOQuoteTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)

public class TestInitiateHOQuoteHO29Endorsement extends HomeCaHO6BaseTest {

	private InitiateHOQuoteTemplate template = new InitiateHOQuoteTemplate();

	/**
	 * @author Dominykas Razgunas
	 * @name Test HO29 Endorsement added by default
	 * @scenario
	 * 1. Create new customer.
	 * 2. Initiate CAH quote creation.
	 * 3. Fill all mandatory fields on all tabs.
	 * 4. Assert that HO-29 Form is added.
	 * 5. Purchase policy.
	 * 6. Initiate HO Quote.
	 * 7. Fill all mandatory fields on all tabs.
	 * 8. Assert that HO-29 Form is added.
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6, testCaseId = "PAS-13261")
	public void pas13261_testInitiateHOQuoteHO29added(@Optional("CA") String state) {

		template.pas13261_testInitiateHOQuoteHO29added(getPolicyType());
	}
}
