/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import org.openqa.selenium.By;

import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import aaa.common.Tab;
import aaa.main.metadata.policy.PurchaseMetaData;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class PurchasePaymentMethodTab extends Tab {
	
	public Button addPaymentMethodButton = new Button(By.id("purchaseForm:addPaymentMethodBtn"));
	public Button addCardButton = new Button(By.id("paymentMethodForm:pciSaveBtn"));
	public Button addSaveButton = new Button(By.id("paymentMethodEFTForm:eftSaveBtn"));
	public Button backButton = new Button(By.id("primaryButtonsForm:backButton_footer"));
	
    public PurchasePaymentMethodTab() {
        super(PurchaseMetaData.PurchasePaymentMethod.class);
    }
    
    @Override
	public Tab fillTab(TestData td) {
    	addPaymentMethodButton.click();
		super.fillTab(td);
		if(addCardButton.isPresent()) {
			addCardButton.click();
		}else if(addSaveButton.isPresent()){
			addSaveButton.click();
		}
		
		return this;
	}

    @Override
    public Tab submitTab() {
    	backButton.click();
        return this;
    }
}
