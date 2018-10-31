package aaa.helpers.openl.testdata_generator;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLForm;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class HomeCaHO6TestDataGenerator extends TestDataGenerator<HomeCaHO6OpenLPolicy> {
	public HomeCaHO6TestDataGenerator(String state) {
		super(state);
	}

	public HomeCaHO6TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeCaHO6OpenLPolicy openLPolicy) {
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

		for (HomeCaHO6OpenLForm form : openLPolicy.getForms()) {
			if (form.getFormCode().contains("61")) {
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

	private TestData getGeneralTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		TestData policyInfo = DataProviderFactory.emptyData();
		TestData currentCarrier = DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.CurrentCarrier.CONTINUOUS_YEARS_WITH_HO_INSURANCE.getLabel(), openLPolicy.getYearsOfPriorInsurance(),
				HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsWithCsaa()).format(DateTimeUtils.MM_DD_YYYY));
		return DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), policyInfo,
				HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), currentCarrier);
	}

	private TestData getApplicantTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		TestData namedInsured = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NamedInsured.OCCUPATION.getLabel(), "Other", //"Unknown"
				HomeCaMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel(), getYesOrNo(openLPolicy.getHasEmployeeDiscount()));
		if (!openLPolicy.getHasSeniorDiscount()) {
			namedInsured.adjust(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(),
					openLPolicy.getEffectiveDate().minusYears(openLPolicy.getAgeOfOldestInsured()).format(DateTimeUtils.MM_DD_YYYY));
		} else {
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

		TestData otherActiveAAAPolicies = DataProviderFactory.emptyData();
		if (openLPolicy.getHasAutoPolicy() || openLPolicy.getHasCeaPolicy()) {
			otherActiveAAAPolicies = DataProviderFactory.dataOf(
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), "Yes",
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), "click",
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData());
			if (openLPolicy.getHasAutoPolicy()) {
				otherActiveAAAPolicies.adjust(
						HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "Auto",
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COMPANION_AUTO_PENDING_WITH_DISCOUNT.getLabel(), "No",
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "345345345",
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_STATE.getLabel(), "CA",
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().minusYears(1).format(DateTimeUtils.MM_DD_YYYY),
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_BI_LIMIT.getLabel(), "index=2"));
			}
			if (openLPolicy.getHasCeaPolicy()) {
				otherActiveAAAPolicies.adjust(
						HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "CEA"));
			}
		}

		return DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), namedInsured,
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembership,
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddress,
				HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), otherActiveAAAPolicies);
	}

	private TestData getReportsTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getPropertyInfoTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		boolean isHO42C = false;
		for (HomeCaHO6OpenLForm form : openLPolicy.getForms()) {
			if ("HO-42C".equals(form.getFormCode())) {
				isHO42C = true;
			}
		}

		TestData dwellingAddressData = DataProviderFactory.emptyData();
		if (isHO42C) {
			String territoryCode = openLPolicy.getForms().stream().filter(n -> "HO-42C".equals(n.getFormCode())).findFirst().get().getTerritoryCode(); 
			if (territoryCode.equals("Office")) {
				dwellingAddressData = DataProviderFactory.dataOf(
						HomeCaMetaData.PropertyInfoTab.DwellingAddress.SECTION_II_TERRITORY.getLabel(), "contains=" + RandomUtils.nextInt(1, 4));
			}
			else {
				dwellingAddressData = DataProviderFactory.dataOf(
						HomeCaMetaData.PropertyInfoTab.DwellingAddress.SECTION_II_TERRITORY.getLabel(), "contains=" + territoryCode);
			}
		} 

		TestData ppcData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getDwelling().getPpcValue());

		Dollar coverageA = new Dollar(openLPolicy.getCovALimit());
		TestData propertyValueData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), coverageA,
				HomeCaMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST.getLabel(), coverageA.multiply(0.85),
				HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), "Mortgagee requirements");

		TestData constructionData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getDwelling().getAgeOfHome()).getYear(),
				HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), "contains=" + openLPolicy.getDwelling().getConstructionType());

		TestData interiorData;
		TestData rentalInformationData = DataProviderFactory.emptyData();
		if (openLPolicy.getDwelling().getSecondaryHome()) {
			interiorData = DataProviderFactory.dataOf(HomeCaMetaData.PropertyInfoTab.Interior.DWELLING_USAGE.getLabel(), "Secondary");
		} else {
			interiorData = DataProviderFactory.dataOf(HomeCaMetaData.PropertyInfoTab.Interior.DWELLING_USAGE.getLabel(), "Primary");
		}

		if (openLPolicy.getRented()) {
			interiorData.adjust(DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.Interior.OCCUPANCY_TYPE.getLabel(), "Tenant occupied",
					HomeCaMetaData.PropertyInfoTab.Interior.NUMBER_OF_RESIDENTS.getLabel(), "3",
					HomeCaMetaData.PropertyInfoTab.Interior.NUMBER_OF_STORIES_INCLUDING_BASEMENT.getLabel(), "1"));
			rentalInformationData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.RentalInformation.DOES_THE_TENANT_HAVE_AN_UNDERLYING_HO4_POLICY.getLabel(), "Yes");
		} else {
			interiorData.adjust(DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.Interior.OCCUPANCY_TYPE.getLabel(), "Owner occupied",
					HomeCaMetaData.PropertyInfoTab.Interior.NUMBER_OF_RESIDENTS.getLabel(), "3",
					HomeCaMetaData.PropertyInfoTab.Interior.NUMBER_OF_STORIES_INCLUDING_BASEMENT.getLabel(), "1"));
		}
		
		TestData theftProtectiveDeviceData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.LOCAL_THEFT_ALARM.getLabel(), "Local".equals(openLPolicy.getDwelling().getBurglarAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.CENTRAL_THEFT_ALARM.getLabel(), "Central".equals(openLPolicy.getDwelling().getBurglarAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.GATED_COMMUNITY.getLabel(), openLPolicy.getDwelling().getGatedCommunity());
		
		TestData fireProtectiveDeviceData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.FireProtectiveDD.LOCAL_FIRE_ALARM.getLabel(), "Local".equals(openLPolicy.getDwelling().getFireAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.FireProtectiveDD.CENTRAL_FIRE_ALARM.getLabel(), "Central".equals(openLPolicy.getDwelling().getFireAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.FireProtectiveDD.FULL_RESIDENTIAL_SPRINKLERS.getLabel(), openLPolicy.getDwelling().getHasSprinklers());

		List<TestData> claimHistoryData = getClaimsHistoryData(openLPolicy, openLPolicy.getExpClaimPoints(), openLPolicy.getClaimPoints());

		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData,
				HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
				HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData,
				HomeCaMetaData.PropertyInfoTab.INTERIOR.getLabel(), interiorData,
				HomeCaMetaData.PropertyInfoTab.RENTAL_INFORMATION.getLabel(), rentalInformationData,
				HomeCaMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceData,
				HomeCaMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), fireProtectiveDeviceData,
				HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(), claimHistoryData);
	}

	private List<TestData> getClaimsHistoryData(HomeCaHO6OpenLPolicy openLPolicy, Integer aaaClaimPoints, Integer notAaaClaimPoints) {
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

	private TestData addClaimData(HomeCaHO6OpenLPolicy openLPolicy, boolean isFirstClaim) {
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

	private TestData getEndorsementTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		TestData endorsementData = DataProviderFactory.emptyData();
		for (HomeCaHO6OpenLForm openLForm : openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!"premium".equals(formCode)) {
				if (!endorsementData.containsKey(HomeCaHO6FormTestDataGenerator.getFormMetaKey(formCode))) {
					List<TestData> tdList = HomeCaHO6FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
					if (tdList != null) {
						TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeCaHO6FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeCaHO6FormTestDataGenerator.getFormMetaKey(formCode), tdList);
						endorsementData.adjust(td);
					}
				}
			}
		}

		if (Boolean.FALSE.equals(openLPolicy.getHasPolicySupportingForm())) {
			List<TestData> tdList = HomeCaHO6FormTestDataGenerator.getFormTestData(openLPolicy, "HO-29");
			endorsementData.adjust(DataProviderFactory.dataOf(HomeCaHO6FormTestDataGenerator.getFormMetaKey("HO-29"), tdList));
		}

		return endorsementData;
	}

	private TestData getPersonalPropertyTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		TestData personalPropertyTabData = DataProviderFactory.emptyData();

		for (HomeCaHO6OpenLForm form : openLPolicy.getForms()) {
			if ("HW 04 61".equals(form.getFormCode()) && CollectionUtils.isNotEmpty(form.getScheduledPropertyItems())) {
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
					case "Firearms":
						TestData firearmsData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.Firearms.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.Firearms.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.FIREARMS.getLabel(), firearmsData));
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
					case "Comics":
						TestData comicsData = DataProviderFactory.dataOf(
								HomeCaMetaData.PersonalPropertyTab.TradingCardsOrComics.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(),
								HomeCaMetaData.PersonalPropertyTab.TradingCardsOrComics.NUMBER_OF_COMIC_BOOKS_OR_CARDS.getLabel(), "10",
								HomeCaMetaData.PersonalPropertyTab.TradingCardsOrComics.CERTIFICATE_OF_AUTHENTICITY_RECEIIVED.getLabel(), "Yes",
								HomeCaMetaData.PersonalPropertyTab.TradingCardsOrComics.DESCRIPTION.getLabel(), "test");
						personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.TRADING_CARDS_OR_COMICS.getLabel(), comicsData));
						break;
					default:
						break;
				}
			} else if ("HO-61C".equals(form.getFormCode())) {
				/*
				 * Class 1: Boat length < 144 and Horsepower < 16
				 * Class 1: Boat length > 167 and Boat length < 192 and Horsepower < 26 
				 * Class 1: Boat length > 191 and Boat length < 216 and Horsepower < 51
				 */
				String horsepower = "15";
				String lenght_inches = "200";
				if (getBoatType(form).equals("Outboard")) {
					if (form.getFormClass().equals("Class 1")) {
						horsepower = "12"; 
						lenght_inches = "143";
					}
					if (form.getFormClass().equals("Class 2")) {
						horsepower = "16"; 
						lenght_inches = "143";
					}
				}

				TestData boatsData = DataProviderFactory.dataOf(
						HomeCaMetaData.PersonalPropertyTab.Boats.BOAT_TYPE.getLabel(), getBoatType(form),
						HomeCaMetaData.PersonalPropertyTab.Boats.YEAR.getLabel(), openLPolicy.getEffectiveDate().minusYears(form.getAge()).getYear(),
						HomeCaMetaData.PersonalPropertyTab.Boats.HORSEPOWER.getLabel(), horsepower,
						HomeCaMetaData.PersonalPropertyTab.Boats.LENGTH_INCHES.getLabel(), lenght_inches,
						HomeCaMetaData.PersonalPropertyTab.Boats.DEDUCTIBLE.getLabel(), "contains=" + form.getDeductible().toString().split("\\.")[0],
						HomeCaMetaData.PersonalPropertyTab.Boats.AMOUNT_OF_INSURANCE.getLabel(), form.getLimit().toString().split("\\.")[0]);
				personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.BOATS.getLabel(), boatsData));
			}
		}
		return personalPropertyTabData;
	}
	
	private String getBoatType(HomeCaHO6OpenLForm form) {
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

	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		//Coverage A is disabled on Premiums & Coverges Quote tab
		//Double covA = openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covC = openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covD = openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covE = openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covF = openLPolicy.getCoverages().stream().filter(c -> "CovF".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();

		return DataProviderFactory.dataOf(
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), covC.toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), covD.toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=" + new Dollar(covE).toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_F.getLabel(), "contains=" + new Dollar(covF).toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), "contains=" + new Dollar(openLPolicy.getDeductible()).toString().split("\\.")[0]);
	}
}
