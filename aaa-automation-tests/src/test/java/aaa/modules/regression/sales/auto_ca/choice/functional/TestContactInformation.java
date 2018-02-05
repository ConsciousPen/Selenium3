/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import toolkit.utils.TestInfo;


public class TestContactInformation extends aaa.modules.regression.sales.auto_ca.select.functional.TestContactInformation {
    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * * @author Viktoriia Lutsenko
     * <p>
     * PAS-270
     * <p>
     * See detailed steps in template file
     * {@link aaa.modules.regression.sales.template.functional.TestContactInformationAbstract}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = {"PAS-270", "PAS-267"})
    public void pas270_contactInformation(@Optional("CA") String state) {

        super.pas270_contactInformation(state);
    }
}
