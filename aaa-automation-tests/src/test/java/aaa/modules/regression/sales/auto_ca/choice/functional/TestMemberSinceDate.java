package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.utils.StateList;
import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestMemberSinceDate extends AutoCaChoiceBaseTest {

    /**
     * @author Brian Bond
     * @name MemberSinceDate in database matches stub response - PAS-17193
     * @scenario
     * Precondition: Have an active valid membership response from the Stub
     * 1. Create Customer.
     * 2. Create Auto CA Quote up to Membership tab.
     * 3. Validate that the Member Since Date in the DB is null.
     * 4. Order report in the UI.
     * 5. Validate that the Member Since Date in the DB now matches the Stub response.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17193: MemberSinceDate in database matches stub response")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-17193")
    public void pas17193_MemberSinceDate_DB_Matches_Stub_Response(@Optional("CA") String state) {

        // BondTODO: Remove this query after getting the DB working.
        String query = Get_RMSPolicyInfoSQLQuery("QCAAS952918572");
        // BondTODO: Get Data from DB
        //String deleteMe = DBService.get().getValue(query).get();

        /*--Step 1--*/
        Log.info("Step 1: Create Customer.");

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_RMS_MembershipValid").resolveLinks());
        mainApp().open();
        createCustomerIndividual();

        /*--Step 2--*/
        Log.info("Step 2: Create Auto CA Quote up to Membership tab.");
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, MembershipTab.class, false);

        /*--Step 3--*/
        Log.info("Step 3: Validate that the Member Since Date in the DB is null.");

        String quoteNumber = policy.getDefaultView().getTab(MembershipTab.class).labelPolicyNumber.getValue();

        // Click save to store the quote in the db so can be accessed.
        policy.getDefaultView().getTab(MembershipTab.class).buttonTopSave.click();

        // BondTODO: Get Data from DB
        String value = DBService.get().getValue(Get_RMSPolicyInfoSQLQuery(quoteNumber)).orElse("");
        // BondTODO: Assert data is not set yet.

        /*--Step 4--*/
        Log.info("Step 4: Order report in the UI.");
        policy.getDefaultView().getTab(MembershipTab.class).fillTab(testData);
        // BondTODO: Click save so value in DB gets updated.

        /*--Step 5--*/
        Log.info("Step 5: Validate that the Member Since Date in the DB now matches the Stub response.");
        // BondTODO: Get Data from DB
        // BondTODO: Assert data is not set yet.
    }

    private String Get_RMSPolicyInfoSQLQuery(String quoteNumber){
        String query =
                "SELECT ps.POLICYNUMBER, ps.CONVERSIONDT, OP.productcd, OP.id, OP.POLICYNUMBER AS MEMBERSHIPPOLICYEntered, MS.ORDERMEMBERSHIPNUMBER, MS.Membershipnumber AS RMSResponse, MS.MEMBERSINCEDATE AS MS_MEMBERSINCEDATE, MS.LASTNAME AS MEMBERSHIP_LASTNAME, OP.INSURERNAME AS OPLASTNAME, TRUNC(MS.ORDERDATE) AS MS_ORDERDATE, TRUNC(MS.RECEIPTDATE) AS MS_RECEIPTDATE, MS.MEMBERSHIPSTATUS, OP.insurercd, OP.OVERRIDETYPE AS OP_OVERRIDETYPE, TRUNC(OP.MEMBERSINCEDATE) AS OP_MEMBERSINCEDATE, PS.ProductCD, ps.POLICYSTATUSCD, ps.TIMEDPOLICYSTATUSCD, ps.TXSUBTYPE, ps.TXTYPE, ps.CURRENTREVISIONIND, MS.AAABESTMEMBERSHIPSTATUS, MS.ERRORCODE, MS.ERRORMESSAGETEXT, TRUNC(MS.EFFECTIVEDATE) AS MS_EFFECTIVEDATE, TRUNC(MS.ENDDATE) AS MS_ENDDATE, EMEM.EMEMBEROPT, EMEM.EVERENROLLED, EMEM.EVALUESTATUS, EMEM.EMEMBEROPTINDATE, MS.REPORTDATAENTITYID, TRUNC(ps.EFFECTIVE) AS EFFECTIVE, TRUNC(ps.EXPIRATION)AS EXPIRATION, ps.CURRENTREVISIONIND, ps.CREATEDON, TRUNC(ps.CREATEDON) AS CREATEDON, TRUNC(ps.TRANSACTIONDATE) AS TRANSACTIONDATE, TRUNC(ps.TRANSACTIONEFFECTIVEDATE) AS TRANSACTIONEFFDATE, MS.REsponsemessageService, TRUNC(MS.AAABESTMEMBERSHIPDATE)AS AAABESTMEMBERSHIPDATE , RDE.REPORTDATA, ps.POLICYDETAIL_ID, ps.id AS POLICYSUMMARYID, ms.id AS MEMSummaryId FROM policysummary ps JOIN OTHERORPRIORPOLICY OP ON OP.POLICYDETAIL_ID=ps.POLICYDETAIL_ID AND OP.PRODUCTCD ='membership' AND ps.quotenumber ='"
        + quoteNumber + "' LEFT JOIN pasadm.AAAEMEMBERDETAILSENTITY EMEM ON EMEM.ID=PS.EMEMBERDETAIL_ID JOIN MEMBERSHIPSUMMARYENTITY MS ON MS.ID=ps.MEMBERSHIPSUMMARY_ID left JOIN AAAREPORTDATAENTITY RDE ON RDE.ID=MS.REPORTDATAENTITYID AND RDE.REPORTTYPE ='MEMBERSHIP' AND RDE.ENTITYREFNO=PS.POLICYNUMBER ORDER BY ps.EFFECTIVE DESC;\n";



        return query;
    }
}
