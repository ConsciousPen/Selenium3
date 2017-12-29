package aaa.modules.regression.billing_and_payments.helpers;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.DisbursementEngineHelper;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import toolkit.config.PropertyProvider;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.StaticElement;

public class RefundProcessHelper extends PolicyBilling {

    private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
    private static final String REMOTE_FOLDER_PATH = "/home/mp2/pas/sit/DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";
    private static final String LOCAL_FOLDER_PATH = "src/test/resources/stubs/";
    private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();

    public void refundRecordInFileCheck(String policyNumber, String refundType, String refundMethod, String productType, String companyId, String refundAmount, String email, String refundEligible) throws IOException {
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
        String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
        acceptPaymentActionTab.back();

        //TODO doesn't work in VDMs
        RemoteHelper.waitForFilesAppearance(REMOTE_FOLDER_PATH, 10, policyNumber);
        String neededFilePath = RemoteHelper.waitForFilesAppearance(REMOTE_FOLDER_PATH, "csv", 10, policyNumber).get(0);
        String fileName = neededFilePath.replace(REMOTE_FOLDER_PATH, "");

        RemoteHelper.downloadFile(neededFilePath, LOCAL_FOLDER_PATH + fileName);

        List<DisbursementEngineHelper.DisbursementFile> listOfRecordsInFile = DisbursementEngineHelper.readDisbursementFile(LOCAL_FOLDER_PATH + fileName);
        DisbursementEngineHelper.DisbursementFile neededLine = null;
        for (DisbursementEngineHelper.DisbursementFile s : listOfRecordsInFile) {
            if (s.getAgreementNumber().equals(policyNumber)) {
                neededLine = s;
            }
        }
        CustomAssert.assertTrue(neededLine.getRecordType().equals("D"));
        neededLine.getRequestRefereceId().equals(transactionID);
        CustomAssert.assertTrue(neededLine.getRefundType().equals(refundType));
        CustomAssert.assertTrue(neededLine.getRefundMethod().equals(refundMethod));
        CustomAssert.assertTrue(neededLine.getIssueDate().equals(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MMddyyyy"))));
        CustomAssert.assertTrue(neededLine.getAgreementNumber().equals(policyNumber));
        CustomAssert.assertTrue(neededLine.getAgreementSourceSystem().equals("PAS"));
        CustomAssert.assertTrue(neededLine.getProductType().equals(productType));
        CustomAssert.assertTrue(neededLine.getCompanyId().equals(companyId));
        CustomAssert.assertFalse(neededLine.getInsuredFirstName().isEmpty());
        CustomAssert.assertFalse(neededLine.getInsuredLastName().isEmpty());
        //TODO update once the deceased indicator is implemented
        //CustomAssert.assertTrue(neededLine.deceasedNamedInsuredFlag.equals("Y"));
        CustomAssert.assertTrue(neededLine.getPolicyState().equals("VA"));
        CustomAssert.assertTrue(neededLine.getRefundAmount().equals(refundAmount+".00"));
        CustomAssert.assertTrue(neededLine.getPayeeName().equals(neededLine.getInsuredFirstName() + " " + neededLine.getInsuredLastName()));
        CustomAssert.assertFalse(neededLine.getPayeeStreetAddress1().isEmpty());
        CustomAssert.assertFalse(neededLine.getPayeeCity().isEmpty());
        CustomAssert.assertFalse(neededLine.getPayeeState().isEmpty());
        CustomAssert.assertFalse(neededLine.getPayeeZip().isEmpty());
        CustomAssert.assertTrue(neededLine.getInsuredEmailId().contains(email));
        CustomAssert.assertTrue(neededLine.getCheckNumber().equals(""));
        CustomAssert.assertTrue(neededLine.getPrinterIdentificationCode().equals("FFD"));
        CustomAssert.assertTrue(neededLine.getRefundReason().equals("Overpayment"));
        CustomAssert.assertTrue(neededLine.getRefundReasonDescription().equals(""));
        CustomAssert.assertTrue(neededLine.getReferencePaymentTransactionNumber().equals(""));
        //TODO missing config in PAS18.1
        CustomAssert.assertTrue(neededLine.geteRefundEligible().equals(refundEligible));
    }
}
