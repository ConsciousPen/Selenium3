package aaa.helpers.mock.model.membership;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.utils.excel.bind.ExcelTableElement;
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

	public Pair<String, Boolean> getMembershipNumber(LocalDateTime policyEffectiveDate, Integer memberPersistency, Double avgAnnualERSperMember) {
		boolean isMemberSinceDateFound = true;
		Set<String> membershipNumbers = getMembershipNumbers(policyEffectiveDate.minusYears(memberPersistency));
		if (membershipNumbers.isEmpty()) {
			isMemberSinceDateFound = false;
			membershipNumbers = getNonActiveAndPrimaryMembershipNumbers();
		}

		for (String membershipNumber : membershipNumbers) {
			int ersCount = 0;
			for (MembershipResponse mResponse : getMembershipResponses(membershipNumber)) {
				if ("Active".equals(mResponse.getStatus()) && "Primary".equals(mResponse.getMemberType())) {
					LocalDateTime serviceDate = mResponse.getServiceDate() != null ? mResponse.getServiceDate() : TimeSetterUtil.getInstance().getCurrentTime();
					if (policyEffectiveDate.getYear() - serviceDate.getYear() <= 3) {
						ersCount++;
					}
				}

				//TODO-dchubkov: calculate correct membershipTotalYears
				double membershipTotalYears = 0;

				if (ersCount / membershipTotalYears == avgAnnualERSperMember) {
					return Pair.of(getMembershipRequestNumber(mResponse.getId()), isMemberSinceDateFound);
				}
			}

		}

		//TODO-dchubkov: add faster search if avgAnnualERSperMember == 0

		return Pair.of("EMPTY", isMemberSinceDateFound);
	}

	public Set<String> getMembershipNumbers(LocalDateTime memberSinceDate) {
		// Collecting all unique membership IDs with memberStartDate=memberSinceDate AND Status=Active AND memberType=Primary
		Set<String> membershipIds = getMembershipResponses().stream()
				.filter(mr -> mr.getMemberStartDate().equals(memberSinceDate) && "Active".equals(mr.getStatus()) && "Primary".equals(mr.getMemberType()))
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

	public Set<String> getNonActiveAndPrimaryMembershipNumbers() {
		return Collections.emptySet();
	}

	//TODO-dchubkov: remove>>>>>>>>>>>>>
	public LocalDateTime getMembershipEffectiveDate(String membershipNumber) {
		return getMembershipResponses().stream().filter(r -> Objects.equals(r.getMembershipNumber(), membershipNumber))
				.findFirst().map(MembershipResponse::getMembershipEffectiveDate).orElse(null);
	}

	public List<MembershipResponse> getMembershipResponses(String membershipRequestNumber) {
		String membershipRequestId = getMembershipRequests().stream().filter(m -> m.getMembershipNumber().equals(membershipRequestNumber)).map(MembershipRequest::getId).findFirst().get();
		return getMembershipResponses().stream().filter(m -> m.getId().equals(membershipRequestId)).collect(Collectors.toList());
	}

	public String getMembershipRequestNumber(String id) {
		return getMembershipRequests().stream().filter(m -> m.getId().equals(id)).findFirst()
				.orElseThrow(() -> new IstfException("There is no request membership number with id=" + id)).getMembershipNumber();
	}
}
