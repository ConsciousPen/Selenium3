/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ca.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


public class TestInitiateHOQuoteHO29Endorsement extends HomeCaHO3BaseTest {

	private EndorsementTab endorsementTab = new EndorsementTab();

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
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
	public void testInitiateHOQuoteHO29added(@Optional("CA") String state) {

		TestData testData = getPolicyTD();

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, EndorsementTab.class);
		assertThat(endorsementTab.tblIncludedEndorsements.getColumn("Form ID").getValue()).contains("HO-29");
		policy.getDefaultView().fillFromTo(testData, EndorsementTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		policy.initiateHOQuote().start();
		policy.getDefaultView().fillUpTo(testData, EndorsementTab.class);

		assertThat(endorsementTab.tblIncludedEndorsements.getColumn("Form ID").getValue()).contains("HO-29");
	}
}
