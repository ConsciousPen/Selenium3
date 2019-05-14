package aaa.helpers.openl.mock_generator;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import aaa.common.enums.Constants;
import aaa.helpers.mock.ApplicationMocksManager;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.mock.model.address.AddressReference;
import aaa.helpers.mock.model.address.AddressReferenceMock;
import aaa.helpers.mock.model.membership.MembershipRequest;
import aaa.helpers.mock.model.membership.MembershipResponse;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;
import aaa.helpers.mock.model.property_classification.*;
import aaa.helpers.mock.model.property_risk_reports.RetrievePropertyRiskReportsMock;
import aaa.helpers.mock.model.property_risk_reports.RiskReportsRequest;
import aaa.helpers.mock.model.property_risk_reports.RiskReportsResponse;
import aaa.utils.excel.bind.ReflectionHelper;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssertions;

public class MockGenerator {
	private static final Integer RISKREPORTS_ELEVATION = 2700;
	private static final String RISKREPORTS_DISTANCE_TO_SHORE_RANGE = "More Than 52800ft From the Coast";
	private static final String GENERATED_ID_PREFIX = "OPENL_";
	protected static final String STREET_ADDRESS_LINE = "6586 PORCUPINE WAY";
	protected static final Integer ADJ_FUEL_RATING = 0;

	private static MocksCollection generatedMocks = new MocksCollection();

	public static void flushGeneratedMocks() {
		generatedMocks.clear();
	}

	public static <M extends UpdatableMock> M getEmptyMock(Class<M> mockDataClass) {
		M mockInstance = ReflectionHelper.getInstance(mockDataClass);
		for (Field tableField : ReflectionHelper.getAllAccessibleTableFieldsFromThisAndSuperClasses(mockDataClass)) {
			ReflectionHelper.setFieldValue(tableField, mockInstance, new ArrayList<>());
		}
		return mockInstance;
	}

	public boolean isPropertyClassificationMockPresent() {
		List<String> validFirelineRequestIDs = getMock(RetrievePropertyClassificationMock.class).getFirelineRequests().stream()
				.filter(r -> StringUtils.isBlank(r.getCityName())
						&& StringUtils.isBlank(r.getZipCode())
						&& StringUtils.isBlank(r.getStreetAddressLine())
						&& StringUtils.isBlank(r.getState()))
				.map(FirelineRequest::getId).collect(Collectors.toList());

		return getMock(RetrievePropertyClassificationMock.class).getPpcResponses().stream()
				.anyMatch(r -> validFirelineRequestIDs.contains(r.getId())
						&& NumberUtils.isCreatable(r.getPpcValue())
						&& StringUtils.isNotBlank(r.getFireDistrict())
						&& "T".equals(r.getFireSubscriberDistrCode()));
	}

	public boolean isPropertyRiskReportsMockPresent() {
		if (generatedMocks.has(RetrievePropertyRiskReportsMock.class)) {
			return true;
		}

		List<String> validRiskReportsRequestIDs = getMock(RetrievePropertyRiskReportsMock.class).getRiskReportsRequests().stream()
				.filter(r -> StringUtils.isBlank(r.getState())
						&& StringUtils.isBlank(r.getCityName())
						&& StringUtils.isBlank(r.getZipCode())
						&& StringUtils.isBlank(r.getStreetAddressLine())
						&& StringUtils.isBlank(r.getStreetAddressLine2()))
				.map(RiskReportsRequest::getId).collect(Collectors.toList());

		return getMock(RetrievePropertyRiskReportsMock.class).getRiskReportsResponses().stream()
				.anyMatch(r -> validRiskReportsRequestIDs.contains(r.getId())
						&& RISKREPORTS_ELEVATION.equals(r.getElevation())
						&& RISKREPORTS_DISTANCE_TO_SHORE_RANGE.equals(r.getDistancetoshorerange()));
	}

	public boolean isMembershipSummaryMockPresent(LocalDate policyEffectiveDate, Integer memberPersistency) {
		return isMembershipSummaryMockPresent(policyEffectiveDate, memberPersistency, RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE);
	}

	public boolean isMembershipSummaryMockPresent(LocalDate policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember) {
		LocalDate serviceDate = avgAnnualERSperMember.equals(0.0) ? policyEffectiveDate.minusYears(4) : policyEffectiveDate.minusYears(1);
		return getMock(RetrieveMembershipSummaryMock.class).getMembershipNumberForAvgAnnualERSperMember(policyEffectiveDate, memberPersistency, avgAnnualERSperMember, serviceDate) != null;
	}

	public boolean isAddressReferenceMockPresent(String postalCode, String state) {
		return getMock(AddressReferenceMock.class).hasAddress(postalCode, state);
	}

	public RetrievePropertyClassificationMock getRetrievePropertyClassificationMock() {
		RetrievePropertyClassificationMock propertyClassificationMock = buildRetrievePropertyClassificationMock();
		generatedMocks.add(propertyClassificationMock);
		return propertyClassificationMock;
	}

	public RetrievePropertyRiskReportsMock getRetrievePropertyRiskReportsMock() {
		List<String> existingMockIDs = getMock(RetrievePropertyRiskReportsMock.class).getRiskReportsRequests().stream().map(RiskReportsRequest::getId).collect(Collectors.toList());
		String id = generateMockId(existingMockIDs);

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

		generatedMocks.add(propertyRiskReportsMock);
		return propertyRiskReportsMock;
	}

