package aaa.helpers.openl.mock_generator;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import aaa.helpers.mock.ApplicationMocksManager;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.mock.model.membership.MembershipRequest;
import aaa.helpers.mock.model.membership.MembershipResponse;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;
import aaa.helpers.mock.model.property_classification.*;
import aaa.helpers.mock.model.property_risk_reports.RetrievePropertyRiskReportsMock;
import aaa.helpers.mock.model.property_risk_reports.RiskReportsRequest;
import aaa.helpers.mock.model.property_risk_reports.RiskReportsResponse;
import aaa.utils.excel.bind.BindHelper;

public class MockGenerator {
	private static Map<Class<? extends UpdatableMock>, List<String>> generatedMockIDs = new HashMap<>();
	private static List<String> generatedMembershipNumbers = new ArrayList<>();
	private static final Integer RISKREPORTS_ELEVATION = 2700;
	private static final String RISKREPORTS_DISTANCE_TO_SHORE_RANGE = "More Than 52800ft From the Coast";

	private static MocksCollection generatedMocks = new MocksCollection();

	public boolean isPropertyClassificationMockPresent() {
		/*boolean hasValidFirelaneRequestResponse = ApplicationMocksManager.getRetrievePropertyClassificationMock().getFirelineRequests().stream().anyMatch(r -> "FIRELINE".equals(r.getProtectionCodeType())
				&& org.apache.commons.lang.StringUtils.isBlank(r.getCityName())
				&& org.apache.commons.lang.StringUtils.isBlank(r.getZipCode())
				&& org.apache.commons.lang.StringUtils.isBlank(r.getStreetAddressLine())
				&& org.apache.commons.lang.StringUtils.isBlank(r.getState()));
		boolean hasValidPPCRequestResponse = ApplicationMocksManager.getRetrievePropertyClassificationMock().getPpcRequests().stream().anyMatch(r -> "FEPPC".equals(r.getProtectionCodeType())
				&& org.apache.commons.lang.StringUtils.isBlank(r.getCityName())
				&& org.apache.commons.lang.StringUtils.isBlank(r.getZipCode())
				&& org.apache.commons.lang.StringUtils.isBlank(r.getStreetAddressLine())
				&& org.apache.commons.lang.StringUtils.isBlank(r.getState()));
		return hasValidFirelaneRequestResponse && hasValidPPCRequestResponse;*/

		//TODO-dchubkov: to be implemented...
		return false;
	}

	public boolean isPropertyRiskReportsMockPresent() {
		//return isPropertyRiskReportsMockPresent(ApplicationMocksManager.getRetrievePropertyRiskReportsMock()) || isPropertyRiskReportsMockPresent(getGeneratedMock(RetrievePropertyRiskReportsMock.class));
		return isPropertyRiskReportsMockPresent(getMock(RetrievePropertyRiskReportsMock.class));
		/*List<String> validRiskReportsRequestIDs = ApplicationMocksManager.getRetrievePropertyRiskReportsMock().getRiskReportsRequests().stream()
				.filter(r -> StringUtils.isBlank(r.getState())
						&& StringUtils.isBlank(r.getCityName())
						&& StringUtils.isBlank(r.getZipCode())
						&& StringUtils.isBlank(r.getStreetAddressLine())
						&& StringUtils.isBlank(r.getStreetAddressLine2()))
				.map(RiskReportsRequest::getId).collect(Collectors.toList());

		return ApplicationMocksManager.getRetrievePropertyRiskReportsMock().getRiskReportsResponses().stream()
				.anyMatch(r -> validRiskReportsRequestIDs.contains(r.getId())
						&& RISKREPORTS_ELEVATION.equals(r.getElevation())
						&& RISKREPORTS_DISTANCE_TO_SHORE_RANGE.equals(r.getDistancetoshorerange()));*/
	}

	protected boolean isPropertyRiskReportsMockPresent(RetrievePropertyRiskReportsMock mock) {
		List<String> validRiskReportsRequestIDs = ApplicationMocksManager.getRetrievePropertyRiskReportsMock().getRiskReportsRequests().stream()
				.filter(r -> StringUtils.isBlank(r.getState())
						&& StringUtils.isBlank(r.getCityName())
						&& StringUtils.isBlank(r.getZipCode())
						&& StringUtils.isBlank(r.getStreetAddressLine())
						&& StringUtils.isBlank(r.getStreetAddressLine2()))
				.map(RiskReportsRequest::getId).collect(Collectors.toList());

		return ApplicationMocksManager.getRetrievePropertyRiskReportsMock().getRiskReportsResponses().stream()
				.anyMatch(r -> validRiskReportsRequestIDs.contains(r.getId())
						&& RISKREPORTS_ELEVATION.equals(r.getElevation())
						&& RISKREPORTS_DISTANCE_TO_SHORE_RANGE.equals(r.getDistancetoshorerange()));
	}

