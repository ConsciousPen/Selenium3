/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

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
import aaa.modules.regression.service.helper.TestMiniServicesNonPremiumBearingAbstract;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesNonPremiumBearing extends TestMiniServicesNonPremiumBearingAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
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
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-1441", "PAS-5986, PAS-343"})
    public void pas1441_emailChangeOutOfPas(@Optional("VA") String state) {

        CustomAssert.enableSoftMode();
        pas1441_emailChangeOutOfPasTestBody(getPolicyType());
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }


    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a policy
     * 3. Create an endorsement, issue
     * 4. Check Green Button endorsement is allowed and there are no errors present
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
    public void pas6560_endorsementValidateAllowedNoEffectiveDate(@Optional("VA") String state) {

        pas6560_endorsementValidateAllowed(getPolicyType());
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a policy
     * 3. Create an endorsement, issue
     * 4. Check Green Button endorsement is allowed and there are no errors present
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
    public void pas6560_endorsementValidateAllowed(@Optional("VA") String state) {

        pas6560_endorsementValidateAllowed(getPolicyType());
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a policy
     * 3. Start an endorsement created by user, but not finish (Pended Endorsement)
     * 4. Check Green Button endorsement is allowed and there are no errors
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560"})
    public void pas6560_endorsementValidateAllowedPendedEndorsementUser(@Optional("VA") String state) {

        pas6560_endorsementValidateAllowedPendedEndorsementUser(getPolicyType());
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a policy
     * 3. Create endorsement in the Future, issue
     * 4. Check Endorsement is Not allowed and there is an error about OOSE or Future Dated Endorsement
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
    public void pas6562_endorsementValidateNotAllowedFutureDatedEndorsement(@Optional("VA") String state) {

        pas6562_endorsementValidateNotAllowedFutureDatedEndorsement(getPolicyType());
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a NANO policy
     * 3. Check Green Button endorsement is not allowed. There is a PolicyRules error about NANO
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
    public void pas6562_endorsementValidateNotAllowedNano(@Optional("VA") String state) {

        pas6562_endorsementValidateNotAllowedNano(getPolicyType(), state);
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a policy
     * 3. Start an endorsement created by System, but not finish (Pended Endorsement)
     * 4. Check Green Button endorsement is not allowed. There is a PolicyRules error about System Pended Endorsement
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
    public void pas6562_endorsementValidateNotAllowedPendedEndorsementSystem(@Optional("VA") String state) {

        pas6562_endorsementValidateNotAllowedPendedEndorsementSystem(getPolicyType());
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a policy with a vehicle with UBI
     * 3. Check Green Button endorsement is not allowed. There is a VehicleRules error about UBI
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
    public void pas6562_endorsementValidateNotAllowedUBI(@Optional("VA") String state) {

        pas6562_endorsementValidateNotAllowedUBI(getPolicyType());
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Email change through service
     * @scenario 1. Create customer
     * 2. Create a policy with a vehicle with UBI
     * 3. Check Green Button endorsement is not allowed. There is a VehicleRules error about UBI
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-6560", "PAS-6562", "PAS-6568"})
    public void pas6562_endorsementValidateNotAllowedOutOfBound(@Optional("VA") String state) {

        pas6562_endorsementValidateNotAllowedUBI(getPolicyType());
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
