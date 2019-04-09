package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Coverage {

	@XmlElement(name = "Code")
	private String code;

	@XmlElement(name = "DeclineFlg")
	private Boolean declineFlg;

	@XmlElement(name = "DeductibleAmt")
	private Double deductibleAmt;

	@XmlElement(name = "LimitAmt")
	private Double limitAmt;

	@XmlElement(name = "LimitPerOccurance")
	private Integer limitPerOccurance;

	@XmlElement(name = "LimitPerPerson")
	private Integer limitPerPerson;

	@XmlElement(name = "Name")
	private String name;

	@XmlElement(name = "PremiumAmt")
	private Double premiumAmt;

	public String getCode() {
		return code;
	}

	public Coverage setCode(String code) {
		this.code = code;
		return this;
	}

	public Boolean getDeclineFlg() {
		return declineFlg;
	}

	public Coverage setDeclineFlg(Boolean declineFlg) {
		this.declineFlg = declineFlg;
		return this;
	}

	public Double getDeductibleAmt() {
		return deductibleAmt;
	}

	public Coverage setDeductibleAmt(Double deductibleAmt) {
		this.deductibleAmt = deductibleAmt;
		return this;
	}

	public Double getLimitAmt() {
		return limitAmt;
	}

	public Coverage setLimitAmt(Double limitAmt) {
		this.limitAmt = limitAmt;
		return this;
	}

	public Integer getLimitPerOccurance() {
		return limitPerOccurance;
	}

	public Coverage setLimitPerOccurance(Integer limitPerOccurance) {
		this.limitPerOccurance = limitPerOccurance;
		return this;
	}

	public Integer getLimitPerPerson() {
		return limitPerPerson;
	}

	public Coverage setLimitPerPerson(Integer limitPerPerson) {
		this.limitPerPerson = limitPerPerson;
		return this;
	}

	public String getName() {
		return name;
	}

	public Coverage setName(String name) {
		this.name = name;
		return this;
	}

	public Double getPremiumAmt() {
		return premiumAmt;
	}

	public Coverage setPremiumAmt(Double premiumAmt) {
		this.premiumAmt = premiumAmt;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Coverage coverage = (Coverage) o;
		return Objects.equals(code, coverage.code) &&
				Objects.equals(declineFlg, coverage.declineFlg) &&
				Objects.equals(deductibleAmt, coverage.deductibleAmt) &&
				Objects.equals(limitAmt, coverage.limitAmt) &&
				Objects.equals(limitPerOccurance, coverage.limitPerOccurance) &&
				Objects.equals(limitPerPerson, coverage.limitPerPerson) &&
				Objects.equals(name, coverage.name) &&
				Objects.equals(premiumAmt, coverage.premiumAmt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, declineFlg, deductibleAmt, limitAmt, limitPerOccurance, limitPerPerson, name, premiumAmt);
	}

	@Override
	public String toString() {
		return "Coverage{" +
				"code='" + code + '\'' +
				", declineFlg=" + declineFlg +
				", deductibleAmt=" + deductibleAmt +
				", limitAmt=" + limitAmt +
				", limitPerOccurance=" + limitPerOccurance +
				", limitPerPerson=" + limitPerPerson +
				", name='" + name + '\'' +
				", premiumAmt=" + premiumAmt +
				'}';
	}
}
