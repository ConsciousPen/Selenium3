/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyEndorsementsTab;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class EndorsementsTab extends PropertyEndorsementsTab {
    public EndorsementsTab() {
        super(PersonalUmbrellaMetaData.EndorsementsTab.class);

	    tblIncludedEndorsements = new Table(By.xpath("//div[@id='policyDataGatherForm:selectedObjects_PupPolicyEndorsementFormManager']/descendant::table[@class='dataObjectManagerStaticTable dataObjectManagerStaticTableSelected']"));
	    tblOptionalEndorsements = new Table(By.xpath("//table[@id='policyDataGatherForm:availableTable_PupPolicyEndorsementFormManager']"));

	    TBL_SUB_ENDORSEMENTS_BY_FORMID_TEMPLATE = "//div[@id='policyDataGatherForm:selectedObjects_PupPolicyEndorsementFormManager:content']/descendant::table[1]/tbody/tr[count(../tr[td[position()=1 and normalize-space(.)='%s']]/preceding-sibling::tr)+3]/descendant::table[1]";
	    LNK_ADD_SUB_ENDORSEMENT_BY_FORMID_TEMPLATE = "//div[@id='policyDataGatherForm:selectedObjects_PupPolicyEndorsementFormManager:content']/descendant::table[1]/tbody/tr[count(../tr[td[position()=1 and normalize-space(.)='%s']]/preceding-sibling::tr)+2]//a[.='Add']";

	    btnSaveForm = new Button(By.id("policyDataGatherForm:editObjectSaveBtnPupPolicyEndorsementFormManager"));
	    btnCancelForm = new Button(By.id("policyDataGatherForm:editObjectCancelBtn_PupPolicyEndorsementFormManager"));
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
}
