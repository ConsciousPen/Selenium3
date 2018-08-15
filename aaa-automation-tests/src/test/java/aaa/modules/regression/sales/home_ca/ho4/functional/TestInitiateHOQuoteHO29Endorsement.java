/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ca.ho4.functional;

import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.modules.regression.sales.template.functional.TestEndorsementsTabAbstract;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)

public class TestInitiateHOQuoteHO29Endorsement extends TestEndorsementsTabAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO4;
	}

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
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = "PAS-13261")
	public void pas13261_testInitiateHOQuoteHO29added(@Optional("CA") String state) {

		createQuoteAndFillUpTo(EndorsementTab.class);

		// Assert that HO-29 is added by default. Purchase Policy.
		checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());

		policy.getDefaultView().fillFromTo(getPolicyTD(), EndorsementTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		// Initiate HO Quote from Policy Summary Page. Fill Policy up to Endorsement Tab.
		policy.initiateHOQuote().start();
		policy.getDefaultView().fillUpTo(getPolicyTD(), EndorsementTab.class);

		// Assert that HO-29 is added by default. Purchase Policy.
		checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeCAEndorsementForms.HO_29.getFormId());
	}
}
