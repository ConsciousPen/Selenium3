package aaa.helpers.openl.model.mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import aaa.utils.excel.bind.ExcelTableElement;

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

	public String getMembershipNumber(LocalDateTime membershipEffectiveDate, Double ersUsageCountPerMember) {
		return getMembershipResponses().stream()
				.filter(r -> Objects.equals(r.getMembershipEffectiveDate(), membershipEffectiveDate)
						&& Objects.equals(r.getErsUsageCountPerActiveMember(), ersUsageCountPerMember))
				.findFirst().map(MembershipResponse::getMembershipNumber).orElse(null);
	}

	public LocalDateTime getMembershipEffectiveDate(String membershipNumber) {
		return getMembershipResponses().stream().filter(r -> Objects.equals(r.getMembershipNumber(), membershipNumber))
				.findFirst().map(MembershipResponse::getMembershipEffectiveDate).orElse(null);
	}
}
