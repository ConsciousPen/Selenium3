package aaa.helpers.mock.model.membership;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
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

	public Set<String> getNonActiveOrWithFaultCodeMembershipNumbers() {
		Set<String> nonActiveOrNotPrimaryMembershipNumbers = new HashSet<>();
		for (String membershipNumber : getMembershipRequestNumbers()) {
			List<MembershipResponse> responses = getMembershipResponses(membershipNumber);
			if (responses.stream().noneMatch(this::isActiveAndPrimary) && responses.stream().allMatch(m -> StringUtils.isBlank(m.getFaultCode()))) {
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
			membershipNumbersSet = getNonActiveOrWithFaultCodeMembershipNumbers();
			assertThat(membershipNumbersSet).as("No valid membership numbers were found").isNotEmpty();
			isMemberSinceDateSet = false;
		}

		membershipNumber = getMembershipNumberForAvgAnnualERSperMember(membershipNumbersSet, policyEffectiveDate, avgAnnualERSperMember);
		if (membershipNumber == null) {
			if (isMemberSinceDateSet) {
				membershipNumbersSet = getNonActiveOrWithFaultCodeMembershipNumbers();
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
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		List<String> validMembershipIds = new ArrayList<>();
		Set<String> membershipIds = getMembershipResponses().stream().map(MembershipResponse::getId).collect(Collectors.toSet());
		for (String id : membershipIds) {
			List<MembershipResponse> membershipResponses = getMembershipResponses().stream().filter(mr -> Objects.equals(id, mr.getId())).collect(Collectors.toList());
			if (membershipResponses.stream().anyMatch(r -> StringUtils.isNotBlank(r.getFaultCode()))) {
				continue;
			}

			boolean isValidId = false;
			for (MembershipResponse r : membershipResponses) {
				if (isActiveAndPrimary(r)) {
					//Response is valid if memberStartDate=memberSinceDate
					if (Objects.equals(r.getMemberStartDate(), memberSinceDate)) {
						isValidId = true;
						break;
					}

					//Response is valid if memberStartDate is empty AND today - memberStartDateMonthsOffset = memberSinceDate
					if (r.getMemberStartDate() == null && r.getMemberStartDateMonthsOffset() != null && today.minusMonths(Math.abs(r.getMemberStartDateMonthsOffset())).equals(memberSinceDate)) {
						isValidId = true;
						break;
					}

					//Response is valid if memberSinceDate == today AND memberStartDate is empty AND memberStartDateMonthsOffset is empty
					if (today.equals(memberSinceDate) && r.getMemberStartDate() == null && r.getMemberStartDateMonthsOffset() == null) {
						isValidId = true;
						break;
					}
				}
			}

			if (isValidId) {
				validMembershipIds.add(id);
			}
		}
		return validMembershipIds.stream().map(this::getMembershipRequestNumber).collect(Collectors.toSet());
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
		double defaultAvgAnnualERSperMember = 99.9;
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		for (String mNumber : membershipNumbers) {
			int ersCount = 0;
			int totalYearsCount = 0;

			List<MembershipResponse> membershipResponses = getMembershipResponses(mNumber);
			if (membershipResponses.stream().anyMatch(m -> StringUtils.isNotBlank(m.getFaultCode()))) {
				if (avgAnnualERSperMember.equals(defaultAvgAnnualERSperMember)) {
					return mNumber;
				}
				continue;
			}

			MembershipResponse activeAndPrimaryResponse = membershipResponses.stream().filter(this::isActiveAndPrimary).findFirst().orElse(null);
			if (activeAndPrimaryResponse != null) {
				LocalDateTime memberStartDate = activeAndPrimaryResponse.getMemberStartDate() != null ? activeAndPrimaryResponse.getMemberStartDate() : today;
				int primMemTotalYears = Math.abs(policyEffectiveDate.getYear() - memberStartDate.getYear());
				if (primMemTotalYears < 2) {
					if (avgAnnualERSperMember.equals(defaultAvgAnnualERSperMember)) {
						return mNumber;
					}
					continue;
				}
			} else {
				if (avgAnnualERSperMember.equals(defaultAvgAnnualERSperMember)) {
					return mNumber;
				}
				continue;
			}

			for (MembershipResponse mResponse : membershipResponses) {
				if ("Active".equals(mResponse.getStatus())) {
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

	private boolean isActiveAndPrimary(MembershipResponse membershipResponse) {
		// Response is valid if Status=Active AND memberType=Primary or empty or does not belong to group "Resident Adult Associate", "Dependent Associate"
		List<String> nonPrimaryTypes = Arrays.asList("Resident Adult Associate", "Dependent Associate");
		return "Active".equals(membershipResponse.getStatus()) && !nonPrimaryTypes.contains(membershipResponse.getMemberType());
	}
}
