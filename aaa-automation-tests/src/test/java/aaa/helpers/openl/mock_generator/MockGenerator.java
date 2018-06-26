package aaa.helpers.openl.mock_generator;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import aaa.helpers.mock.ApplicationMocksManager;
import aaa.helpers.mock.MockType;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.helpers.mock.model.membership.MembershipRequest;
import aaa.helpers.mock.model.membership.MembershipResponse;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;
import aaa.utils.excel.bind.BindHelper;

public class MockGenerator {
	private static List<String> generatedMembershipIDs = new ArrayList<>();
	private static List<String> generatedMembershipNumbers = new ArrayList<>();

	public static RetrieveMembershipSummaryMock getRetrieveMembershipSummaryMock(LocalDate policyEffectiveDate, Integer memberPersistency) {
		return getRetrieveMembershipSummaryMock(policyEffectiveDate, memberPersistency, RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE);
	}

	public static RetrieveMembershipSummaryMock getRetrieveMembershipSummaryMock(LocalDate policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember) {
		String id = generateMembershipId();
		String membershipNumber = generateMembershipNumber();

		RetrieveMembershipSummaryMock membershipMock = new RetrieveMembershipSummaryMock();
		MembershipRequest mRequest = new MembershipRequest();
		mRequest.setId(id);
		mRequest.setMembershipNumber(membershipNumber);

		int ersCount = avgAnnualERSperMember.equals(RetrieveMembershipSummaryMock.AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE) || avgAnnualERSperMember.equals(0.0) ? 1 : avgAnnualERSperMember.intValue();
		LocalDate serviceDate = avgAnnualERSperMember.equals(0.0) ? policyEffectiveDate.minusYears(4) : policyEffectiveDate.minusYears(1);
		LocalDate memberStartDate = policyEffectiveDate.minusYears(memberPersistency);

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

		membershipMock.setMembershipRequests(Collections.singletonList(mRequest));
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

	private static synchronized String generateMembershipId() {
		String idPrefix = "OPENL_";
		RetrieveMembershipSummaryMock retrieveMembershipSummaryMock = ApplicationMocksManager.getMock(MockType.RETRIEVE_MEMBERSHIP_SUMMARY);
		List<String> existingIDs = retrieveMembershipSummaryMock.getMembershipRequests().stream().map(MembershipRequest::getId).collect(Collectors.toList());

		int idNumber = 1;
		if (!generatedMembershipIDs.isEmpty()) {
			existingIDs.addAll(generatedMembershipIDs);
			idNumber = Integer.valueOf(generatedMembershipIDs.get(generatedMembershipIDs.size() - 1).replaceAll(idPrefix, "")) + 1;
		}

		String generatedId;
		do {
			generatedId = idPrefix + idNumber;
			idNumber++;
		}
		while (existingIDs.contains(generatedId));
		generatedMembershipIDs.add(generatedId);
		return generatedId;
	}

	private static synchronized String generateMembershipNumber() {
		RetrieveMembershipSummaryMock retrieveMembershipSummaryMock = ApplicationMocksManager.getMock(MockType.RETRIEVE_MEMBERSHIP_SUMMARY);
		List<String> existingMembershipNumbers = retrieveMembershipSummaryMock.getMembershipRequests().stream().map(MembershipRequest::getMembershipNumber).collect(Collectors.toList());

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

	private static MembershipResponse buildMembershipResponse(String id, String membershipNumber, LocalDate memberStartDate, LocalDate serviceDate, boolean isPrimaryType) {
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
