/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class PremiumAndCoveragesQuoteTab extends Tab {
    public PremiumAndCoveragesQuoteTab() {
        super(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class);
    }
    
    public static Button btnCalculatePremium = new Button(By.id("policyDataGatherForm:calculatePremiumPup"), Waiters.AJAX);
	public static Button btnContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);
	public static Table tableTotalPremium = new Table(By.id("policyDataGatherForm:pupTableTotalPremium"));
	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(td);
		calculatePremium();
		return this;
	}

	public void calculatePremium() {
		btnCalculatePremium.click();
	}
	
    @Override
    public Tab submitTab() {
    	btnContinue.click();
        return this;
    }

    public static Dollar getPolicyTermPremium() {
  		return new Dollar(tableTotalPremium.getRow(1).getCell(2).getValue());
      }
}
