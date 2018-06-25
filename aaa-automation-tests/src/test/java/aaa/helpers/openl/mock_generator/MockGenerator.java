package aaa.helpers.openl.mock_generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import aaa.helpers.mock.ApplicationMocksManager;
import aaa.helpers.mock.MockType;
import aaa.helpers.mock.model.membership.MembershipRequest;
import aaa.helpers.mock.model.membership.MembershipResponse;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;

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
		List<MembershipResponse> membershipResponses = new ArrayList<>(ersCount);
		for (int i = 0; i < ersCount; i++) {
			LocalDate serviceDate = policyEffectiveDate.minusYears(1);
			LocalDate memberStartDate = policyEffectiveDate;
			boolean isPrimaryType = false;
			if (i == ersCount - 1) {
				memberStartDate = policyEffectiveDate.minusYears(1);
				isPrimaryType = true;
			}
			membershipResponses.add(buildMembershipResponse(id, membershipNumber, memberStartDate, serviceDate, policyEffectiveDate, memberPersistency, isPrimaryType));
		}

		membershipMock.setMembershipRequests(Collections.singletonList(mRequest));
		membershipMock.setMembershipResponses(membershipResponses);
		return membershipMock;
	}

	private static synchronized String generateMembershipId() {
		RetrieveMembershipSummaryMock retrieveMembershipSummaryMock = ApplicationMocksManager.getMock(MockType.RETRIEVE_MEMBERSHIP_SUMMARY);
		List<String> existingIDs = retrieveMembershipSummaryMock.getMembershipRequests().stream().map(MembershipRequest::getId).collect(Collectors.toList());

		int idNumber = 1;
		if (!generatedMembershipIDs.isEmpty()) {
			existingIDs.addAll(generatedMembershipIDs);
			idNumber = Integer.valueOf(generatedMembershipIDs.get(generatedMembershipIDs.size() - 1).replaceAll("OPENL_", "")) + 1;
		}

		String generatedId;
		while (existingIDs.contains(generatedId = String.format("OPENL_%d", idNumber))) {
			idNumber++;
		}
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
		while (existingMembershipNumbers.contains(generatedMembershipId = RandomStringUtils.randomNumeric(16))) {
		}
		generatedMembershipNumbers.add(generatedMembershipId);
		return generatedMembershipId;
	}

	private static MembershipResponse buildMembershipResponse(String id, String membershipNumber, LocalDate memberStartDate, LocalDate serviceDate, LocalDate effectiveDate,
			Integer memberPersistency, boolean isPrimaryType) {

		LocalDate memberSinceDate = effectiveDate.minusYears(memberPersistency);
		MembershipResponse mResponse = new MembershipResponse();
		mResponse.setId(id);
		mResponse.setMembershipStatusAcl("Active");
		mResponse.setMembershipEndDate(memberSinceDate.plusYears(15));
		mResponse.setMembershipEffectiveDate(memberSinceDate);
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
		mResponse.setBirthDate(memberSinceDate.minusYears(30));
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
