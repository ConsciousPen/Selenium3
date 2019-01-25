/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.service.template.PolicyRenewOose;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @scenario
 * Common functionality from template:
 * 1. Create Customer
 * 2. Create Policy
 * 3. Create manual renew image -> change payment plan
 * 4. Bind endorsement without conflicting change - change some coverage (payment plan remains the same)
 * 5. Bind second endorsement with conflicting change - change payment plan
 * 
 * Product specific checking:
 * 6. Check renewal versions: 
 * (currently implemented case for OFF oose feature)
 *  6.1 first renewal version (created after first endorsement)
 *      in case if oose feature is turned off payment plan should be taken from original policy = Annual
 *      in case if oose feature is turned on, values from renew and endorsement should be merged and payment plan should be taken from renew = Semi-Annual
 *  
 *  6.2 second renewal version (created after second endorsement)
 *      in case if oose feature is turned off payment plan should be taken from endorsement = Quarterly (Renewal)
 *      in case if oose feature is turned on, values are depends on selected on OOSE screen
 *  
 * @details
 */
public class TestPolicyRenewOose extends PolicyRenewOose {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }
    
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	   public void testPolicyRenewOOSE(@Optional("") String state) {
		
           super.testPolicyRenewOOSE();
        
           
           //product's specific verifications
           //check version2 - without conflicts
           PolicySummaryPage.tableTransactionHistory.getRow(2).getCell("Type").controls.links.get(1).click();
           
           
           NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
    
           //in case if oose feature is turned off payment plan should be taken from original policy = Annual
           //in case if oose feature is turned on, values from renew and endorsement should be merged and payment plan should be taken from renew = Semi-Annual
    	   assertThat(new PremiumAndCoveragesTab()
    			   .getInquiryAssetList()
    			   .getStaticElement(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN))
    			   .hasValue("Semi-Annual (Renewal)");
    			   //.getValue()).isEqualTo("Annual (Renewal)");
    	    
	   	   PremiumAndCoveragesTab.RatingDetailsView.open();
	   	   PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
	   	   assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Bodily Injury Liability").getCell(2)).hasValue("$250,000/$500,000");
	   	   PremiumAndCoveragesTab.RatingDetailsView.close();
	    	   
    	   Tab.buttonCancel.click();
    	   
    	   //check current version - with conflict
           PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type").controls.links.get(1).click();
           
           NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
           
    	   assertThat(new PremiumAndCoveragesTab()
    			   .getInquiryAssetList()
			       .getStaticElement(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN))
			       .hasValue("Quarterly (Renewal)");
	}
	
}