package aaa.helpers.rest.wiremock.dto;

public class PaperlessPreferencesErrorTemplateData implements WireMockTemplateData {

	public String policyNumber;

	public static PaperlessPreferencesErrorTemplateData create(String policyNumber) {
		final PaperlessPreferencesErrorTemplateData data = new PaperlessPreferencesErrorTemplateData();
		data.policyNumber = policyNumber;
		return data;
	}
}