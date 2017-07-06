/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.general.numberrange;

import aaa.EntityLogger;
import aaa.admin.modules.general.numberrange.NumberRangeActions.EliminateNumberRange;
import aaa.admin.modules.general.numberrange.views.DefaultView;
import aaa.admin.pages.general.NumberRangePage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class NumberRange implements INumberRange {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        NumberRangePage.buttonAddRange.click();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.GENERAL.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_NUMBER_RANGES.get());
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.NUMBER_RANGE);
        NumberRangePage.buttonSubmitRange.click();
        log.info("Created Number Range " + entity);
    }

    @Override
    public void search(TestData td) {
        NumberRangePage.search(td);
    }

    @Override
    public EliminateNumberRange eliminate() {
        return new NumberRangeActions.EliminateNumberRange();
    }
}
