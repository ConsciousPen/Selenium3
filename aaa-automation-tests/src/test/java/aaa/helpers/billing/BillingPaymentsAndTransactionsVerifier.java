/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingPaymentsAndTransactionsVerifier extends TableVerifier {

    public BillingPaymentsAndTransactionsVerifier setType(String value) {
        setValue("Type", value);
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setSubtypeReason(String value) {
        setValue("Subtype/Reason", value);
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setAmount(Dollar value) {
        setValue("Amount", value.toString());
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setStatus(String value) {
        setValue("Status", value);
        return this;
    }

    public void verifyPolicyPremium(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.POLICY);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyRescindCancellationPremium(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_RESCIND_CANCELLATION);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyPolicyMoved(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.ADJUSTMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.POLICY_MOVED);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyRenewal(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyInstallmentFee(int rowNumber) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.INSTALLMENT_FEE);
        setAmount(BillingHelper.INSTALLMENT_FEE);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyInstallmentFee(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.INSTALLMENT_FEE);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyInstallmentFeeWaived(int rowNumber) {
        verifyInstallmentFeeWaived(rowNumber, BillingHelper.INSTALLMENT_FEE.negate());
    }

    public void verifyInstallmentFeeWaived(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.INSTALLMENT_FEE_WAIVED);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyServiceFeeApplied(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.SERVICE_FEE);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyServiceFeeWaived(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.SERVICE_FEE_WAIVED);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyManualPayment(int rowNumber, Dollar amount, String status) {
        setType(PaymentsAndOtherTransactionType.PAYMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT);
        setAmount(amount);
        setStatus(status);
        verify(rowNumber);
    }

    public void verifyManualPaymentCleared(int rowNumber, Dollar amount) {
        verifyManualPayment(rowNumber, amount, PaymentsAndOtherTransactionStatus.CLEARED);
    }

    public void verifyManualPaymentApplied(int rowNumber, Dollar amount) {
        verifyManualPayment(rowNumber, amount, PaymentsAndOtherTransactionStatus.APPLIED);
    }

    public void verifyManualPaymentDeclined(int rowNumber, Dollar amount) {
        verifyManualPayment(rowNumber, amount, PaymentsAndOtherTransactionStatus.DECLINED);
    }

    public void verifyBulkPayment(int rowNumber, Dollar amount, String status) {
        setType(PaymentsAndOtherTransactionType.PAYMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.BULK_PAYMENT);
        setAmount(amount);
        setStatus(status);
        verify(rowNumber);
    }

    public void verifyBulkPaymentIssued(int rowNumber, Dollar amount) {
        verifyBulkPayment(rowNumber, amount, PaymentsAndOtherTransactionStatus.ISSUED);
    }

    public void verifyPaymentTransferred(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.ADJUSTMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.PAYMENT_TRANSFERRED);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyPaymentDeclined(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.ADJUSTMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyNotSufficientFundsFee(int rowNumber) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.NOT_SUFFICIENT_FUNDS_FEE);
        setAmount(BillingHelper.PAYMENT_DECLINE_FEE);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyCancelNoticeFee(int rowNumber) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_NOTICE_FEE);
        setAmount(BillingHelper.CANCEL_NOTICE_FEE);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyCancellationFee(int rowNumber) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_FEE);
        setAmount(BillingHelper.CANCELLATION_FEE);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyCancellationFeeWaived(int rowNumber) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_FEE_WAIVED);
        setAmount(BillingHelper.CANCELLATION_FEE.negate());
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyCancellationNonPaymentOfPremium(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_NON_PAYMENT_OF_PREMIUM);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyCancellationInsuredRequestDueToHighPremium(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_REQUEST_DUE_TO_HIGH_PREMIUM);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyOfferWithoutLapse(int rowNumber) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.OFFER_WITHOUT_LAPSE);
        setAmount(BillingHelper.OFFER_WITHOUT_LAPSE);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyRenewalProposal(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyReinstatementWithLapsePremium(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_WITH_LAPSE);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyReinstatementWithoutLapseFee(int rowNumber) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_FEE);
        setAmount(BillingHelper.REINSTATEMENT_FEE_WITHOUT_LAPSE);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyReinstatementWithoutLapsePremium(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_WITHOUT_LAPSE);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyReinstatementWithLapseFee(int rowNumber) {
        setType(PaymentsAndOtherTransactionType.FEE);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_FEE_WITH_LAPSE);
        setAmount(BillingHelper.REINSTATEMENT_FEE_WITH_LAPSE);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyManualRefund(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.REFUND);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.MANUAL_REFUND);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.CLEARED);
        verify(rowNumber);
    }

    public void verifyAutomaticRefund(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.REFUND);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPROVED);
        verify(rowNumber);
    }

    public void verifyWriteOff(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.ADJUSTMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.WRITE_OFF);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyEarnedPremiumWriteOff(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.ADJUSTMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyEarnedPremiumWriteOffReversed(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.ADJUSTMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF_REVERSED);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyEndorsement(int rowNumber, Dollar amount, String reason) {
        setType(PaymentsAndOtherTransactionType.PREMIUM);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT + " - " + reason);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyCrossPolicyAdjustment(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.ADJUSTMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.CROSS_POLICY_TRANSFER);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    public void verifyCrossPolicyPayment(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PAYMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.CROSS_POLICY_TRANSFER);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.CLEARED);
        verify(rowNumber);
    }

    public void verifyReallocatedPayment(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.PAYMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.CLEARED);
        verify(rowNumber);
    }

    public void verifyReallocatePayment(int rowNumber, Dollar amount) {
        setType(PaymentsAndOtherTransactionType.ADJUSTMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT);
        setAmount(amount);
        setStatus(PaymentsAndOtherTransactionStatus.APPLIED);
        verify(rowNumber);
    }

    @Override
    protected Table getTable() {
        return BillingSummaryPage.tablePaymentsOtherTransactions;
    }

    @Override
    protected String getTableName() {
        return "Payments Other Transactions";
    }
}
