package aaa.helpers.mock.model.property_risk_reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.helpers.mock.model.AbstractMock;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class RetrievePropertyRiskReportsMock extends AbstractMock {
	@ExcelTransient
	public static final String FILE_NAME = "RetrievePropertyRiskReportsMockData.xls";

	private List<RiskReportsRequest> riskReportsRequests;
	private List<RiskReportsResponse> riskReportsResponses;

	public List<RiskReportsRequest> getRiskReportsRequests() {
		return Collections.unmodifiableList(riskReportsRequests);
	}

	public void setRiskReportsRequests(List<RiskReportsRequest> riskReportsRequests) {
		this.riskReportsRequests = new ArrayList<>(riskReportsRequests);
	}

	public List<RiskReportsResponse> getRiskReportsResponses() {
		return Collections.unmodifiableList(riskReportsResponses);
	}

	public void setRiskReportsResponses(List<RiskReportsResponse> riskReportsResponses) {
		this.riskReportsResponses = new ArrayList<>(riskReportsResponses);
	}

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	@Override
	public String toString() {
		return "RetrievePropertyRiskReportsMock{" +
				"riskReportsRequests=" + riskReportsRequests +
				", riskReportsResponses=" + riskReportsResponses +
				'}';
	}
}
