/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;

import org.openqa.selenium.By;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * @author N. Belakova
 * @name Test Policy OOSE functionality
 * @scenario
 * 1. Create new Auto SS Policy;
 * 2. Create Endorsement Policy with Endorsement effective date is today + 10 days: Add second Vehicle
 * 3. Create OOSE Policy with Endorsement effective date + 5 days: Add second NI/Driver
 * 4. Execute action 'Roll on Changes'
 * 5. Select values manually to have all changes
 * 6. Verify data quantity on Differences screen
 * 7. Complete Roll On
 * 8. Verify 2nd NI, Driver & Vehicle are added
 * @details
 */

public abstract class PolicyOose extends PolicyBaseTest {
    
    public void testPolicyOose() {
    	
        mainApp().open();

        getCopiedPolicy();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        
        TestData tdEndorsement = getTestSpecificTD("TestData_E1");

        //Create Endorsement Policy effective date + 10 days: Add second Vehicle
        getPolicyType().get().createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData_Plus10Day")));
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        
        TestData tdEndorsement2 = getTestSpecificTD("TestData_E2");

        //Create OOSE Policy effective date + 5 days: Add second NI/Driver
        getPolicyType().get().createEndorsement(tdEndorsement2.adjust(getPolicyTD("Endorsement", "TestData_Plus5Day")));
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        
        CustomAssert.enableSoftMode();
        
        //Execute action 'Roll on Changes' and select values manually
        rollOnPerformManual();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        //check if there is 2nd NI, Driver & Vehicle
        PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Name").verify.contains("Violeta Minolta");
        PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Name").verify.contains("Violeta Minolta");
        PolicySummaryPage.tablePolicyVehicles.getRow(2).getCell("Make").verify.present();

        CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
    }
	
	private void rollOnPerformManual(){
		 
		getPolicyType().get().rollOn().start();
	    	
		Table tableOosEndorsements = new Table(By.id("affectedEndoresmentForm:historyTable"));
		tableOosEndorsements.getRow(1).getCell(8).controls.links.get("Manual").click();

        Table tableDifferences = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));
        int columnsCount = tableDifferences.getColumnsCount();

        //expand rows
        for (int i = 0; i < tableDifferences.getRowsCount(); i++) {
            Link linkTriangle = new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i
                         + "']/td[1]/span[contains(@class, 'ui-treetable-toggler')]"));
            if (linkTriangle.isPresent() && linkTriangle.isVisible()) {
            	linkTriangle.click();
            }
        }

        //check data quantity
        Integer rowsCountExpanded = tableDifferences.getRowsCount(); 
        CustomAssert.assertTrue(rowsCountExpanded.toString().equals(getTestSpecificTD("Differences").getValue("LineCount")));
        
        //apply values
        for (int i = 1; i <= rowsCountExpanded; i++) {
        	Link linkSetCurrent = tableDifferences.getRow(i).getCell(columnsCount).controls.links.get("Current");
            Link linkSetAvailable= tableDifferences.getRow(i).getCell(columnsCount).controls.links.get("Available");
                 
            if (tableDifferences.getRow(i).getCell(3).getValue().isEmpty()) {   //column: (Roll On Pending) Endorsement (Avail. for Roll on)

            	if (linkSetCurrent.isPresent() && linkSetCurrent.isVisible()) {
            		linkSetCurrent.click();
                	 }
            } else { //value not empty (column: (Roll On Pending) Endorsement (Avail. for Roll on))
                
                if (linkSetAvailable.isPresent() && linkSetAvailable.isVisible()) {
                	linkSetAvailable.click();
                }
                 
            }
        }
        
        getPolicyType().get().rollOn().submit();

     }
		

    
}