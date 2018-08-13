/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.actiontabs.UpdateRulesOverrideActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jelena Dembovska
 * @name Test rules override tab
 * @scenario
 * 1. Create new quote, fill all mandatory fields
 * 2. On Documents tab set Yes for agreement, No for all documents required to bind
 * 3. Error override tab is opened, override rule, Save&Exit quote
 * 4. On Consolidated view select "Update Rules Override", change value for rule
 * 5. From consolidated view open quote in "Data Gathering" mode. Bind policy.
 * 6. From consolidated view select "Update Rules Override", check values on Override page
 * 7. Create manual renewal image
 * 8. Open "Update Rules Override" for renewal, check - rule which was overridden for life is still displayed
 * @details
 */
public class TestPolicyRulesOverride extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyRulesOverride(@Optional("CA") String state) {

		TestData class_td = getTestSpecificTD("TestData");

		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");

		policy.initiate();
		policy.getDefaultView().fillUpTo(class_td, ErrorTab.class, true);
		new ErrorTab().submitTab();

		PurchaseTab.buttonCancel.click();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

		//override rule for quote
		policy.updateRulesOverride().start();
		checkRuleIsPresent("Life", "Other");

		policy.updateRulesOverride().perform(getTestSpecificTD("UpdateRulesTestData_1"));

		policy.updateRulesOverride().start();
		checkRuleIsPresent("Term", "Justified through supporting documentation");

		//bind policy
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		new DocumentsAndBindTab().submitTab();
		new PurchaseTab().fillTab(class_td);
		new PurchaseTab().submitTab();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//override rule for policy
		policy.updateRulesOverride().perform(getTestSpecificTD("UpdateRulesTestData_2"));

		policy.updateRulesOverride().start();
		checkRuleIsPresent("Life", "Temporary Issue");

		//create renewal and check overridden for life rule
		policy.renew().performAndExit();

		PolicySummaryPage.buttonRenewals.click();
		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.comboBoxes.getFirst().setValue("Update Rules Override");
		PolicySummaryPage.tableRenewals.getRow(1).getCell("Action").controls.buttons.get("Go").click();

		checkRuleIsPresent("Life", "Temporary Issue");

	}

	private void checkRuleIsPresent(String duration, String reason) {

		Map<String, String> query = new HashMap<>();
		query.put("Status", "overridden");
		query.put("Rule name", "200040");

		UpdateRulesOverrideActionTab.tblRulesList.getRow(query).verify.present();

		UpdateRulesOverrideActionTab.tblRulesList.getRow(query).getCell("Duration").controls.radioGroups.getFirst().verify.value(duration);
		UpdateRulesOverrideActionTab.tblRulesList.getRow(query).getCell("Reason for override").controls.comboBoxes.getFirst().verify.value(reason);

		UpdateRulesOverrideActionTab.btnCancel.click();

	}
}
