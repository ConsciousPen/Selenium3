package aaa.modules.cft.csv.model;

public class Record implements Comparable {

	private String code;
	private String billingAccountNumber;
	private String productCode;
	private String stateInfo;
	private String amount;
	private String action;
	private String actionDescription;
	private String plusMinus;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBillingAccountNumber() {
		return billingAccountNumber;
	}

	public void setBillingAccountNumber(String billingAccountNumber) {
		this.billingAccountNumber = billingAccountNumber;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public String getAmount() {
		return amount;
	}

	public double getAmountDoubleValue() {
		if (positiveTransaction()) {
			return Double.parseDouble(amount);
		} else {
			return Double.parseDouble("-" + amount);
		}
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionDescription() {
		return actionDescription;
	}

	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}

	public boolean positiveTransaction() {
		return "+".equals(plusMinus);
	}

	public void setPlusMinus(String plusMinus) {
		this.plusMinus = plusMinus;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Record body = (Record) o;

		if (code != null ? !code.equals(body.code) : body.code != null) return false;
		if (billingAccountNumber != null ? !billingAccountNumber.equals(body.billingAccountNumber) : body.billingAccountNumber != null) return false;
		if (productCode != null ? !productCode.equals(body.productCode) : body.productCode != null) return false;
		if (stateInfo != null ? !stateInfo.equals(body.stateInfo) : body.stateInfo != null) return false;
		if (amount != null ? !amount.equals(body.amount) : body.amount != null) return false;
		if (action != null ? !action.equals(body.action) : body.action != null) return false;
		if (actionDescription != null ? !actionDescription.equals(body.actionDescription) : body.actionDescription != null) return false;
		return plusMinus != null ? plusMinus.equals(body.plusMinus) : body.plusMinus == null;
	}

	@Override
	public int hashCode() {
		int result = code != null ? code.hashCode() : 0;
		result = 31 * result + (billingAccountNumber != null ? billingAccountNumber.hashCode() : 0);
		result = 31 * result + (productCode != null ? productCode.hashCode() : 0);
		result = 31 * result + (stateInfo != null ? stateInfo.hashCode() : 0);
		result = 31 * result + (amount != null ? amount.hashCode() : 0);
		result = 31 * result + (action != null ? action.hashCode() : 0);
		result = 31 * result + (actionDescription != null ? actionDescription.hashCode() : 0);
		result = 31 * result + (plusMinus != null ? plusMinus.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Body{" +
				"code='" + code + '\'' +
				", billingAccountNumber='" + billingAccountNumber + '\'' +
				", productCode='" + productCode + '\'' +
				", stateInfo='" + stateInfo + '\'' +
				", amount='" + amount + '\'' +
				", action='" + action + '\'' +
				", actionDescription='" + actionDescription + '\'' +
				", plusMinus='" + plusMinus + '\'' +
				'}';
	}

	@Override
	public int compareTo(Object o) {
		if (this.getBillingAccountNumber().equals(((Record) o).getBillingAccountNumber())) {
			return 1;
		}
		return -1;
	}
}
