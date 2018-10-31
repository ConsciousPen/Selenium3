package aaa.helpers.openl.testdata_generator;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLForm;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class HomeCaHO4TestDataGenerator extends TestDataGenerator<HomeCaHO4OpenLPolicy> {
	public HomeCaHO4TestDataGenerator(String state) {
		super(state);
	}

	public HomeCaHO4TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeCaHO4OpenLPolicy openLPolicy) {
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();
		TestData maskedMembershipData = ratingDataPattern.getTestData(new ApplicantTab().getMetaKey()).mask(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
		TestData maskedReportsData = ratingDataPattern.getTestData(new ReportsTab().getMetaKey()).mask(HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());
		if (Boolean.FALSE.equals(openLPolicy.getAaaMember())) {
			ratingDataPattern.adjust(new ApplicantTab().getMetaKey(), maskedMembershipData);
			ratingDataPattern.adjust(new ReportsTab().getMetaKey(), maskedReportsData);
		}

		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new ApplicantTab().getMetaKey(), getApplicantTabData(openLPolicy),
				new ReportsTab().getMetaKey(), getReportsTabData(openLPolicy),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy));

		for (HomeCaHO4OpenLForm form : openLPolicy.getForms()) {
			if (form.getFormCode().contains("HO-61")) {
				td.adjust(DataProviderFactory.dataOf(new PersonalPropertyTab().getMetaKey(), getPersonalPropertyTabData(openLPolicy)));
			}
		}

		return TestDataHelper.merge(ratingDataPattern, td);
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}

	private TestData getGeneralTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		TestData policyInfo = DataProviderFactory.emptyData();
		TestData currentCarrier = DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.CurrentCarrier.CONTINUOUS_YEARS_WITH_HO_INSURANCE.getLabel(), openLPolicy.getYearsOfPriorInsurance(),
				HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsWithCsaa()).format(DateTimeUtils.MM_DD_YYYY));
		return DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), policyInfo,
				HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), currentCarrier);
	}

	private TestData getApplicantTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		TestData namedInsured = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel(), getYesOrNo(openLPolicy.getHasEmployeeDiscount()));
		if (openLPolicy.getHasSeniorDiscount()) {
			namedInsured.adjust(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(),
					openLPolicy.getEffectiveDate().minusYears(60).format(DateTimeUtils.MM_DD_YYYY));
		}
		TestData aaaMembership = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.getAaaMember()));
		if (Boolean.TRUE.equals(openLPolicy.getAaaMember())) {
			aaaMembership.adjust(HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "4290023667710001");
		}
		TestData dwellingAddress = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwelling().getAddress().getZipCode());
		return DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), namedInsured,
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembership,
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddress);
	}

	private TestData getReportsTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getPropertyInfoTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		boolean isHO42 = false;
		for (HomeCaHO4OpenLForm form : openLPolicy.getForms()) {
			if ("HO-42".equals(form.getFormCode())) {
				isHO42 = true;
			}
		}

		TestData dwellingAddressData = DataProviderFactory.emptyData();
		if ("CO1".equals(openLPolicy.getConstructionGroup())) {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + RandomUtils.nextInt(1, 4));
		} else if ("CO2".equals(openLPolicy.getConstructionGroup())) {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "5-15");
		}

		if (isHO42) {
			String territoryCode = openLPolicy.getForms().stream().filter(n -> "HO-42".equals(n.getFormCode())).findFirst().get().getTerritoryCode(); 
			if (territoryCode.equals("Office")) {
				dwellingAddressData.adjust(DataProviderFactory.dataOf(
						HomeCaMetaData.PropertyInfoTab.DwellingAddress.SECTION_II_TERRITORY.getLabel(), "contains=" + RandomUtils.nextInt(1, 4)));
			}
			else {
				dwellingAddressData.adjust(DataProviderFactory.dataOf(
						HomeCaMetaData.PropertyInfoTab.DwellingAddress.SECTION_II_TERRITORY.getLabel(), "contains=" + territoryCode));
			}
		}

		TestData ppcData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getDwelling().getPpcValue());
		TestData propertyValueData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PropertyValue.PERSONAL_PROPERTY_VALUE.getLabel(), new Dollar(openLPolicy.getCovCLimit()));

		TestData interiorData = DataProviderFactory.emptyData();
		if ("Renter".equals(openLPolicy.getOccupancyType())) {
			interiorData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.Interior.OCCUPANCY_TYPE.getLabel(), "Tenant occupied");
		}
		if ("Owner".equals(openLPolicy.getOccupancyType())) {
			interiorData = DataProviderFactory.dataOf(
					//TODO need clarify this value
					HomeCaMetaData.PropertyInfoTab.Interior.OCCUPANCY_TYPE.getLabel(), "Tenant occupied");
		}

		List<TestData> claimHistoryData = getClaimsHistoryData(openLPolicy, openLPolicy.getExpClaimPoints(), openLPolicy.getClaimPoints());

		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData,
				HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
				HomeCaMetaData.PropertyInfoTab.INTERIOR.getLabel(), interiorData,
				HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(), claimHistoryData);
	}

	private List<TestData> getClaimsHistoryData(HomeCaHO4OpenLPolicy openLPolicy, Integer aaaClaimPoints, Integer notAaaClaimPoints) {
		List<TestData> claimsDataList = new ArrayList<>();
		TestData claim = DataProviderFactory.emptyData();

		int totalPoints = aaaClaimPoints + notAaaClaimPoints;
		boolean isFirstClaim = true;

		if (totalPoints == 0) {
			claimsDataList.add(claim);
		}

		if (notAaaClaimPoints != 0) {
			for (int i = 0; i < notAaaClaimPoints; i++) {
				claim = addClaimData(openLPolicy, isFirstClaim);
				isFirstClaim = false;
				claimsDataList.add(claim);
			}
		}

		if (aaaClaimPoints != 0) {
			for (int j = 0; j < aaaClaimPoints; j++) {
				claim = addClaimData(openLPolicy, isFirstClaim);
				claim.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM.getLabel(), "Yes"));
				isFirstClaim = false;
				claimsDataList.add(claim);
			}
		}

		return claimsDataList;
	}

	private TestData addClaimData(HomeCaHO4OpenLPolicy openLPolicy, boolean isFirstClaim) {
		TestData claimData;
		if (isFirstClaim) {
			claimData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.BTN_ADD.getLabel(), "Click",
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel(),
					openLPolicy.getEffectiveDate().minusYears(RandomUtils.nextInt(1, 3)).format(DateTimeUtils.MM_DD_YYYY),
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel(), AdvancedComboBox.RANDOM_MARK,
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel(), RandomUtils.nextInt(10000, 20000),
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS.getLabel(), "Open");
		} else {
			claimData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel(),
					openLPolicy.getEffectiveDate().minusYears(RandomUtils.nextInt(1, 3)).format(DateTimeUtils.MM_DD_YYYY),
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel(), AdvancedComboBox.RANDOM_MARK,
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel(), RandomUtils.nextInt(10000, 20000),
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS.getLabel(), "Open");
		}
		return claimData;
	}

	private TestData getEndorsementTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		TestData endorsementData = new SimpleDataProvider();
		for (HomeCaHO4OpenLForm openLForm : openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!"premium".equals(formCode)) {
				if (!endorsementData.containsKey(HomeCaHO4FormTestDataGenerator.getFormMetaKey(formCode))) {
					List<TestData> tdList = HomeCaHO4FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
					if (tdList != null) {
						TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeCaHO4FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeCaHO4FormTestDataGenerator.getFormMetaKey(formCode), tdList);
						endorsementData.adjust(td);
					}
				}
			}
		}

		if (Boolean.FALSE.equals(openLPolicy.getHasPolicySupportingForm())) {
			List<TestData> tdList = HomeCaHO4FormTestDataGenerator.getFormTestData(openLPolicy, "HO-29");
			endorsementData.adjust(DataProviderFactory.dataOf(HomeCaHO4FormTestDataGenerator.getFormMetaKey("HO-29"), tdList));
		}

		return endorsementData;
	}

	private TestData getPersonalPropertyTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		TestData personalPropertyTabData = new SimpleDataProvider();
		for (HomeCaHO4OpenLForm form : openLPolicy.getForms()) {
			if ("HO-61".equals(form.getFormCode()) && CollectionUtils.isNotEmpty(form.getScheduledPropertyItems())) {
				switch (form.getScheduledPropertyItems().get(0).getPropertyType()) {
					case "Cameras":
						TestData camerasData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.Cameras.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.Cameras.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.CAMERAS.getLabel(), camerasData));
						break;
					case "Coins":
						TestData coinsData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.Coins.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.Coins.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.COINS.getLabel(), coinsData));
						break;
					case "Fine Art":
						TestData fineArtData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.FineArts.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.FineArts.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.FINE_ARTS.getLabel(), fineArtData));
						break;
					case "Furs":
						TestData fursData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.Furs.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.Furs.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.FURS.getLabel(), fursData));
						break;
					case "Golf Equipment":
						TestData golfEquipmentData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.GolfEquipment.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.GolfEquipment.DESCRIPTION.getLabel(), "test",
								HomeCaMetaData.PersonalPropertyTab.GolfEquipment.LEFT_OR_RIGHT_HANDED_CLUB.getLabel(), "index=1");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.GOLF_EQUIPMENT.getLabel(), golfEquipmentData));
						break;
					case "Jewelry":
						TestData jewerlyData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.Jewelry.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.Jewelry.JEWELRY_CATEGORY.getLabel(), "index=1",
								HomeCaMetaData.PersonalPropertyTab.Jewelry.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.JEWELRY.getLabel(), jewerlyData));
						break;
					case "Musical Instruments":
						TestData musicalInstrumentsData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.MusicalInstruments.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.MusicalInstruments.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.MUSICAL_INSTRUMENTS.getLabel(), musicalInstrumentsData));
						break;
					case "Stamps":
						TestData stampsData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.PostageStamps.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.PostageStamps.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.POSTAGE_STAMPS.getLabel(), stampsData));
						break;
					case "Silverware":
						TestData silverwareData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.Silverware.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.Silverware.DESCRIPTION.getLabel(), "test",
								HomeCaMetaData.PersonalPropertyTab.Silverware.SET_OR_INDIVIDUAL_PIECE.getLabel(), "index=1");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.SILVERWARE.getLabel(), silverwareData));
						break;
					default:
						break;
				}
			} else if ("HO-61C".equals(form.getFormCode())) {
				String horsepower = "15";
				String length_inches = "200";
				/*
				 * Class 1: Boat length < 144 and Horsepower < 16
				 * Class 1: Boat length > 167 and Boat length < 192 and Horsepower < 26 
				 * Class 1: Boat length > 191 and Boat length < 216 and Horsepower < 51
				 */
				if (getBoatType(form).equals("Outboard")) {
					if (form.getFormClass().equals("Class 1")) {
						horsepower = "25"; 
						length_inches = "167";
					}
					if (form.getFormClass().equals("Class 2")) {
						horsepower = "27"; 
						length_inches = "167";
					}
				}
				TestData boatsData = DataProviderFactory.dataOf(
						HomeCaMetaData.PersonalPropertyTab.Boats.BOAT_TYPE.getLabel(), getBoatType(form),
						HomeCaMetaData.PersonalPropertyTab.Boats.YEAR.getLabel(), openLPolicy.getEffectiveDate().minusYears(form.getAge()).getYear(),
						HomeCaMetaData.PersonalPropertyTab.Boats.HORSEPOWER.getLabel(), horsepower,
						HomeCaMetaData.PersonalPropertyTab.Boats.LENGTH_INCHES.getLabel(), length_inches,
						HomeCaMetaData.PersonalPropertyTab.Boats.DEDUCTIBLE.getLabel(), "contains=" + form.getDeductible().toString().split("\\.")[0],
						HomeCaMetaData.PersonalPropertyTab.Boats.AMOUNT_OF_INSURANCE.getLabel(), form.getLimit().toString().split("\\.")[0]);
				personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.BOATS.getLabel(), boatsData));
			}
		}
		return personalPropertyTabData;
	}
	
	private String getBoatType(HomeCaHO4OpenLForm form) {
		String boatType;
		switch (form.getType()) {
			case "Outboard":
				boatType = "Outboard";
				break;
			case "Sailboat": 
				boatType = "Sailboat";
				break;
			case "Inboard": 
				boatType = "Inboard";
				break;
			case "In/Outboard": 
				boatType = "Inboard/Outboard"; 
				break;
			case "Canoe": 
				boatType = "Other";
				break;
			default: 
				throw new IstfException("Unknown mapping for Boat Type = " + form.getType());
		}
		return boatType;
	}
	
	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		Double covD = openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covE = openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();

		return DataProviderFactory.dataOf(
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), covD.toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), new Dollar(covE).toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), getDeductibleValueByForm(openLPolicy));
	}
	
	
	private String getDeductibleValueByForm(HomeCaHO4OpenLPolicy openLPolicy) {
		String deductible = null;
		for (HomeCaHO4OpenLForm form : openLPolicy.getForms()) {
			switch (form.getFormCode()) {
				case "HO-58":
					deductible = "contains=" + new Dollar(250).toString().split("\\.")[0];
					break;
				case "HO-59":
					deductible = "contains=" + new Dollar(500).toString().split("\\.")[0];
					break;
				case "HO-60":
					deductible = "contains=" + new Dollar(1000).toString().split("\\.")[0];
					break;
				case "HO-76":
					deductible = "contains=" + new Dollar(1500).toString().split("\\.")[0];
					break;
				case "HO-77":
					deductible = "contains=" + new Dollar(2000).toString().split("\\.")[0];
					break;
				case "HO-78":
					deductible = "contains=" + new Dollar(2500).toString().split("\\.")[0];
					break;
				case "HO-79":
					deductible = "contains=" + new Dollar(3000).toString().split("\\.")[0];
					break;
				case "HO-80":
					deductible = "contains=" + new Dollar(4000).toString().split("\\.")[0];
					break;
				case "HO-81":
					deductible = "contains=" + new Dollar(5000).toString().split("\\.")[0];
					break;
				case "HO-82":
					deductible = "contains=" + new Dollar(7500).toString().split("\\.")[0];
					break;
				case "HO-177":
					deductible = "contains=theft";
					break;
				default:
					deductible = "contains=250";
			}
		}
		return deductible;
	}
	
}
