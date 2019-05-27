package aaa.modules.regression.service.auto_ca.select.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
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
                assertSoftly(softly ->
            pas15369_reportOrderAndDriverBody(softly)//TODO-mstrazds: Not in suite. Needs to be finished verified in scope of PAS-29245 in Sprint 55
        );
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

        /**
         * @author Maris Strazds
         * @name Test Report Ordering for Endorsement (not a Named Insured)
         * @scenario 1. Create a policy in PAS
         * 2. Create an endorsement through service
         * 3. Add Driver 1 (not a Named Insured) through service with MVR status =  Hit - Activity Found, CLUE status = processing complete, results clear
         * 4. Run the Report Order Service for MVR/CLUE
         * 5. Open the Endorsement in PAS, navigate to "Driver Activity Reports" tab and validate that MVR/CLUE reports have been ordered successfully with no errors
         * 6. Validate that I receive the report response
         * 		AND it is viewable in PAS (pdf)
         * 		AND it is reconciled in PAS
         * 		AND a positive response is provided
         * 7. Rate and bind the policy
         * 8. Rate and Bind
         * 9. Create an endorsement through service
         * 10. Add Driver 2 (not Named Insured) through service with MVR status =  Clear, CLUE status = processing complete, with results information
         * 11. Repeat steps 4 - 8
         */

        @Parameters({"state"})
        @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
        @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-16694", "PAS-15431"})
        public void pas16694_orderReports_not_Named_Insured_endorsement(@Optional("CA") String state) {

                pas16694_orderReports_not_Named_Insured_endorsementBody(getPolicyType());
        }

        /**
         * @author Megha Gubbala
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
        @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-29245"})
        public void pas29245_reportOrderAndDriver_License_status(@Optional("CA") String state) {
                assertSoftly(softly ->
                        pas15369_reportOrderAndDriverBody(softly)
                );
        }

        /**
         * @author Megha Gubbala
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
        @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-29245"})
        public void pas27222_UnderwritingRulesCASelectAAA_CSA3292736(@Optional("CA") String state) {
                assertSoftly(softly ->
                        pas27222_UnderwritingRulesCASelectAAA_CSA3292736_Body(softly)
                );
        }
}




