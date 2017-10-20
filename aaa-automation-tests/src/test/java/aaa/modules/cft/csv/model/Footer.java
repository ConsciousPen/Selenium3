package aaa.modules.cft.csv.model;

public class Footer {

	private String code;
	private String overallExpSum;
	private String overallSum;
	private String amountOfRecords;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOverallExpSum() {
		return overallExpSum;
	}

	public void setOverallExpSum(String overallExpSum) {
		this.overallExpSum = overallExpSum;
	}

	public String getOverallSum() {
		return overallSum;
	}

	public void setOverallSum(String overallSum) {
		this.overallSum = overallSum;
	}

	public String getAmountOfRecords() {
		return amountOfRecords;
	}

	public void setAmountOfRecords(String amountOfRecords) {
		this.amountOfRecords = amountOfRecords;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Footer footer = (Footer) o;

		if (code != null ? !code.equals(footer.code) : footer.code != null) return false;
		if (overallExpSum != null ? !overallExpSum.equals(footer.overallExpSum) : footer.overallExpSum != null) return false;
		if (overallSum != null ? !overallSum.equals(footer.overallSum) : footer.overallSum != null) return false;
		return amountOfRecords != null ? amountOfRecords.equals(footer.amountOfRecords) : footer.amountOfRecords == null;
	}

	@Override
	public int hashCode() {
		int result = code != null ? code.hashCode() : 0;
		result = 31 * result + (overallExpSum != null ? overallExpSum.hashCode() : 0);
		result = 31 * result + (overallSum != null ? overallSum.hashCode() : 0);
		result = 31 * result + (amountOfRecords != null ? amountOfRecords.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Footer{" +
				"code='" + code + '\'' +
				", overallExpSum='" + overallExpSum + '\'' +
				", overallSum='" + overallSum + '\'' +
				", amountOfRecords='" + amountOfRecords + '\'' +
				'}';
	}
}
