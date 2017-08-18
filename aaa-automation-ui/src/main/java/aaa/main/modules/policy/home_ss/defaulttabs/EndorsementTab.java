/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.toolkit.webdriver.customcontrols.ProductOfferingVariationControl;
import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyEndorsementsTab;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class EndorsementTab extends PropertyEndorsementsTab {
	public Button addEndorsement = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);
	
    public EndorsementTab() {
        super(HomeSSMetaData.EndorsementTab.class);

	    tblIncludedEndorsements = new Table(By.xpath("//div[@id='policyDataGatherForm:selectedObjects_AAAHoPolicyEndorsementFormManager:content']/div/table"));
	    tblOptionalEndorsements = new Table(By.xpath("//table[@id='policyDataGatherForm:availableTable_AAAHoPolicyEndorsementFormManager']"));

	    TBL_SUB_ENDORSEMENTS_BY_FORMID_TEMPLATE = "//div[@id='policyDataGatherForm:selectedObjects_AAAHoPolicyEndorsementFormManager:content']/descendant::table[1]/tbody/tr[count(../tr[td[position()=1 and normalize-space(.)='%s']]/preceding-sibling::tr)+3]/descendant::table[1]";
	    LNK_ADD_SUB_ENDORSEMENT_BY_FORMID_TEMPLATE = "//div[@id='policyDataGatherForm:selectedObjects_AAAHoPolicyEndorsementFormManager:content']/descendant::table[1]/tbody/tr[count(../tr[td[position()=1 and normalize-space(.)='%s']]/preceding-sibling::tr)+2]//a[.='Add']";

	    btnSaveForm = new Button(By.id("policyDataGatherForm:editObjectSaveBtnAAAHoPolicyEndorsementFormManager"));
	    btnCancelForm = new Button(By.xpath("//input[@id='policyDataGatherForm:editObjectCancelEliminateBtn_AAAHoPolicyEndorsementFormManager' or @id='policyDataGatherForm:editObjectCancelBtn_AAAHoPolicyEndorsementFormManager']"));
    }

	public void includedEndorsementIsAded(List includedEndorsementList){
		List<String> addedEndorsements = new ArrayList<>();
		for (int i = 1; i == tblIncludedEndorsements.getRowsCount(); i++)
    		addedEndorsements.add(tblIncludedEndorsements.getRow(i).getCell(1).getValue());

    	for (int i = 0; i < includedEndorsementList.size(); i++)
			CustomAssert.assertTrue(addedEndorsements.toString().contains(includedEndorsementList.get(i).toString()));
	}

	@Override
    public Tab submitTab() {
    	buttonNext.click();
        return this;
    }
}
