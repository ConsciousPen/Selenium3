package aaa.helpers.openl.testdata_generator;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLForm;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class HomeCaHO3TestDataGenerator extends TestDataGenerator<HomeCaHO3OpenLPolicy> {
	public HomeCaHO3TestDataGenerator(String state) {
		super(state);
	}

	public HomeCaHO3TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();
		TestData maskedMembershipData = ratingDataPattern.getTestData(new ApplicantTab().getMetaKey()).mask(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
		TestData maskedReportsData = ratingDataPattern.getTestData(new ReportsTab().getMetaKey()).mask(HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());
		TestData maskedDetachedStructuresData = ratingDataPattern.getTestData(new PropertyInfoTab().getMetaKey()).mask(HomeCaMetaData.PropertyInfoTab.DETACHED_STRUCTURES.getLabel());

		if (Boolean.FALSE.equals(openLPolicy.getAaaMember())) {
			ratingDataPattern.adjust(new ApplicantTab().getMetaKey(), maskedMembershipData);
			ratingDataPattern.adjust(new ReportsTab().getMetaKey(), maskedReportsData);
		}
		ratingDataPattern.adjust(new PropertyInfoTab().getMetaKey(), maskedDetachedStructuresData);

		//openLPolicy.setEffectiveDate(TimeSetterUtil.getInstance().parse("09/14/2017", DateTimeUtils.MM_DD_YYYY));

		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new ApplicantTab().getMetaKey(), getApplicantTabData(openLPolicy),
				new ReportsTab().getMetaKey(), getReportsTabData(openLPolicy),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy));

		for (HomeCaHO3OpenLForm form : openLPolicy.getForms()) {
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

	private TestData getGeneralTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData policyInfo = DataProviderFactory.emptyData();
		//TestData policyInfo = DataProviderFactory.dataOf(
		//		HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY));
		TestData currentCarrier = DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.CurrentCarrier.CONTINUOUS_YEARS_WITH_HO_INSURANCE.getLabel(), openLPolicy.getYearsOfPriorInsurance(),
				HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsWithCsaa()).format(DateTimeUtils.MM_DD_YYYY));
		return DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), policyInfo,
				HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), currentCarrier);
	}

	private TestData getApplicantTabData(HomeCaHO3OpenLPolicy openLPolicy) {
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
				/*HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel(), "265 CHIPMAN AVE",
				HomeCaMetaData.ApplicantTab.DwellingAddress.CITY.getLabel(), "LOS ANGELES",
				HomeCaMetaData.ApplicantTab.DwellingAddress.COUNTY.getLabel(), "Los Angeles",
				HomeCaMetaData.ApplicantTab.DwellingAddress.VALIDATE_ADDRESS_BTN.getLabel(), "click",
				HomeCaMetaData.ApplicantTab.DwellingAddress.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData());*/

		TestData otherActiveAAAPolicies;
		if (Boolean.TRUE.equals(openLPolicy.getHasMultiPolicyDiscount())) {
			otherActiveAAAPolicies = DataProviderFactory.dataOf(
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), "Yes",
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), "click",
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "HO3",
							HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "345345345",
							HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COVERAGE_E.getLabel(), "1000",
							HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.DEDUCTIBLE.getLabel(), "1000",
							HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.DWELLING_USAGE.getLabel(), "Primary",
							HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.OCCUPANCY_TYPE.getLabel(), "Owner occupied"));
		} else {
			otherActiveAAAPolicies = DataProviderFactory.dataOf(
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), getYesOrNo(openLPolicy.getHasMultiPolicyDiscount()));
		}

		return DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), namedInsured,
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembership,
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddress,
				HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), otherActiveAAAPolicies);
	}

	private TestData getReportsTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getPropertyInfoTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		Dollar coverageA = new Dollar(openLPolicy.getCovALimit());

		boolean isHO42 = false;
		boolean isHO44 = false;
		for (HomeCaHO3OpenLForm form : openLPolicy.getForms()) {
			if ("HO-42".equals(form.getFormCode())) {
				isHO42 = true;
			}
			if ("HO-44".equals(form.getFormCode())) {
				isHO44 = true;
			}
		}

		TestData dwellingAddressData; 
		if (isHO42) {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + openLPolicy.getDwelling().getNumOfFamilies());
			String territoryCode = openLPolicy.getForms().stream().filter(n -> "HO-42".equals(n.getFormCode())).findFirst().get().getTerritoryCode();
			if (territoryCode.equals("Office")) {
				dwellingAddressData.adjust(DataProviderFactory.dataOf(
						HomeCaMetaData.PropertyInfoTab.DwellingAddress.SECTION_II_TERRITORY.getLabel(),	"contains=" + RandomUtils.nextInt(1, 4)));
			}
			else {
				dwellingAddressData.adjust(DataProviderFactory.dataOf(
						HomeCaMetaData.PropertyInfoTab.DwellingAddress.SECTION_II_TERRITORY.getLabel(),	"contains=" + territoryCode));
			}
		} 
		else if (isHO44) {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(),
					"contains=" + openLPolicy.getForms().stream().filter(n -> "HO-44".equals(n.getFormCode())).findFirst().get().getNumOfFamilies(),
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.SECTION_II_TERRITORY.getLabel(),
					"contains=" + openLPolicy.getForms().stream().filter(n -> "HO-44".equals(n.getFormCode())).findFirst().get().getTerritoryCode());
		} 
		else {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + openLPolicy.getDwelling().getNumOfFamilies());
		}

		TestData ppcData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getDwelling().getPpcValue());

		//Wildfire score should be returned from reports, UI field is disabled
		//TestData wildfireScoreData = DataProviderFactory.dataOf(
		//		HomeCaMetaData.PropertyInfoTab.FireReport.WILDFIRE_SCORE.getLabel(), openLPolicy.getDwelling().getFirelineScore());

		TestData propertyValueData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), coverageA,
				HomeCaMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST.getLabel(), coverageA.multiply(0.85),
				HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), "Mortgagee requirements");

		TestData constructionData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getDwelling().getAgeOfHome()).getYear(),
				HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), "contains=" + openLPolicy.getDwelling().getConstructionType());

		TestData theftProtectiveDeviceData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.LOCAL_THEFT_ALARM.getLabel(), "Local".equals(openLPolicy.getDwelling().getBurglarAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.CENTRAL_THEFT_ALARM.getLabel(), "Central".equals(openLPolicy.getDwelling().getBurglarAlarmType()));

		List<TestData> detachedStructuresData = getDetachedStructuresData(openLPolicy);

		List<TestData> claimHistoryData = getClaimsHistoryData(openLPolicy, openLPolicy.getExpClaimPoints(), openLPolicy.getClaimPoints());

		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData,
				//HomeCaMetaData.PropertyInfoTab.FIRE_REPORT.getLabel(), wildfireScoreData,
				HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
				HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData,
				HomeCaMetaData.PropertyInfoTab.DETACHED_STRUCTURES.getLabel(), detachedStructuresData,
				HomeCaMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceData,
				HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(), claimHistoryData);
	}

	private List<TestData> getDetachedStructuresData(HomeCaHO3OpenLPolicy openLPolicy) {
		List<TestData> detachedStructuresDataList = new ArrayList<>();
		TestData detachedStructure;
		boolean isFirstStructure = true;

		for (HomeCaHO3OpenLForm form : openLPolicy.getForms()) {
			if ("HO-40".equals(form.getFormCode())) {
				if (isFirstStructure) {
					detachedStructure = DataProviderFactory.dataOf(
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY.getLabel(), "Yes");
					detachedStructure.adjust(getRentedStructure(form));
					isFirstStructure = false;
				} else {
					detachedStructure = getRentedStructure(form);
				}
				detachedStructuresDataList.add(detachedStructure);
			}
			if ("HO-48".equals(form.getFormCode())) {
				if (isFirstStructure) {
					detachedStructure = DataProviderFactory.dataOf(
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY.getLabel(), "Yes");
					detachedStructure.adjust(getNotRentedStructure(form));
					isFirstStructure = false;
				} else {
					detachedStructure = getNotRentedStructure(form);
				}
				detachedStructuresDataList.add(detachedStructure);
			}
		}
		return detachedStructuresDataList;
	}

	private TestData getRentedStructure(HomeCaHO3OpenLForm form) {
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DetachedStructures.RENTED_TO_OTHERS.getLabel(), "Yes",
				HomeCaMetaData.PropertyInfoTab.DetachedStructures.DESCRIPTION.getLabel(), "Description",
				HomeCaMetaData.PropertyInfoTab.DetachedStructures.LIMIT_OF_LIABILITY.getLabel(), new Dollar(form.getLimit()).toString(),
				HomeCaMetaData.PropertyInfoTab.DetachedStructures.NUMBER_OF_FAMILY_UNITS.getLabel(), form.getNumOfFamilies().toString(),
				HomeCaMetaData.PropertyInfoTab.DetachedStructures.NUMBER_OF_OCCUPANTS.getLabel(), "index=2");
	}

	private TestData getNotRentedStructure(HomeCaHO3OpenLForm form) {
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DetachedStructures.RENTED_TO_OTHERS.getLabel(), "No",
				HomeCaMetaData.PropertyInfoTab.DetachedStructures.DESCRIPTION.getLabel(), "Description",
				HomeCaMetaData.PropertyInfoTab.DetachedStructures.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString());
	}

	private List<TestData> getClaimsHistoryData(HomeCaHO3OpenLPolicy openLPolicy, Integer aaaClaimPoints, Integer notAaaClaimPoints) {
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

	private TestData addClaimData(HomeCaHO3OpenLPolicy openLPolicy, boolean isFirstClaim) {
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

	private TestData getEndorsementTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData endorsementData = new SimpleDataProvider();
		for (HomeCaHO3OpenLForm openLForm : openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!"premium".equals(formCode)) {
				if (!endorsementData.containsKey(HomeCaHO3FormTestDataGenerator.getFormMetaKey(formCode))) {
					List<TestData> tdList = HomeCaHO3FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
					if (tdList != null) {
						TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeCaHO3FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeCaHO3FormTestDataGenerator.getFormMetaKey(formCode), tdList);
						endorsementData.adjust(td);
					}
				}
			}
		}

		if (Boolean.FALSE.equals(openLPolicy.getHasPolicySupportingForm())) {
			List<TestData> tdList = HomeCaHO3FormTestDataGenerator.getFormTestData(openLPolicy, "HO-29");
			endorsementData.adjust(DataProviderFactory.dataOf(HomeCaHO3FormTestDataGenerator.getFormMetaKey("HO-29"), tdList));
		}

		return endorsementData;
	}

	private TestData getPersonalPropertyTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData personalPropertyTabData = new SimpleDataProvider();

		for (HomeCaHO3OpenLForm form : openLPolicy.getForms()) {
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
						horsepower = "50"; 
						length_inches = "192";
					}
					if (form.getFormClass().equals("Class 2")) {
						horsepower = "52"; 
						length_inches = "192";
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
	
	private String getBoatType(HomeCaHO3OpenLForm form) {
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

	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		//Coverage A is disabled on Premiums & Coverges Quote tab
		//Double covA = openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covC = openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covD = openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covE = openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();

		return DataProviderFactory.dataOf(
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), covC.toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), covD.toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), new Dollar(covE).toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), getDeductibleValueByForm(openLPolicy));
	}

	private String getDeductibleValueByForm(HomeCaHO3OpenLPolicy openLPolicy) {
		String deductible = "index=1";

		for (HomeCaHO3OpenLForm form : openLPolicy.getForms()) {
			if (form.getFormCode().contains("HO-57")) {
				deductible = "contains=" + new Dollar(100).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-59")) {
				deductible = "contains=" + new Dollar(500).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-60")) {
				deductible = "contains=" + new Dollar(1000).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-76")) {
				deductible = "contains=" + new Dollar(1500).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-77")) {
				deductible = "contains=" + new Dollar(2000).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-78")) {
				deductible = "contains=" + new Dollar(2500).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-79")) {
				deductible = "contains=" + new Dollar(3000).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-80")) {
				deductible = "contains=" + new Dollar(4000).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-81")) {
				deductible = "contains=" + new Dollar(5000).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-82")) {
				deductible = "contains=" + new Dollar(7500).toString().split("\\.")[0];
			} else if (form.getFormCode().contains("HO-177")) {
				deductible = "contains=Theft";
			}
		}
		return deductible;
	}
}
