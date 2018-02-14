package aaa.helpers.mock.model.membership;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import toolkit.exceptions.IstfException;

public class MembershipMockData {
	@ExcelTableElement(sheetName = "MEMBERSHIP_REQUEST")
	private List<MembershipRequest> membershipRequests;

	@ExcelTableElement(sheetName = "MEMBERSHIP_RESPONSE")
	private List<MembershipResponse> membershipResponses;

	public List<MembershipRequest> getMembershipRequests() {
		return Collections.unmodifiableList(membershipRequests);
	}

	public void setMembershipRequests(List<MembershipRequest> membershipRequests) {
		this.membershipRequests = new ArrayList<>(membershipRequests);
	}

	public List<MembershipResponse> getMembershipResponses() {
		return Collections.unmodifiableList(membershipResponses);
	}

	public void setMembershipResponses(List<MembershipResponse> membershipResponses) {
		this.membershipResponses = new ArrayList<>(membershipResponses);
	}

	public Set<String> getNonActiveAndNotPrimaryMembershipNumbers() {
		//TODO-dchubkov: current algorithm is incomplete, to be fixed
		Set<String> nonActiveOrNotPrimaryMembershipNumbers = new HashSet<>();
		for (String membershipNumber : getMembershipRequestNumbers()) {
			boolean isValidMembership = getMembershipResponses(membershipNumber).stream().noneMatch(m -> "Active".equals(m.getStatus()) && "Primary".equals(m.getMemberType()));
			if (isValidMembership) {
				nonActiveOrNotPrimaryMembershipNumbers.add(membershipNumber);
			}
		}
		return nonActiveOrNotPrimaryMembershipNumbers;
	}

	public List<String> getMembershipRequestNumbers() {
		return getMembershipRequests().stream().map(MembershipRequest::getMembershipNumber).collect(Collectors.toList());
	}

	public Pair<String, Boolean> getMembershipNumberForAvgAnnualERSperMember(LocalDateTime policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember) {
		//TODO-dchubkov: handle default value of avgAnnualERSperMember=99.9
		String membershipNumber;
		boolean isMemberSinceDateSet = true;

		Set<String> membershipNumbersSet = getActiveAndPrimaryMembershipNumbers(policyEffectiveDate.minusYears(memberPersistency));
		if (membershipNumbersSet.isEmpty()) {
			membershipNumbersSet = getNonActiveAndNotPrimaryMembershipNumbers();
			assertThat(membershipNumbersSet).as("No valid membership numbers were found").isNotEmpty();
			isMemberSinceDateSet = false;
		}

		membershipNumber = getMembershipNumberForAvgAnnualERSperMember(membershipNumbersSet, policyEffectiveDate, avgAnnualERSperMember);
		if (membershipNumber == null) {
			if (isMemberSinceDateSet) {
				membershipNumbersSet = getNonActiveAndNotPrimaryMembershipNumbers();
				membershipNumber = getMembershipNumberForAvgAnnualERSperMember(membershipNumbersSet, policyEffectiveDate, avgAnnualERSperMember);
				assertThat(membershipNumber).as("No valid membership numbers were found").isNotNull();
				isMemberSinceDateSet = false;
			} else {
				throw new IstfException("No valid membership numbers were found");
			}
		}

		return Pair.of(membershipNumber, isMemberSinceDateSet);
	}

	public Set<String> getActiveAndPrimaryMembershipNumbers(LocalDateTime memberSinceDate) {
		// Collecting all unique membership IDs with memberStartDate=memberSinceDate AND Status=Active AND memberType=Primary
		Set<String> membershipIds = getMembershipResponses().stream()
				.filter(mr -> Objects.equals(mr.getMemberStartDate(), memberSinceDate) && "Active".equals(mr.getStatus()) && "Primary".equals(mr.getMemberType()))
				.map(MembershipResponse::getId).collect(Collectors.toSet());

		// Adding membership IDs with empty memberStartDate AND Status=Active AND memberType=Primary AND today - memberStartDateMonthsOffset = memberStartDate
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		membershipIds.addAll(getMembershipResponses().stream()
				.filter(m -> m.getMemberStartDate() == null && "Active".equals(m.getStatus()) && "Primary".equals(m.getMemberType())
						&& m.getMemberStartDateMonthsOffset() != null && today.plusMonths(m.getMemberStartDateMonthsOffset()).equals(memberSinceDate))
				.map(MembershipResponse::getMembershipNumber).collect(Collectors.toSet()));

		// If memberStartDate == today then adding membership IDs with empty memberStartDate AND empty memberStartDateMonthsOffset AND Status=Active AND memberType=Primary AND
		if (today.equals(memberSinceDate)) {
			membershipIds.addAll(getMembershipResponses().stream()
					.filter(m -> m.getMemberStartDate() == null && "Active".equals(m.getStatus()) && "Primary".equals(m.getMemberType()) && m.getMemberStartDateMonthsOffset() == null)
					.map(MembershipResponse::getMembershipNumber).collect(Collectors.toSet())
			);
		}

		return getMembershipRequests().stream().filter(m -> membershipIds.contains(m.getId())).map(MembershipRequest::getId).collect(Collectors.toSet());
	}

	public List<MembershipResponse> getMembershipResponses(String membershipRequestNumber) {
		String membershipRequestId = getMembershipRequests().stream().filter(m -> m.getMembershipNumber().equals(membershipRequestNumber)).map(MembershipRequest::getId).findFirst().get();
		return getMembershipResponses().stream().filter(m -> m.getId().equals(membershipRequestId)).collect(Collectors.toList());
	}

	public String getMembershipRequestNumber(String id) {
		return getMembershipRequests().stream().filter(m -> m.getId().equals(id)).findFirst()
				.orElseThrow(() -> new IstfException("There is no request membership number with id=" + id)).getMembershipNumber();
	}

	private String getMembershipNumberForAvgAnnualERSperMember(Set<String> membershipNumbers, LocalDateTime policyEffectiveDate, Double avgAnnualERSperMember) {
		for (String mNumber : membershipNumbers) {
			int ersCount = 0;
			int totalYearsCount = 0;

			for (MembershipResponse mResponse : getMembershipResponses(mNumber)) {
				if ("Active".equals(mResponse.getStatus())) {
					LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
					LocalDateTime serviceDate = mResponse.getServiceDate() != null ? mResponse.getServiceDate() : today;
					LocalDateTime memberStartDate = mResponse.getMemberStartDate() != null ? mResponse.getMemberStartDate() : today;

					if (Math.abs(policyEffectiveDate.getYear() - serviceDate.getYear()) <= 3) {
						ersCount++;
					}
					int yearsCount = Math.abs(policyEffectiveDate.getYear() - memberStartDate.getYear());
					totalYearsCount += yearsCount <= 3 ? yearsCount : 3;
				}
			}

			if (totalYearsCount == 0) {
				// to not allow division by zero
				continue;
			}

			if (ersCount / totalYearsCount == avgAnnualERSperMember) {
				return mNumber;
			}
		}

		return null;
	}
}
