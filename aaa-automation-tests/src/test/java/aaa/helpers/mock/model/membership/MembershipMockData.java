package aaa.helpers.mock.model.membership;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.exceptions.IstfException;

public class MembershipMockData {
	@ExcelTransient
	private static final Double AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE = 99.9;

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

	public List<String> getMembershipRequestNumbers() {
		return getMembershipRequests().stream().map(MembershipRequest::getMembershipNumber).collect(Collectors.toList());
	}

	public Set<String> getActiveAndPrimaryMembershipNumbersWithoutFaultCodes() {
		Set<String> membershipNembers = new HashSet<>();
		for (MembershipRequest request : getMembershipRequests()) {
			List<MembershipResponse> responses = getMembershipResponses(request.getMembershipNumber());
			if (responses.stream().anyMatch(r -> StringUtils.isNotBlank(r.getFaultCode()))) {
				continue;
			}
			if (responses.stream().anyMatch(this::isActiveAndPrimary)) {
				membershipNembers.add(request.getMembershipNumber());
			}
		}
		assertThat(membershipNembers).as("There is no active and primary membership numbers without fault codes").isNotEmpty();
		return membershipNembers;
	}

	public String getMembershipNumber(LocalDateTime policyEffectiveDate, Integer memberPersistency) {
		return getMembershipNumberForAvgAnnualERSperMember(policyEffectiveDate, memberPersistency, AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE);
	}

	public String getMembershipNumberForAvgAnnualERSperMember(LocalDateTime policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember) {
		Set<String> membershipNumbersSet = getActiveAndPrimaryMembershipNumbers(policyEffectiveDate.minusYears(memberPersistency));
		assertThat(membershipNumbersSet).as("No active and primary membership numbers were found for policyEffectiveDate=%1$s and memberPersistency=%2$s", policyEffectiveDate, memberPersistency)
				.isNotEmpty();

		if (avgAnnualERSperMember.equals(AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE)) {
			return membershipNumbersSet.stream().findFirst().get();
		}
		String membershipNumber = getMembershipNumberForAvgAnnualERSperMember(membershipNumbersSet, policyEffectiveDate, avgAnnualERSperMember);
		assertThat(membershipNumber).as("No valid membership number was found for avgAnnualERSperMember field").isNotNull();
		return membershipNumber;
	}

	public Set<String> getActiveAndPrimaryMembershipNumbers(LocalDateTime memberSinceDate) {
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		Set<String> validMembershipNumbers = new HashSet<>();
		for (String membershipNumber : getActiveAndPrimaryMembershipNumbersWithoutFaultCodes()) {
			for (MembershipResponse r : getMembershipResponses(membershipNumber)) {
				//Response is valid if memberStartDate=memberSinceDate
				if (isEqualYears(r.getMemberStartDate(), memberSinceDate)) {
					validMembershipNumbers.add(membershipNumber);
					break;
				}

				//Response is valid if memberStartDate is empty AND today - memberStartDateMonthsOffset = memberSinceDate
				if (r.getMemberStartDate() == null && r.getMemberStartDateMonthsOffset() != null && isEqualYears(today.minusMonths(Math.abs(r.getMemberStartDateMonthsOffset())), memberSinceDate)) {
					validMembershipNumbers.add(membershipNumber);
					break;
				}

				//Response is valid if memberSinceDate == today AND memberStartDate is empty AND memberStartDateMonthsOffset is empty
				if (isEqualYears(today, memberSinceDate) && r.getMemberStartDate() == null && r.getMemberStartDateMonthsOffset() == null) {
					validMembershipNumbers.add(membershipNumber);
					break;
				}
			}
		}
		return validMembershipNumbers;
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
		//double defaultAvgAnnualERSperMember = 99.9;
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		for (String mNumber : membershipNumbers) {
			int ersCount = 0;
			int totalYearsCount = 0;

			List<MembershipResponse> membershipResponses = getMembershipResponses(mNumber);
			/*if (membershipResponses.stream().anyMatch(m -> StringUtils.isNotBlank(m.getFaultCode()))) {
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
			}*/

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

	private boolean isEqualDates(LocalDateTime date1, LocalDateTime date2) {
		return Objects.equals(date1, date2) || date1 != null && date2 != null && date1.toLocalDate().equals(date2.toLocalDate());
	}

	private boolean isEqualYears(LocalDateTime date1, LocalDateTime date2) {
		return Objects.equals(date1, date2) || date1 != null && date2 != null && date1.getYear() == date2.getYear();
	}
}
