package aaa.helpers.xml.model.pasdoc;

import aaa.main.enums.DocGenEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Vehicle {

	@XmlElementWrapper(name = "AdditionalInterests")
	@XmlElement(name = "AdditionalInterest", namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI2)
	private List<AdditionalInterest> additionalInterests = new LinkedList<>();

	@XmlElement(name = "AirbagLocation")
	private String airbagLocation;

	@XmlElement(name = "BodyType")
	private String bodyType;

	@XmlElement(name = "CollDmgSymbl")
	private String collDmgSymbl;

	@XmlElement(name = "CompDamSymb")
	private String compDamSymb;

	@XmlElementWrapper(name = "Coverages")
	@XmlElement(name = "Coverage")
	private List<Coverage> coverages = new ArrayList<>();

	@XmlElementWrapper(name = "Discounts")
	@XmlElement(name = "Discount")
	private List<Discount> discounts = new ArrayList<>();

	@XmlElementWrapper(name = "Endorsements")
	@XmlElement(name = "Endorsement")
	private List<Discount> endorsements = new ArrayList<>();

	@XmlElement(name = "ExistingDamage")
	private String existingDamage;

	@XmlElement(name = "ExistingSalvageDamage")
	private String existingSalvageDamage;

	@XmlElement(name = "FullPremium")
	private Double fullPremium;

	@XmlElement(name = "GarageAddress")
	private Address garageAddress;

	@XmlElement(name = "Make")
	private String make;

	@XmlElement(name = "Model")
	private String model;

	@XmlElement(name = "RatedDriver")
	private String ratedDriver;

	@XmlElement(name = "SeqNumber")
	private Integer seqNumber;

	@XmlElement(name = "StatCode")
	private String statCode;

	@XmlElement(name = "StatedAmount")
	private Double statedAmount;

	@XmlElement(name = "TerritoryCode")
	private String territoryCode;

	@XmlElement(name = "Type")
	private String type;

	@XmlElement(name = "Usage")
	private String usage;

	@XmlElement(name = "VIN")
	private String vin;

	@XmlElement(name = "Year")
	private String year;

	public List<AdditionalInterest> getAdditionalInterests() {
		return additionalInterests;
	}

	public Vehicle setAdditionalInterests(List<AdditionalInterest> additionalInterests) {
		this.additionalInterests = additionalInterests;
		return this;
	}

	public String getAirbagLocation() {
		return airbagLocation;
	}

	public Vehicle setAirbagLocation(String airbagLocation) {
		this.airbagLocation = airbagLocation;
		return this;
	}

	public String getBodyType() {
		return bodyType;
	}

	public Vehicle setBodyType(String bodyType) {
		this.bodyType = bodyType;
		return this;
	}

	public String getCollDmgSymbl() {
		return collDmgSymbl;
	}

	public Vehicle setCollDmgSymbl(String collDmgSymbl) {
		this.collDmgSymbl = collDmgSymbl;
		return this;
	}

	public String getCompDamSymb() {
		return compDamSymb;
	}

	public Vehicle setCompDamSymb(String compDamSymb) {
		this.compDamSymb = compDamSymb;
		return this;
	}

	public List<Coverage> getCoverages() {
		return coverages;
	}

	public Vehicle setCoverages(List<Coverage> coverages) {
		this.coverages = coverages;
		return this;
	}

	public List<Discount> getDiscounts() {
		return discounts;
	}

	public Vehicle setDiscounts(List<Discount> discounts) {
		this.discounts = discounts;
		return this;
	}

	public List<Discount> getEndorsements() {
		return endorsements;
	}

	public Vehicle setEndorsements(List<Discount> endorsements) {
		this.endorsements = endorsements;
		return this;
	}

	public String getExistingDamage() {
		return existingDamage;
	}

	public Vehicle setExistingDamage(String existingDamage) {
		this.existingDamage = existingDamage;
		return this;
	}

	public String getExistingSalvageDamage() {
		return existingSalvageDamage;
	}

	public Vehicle setExistingSalvageDamage(String existingSalvageDamage) {
		this.existingSalvageDamage = existingSalvageDamage;
		return this;
	}

	public Double getFullPremium() {
		return fullPremium;
	}

	public Vehicle setFullPremium(Double fullPremium) {
		this.fullPremium = fullPremium;
		return this;
	}

	public Address getGarageAddress() {
		return garageAddress;
	}

	public Vehicle setGarageAddress(Address garageAddress) {
		this.garageAddress = garageAddress;
		return this;
	}

	public String getMake() {
		return make;
	}

	public Vehicle setMake(String make) {
		this.make = make;
		return this;
	}

	public String getModel() {
		return model;
	}

	public Vehicle setModel(String model) {
		this.model = model;
		return this;
	}

	public String getRatedDriver() {
		return ratedDriver;
	}

	public Vehicle setRatedDriver(String ratedDriver) {
		this.ratedDriver = ratedDriver;
		return this;
	}

	public Integer getSeqNumber() {
		return seqNumber;
	}

	public Vehicle setSeqNumber(Integer seqNumber) {
		this.seqNumber = seqNumber;
		return this;
	}

	public String getStatCode() {
		return statCode;
	}

	public Vehicle setStatCode(String statCode) {
		this.statCode = statCode;
		return this;
	}

	public Double getStatedAmount() {
		return statedAmount;
	}

	public Vehicle setStatedAmount(Double statedAmount) {
		this.statedAmount = statedAmount;
		return this;
	}

	public String getTerritoryCode() {
		return territoryCode;
	}

	public Vehicle setTerritoryCode(String territoryCode) {
		this.territoryCode = territoryCode;
		return this;
	}

	public String getType() {
		return type;
	}

	public Vehicle setType(String type) {
		this.type = type;
		return this;
	}

	public String getUsage() {
		return usage;
	}

	public Vehicle setUsage(String usage) {
		this.usage = usage;
		return this;
	}

	public String getVin() {
		return vin;
	}

	public Vehicle setVin(String vin) {
		this.vin = vin;
		return this;
	}

	public String getYear() {
		return year;
	}

	public Vehicle setYear(String year) {
		this.year = year;
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
		Vehicle vehicle = (Vehicle) o;
		return Objects.equals(additionalInterests, vehicle.additionalInterests) &&
				Objects.equals(airbagLocation, vehicle.airbagLocation) &&
				Objects.equals(bodyType, vehicle.bodyType) &&
				Objects.equals(collDmgSymbl, vehicle.collDmgSymbl) &&
				Objects.equals(compDamSymb, vehicle.compDamSymb) &&
				Objects.equals(coverages, vehicle.coverages) &&
				Objects.equals(discounts, vehicle.discounts) &&
				Objects.equals(endorsements, vehicle.endorsements) &&
				Objects.equals(existingDamage, vehicle.existingDamage) &&
				Objects.equals(existingSalvageDamage, vehicle.existingSalvageDamage) &&
				Objects.equals(fullPremium, vehicle.fullPremium) &&
				Objects.equals(garageAddress, vehicle.garageAddress) &&
				Objects.equals(make, vehicle.make) &&
				Objects.equals(model, vehicle.model) &&
				Objects.equals(ratedDriver, vehicle.ratedDriver) &&
				Objects.equals(seqNumber, vehicle.seqNumber) &&
				Objects.equals(statCode, vehicle.statCode) &&
				Objects.equals(statedAmount, vehicle.statedAmount) &&
				Objects.equals(territoryCode, vehicle.territoryCode) &&
				Objects.equals(type, vehicle.type) &&
				Objects.equals(usage, vehicle.usage) &&
				Objects.equals(vin, vehicle.vin) &&
				Objects.equals(year, vehicle.year);
	}

	@Override
	public int hashCode() {
		return Objects.hash(additionalInterests, airbagLocation, bodyType, collDmgSymbl, compDamSymb, coverages, discounts, endorsements, existingDamage, existingSalvageDamage, fullPremium, garageAddress, make, model, ratedDriver, seqNumber, statCode, statedAmount, territoryCode, type, usage, vin, year);
	}

	@Override
	public String toString() {
		return "Vehicle{" +
				"additionalInterests=" + additionalInterests +
				", airbagLocation='" + airbagLocation + '\'' +
				", bodyType='" + bodyType + '\'' +
				", collDmgSymbl='" + collDmgSymbl + '\'' +
				", compDamSymb='" + compDamSymb + '\'' +
				", coverages=" + coverages +
				", discounts=" + discounts +
				", endorsements=" + endorsements +
				", existingDamage='" + existingDamage + '\'' +
				", existingSalvageDamage='" + existingSalvageDamage + '\'' +
				", fullPremium=" + fullPremium +
				", garageAddress=" + garageAddress +
				", make='" + make + '\'' +
				", model='" + model + '\'' +
				", ratedDriver='" + ratedDriver + '\'' +
				", seqNumber=" + seqNumber +
				", statCode='" + statCode + '\'' +
				", statedAmount=" + statedAmount +
				", territoryCode='" + territoryCode + '\'' +
				", type='" + type + '\'' +
				", usage='" + usage + '\'' +
				", vin='" + vin + '\'' +
				", year='" + year + '\'' +
				'}';
	}
}