	public boolean isMembershipSummaryMockPresent(LocalDate policyEffectiveDate, Integer memberPersistency) {
		return isMembershipSummaryMockPresent(policyEffectiveDate, memberPersistency, RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE);
	}

	public boolean isMembershipSummaryMockPresent(LocalDate policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember) {
		return ApplicationMocksManager.getRetrieveMembershipSummaryMock()
				.getMembershipNumberForAvgAnnualERSperMember(policyEffectiveDate, memberPersistency, avgAnnualERSperMember) != null;
	}

	public RetrievePropertyClassificationMock getRetrievePropertyClassificationMock() {
		List<String> existingAppIDs = ApplicationMocksManager.getRetrievePropertyClassificationMock().getFirelineRequests().stream().map(FirelineRequest::getId).collect(Collectors.toList());
		String id = generateMockId(RetrievePropertyClassificationMock.class, existingAppIDs);
		int responseCode = 0;
		String responseDescription = "SUCCESS";
		String returnCode = "L000";
		String returnMessage = "success";
		String returnSource = "ADDRESS";

		RetrievePropertyClassificationMock propertyClassificationMock = new RetrievePropertyClassificationMock();
		FirelineRequest firelineRequest = new FirelineRequest();
		firelineRequest.setId(id);
		firelineRequest.setProtectionCodeType("FIRELINE");

		FirelineResponse firelineResponse = new FirelineResponse();
		firelineResponse.setId(id);
		firelineResponse.setResponseCode(responseCode);
		firelineResponse.setResponseDescription(responseDescription);
		firelineResponse.setReturnCode(returnCode);
		firelineResponse.setReturnMessage(returnMessage);
		firelineResponse.setReturnSource(returnSource);
		firelineResponse.setReportName("FIRELINE NETWORK");
		firelineResponse.setGlobeXSatMapURL("NA");
		firelineResponse.setFireLineRingMapURL("NA");
		firelineResponse.setFips("N/A");
		firelineResponse.setMatchType("E");
		firelineResponse.setAccessType("A0");
		firelineResponse.setAdjFuelRating(0); // this is important
		firelineResponse.setWfRiskAssessment("NEGLIGIBLE");
		firelineResponse.setGaugeMapURL("https://landscapea.iso.com/landscape/images/Gauge1.jpg");
		firelineResponse.setGeocodeSrc("T");
		firelineResponse.setFuelType("NF");
		firelineResponse.setFuelPercentage(0.0915);
		firelineResponse.setSlopeType("S1");
		firelineResponse.setSlopePercentage(0.0389);
		firelineResponse.setAdjRateScoreType1(0);
		firelineResponse.setAdjRateScorePercentage(0.0040);

		PPCRequest ppcRequest = new PPCRequest();
		ppcRequest.setId(id);
		ppcRequest.setProtectionCodeType("FEPPC");

		PPCResponse ppcResponse = new PPCResponse();
		ppcResponse.setId(id);
		ppcResponse.setResponseCode(responseCode);
		ppcResponse.setResponseDescription(responseDescription);
		ppcResponse.setReturnCode(returnCode);
		ppcResponse.setReturnMessage(returnMessage);
		ppcResponse.setReturnSource(returnSource);
		ppcResponse.setReportName("PPC FUTURE EFFECTIVE NETWORK");
		ppcResponse.setFeReturnSource("ADDRESS");
		ppcResponse.setAvailabilityIndicator(true);
		ppcResponse.setFePpcValue("4");
		ppcResponse.setFeEffectiveDate(LocalDate.of(2019, 2, 7));
		ppcResponse.setPpcValue("4");
		ppcResponse.setPpcPercentage(0.2452);
		ppcResponse.setPpcPercentage1(0.2163);
		ppcResponse.setPpcPercentage2(0.1185);
		ppcResponse.setPpcCountyFIPS("001");
		ppcResponse.setPpcCountyFIPS1("001");
		ppcResponse.setPpcCountyFIPS2("001");
		ppcResponse.setPpcCountyName("CITY");
		ppcResponse.setPpcCountyName1("CITY");
		ppcResponse.setPpcCountyName2("CITY");
		ppcResponse.setFireDistrict("CITY");
		ppcResponse.setFireDistrict1("CITY");
		ppcResponse.setFireDistrict2("CITY");
		ppcResponse.setFireSubscriberDistrCode("T");

		List<FirelineRequest> firelineRequests = new ArrayList<>();
		List<FirelineResponse> firelineResponses = new ArrayList<>();
		List<PPCRequest> ppcRequests = new ArrayList<>();
		List<PPCResponse> ppcResponses = new ArrayList<>();

		firelineRequests.add(firelineRequest);
		firelineResponses.add(firelineResponse);
		ppcRequests.add(ppcRequest);
		ppcResponses.add(ppcResponse);

		propertyClassificationMock.setFirelineRequests(firelineRequests);
		propertyClassificationMock.setFirelineResponses(firelineResponses);
		propertyClassificationMock.setPpcRequests(ppcRequests);
		propertyClassificationMock.setPpcResponses(ppcResponses);

		return propertyClassificationMock;
	}

