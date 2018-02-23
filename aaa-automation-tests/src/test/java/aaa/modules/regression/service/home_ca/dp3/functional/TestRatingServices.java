/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.regression.service.helper.TestRatingServicesAbstract;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestRatingServices extends TestRatingServicesAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_DP3;
    }

    /**
     * @author Oleg Stasyuk
     * @name Retrieve Mmebership Discount % from rating algo
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10302"})
    public void pas10302_discountPercentageRetrieve(@Optional("VA") String state) {

        pas10302_discountPercentageRetrieveBody("DP3", "CA", "CovA", "1.0");
    }


    @Override
    protected String getGeneralTab() {
        return NavigationEnum.AutoSSTab.GENERAL.get();
    }

    @Override
    protected String getPremiumAndCoverageTab() {
        return NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get();
    }

    @Override
    protected String getDocumentsAndBindTab() {
        return NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get();
    }

    @Override
    protected String getVehicleTab() {
        return NavigationEnum.AutoSSTab.VEHICLE.get();
    }

    @Override
    protected Tab getGeneralTabElement() {
        return new GeneralTab();
    }

    @Override
    protected Tab getPremiumAndCoverageTabElement() {
        return new PremiumAndCoveragesTab();
    }

    @Override
    protected Tab getDocumentsAndBindTabElement() {
        return new DocumentsAndBindTab();
    }

    @Override
    protected Tab getVehicleTabElement() {
        return new VehicleTab();
    }

    @Override
    protected AssetDescriptor<Button> getCalculatePremium() {
        return AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM;
    }

}
