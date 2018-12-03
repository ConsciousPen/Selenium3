package aaa.helpers.mock.model.membership;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.timesetter.client.TimeSetterClient;
import aaa.helpers.mock.model.AbstractMock;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class RetrieveMembershipSummaryMock extends AbstractMock {
	@ExcelTransient
	public static final Double AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE = 99.9;

	@ExcelTransient
	public static final String FILE_NAME = "RetrieveMembershipSummaryMockData.xls";

	private List<MembershipRequest> membershipRequests;
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

	public Set<String> getActiveAndPrimaryMembershipNumbersWithoutFaultCodes() {
		Set<String> membershipNembers = new HashSet<>();
		for (MembershipRequest request : getMembershipRequests()) {
			if (request.getMembershipNumber() == null) {
				continue;
			}
			List<MembershipResponse> responses = getMembershipResponses(request.getMembershipNumber());
			if (responses.stream().anyMatch(r -> StringUtils.isNotBlank(r.getFaultCode()))) {
				continue;
			}
			if (responses.stream().anyMatch(this::isActiveAndPrimary)) {
				membershipNembers.add(request.getMembershipNumber());
			}
		}
		return membershipNembers;
	}

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	@Override
	public String toString() {
		return "RetrieveMembershipSummaryMock{" +
				"membershipRequests=" + membershipRequests +
				", membershipResponses=" + membershipResponses +
				'}';
	}

	public String getMembershipNumber(LocalDate policyEffectiveDate, Integer memberPersistency) {
		return getMembershipNumberForAvgAnnualERSperMember(policyEffectiveDate, memberPersistency, AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE);
	}

	public String getMembershipNumberForAvgAnnualERSperMember(LocalDate policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember) {
		Set<String> membershipNumbersSet = getActiveAndPrimaryMembershipNumbers(policyEffectiveDate.minusYears(memberPersistency));
		if (!membershipNumbersSet.isEmpty()) {
			if (avgAnnualERSperMember.equals(AVG_ANNUAL_ERS_PER_MEMBER_DEFAULT_VALUE)) {
				return membershipNumbersSet.stream().findFirst().get();
			}

			return getMembershipNumberForAvgAnnualERSperMember(membershipNumbersSet, policyEffectiveDate, avgAnnualERSperMember);
		}
		return null;
	}

	public Set<String> getActiveAndPrimaryMembershipNumbers(LocalDate memberSinceDate) {
		// TimeSetterUtil.getInstance().getCurrentTime() breaks time shifting if executed in before suite and "timeshift-scenario-mode" != "suite"
		//LocalDate today = TimeSetterUtil.istfDateToJava(new TimeSetterClient().getStartTime()).toLocalDate();
		Set<String> validMembershipNumbers = new HashSet<>();
		for (String membershipNumber : getActiveAndPrimaryMembershipNumbersWithoutFaultCodes()) {
			for (MembershipResponse r : getMembershipResponses(membershipNumber)) {
				//Response is valid if memberStartDate=memberSinceDate
				if (Objects.equals(r.getMemberStartDate(), memberSinceDate)) {
					validMembershipNumbers.add(membershipNumber);
					break;
				}
				/*//Response is valid if memberStartDate is empty AND today - memberStartDateMonthsOffset = memberSinceDate
				if (r.getMemberStartDate() == null && r.getMemberStartDateMonthsOffset() != null && Objects.equals(today.minusMonths(Math.abs(r.getMemberStartDateMonthsOffset())), memberSinceDate)) {
					validMembershipNumbers.add(membershipNumber);
					break;
				}

				//Response is valid if memberSinceDate == today AND memberStartDate is empty AND memberStartDateMonthsOffset is empty
				if (Objects.equals(today, memberSinceDate) && r.getMemberStartDate() == null && r.getMemberStartDateMonthsOffset() == null) {
					validMembershipNumbers.add(membershipNumber);
					break;
				}*/
			}
		}
		return validMembershipNumbers;
	}

	public List<MembershipResponse> getMembershipResponses(String membershipRequestNumber) {
		String membershipRequestId = getMembershipRequests().stream().filter(m -> Objects.equals(m.getMembershipNumber(), membershipRequestNumber)).map(MembershipRequest::getId).findFirst().get();
		return getMembershipResponses().stream().filter(m -> Objects.equals(m.getId(), membershipRequestId)).collect(Collectors.toList());
	}

	private String getMembershipNumberForAvgAnnualERSperMember(Set<String> membershipNumbers, LocalDate policyEffectiveDate, Double avgAnnualERSperMember) {
		// TimeSetterUtil.getInstance().getCurrentTime() breaks time shifting if executed in before suite and "timeshift-scenario-mode" != "suite"
		LocalDate today = TimeSetterUtil.istfDateToJava(new TimeSetterClient().getStartTime()).toLocalDate();
		for (String mNumber : membershipNumbers) {
			int ersCount = 0;
			int totalYearsCount = 0;

			for (MembershipResponse mResponse : getMembershipResponses(mNumber)) {
				if ("Active".equals(mResponse.getStatus())) {
					LocalDate serviceDate = mResponse.getServiceDate() != null ? mResponse.getServiceDate() : today;
					LocalDate memberStartDate = mResponse.getMemberStartDate() != null ? mResponse.getMemberStartDate() : today;
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
