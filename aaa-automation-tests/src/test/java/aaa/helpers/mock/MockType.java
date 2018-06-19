package aaa.helpers.mock;

import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;
import aaa.helpers.mock.model.vehicle.VehicleUBIDetailsMock;

public enum MockType {
	ACCOUNT_VALIDATION("AccountValidationMockData.xls", null),
	ADDRESS_REFERENCE("AddressReferenceMockData.xls", null),
	ADDRESS_VALIDATION("AddressValidationMockData.xls", null),
	ADDRESS_VALIDATION_V2("AddressValidationV2MockData.xls", null),
	AVS("AvsMockData.xls", null),
	BCT_EARTHQUAKE_PLCY("BCTEarthquakePlcyMockData.xls", null),
	CHOICE_POINT_CLUE("ChoicePointClueMockData.xls", null),
	CHOICE_POINT_CLUE_PROPERTY("ChoicePointCluePropertyMockData.xls", null),
	CHOICE_POINT_MESSAGE("ChoicePointMessageMockData.xls", null),
	CHOICE_POINT_MVR("ChoicePointMvrMockData.xls", null),
	CHOICE_POINT_NCF("ChoicePointNcfMockData.xls", null),
	CHOICE_POINT_PREFILL("ChoicePointPrefillMockData.xls", null),
	CLAIMS_BATCH("ClaimsBatchMockData.xls", null),
	CLAIMS("ClaimsMockData.xls", null),
	CUSTOMER_MASTER("CustomerMasterMockData.xls", null),
	DISBURSEMENT_ENGINE("DisbursementEngineMockData.xls", null),
	EARS("EarsMockData.xls", null),
	ISSUE_DIRECT_PAYMENT("IssueDirectPaymentMockData.xls", null),
	ISSUE_DIRECT_PAYMENT_V2("IssueDirectPaymentV2MockData.xls", null),
	JOB_EARS_RECEIVE("JobEarsReceiveMockData.xls", null),
	JOB_GET_HISTORY_BY_MD5HASH_SERVICE("JobGetHistoryByMD5HashServiceMockData.xls", null),
	JOB_MAINTAIN_DATA_EVENT_SERVICE("JobMaintainDataEventServiceMockData.xls", null),
	JOB_MAINTAIN_STATUS_EVENT_SERVICE("JobMaintainStatusEventServiceMockData.xls", null),
	JOB_RETRIEVE_CORRELATION_SERVICE("JobRetrieveCorrelationServiceMockData.xls", null),
	JOB_SUMMARY_SERVICE("JobSummaryServiceMockData.xls", null),
	LAST_PAYMENT("LastPaymentMockData.xls", null),
	POLICY_PREFERENCE_API("PolicyPreferenceApiMockData.xls", null),
	RECORD_FINANCIAL_ACCOUNT("RecordFinancialAccountMockData.xls", null),
	RETRIEVE_AGENT_SUMMARY("RetrieveAgentSummaryMockData.xls", null),
	RETRIEVE_AUTO_POLICY_DETAILS("RetrieveAutoPolicyDetailsMockData.xls", null),
	RETRIEVE_CHANNEL_ID("RetrieveChannelIdMockData.xls", null),
	RETRIEVE_ELASTIC_SEARCH("RetrieveElasticSearchMockData.xls", null),
	RETRIEVE_MEMBERSHIP_SUMMARY("RetrieveMembershipSummaryMockData.xls", RetrieveMembershipSummaryMock.class),
	RETRIEVE_PAYMENT_ENROLLMENT_DETAILS("RetrievePaymentEnrollmentDetailsMockData.xls", null),
	RETRIEVE_PAYMENT_TRANSACTION("RetrievePaymentTransactionData.xls", null),
	RETRIEVE_PREMIUM_TAX_RATE("RetrievePremiumTaxRateMockData.xls", null),
	RETRIEVE_PROPERTY_CLASSIFICATION("RetrievePropertyClassificationMockData.xls", null),
	RETRIEVE_PROPERTY_POLICY_DETAILS("RetrievePropertyPolicyDetailsMockData.xls", null),
	RETRIEVE_PROPERTY_RISK_REPORTS("RetrievePropertyRiskReportsMockData.xls", null),
	RETRIEVE_PROPERTY_VALUATION("RetrievePropertyValuationMockData.xls", null),
	RETRIEVE_SPECIALITY_POLICY_DETAILS("RetrieveSpecialityPolicyDetailsMockData.xls", null),
	TELEMATIC_VIN_ELIGIBILITY("TelematicVINEligibilityMockData.xls", null),
	VEHICLE_UBI_DETAILS("VehicleUBIDetailsMockData.xls", VehicleUBIDetailsMock.class),
	VEHICLE_UBI_SCORE_SUMMARY("VehicleUBIScoreSummaryMockData.xls", null);

	private final String fileName;
	private final Class<? extends UpdatableMock> mockModel;

	MockType(String fileName, Class<? extends UpdatableMock> mockModel) {
		this.fileName = fileName;
		this.mockModel = mockModel;
	}

	public String getFileName() {
		return fileName;
	}

	@SuppressWarnings("unchecked")
	public <M extends UpdatableMock> Class<M> getMockModel() {
		return (Class<M>) mockModel;
	}
}