	public RetrieveMembershipSummaryMock getRetrieveMembershipSummaryMock(LocalDate policyEffectiveDate, Integer memberPersistency) {
		return getRetrieveMembershipSummaryMock(policyEffectiveDate, memberPersistency, RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE, false);
	}

	public RetrieveMembershipSummaryMock getRetrieveMembershipSummaryMock(LocalDate policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember, Boolean isRenewal) {
		RetrieveMembershipSummaryMock existingMock = getMock(RetrieveMembershipSummaryMock.class);
		String id = generateMockId(existingMock.getMembershipRequests().stream().map(MembershipRequest::getId).collect(Collectors.toList()));
		String membershipNumber = generateMembershipNumber(existingMock.getMembershipRequests().stream().map(MembershipRequest::getMembershipNumber).collect(Collectors.toList()));

		RetrieveMembershipSummaryMock membershipMock = new RetrieveMembershipSummaryMock();
		MembershipRequest mRequest = new MembershipRequest();
		mRequest.setId(id);
		mRequest.setMembershipNumber(membershipNumber);

		int ersCount = avgAnnualERSperMember.equals(RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE) || avgAnnualERSperMember.equals(0.0) ? 1 : avgAnnualERSperMember.intValue();
		if (!avgAnnualERSperMember.equals(RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE) && avgAnnualERSperMember > 0 && memberPersistency > 0) {
			throw new IstfException(String.format("Unable to generate \"%s\" mock with avgAnnualERSperMember > 0 and memberPersistency > 0", existingMock.getFileName()));
		}
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
			membershipResponses.add(buildMembershipResponse(id, membershipNumber, memberStartDate, serviceDate, isPrimaryType, isRenewal));
		}

		membershipRequests.add(mRequest);
		membershipMock.setMembershipRequests(membershipRequests);
		membershipMock.setMembershipResponses(membershipResponses);

		generatedMocks.add(membershipMock);
		return membershipMock;
	}

	public AddressReferenceMock getAddressReferenceMock(String postalCode, String state) {
		String getZipQuery = String.format("select * from LOOKUPVALUE where %s = ? and LOOKUPLIST_ID in (select ID from LOOKUPLIST where LOOKUPNAME = 'AAACountyTownship') and RISKSTATECD = ?",
				Constants.States.CT.equals(state) ? "CODE" : "POSTALCODE");

		CustomAssertions.assertThat(DBService.get().getValue(getZipQuery, postalCode, state)).as("Zip code %s is not valid for %s state, mock generation is useless", postalCode, state).isPresent();

		AddressReferenceMock addressReferenceMock = new AddressReferenceMock();
		List<AddressReference> addressReferences = new ArrayList<>();
		AddressReference addressReference = new AddressReference();
		addressReference.setPostalCode(postalCode);
		addressReference.setCity("SomeCity " + RandomStringUtils.randomAlphabetic(5));
		addressReference.setState(state);
		addressReference.setCounty("SomeCounty " + RandomStringUtils.randomAlphabetic(5));
		addressReference.setCountry("US");
		addressReferences.add(addressReference);
		addressReferenceMock.setAddressReferences(addressReferences);

		generatedMocks.add(addressReferenceMock);
		return addressReferenceMock;
	}

	protected static synchronized String generateMockId(List<String> existingMockIDs) {
		int idLastIndex = existingMockIDs.stream().filter(id -> id != null && id.startsWith(GENERATED_ID_PREFIX))
				.map(id -> Integer.valueOf(id.replaceAll("\\D", "").matches("^[0-9]+$") ? id.replaceAll("\\D", "") : "0")).max(Integer::compare).orElse(0);
		return GENERATED_ID_PREFIX + (idLastIndex + 1);
	}

	protected static synchronized String generateMembershipNumber(List<String> existingMembershipNumbers) {
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

		return generatedMembershipId;
	}

	@SuppressWarnings("unchecked")
	protected static synchronized <M extends UpdatableMock> M getMock(Class<M> mockModelClass) {
		UpdatableMock mock = ApplicationMocksManager.getMock(mockModelClass).clone();
		if (generatedMocks.has(mockModelClass)) {
			mock.add(generatedMocks.get(mockModelClass));
		}
		return (M) mock;
	}

	protected RetrievePropertyClassificationMock buildRetrievePropertyClassificationMock() {
		List<String> existingMockIDs = getMock(RetrievePropertyClassificationMock.class).getFirelineRequests().stream().map(FirelineRequest::getId).collect(Collectors.toList());
		String id = generateMockId(existingMockIDs);
		String responseCode = "0";
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
		firelineResponse.setAdjFuelRating(ADJ_FUEL_RATING); // this is important
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

	protected MembershipResponse buildMembershipResponse(String id, String membershipNumber, LocalDate memberStartDate, LocalDate serviceDate, boolean isPrimaryType, boolean isRenewal) {
		MembershipResponse mResponse = new MembershipResponse();
		mResponse.setId(id);
		mResponse.setMembershipStatusAcl("Active");
		mResponse.setMembershipEndDate(memberStartDate.plusYears(15));
		mResponse.setMembershipEffectiveDate(memberStartDate);
		mResponse.setMembershipNumber(membershipNumber);
		if (!isRenewal){
			mResponse.setErsUsageCountPerActiveMember(3.0);
		} else {
			mResponse.setErsUsageCountPerActiveMember(7.0);
		}
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
