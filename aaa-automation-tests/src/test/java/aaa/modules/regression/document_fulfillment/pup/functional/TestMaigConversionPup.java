package aaa.modules.regression.document_fulfillment.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeAbstract;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMaigConversionPup extends TestMaigConversionHomeAbstract {

    /**
     * @name Test Conversion Document generation (Insurance Renewal Bill)
     * @scenario 1. Create Customer
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data for Home
     * 4. Check that AHRBXX document is getting generated
     * @details
     */
    @Override
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-8789"})
    public void pas8789_insuranceRenewalBillHomeAHRBXX(@Optional("VA") String state) throws NoSuchFieldException {
        pas8789_insuranceRenewalBillPupAHRBXX(state, PolicyType.HOME_SS_HO3);
    }

    /**
     * @name Test MAIG Document generation (Renewal offer package)
     * @scenario
     * 1. Create a Customer and a HO4 policy
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data, add a previously created policy to 'other policies' section
     * 3. Check that HSRNPUPXX document section is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2571"})
    public void pas2571_renewalCoverLetterHSRNPUPXXHO4(@Optional("VA") String state) throws NoSuchFieldException {
        pas2571_renewalCoverLetterHSRNPUPXX(state, PolicyType.HOME_SS_HO4);
    }

    /**
     * @name Test MAIG Document generation (Renewal offer package)
     * @scenario
     * 1. Create a Customer and a HO6 policy
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data, add a previously created policy to 'other policies' section
     * 3. Check that HSRNPUPXX document section is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2571"})
    public void pas2571_renewalCoverLetterHSRNPUPXXHO6(@Optional("VA") String state) throws NoSuchFieldException {
        pas2571_renewalCoverLetterHSRNPUPXX(state, PolicyType.HOME_SS_HO6);
    }

    /**
     * @name Test MAIG Document generation (Renewal offer package)
     * @scenario
     * 1. Create a Customer and a HO3 policy
     * 2. Initiate Renewal Entry
     * 3. Fill Conversion Policy data, add a previously created policy to 'other policies' section
     * 3. Check that HSRNPUPXX document section is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.DocumentFulfillment.PUP, testCaseId = {"PAS-2571"})
    public void pas2571_renewalCoverLetterHSRNPUPXXHO3(@Optional("VA") String state) throws NoSuchFieldException {
        pas2571_renewalCoverLetterHSRNPUPXX(state, PolicyType.HOME_SS_HO3);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.PUP;
    }

}