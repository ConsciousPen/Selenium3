/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.regression.service.helper.TestMiniServicesNonPremiumBearingAbstract;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesNonPremiumBearing extends TestMiniServicesNonPremiumBearingAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }


    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a policy
     * 3. Run a request in Swagger-UI
     * 4. Check Transaction history contains "Email Updated - External System"  for a new endorsement
     * 5. Check quote in Inquiry mode has new eMail address in General Tab and in Documents Tab
     * 6. Do one more endorsement, issue
     * Check there is no extra Actions for the product
     * Check that number of document records in the DB before running the eMail update is equal to the number of document records after update
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-1441", "PAS-5986", "PAS-343"})
    public void pas1441_emailChangeOutOfPas(@Optional("CA") String state) {

        CustomAssert.enableSoftMode();
        pas1441_emailChangeOutOfPasTestBody(getPolicyType());
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Override
    protected String getGeneralTab() {
        return NavigationEnum.AutoCaTab.GENERAL.get();
    }

    @Override
    protected String getPremiumAndCoverageTab() {
        return NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get();
    }

    @Override
    protected String getDocumentsAndBindTab() {
        return NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get();
    }

    @Override
    protected String getVehicleTab() {
        return NavigationEnum.AutoCaTab.VEHICLE.get();
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
