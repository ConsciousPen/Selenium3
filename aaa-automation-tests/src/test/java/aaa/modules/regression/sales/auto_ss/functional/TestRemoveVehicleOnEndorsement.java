/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
* CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestRemoveVehicleOnEndorsement extends AutoSSBaseTest {

	private VehicleTab vehicleTab = new VehicleTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	/**
	*@author Dominykas Razgunas
	*@name Remove Vehicle on Current Term Endorsement
	*@scenario
	 *1. Initiate quote creation
	 *2. Go to the vehicle tab
	 *3. Add second vehicle
	 *4. Rate quote
	 *5. Issue Policy
	 *6. Renew Policy
	 *7. Endorse current term
	 *8. Remove Vehicle
	 *9. Rate Policy
	 *10. Issue Endorsement
	*@details
	*/
	@StateList(states = Constants.States.VA)
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11404")
	public void pas11404_removeVehicleOnEndorsement(@Optional("VA") String state) {

		// Create Customer
		mainApp().open();
		createCustomerIndividual();

		// Issue Policy with 2 Vehicles
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), VehicleTab.class, true);
		vehicleTab.fillTab(getTestSpecificTD("TestData_Vehicle"));
		vehicleTab.submitTab();
		policy.getDefaultView().fillFromTo(getPolicyTD(), AssignmentTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		// Create Renewal for Policy
		policy.renew().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();

		// Endorse Policy Remove Vehicle Rate policy and issue endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.removeRow(2);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}
}
