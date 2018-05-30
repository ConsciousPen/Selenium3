package aaa.modules.regression.service.helper.wiremock.dto;

public class PaperlessPreferencesTemplateData implements WireMockTemplateData {

	public String policyNumber;
	public PaperlessPreferencesAction billNotificationAction;
	public PaperlessPreferencesAction paymentReminderAction;
	public PaperlessPreferencesAction paymentConfirmationAction;
	public PaperlessPreferencesAction policyDocumentsAction;

	public static PaperlessPreferencesTemplateData create(String policyNumber,
														  PaperlessPreferencesAction billNotificationAction,
														  PaperlessPreferencesAction policyDocumentsAction) {
		final PaperlessPreferencesTemplateData data = new PaperlessPreferencesTemplateData();
		data.policyNumber = policyNumber;
		data.billNotificationAction = billNotificationAction;
		data.policyDocumentsAction = policyDocumentsAction;
		data.paymentReminderAction = PaperlessPreferencesAction.PAPERLESS_OPT_IN;
		data.paymentConfirmationAction = PaperlessPreferencesAction.PAPERLESS_OPT_IN;
		return data;
	}
}