package aaa.modules.regression.service.auto_ca.select.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesMVRAndClueReportOrderHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestMiniServicesMVRAndClueReportOrder extends TestMiniServicesMVRAndClueReportOrderHelper {

        @Override
        protected PolicyType getPolicyType() {
            return PolicyType.AUTO_CA_SELECT;
        }

        /**
         * @author Chaitanya Boyapati
         * @name Report Information and the Conviction Date and driver reports
         * @scenario 1. Create policy.
         * 2. Create endorsement outside of PAS.
         * 3. Add driver with: Accident fault Violation
         * 4. Verify response on DXP in License status and conviction date as correct
         * 5. Check Pas And verify if the dates are matching to pas.
         * 6. Verify driver activity and verify status there.
         */
        @Parameters({"state"})
        @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
        @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15431"})
        public void pas15431_reportOrderAndDriver(@Optional("CA") String state) {
            pas15369_reportOrderAndDriverBody();

        }

        /**
         * @author Chaitanya Boyapati
         * @name Report Information and the Conviction Date and driver reports
         * @scenario 1. Create policy.
         * 2. Create endorsement outside of PAS.
         * 3. Add driver with: Accident fault Violation
         * 4. Verify response on DXP in License status and conviction date as correct
         * 5. Check Pas And verify if the dates are matching to pas.
         * 6. Verify driver activity and verify status there.
         */
        @Parameters({"state"})
        @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
        @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15431"})
        public void pas15431_reportOrderAndDriver1(@Optional("CA") String state) {
            pas15372_driverDetailsAndMvrRulesThatProvidedBody();

        }

        /**
         * @author Chaitanya Boyapati
         * @name Report Information and the Conviction Date and driver reports
         * @scenario 1. Create policy.
         * 2. Create endorsement outside of PAS.
         * 3. Add driver with: Accident fault Violation
         * 4. Verify response on DXP in License status and conviction date as correct
         * 5. Check Pas And verify if the dates are matching to pas.
         * 6. Verify driver activity and verify status there.
         */
        @Parameters({"state"})
        @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
        @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15431"})
        public void pas15431_reportOrderAndDriver2(@Optional("CA") String state) {
            pas15077_orderReports_endorsementBody(getPolicyType());

        }
    }




