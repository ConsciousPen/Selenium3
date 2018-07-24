package aaa.modules.regression.service.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * @author Jelena Dembovska
 * @scenario
 * Common functionality from template:
 * 1. Create Customer
 * 2. Create Policy
 * 3. Create manual renew image -> change payment plan
 * 4. Bind endorsement without conflicting change - change some coverage (payment plan remains the same)
 * 5. Bind second endorsement with conflicting change - change payment plan
 * @details
 */

public class PolicyRenewOose extends PolicyBaseTest {

    public void testPolicyRenewOOSE() {
			
	        mainApp().open();

	        
	        createCustomerIndividual();
	        createPolicy();
	        
	        //change payment plan value
	        policy.renew().performAndExit(getTestSpecificTD("TestData_Renew"));
	         
	        //change value for some coverage
	        policy.createEndorsement(getTestSpecificTD("TestData_endorsement_without_conflict").adjust(getPolicyTD("Endorsement", "TestData")));
	        
	        //check: new renewal version is created automatically
	        PolicySummaryPage.buttonRenewalQuoteVersion.click();
	        assertThat(PolicySummaryPage.tableTransactionHistory.getRowsCount()).isEqualTo(2);	        
	        PolicySummaryPage.buttonQuoteOverview.click();
	        
	        
	        //bind endorsement with conflicting change - change payment plan
	        policy.createEndorsement(getTestSpecificTD("TestData_endorsement_with_conflict").adjust(getPolicyTD("Endorsement", "TestData")));
	        
	        //here will be comparison screen when OOSE feature will be turned on
	        rollOnChangesOnDifferencesTab();
	        
	        //check: one more renewal version is created automatically
	        PolicySummaryPage.buttonRenewalQuoteVersion.click();
	        assertThat(PolicySummaryPage.tableTransactionHistory.getRowsCount()).isEqualTo(3);
	        
	        //product's specific checking will follow in test for concrete product
	        
	    }
    
    private void rollOnChangesOnDifferencesTab() {
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
        
        //apply current value
        for (int i = 1; i <= rowsCountExpanded; i++) {
        	Link linkSetCurrent = tableDifferences.getRow(i).getCell(columnsCount).controls.links.get("Current");
                 
            if (linkSetCurrent.isPresent() && linkSetCurrent.isVisible()) {
            	linkSetCurrent.click();
            }
        }
        
        getPolicyType().get().rollOn().submit();
    }
		
}
