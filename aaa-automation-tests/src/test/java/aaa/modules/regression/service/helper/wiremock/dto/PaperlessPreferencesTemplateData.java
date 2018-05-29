package aaa.modules.regression.service.helper.wiremock.dto;

public class PaperlessPreferencesTemplateData implements WireMockTemplateData {

	public String policyNumber;
	public PaperlessPreferencesActions billNotificationAction;
	public PaperlessPreferencesActions paymentReminderAction;
	public PaperlessPreferencesActions paymentConfirmationAction;
	public PaperlessPreferencesActions policyDocumentsAction;

	public static PaperlessPreferencesTemplateData create(String policyNumber,
														  PaperlessPreferencesActions billNotificationAction,
														  PaperlessPreferencesActions policyDocumentsAction) {
		final PaperlessPreferencesTemplateData data = new PaperlessPreferencesTemplateData();
		data.policyNumber = policyNumber;
		data.billNotificationAction = billNotificationAction;
		data.policyDocumentsAction = policyDocumentsAction;
		data.paymentReminderAction = PaperlessPreferencesActions.PAPERLESS_OPT_IN;
		data.paymentConfirmationAction = PaperlessPreferencesActions.PAPERLESS_OPT_IN;
		return data;
	}

	public enum PaperlessPreferencesActions {
		PAPERLESS_OPT_IN_PENDING,
		PAPERLESS_OPT_IN,
		PAPERLESS_OPT_OUT
	}
}