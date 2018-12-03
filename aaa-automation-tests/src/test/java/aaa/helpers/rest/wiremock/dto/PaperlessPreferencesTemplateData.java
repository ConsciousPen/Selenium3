package aaa.helpers.rest.wiremock.dto;

public class PaperlessPreferencesTemplateData implements WireMockTemplateData {

	public static final String OPT_IN_PENDING = "OPT_IN_PENDING";
	public static final String OPT_IN = "OPT_IN";
	public static final String OPT_OUT = "OPT_OUT";
	public static final String AGENT_INTERACTION = "AGENT_INTERACTION";

	public String policyNumber;
	public String billNotificationAction;
	public String paymentReminderAction;
	public String paymentConfirmationAction;
	public String policyDocumentsAction;

	public String billNotificationReason;
	public String paymentReminderReason;
	public String paymentConfirmationReason;
	public String policyDocumentsReason;

	public static PaperlessPreferencesTemplateData create(String policyNumber, String paperlessAction) {
		final PaperlessPreferencesTemplateData data = new PaperlessPreferencesTemplateData();
		data.policyNumber = policyNumber;
		data.billNotificationAction = paperlessAction;
		data.policyDocumentsAction = paperlessAction;
		data.paymentReminderAction = paperlessAction;
		data.paymentConfirmationAction = paperlessAction;
		data.billNotificationReason = AGENT_INTERACTION;
		data.paymentReminderReason = AGENT_INTERACTION;
		data.paymentConfirmationReason = AGENT_INTERACTION;
		data.policyDocumentsReason = AGENT_INTERACTION;
		return data;
	}

	public static PaperlessPreferencesTemplateData createPolicyBillingActions(String policyNumber, String policyAction, String billingAction) {
		final PaperlessPreferencesTemplateData data = new PaperlessPreferencesTemplateData();
		data.policyNumber = policyNumber;
		data.billNotificationAction = billingAction;
		data.policyDocumentsAction = policyAction;
		data.paymentReminderAction = OPT_IN;
		data.paymentConfirmationAction = OPT_IN;
		data.billNotificationReason = AGENT_INTERACTION;
		data.paymentReminderReason = AGENT_INTERACTION;
		data.paymentConfirmationReason = AGENT_INTERACTION;
		data.policyDocumentsReason = AGENT_INTERACTION;
		return data;
	}
}