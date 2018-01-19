package aaa.modules.regression.sales.auto_ss.functional;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import aaa.helpers.listeners.AaaTestListener;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions;
import toolkit.db.DBService;

@Listeners({AaaTestListener.class})
public class EvalueInsertSetup implements EvalueInsertSetupPreConditions {

    @Test(description = "Delete old tasks")
    public static void deleteOldTasksUpdate() {
        DBService.get().executeUpdate(DELETE_OLD_TASKS1);
        DBService.get().executeUpdate(DELETE_OLD_TASKS2);
    }

    @Test(description = "Precondition updating Payperless Preferences Endpoint to a Stub")
    public static void paperlessPreferencesStubEndpointUpdate() {
        DBService.get().executeUpdate(String.format(PAPERLESS_PREFERENCE_API_SERVICE_UPDATE, APP_HOST));
    }

    @Test(description = "setting Agent/Agency check against Zip to stub", enabled=false)
    public static void channelIdResolverStubEndpointUpdate() {
        DBService.get().executeUpdate(String.format(CHANNEL_ID_RESOLVER_STUB_POINT_UPDATE, APP_HOST));
    }

    @Test(description = "Precondition updating Membership Summary Endpoint to Stub")
    public static void retrieveMembershipSummaryStubEndpointUpdate() {
        DBService.get().executeUpdate(String.format(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_UPDATE, APP_HOST));
    }

    @Test(description = "Precondition for AHDRXX form generation")
    public static void ahdrxxConfigCheckUpdate() {
        List<String> configForStatesLimits = Arrays.asList(
                "VA"
                , "DC");
        for (String configForStatesLimit : configForStatesLimits) {
            if (!DBService.get().getValue(String.format(AHDRXX_CONFIG_CHECK, configForStatesLimit)).isPresent()) {
                DBService.get().executeUpdate(String.format(AHDRXX_CONFIG_INSERT, configForStatesLimit));
            }
        }
    }

    @Test(description = "Precondition for AHDEXX form generation")
    public static void ahdexxConfigCheckUpdate() {
        List<String> configForStatesLimits = Arrays.asList(
                "VA"
                , "DC");
        for (String configForStatesLimit : configForStatesLimits) {
            if (!DBService.get().getValue(String.format(AHDEXX_CONFIG_CHECK, configForStatesLimit)).isPresent()) {
                DBService.get().executeUpdate(String.format(AHDEXX_CONFIG_INSERT, configForStatesLimit));
            }
        }
    }

    @Test(description = "Precondition for eValue related Document Generation, different endpoint than Master or PAS13")
    public static void eValueDocGenStubEndpointInsert() {
        DBService.get().executeUpdate(DOC_GEN_WEB_CLIENT);
        DBService.get().executeUpdate(AAA_RETRIEVE_AGREEMENT_WEB_CLIENT);
        DBService.get().executeUpdate(AAA_RETRIEVE_DOCUMENT_WEB_CLIENT);
    }

    @Test(description = "Precondition for enabling eValue Configuration for States with Paperless Preferences stubbed")
    public static void eValueConfigInsert() {
        List<String> configForStates = Arrays.asList("VA"  //for Paperless Preferences = Yes
                , "MD"  //for Paperless Preferences = Pending
                , "DC"); //for Paperless Preferences = No
        //PA should not have eValue or Paperless Preferences Configuration
        for (String configForState : configForStates) {
            insertConfigForRegularStates(configForState);
        }

        List<String> configForStatesLimits = Arrays.asList(
                "MD"
                , "DC");
        for (String configForStatesLimit : configForStatesLimits) {
            insertConfigForLimitsRegularStates(configForStatesLimit);
        }
    }

    @Test(description = "Precondition for Prior BI Limits configurations")
    public static void eValuePriorBiConfigUpdateInsert() {
        DBService.get().executeUpdate(EVALUE_PRIOR_BI_CONFIG_INSERT);
    }

    @Test(description = "Precondition for Current BI Limits configurations")
    public static void eValueCurrentBiConfigUpdateInsert() {
        DBService.get().executeUpdate(EVALUE_CURRENT_BI_CONFIG_INSERT);
    }

