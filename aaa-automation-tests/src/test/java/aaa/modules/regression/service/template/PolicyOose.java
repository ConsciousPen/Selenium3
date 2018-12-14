/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;

import org.openqa.selenium.By;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
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

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        
        TestData tdEndorsement = getTestSpecificTD("TestData_E1");

        //Create Endorsement Policy effective date + 10 days: Add second Vehicle
        getPolicyType().get().createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData_Plus10Day")));
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        
        TestData tdEndorsement2 = getTestSpecificTD("TestData_E2");

        //Create OOSE Policy effective date + 5 days: Add second NI/Driver
        getPolicyType().get().createEndorsement(tdEndorsement2.adjust(getPolicyTD("Endorsement", "TestData_Plus5Day")));
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        
        //Execute action 'Roll on Changes' and select values manually
        rollOnPerformManual();
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
		CustomSoftAssertions.assertSoftly(softly -> {
        //check if there is 2nd NI, Driver & Vehicle
			softly.assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Name").getValue()).isEqualTo("Violeta Minolta");
			softly.assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Name").getValue()).isEqualTo("Violeta Minolta");
			softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRow(2).getCell("Make").getValue()).isNotNull();

		});

		CustomSoftAssertions.assertSoftly(softly -> {

			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			softly.assertThat(PolicySummaryPage.tablePolicyDrivers).hasRows(2);
			softly.assertThat(PolicySummaryPage.tablePolicyVehicles).hasRows(2);
			softly.assertThat(PolicySummaryPage.tableInsuredInformation).hasRows(2);
		});
    }
	
	private void rollOnPerformManual(){
		 
		getPolicyType().get().rollOn().start();
	    	
		Table tableOosEndorsements = new Table(By.id("affectedEndoresmentForm:historyTable"));
		tableOosEndorsements.getRow(1).getCell(8).controls.links.get("Manual").click();

        Table tableDifferences = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));
        int columnsCount = tableDifferences.getColumnsCount();

        //expand rows
        /*
        for (int i = 0; i < tableDifferences.getRowsCount(); i++) {
            Link linkTriangle = new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i
                         + "']/td[1]/span[contains(@class, 'ui-treetable-toggler')]"));
            if (linkTriangle.isPresent() && linkTriangle.isVisible()) {
            	linkTriangle.click();
            }
        }*/

        //check data quantity
        Integer rowsCountExpanded = tableDifferences.getRowsCount();
        //String rowsCountExpected = getTestSpecificTD("Differences").getValue("LineCount");
        //CustomAssert.assertTrue("Found value: "+ rowsCountExpanded.toString()+". Expected value: "+rowsCountExpected+". ", rowsCountExpanded.toString().equals(rowsCountExpected));
        
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

		//for VA and NY [Oose rull JIRA https://jira.exigeninsurance.com/browse/AAASS-23637]
		//At “Roll On” of the pended endorsement, the system will fire an error that user must manually re-enter the assignment page for the non-OOS endorsement(s)
        getPolicyType().get().rollOn().getView().fill(getTestSpecificTD("ErrorProceed"));
        getPolicyType().get().dataGather().getView().fill(getTestSpecificTD("DataGather_EP"));
        
     }
}
