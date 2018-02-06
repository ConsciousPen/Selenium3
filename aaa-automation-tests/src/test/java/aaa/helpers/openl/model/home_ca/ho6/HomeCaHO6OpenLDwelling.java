package aaa.helpers.openl.model.home_ca.ho6;

import aaa.helpers.openl.model.home_ca.HomeCaOpenLDwelling;

public class HomeCaHO6OpenLDwelling extends HomeCaOpenLDwelling {
	private Integer ageOfHome;
	private String burglarAlarmType;
	private String constructionType;
	private Integer firelineScore;
	private String fireAlarmType;
	private Boolean hasSprinklers;
	private Boolean isGatedCommunity;
	private Boolean isSecondaryHome;

	public Integer getAgeOfHome() {
		return ageOfHome;
	}

	public void setAgeOfHome(Integer ageOfHome) {
		this.ageOfHome = ageOfHome;
	}

	public String getBurglarAlarmType() {
		return burglarAlarmType;
	}

	public void setBurglarAlarmType(String burglarAlarmType) {
		this.burglarAlarmType = burglarAlarmType;
	}

	public String getConstructionType() {
		return constructionType;
	}

	public void setConstructionType(String constructionType) {
		this.constructionType = constructionType;
	}

	public Integer getFirelineScore() {
		return firelineScore;
	}

	public void setFirelineScore(Integer firelineScore) {
		this.firelineScore = firelineScore;
	}

	public String getFireAlarmType() {
		return fireAlarmType;
	}

	public void setFireAlarmType(String fireAlarmType) {
		this.fireAlarmType = fireAlarmType;
	}

	public Boolean getHasSprinklers() {
		return hasSprinklers;
	}

	public void setHasSprinklers(Boolean hasSprinklers) {
		this.hasSprinklers = hasSprinklers;
	}

	public Boolean getGatedCommunity() {
		return isGatedCommunity;
	}

	public void setGatedCommunity(Boolean gatedCommunity) {
		isGatedCommunity = gatedCommunity;
	}

	public Boolean getSecondaryHome() {
		return isSecondaryHome;
	}

	public void setSecondaryHome(Boolean secondaryHome) {
		isSecondaryHome = secondaryHome;
	}

	@Override
	public String toString() {
		return "HomeCaHO6OpenLDwelling{" +
				"ageOfHome=" + ageOfHome +
				", burglarAlarmType='" + burglarAlarmType + '\'' +
				", constructionType='" + constructionType + '\'' +
				", firelineScore=" + firelineScore +
				", fireAlarmType='" + fireAlarmType + '\'' +
				", hasSprinklers=" + hasSprinklers +
				", isGatedCommunity=" + isGatedCommunity +
				", isSecondaryHome=" + isSecondaryHome +
				", number=" + number +
				", address=" + address +
				'}';
	}
}