	public RetrievePropertyRiskReportsMock getRetrievePropertyRiskReportsMock() {
		List<String> existingAppIDs = ApplicationMocksManager.getRetrievePropertyRiskReportsMock().getRiskReportsRequests().stream().map(RiskReportsRequest::getId).collect(Collectors.toList());
		String id = generateMockId(RetrievePropertyRiskReportsMock.class, existingAppIDs);

		RetrievePropertyRiskReportsMock propertyRiskReportsMock = new RetrievePropertyRiskReportsMock();
		RiskReportsRequest riskReportsRequest = new RiskReportsRequest();
		riskReportsRequest.setId(id);

		RiskReportsResponse riskReportsResponse = new RiskReportsResponse();
		riskReportsResponse.setId(id);
		riskReportsResponse.setElevation(RISKREPORTS_ELEVATION);
		riskReportsResponse.setDistancetoshorerange(RISKREPORTS_DISTANCE_TO_SHORE_RANGE);

		List<RiskReportsRequest> riskReportsRequests = new ArrayList<>();
		List<RiskReportsResponse> riskReportsResponses = new ArrayList<>();

		riskReportsRequests.add(riskReportsRequest);
		riskReportsResponses.add(riskReportsResponse);

		propertyRiskReportsMock.setRiskReportsRequests(riskReportsRequests);
		propertyRiskReportsMock.setRiskReportsResponses(riskReportsResponses);

		return propertyRiskReportsMock;
	}

	public RetrieveMembershipSummaryMock getRetrieveMembershipSummaryMock(LocalDate policyEffectiveDate, Integer memberPersistency) {
		return getRetrieveMembershipSummaryMock(policyEffectiveDate, memberPersistency, RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE);
	}

	public RetrieveMembershipSummaryMock getRetrieveMembershipSummaryMock(LocalDate policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember) {
		List<String> existingAppIDs = ApplicationMocksManager.getRetrieveMembershipSummaryMock().getMembershipRequests().stream().map(MembershipRequest::getId).collect(Collectors.toList());
		String id = generateMockId(RetrieveMembershipSummaryMock.class, existingAppIDs);
		String membershipNumber = generateMembershipNumber();

		RetrieveMembershipSummaryMock membershipMock = new RetrieveMembershipSummaryMock();
		MembershipRequest mRequest = new MembershipRequest();
		mRequest.setId(id);
		mRequest.setMembershipNumber(membershipNumber);

		int ersCount = avgAnnualERSperMember.equals(RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE) || avgAnnualERSperMember.equals(0.0) ? 1 : avgAnnualERSperMember.intValue();
		LocalDate serviceDate = avgAnnualERSperMember.equals(0.0) ? policyEffectiveDate.minusYears(4) : policyEffectiveDate.minusYears(1);
		LocalDate memberStartDate = policyEffectiveDate.minusYears(memberPersistency);

		List<MembershipRequest> membershipRequests = new ArrayList<>();
		List<MembershipResponse> membershipResponses = new ArrayList<>(ersCount);
		for (int i = 0; i < ersCount; i++) {
			boolean isPrimaryType = false;
			if (i == ersCount - 1) {
				if (ersCount > 1) {
					memberStartDate = policyEffectiveDate.minusYears(1);
				}
				isPrimaryType = true;
			}
			membershipResponses.add(buildMembershipResponse(id, membershipNumber, memberStartDate, serviceDate, isPrimaryType));
		}

		membershipRequests.add(mRequest);
		membershipMock.setMembershipRequests(membershipRequests);
		membershipMock.setMembershipResponses(membershipResponses);
		return membershipMock;
	}

	@SuppressWarnings("unchecked")
	public static <M extends UpdatableMock> M getEmptyMock(Class<M> mockDataClass) {
		M mockInstance = (M) BindHelper.getInstance(mockDataClass);
		for (Field tableField : BindHelper.getAllAccessibleFields(mockDataClass, true)) {
			BindHelper.setFieldValue(tableField, mockInstance, new ArrayList<>());
		}
		return mockInstance;
	}