    private static void insertConfigForRegularStates(String state) {
        DBService.get().executeUpdate(String.format(EVALUE_CONFIGURATION_PER_STATE_INSERT, state));
        DBService.get().executeUpdate(String.format(PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_INSERT, state));
    }

    private static void insertConfigForLimitsRegularStates(String state) {
        DBService.get().executeUpdate(String.format(EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT, state));
        DBService.get().executeUpdate(String.format(EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT, state));
    }

    @Test(description = "Precondition for eValue Channel and Territory configurations")
    public static void eValueTerritoryChannelForVAConfigUpdate() {
        DBService.get().executeUpdate(EVALUE_TERRITORY_CHANNEL_FOR_VA_CONFIG_UPDATE);
    }

    @Test(description = "Precondition Refund/Payment handling, turning on pcDisbursementEngine related functionality")
    public static void refundDocumentGenerationConfigInsert() {
        DBService.get().executeUpdate(REFUND_DOCUMENT_GENERATION_CONFIGURATION_INSERT_SQL);
    }

    @Test(description = "Precondition for to be able to Add Payment methods, Payment Central is stubbed")
    public static void paymentCentralStubEndPointUpdate() {
        DBService.get().executeUpdate(String.format(PAYMENT_CENTRAL_STUB_ENDPOINT_UPDATE, APP_HOST));
    }

    @Test(description = "Precondition")
    public static void eValueMembershipAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_MEMBERSHIP_CONFIG_ACKNOWLEDGEMENT_INSERT); }

    @Test(description = "Precondition")
    public static void eValueCurrentBIAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_CURRENT_BI_CONFIG_ACKNOWLEDGEMENT_INSERT); }

    @Test(description = "Precondition")
    public static void eValuePayPlanAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_PAYPLAN_CONFIG_ACKNOWLEDGEMENT_INSERT); }

    @Test(description = "Precondition")
    public static void eValueMyPolicyAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_MYPOLICY_CONFIG_ACKNOWLEDGEMENT_INSERT); }

    @Test(description = "Precondition")
    public static void eValueCreditCardAcknowledgementConfigInsert() { DBService.get().executeUpdate(EVALUE_CREDITCARD_CONFIG_ACKNOWLEDGEMENT_INSERT); }

    @Test(description = "Precondition")
    public static void eValuePaperlessPreferencesBlueBoxConfigInsert() { DBService.get().executeUpdate(EVALUE_PAPERLESS_PREFERENCES_CONFIG_BLUE_BOX_INSERT); }

    @Test(description = "Precondition")
    public static void eValuePriorInsuranceBlueBoxConfigInsert() { DBService.get().executeUpdate(EVALUE_PRIOR_INSURANCE_CONFIG_BLUE_BOX_INSERT); }

    @Test(description = "Precondition for eRefund configuration")
    public static void refundConfigurationUpdate() { DBService.get().executeUpdate(REFUND_CONFIG_UPDATE); }

    @Test(description = "Precondition updating last payment method stub end points")
    public static void lastPaymentMethodStubPointUpdate() {
        DBService.get().executeUpdate(String.format(LAST_PAYMENT_METHOD_STUB_POINT_UPDATE, APP_HOST));
    }

    @Test(description = "Precondition updating pending refund configuration")
    public static void pendingRefundConfigurationUpdate() {
        DBService.get().executeUpdate(PENDING_REFUND_CONFIGURATION_UPDATE);
    }

    @Test(description = "Precondition updating Authentication stub end points")
    public static void authenticationStubPointUpdate() {
        DBService.get().executeUpdate(String.format(AUTHENTICATION_STUB_POINT_UPDATE, APP_HOST));
    }

    @Test(description = "delete unnecessary privilege from all roles")
    public static void deleteUnnecessaryPrivilegeFromAllRoles() {
        DBService.get().executeUpdate(DELETE_UNNECESSARY_PRIVILEGE_FROM_ALL_ROLES);
    }
}
