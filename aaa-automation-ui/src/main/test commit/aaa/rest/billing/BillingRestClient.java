package aaa.rest.billing;
	
import aaa.rest.IRestClient;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;
import toolkit.rest.RestServiceUtil.RestMethod;

public class BillingRestClient implements IRestClient{

	private ThreadLocal<RestServiceUtil> restClient = new ThreadLocal<RestServiceUtil>() {
		@Override protected RestServiceUtil initialValue() {
			return new RestServiceUtil("billing-rs");
		}
	};
	/**
	 * <b>Target:</b> Accounts 
	 */
	public ResponseWrapper getAccounts(TestData data) {
		return getRestClient().processRequest("ACCOUNTS", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber} 
	 */
	public ResponseWrapper getAccountsItem(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.ITEM", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber} 
	 */
	public ResponseWrapper putAccountsItem(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.ITEM", RestMethod.PUT, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/installments 
	 */
	public ResponseWrapper getAccountsInstallments(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.INSTALLMENTS", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/invoices 
	 */
	public ResponseWrapper getAccountsInvoices(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.INVOICES", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/invoices/{invoiceNumber} 
	 */
	public ResponseWrapper getAccountsInvoicesItem(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.INVOICES.ITEM", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/invoices/{invoiceNumber}/coverages/{referenceNumber}/participants 
	 */
	public ResponseWrapper getAccountsInvoicesCoveragesParticipants(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.INVOICES.COVERAGES.PARTICIPANTS", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/payments 
	 */
	public ResponseWrapper getAccountsPayments(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.PAYMENTS", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/payments 
	 */
	public ResponseWrapper postAccountsPayments(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.PAYMENTS", RestMethod.POST, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/payments/pay-by-policy 
	 */
	public ResponseWrapper postAccountsPaymentsPayByPolicy(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.PAYMENTS.PAY-BY-POLICY", RestMethod.POST, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/statement 
	 */
	public ResponseWrapper getAccountsStatement(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.STATEMENT", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /accounts/{accountNumber}/transactions 
	 */
	public ResponseWrapper getAccountsTransactions(TestData data) {
		return getRestClient().processRequest("ACCOUNTS.TRANSACTIONS", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /customers/{customerNumber}/accounts 
	 */
	public ResponseWrapper getCustomersAccounts(TestData data) {
		return getRestClient().processRequest("CUSTOMERS.ACCOUNTS", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /customers/{customerNumber}/payment-methods 
	 */
	public ResponseWrapper getCustomersPaymentMethods(TestData data) {
		return getRestClient().processRequest("CUSTOMERS.PAYMENT-METHODS", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /customers/{customerNumber}/payment-methods 
	 */
	public ResponseWrapper postCustomersPaymentMethods(TestData data) {
		return getRestClient().processRequest("CUSTOMERS.PAYMENT-METHODS", RestMethod.POST, data);
	}

	/**
	 * <b>Target:</b> /customers/{customerNumber}/payment-methods/{paymentMethodId} 
	 */
	public ResponseWrapper deleteCustomersPaymentMethodsItem(TestData data) {
		return getRestClient().processRequest("CUSTOMERS.PAYMENT-METHODS.ITEM", RestMethod.DELETE, data);
	}

	/**
	 * <b>Target:</b> /customers/{customerNumber}/payment-methods/{paymentMethodId} 
	 */
	public ResponseWrapper getCustomersPaymentMethodsItem(TestData data) {
		return getRestClient().processRequest("CUSTOMERS.PAYMENT-METHODS.ITEM", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /customers/{customerNumber}/payment-methods/{paymentMethodId} 
	 */
	public ResponseWrapper putCustomersPaymentMethodsItem(TestData data) {
		return getRestClient().processRequest("CUSTOMERS.PAYMENT-METHODS.ITEM", RestMethod.PUT, data);
	}

	/**
	 * <b>Target:</b> /policies/available-payment-plans 
	 */
	public ResponseWrapper getPoliciesAvailablePaymentPlans(TestData data) {
		return getRestClient().processRequest("POLICIES.AVAILABLE-PAYMENT-PLANS", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /policies/{policyNumber}/issue 
	 */
	public ResponseWrapper postPoliciesIssue(TestData data) {
		return getRestClient().processRequest("POLICIES.ISSUE", RestMethod.POST, data);
	}

	/**
	 * <b>Target:</b> SwaggerJson 
	 */
	public ResponseWrapper getSwaggerJson(TestData data) {
		return getRestClient().processRequest("SWAGGERJSON", RestMethod.GET, data);
	}

	@Override public RestServiceUtil getRestClient() {
		return restClient.get();
	}
}