	protected static synchronized String generateMockId(Class<? extends UpdatableMock> mockModelClass, List<String> existingAppIDs) {
		List<String> existingIDs = new ArrayList<>(existingAppIDs);
		String idPrefix = "OPENL_";

		int idIndex = 1;
		if (generatedMockIDs.containsKey(mockModelClass)) {
			List<String> generatedIDs = generatedMockIDs.get(mockModelClass);
			if (!generatedIDs.isEmpty()) {
				existingIDs.addAll(generatedIDs);
				idIndex = Integer.valueOf(generatedIDs.get(generatedIDs.size() - 1).replaceAll(idPrefix, "")) + 1;
			}
		} else {
			generatedMockIDs.put(mockModelClass, new ArrayList<>());
		}

		String generatedId;
		do {
			generatedId = idPrefix + idIndex;
			idIndex++;
		}
		while (existingIDs.contains(generatedId));
		generatedMockIDs.get(mockModelClass).add(generatedId);
		return generatedId;
	}

	protected static synchronized String generateMembershipNumber() {
		List<String> existingMembershipNumbers = ApplicationMocksManager.getRetrieveMembershipSummaryMock().getMembershipRequests()
				.stream().map(MembershipRequest::getMembershipNumber).collect(Collectors.toList());

		if (!generatedMembershipNumbers.isEmpty()) {
			existingMembershipNumbers.addAll(generatedMembershipNumbers);
		}

		String generatedMembershipId;
		do {
			//Generating Luhn number
			String randomNumber = RandomStringUtils.randomNumeric(15);
			int sum = 0;
			for (int i = 0; i < randomNumber.length(); i++) {
				int digit = Integer.parseInt(randomNumber.substring(i, i + 1));
				if (i % 2 == 0) {
					digit = digit * 2;
					if (digit > 9) {
						digit = digit % 10 + 1;
					}
				}
				sum += digit;
			}
			int checkDigit = sum % 10;
			int lastDigit = checkDigit == 0 ? 0 : 10 - checkDigit;
			generatedMembershipId = randomNumber + lastDigit;
		}
		while (existingMembershipNumbers.contains(generatedMembershipId));

		generatedMembershipNumbers.add(generatedMembershipId);
		return generatedMembershipId;
	}

	@SuppressWarnings("unchecked")
	protected static synchronized <M extends UpdatableMock> M getMock(Class<M> mockModelClass) {
		UpdatableMock appMock = ApplicationMocksManager.getMock(mockModelClass);
		if (generatedMocks.has(mockModelClass)) {
			appMock.add(generatedMocks.get(mockModelClass));
		}
		return (M) appMock;
	}

	/*@SuppressWarnings("unchecked")
	protected static synchronized <M extends UpdatableMock> M getGeneratedMock(Class<M> mockModelClass) {
		*//*if (!generatedMocks.has(mockModelClass)) {
			M mock = getMockDataObject(mockModelClass, fileName);
			appMocks.add(mock);
		}*//*
		return (M) generatedMocks.get(mockModelClass);
	}*/

	protected MembershipResponse buildMembershipResponse(String id, String membershipNumber, LocalDate memberStartDate, LocalDate serviceDate, boolean isPrimaryType) {
		MembershipResponse mResponse = new MembershipResponse();
		mResponse.setId(id);
		mResponse.setMembershipStatusAcl("Active");
		mResponse.setMembershipEndDate(memberStartDate.plusYears(15));
		mResponse.setMembershipEffectiveDate(memberStartDate);
		mResponse.setMembershipNumber(membershipNumber);
		mResponse.setErsUsageCountPerActiveMember(3.0);
		mResponse.setErsUsageAbuse(true);
		mResponse.setResponseMessageRuleDecision("Membership Details are retrieved from CDX");
		mResponse.setService("CDX");
		mResponse.setMemberStartDate(memberStartDate);
		mResponse.setStatus("Active");
		mResponse.setServiceDate(serviceDate);
		mResponse.setChargeble(true);
		mResponse.setMemberCoverageType("Classic");
		mResponse.setFirstName("JULIA");
		mResponse.setLastName("STERLING");
		mResponse.setPrefixTitle("Mrs");
		mResponse.setBirthDate(memberStartDate.minusYears(30));
		mResponse.setCity("Phoenix");
		mResponse.setRegion("AZ");
		mResponse.setPostalCode("85012");
		mResponse.setAddressLine1("some address123");

		if (isPrimaryType) {
			mResponse.setMemberType("Primary");
		}

		return mResponse;
	}
}
