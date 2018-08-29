package aaa.modules.regression.sales.auto_ca.choice.functional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

public class VersionsConflictConstants {

	private static final String AVAILABLE = "Available";
	private static final String CURRENT = "Current";

	//Values that are not in Test Data, but are used in comparison
	static final Multimap<String, String> predefinedExpectedValues = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			.putAll("Named Insured Information (VIFirstName VI VILastName).Residential Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			.putAll("Named Insured Information (VIIFirstName VII VIILastName).Prior Address", "VI prior address 1, VI prior address 2, Phoenix, AZ, 85085", "VII prior address 1, VII prior address 2, Red Rock, AZ, 85245")
			.putAll("Named Insured Information (VIFirstName VI VILastName).Mailing Address", "VII mailing address 1, VII mailing address 2, Red Rock, AZ, 85245", "VI mailing address 1, VI mailing address 2, Phoenix, AZ, 85085")
			//AAA Products Owned
			.putAll("AAA Products Owned.Override Type","Term","Life")
			//Policy Information
			.putAll("Policy Information.Override ASD Level","true","false")
			.putAll("Policy Information.Agency of Record","500023745","500001406")
			.putAll("Policy Information.Agent of Record","500034992","500012749")
			.putAll("Policy Information.Agent Number","500034992","500012749")
			//Current Currier Information
			.putAll("Current Carrier Information.Months with Carrier","12","4")
			//Driver Tab
			.putAll("Driver Information (NBFirstName NB NBLastName).Age", "18","55")
			.putAll("Driver Information (NBFirstName NB NBLastName).Total Years Driving Experience", "3","39")
			.putAll("Driver Information (NBFirstName NB NBLastName).Smart Driver Course Completed?", "true","false")
			.putAll("Driver Information (NBFirstName NB NBLastName).Defensive Driver Course Completed?", "No","Yes")
			.putAll("Driver Information (NBFirstName NB NBLastName).ADB Coverage", "false","true")
			.putAll("Driver Information (NBFirstName NB NBLastName).Financial Responsibility Filing Needed", "false","true")
			.putAll("Driver Information (NBFirstName NB NBLastName).Filing State", "Utah","Indiana")
			.putAll("Driver Information (NBFirstName NB NBLastName).Form Type", "","SR22")
			//TODO return when Activity section is done
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Claim Points","","0")
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Violation points","0","")
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Include in Points and/or Tier?","false","true")
			//Vehicle Information
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Year","1998","2003")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Make","DODG","MERZ")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Model","CARAVAN","SL500R")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Series","GRAND CARAVAN SE/SPORT","SL500R")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Body Style","SPORT VAN","ROADSTER")
			//TODO should be return to TD when True/False vs Yes/No is fixed
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged?", "true","false")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "","false")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Air Bags","Both Front","Both Front and Side")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Alternative Fuel Vehicle","true","false")
			//Garaging address
			.putAll("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Is Garaging different from Residential?", "false", "true")
			.putAll("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Garaging Address", "NB residence address 1, NB residence address 2, Gold Canyon, AZ, 85118","VII garaging address 1, VII garaging address 2, Red Rock, AZ, 85245")
			//Usage Based Insurance
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Enroll in Usage Based Insurance?", "false","true")
			//Ownership address
/*			.putAll("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Ownership Type", "Financed","Owned")
			.putAll("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).First Name", "LI011","")
			.putAll("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Vehicle Ownership Address", "674, VII ownership address 2, Gold Canyon, AZ, 55440-0674","")*/
			//Additional Interest Information
			.putAll("Additional Interest Information (First Name version2, Second Name version2).First Name", "First Name version1", "First Name version2")
			.putAll("Additional Interest Information (First Name version2, Second Name version2).Second Name", "Second Name version1", "Second Name version2")
			.putAll("Additional Interest Information (First Name version2, Second Name version2).Additional Interest Address", "interest address 1, VI interest address 2, Phoenix, AZ, 85085", "interest address 1, VII interest address 2, Red Rock, AZ, 85245")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> uiFieldsToTDMapping = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap predefinedExpectedValues)
			.put("Driver Information (VIFirstName VI VILastName).First Name", "First Name")
			.put("Driver Information (VIFirstName VI VILastName).Middle Name", "Middle Name")
			.put("Driver Information (VIFirstName VI VILastName).Last Name", "Last Name")
			.put("Driver Information (VIFirstName VI VILastName).Suffix", "Suffix")
			.put("Driver Information (VIFirstName VI VILastName).Base Date", "Base Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName).Prefix", "Prefix")
			.put("Named Insured Information (VIFirstName VI VILastName).First Name", "First Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Middle Name", "Middle Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Last Name", "Last Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Suffix", "Suffix")
			.put("Named Insured Information (VIFirstName VI VILastName).Social Security Number", "Social Security Number")
			.put("Named Insured Information (VIFirstName VI VILastName).Base Date", "Base Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName).Move-In Date", "Move-In Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName).Residence", "Residence")
			//AAA Products Owned section
			.put("AAA Products Owned.Current AAA Member", "Current AAA Member")
			.put("AAA Products Owned.Membership Number", "Membership Number")
			//we verify it as predefined because during NB we have Term as default
			//.put("AAA Products Owned.Override Type", "Override Type")
			.put("AAA Products Owned.Member Since Date", "Member Since Date")
			.put("AAA Products Owned.Policy #", "Motorcycle Policy #")
			.put("AAA Products Owned.Policy #", "Life Policy #")
			.put("AAA Products Owned.Policy #", "Home Motorcycle Policy #")
			.put("AAA Products Owned.Policy #", "Renters Policy #")
			.put("AAA Products Owned.Policy #", "Condo Policy #")
			.put("AAA Products Owned.Policy #", "PUP Motorcycle Policy #")
			//Contact Information
			.put("Contact Information.Home Phone Number", "Home Phone Number")
			.put("Contact Information.Work Phone Number", "Work Phone Number")
			.put("Contact Information.Mobile Phone Number", "Mobile Phone Number")
			.put("Contact Information.Preferred Phone #", "Preferred Phone #")
			.put("Contact Information.Email", "Email")
			//Current Carrier Information
			//TODO Should be return after implementation story - Clean up for Current Carrier
			//.put("Current Carrier Information.More than 6 months Total Insurance Experience","More than 6 months Total Insurance Experience")
			.put("Current Carrier Information.Agent Entered Current/Prior Carrier","Agent Entered Current/Prior Carrier")
			//TODO Should be return after implementation story - Clean up for Current Carrier
			//.put("Current Carrier Information.Agent Entered Other Carrier","Agent Entered Other Carrier")
			.put("Current Carrier Information.Agent Entered Inception Date","Agent Entered Inception Date")
			.put("Current Carrier Information.Agent Entered Expiration Date","Agent Entered Expiration Date")
			.put("Current Carrier Information.Agent Entered Policy Number","Agent Entered Policy Number")
			.put("Current Carrier Information.Days Lapsed","Days Lapsed")
			.put("Current Carrier Information.Months with Carrier","Months with Carrier")
			.put("Current Carrier Information.Agent Entered BI Limits","Agent Entered BI Limits")
			//Policy Information
			.put("Policy Information.Source of Business","Source of Business")
			.put("Policy Information.Source Policy #","Source Policy #")
			//TODO should be returned when Yes/NO vs true/false is fixed
			//.put("Policy Information.Override ASD Level","Override ASD Level")
			.put("Policy Information.Advance Shopping Discount Override","Advance Shopping Discount Override")
			.put("Policy Information.ASD Overriden By","ASD Overriden By")
			.put("Policy Information.Channel Type","Channel Type")
			.put("Policy Information.Agency","Agency")
			.put("Policy Information.Sales Channel","Sales Channel")
			.put("Policy Information.Agent","Agent")
			.put("Policy Information.Authorized by","Authorized by")
			.put("Policy Information.TollFree Number","TollFree Number")
			.put("Policy Information.Suppress Print","Suppress Print")
			//Driver Information
			.put("Driver Information (NBFirstName NB NBLastName).Date of Birth", "Date of Birth")
			.put("Driver Information (NBFirstName NB NBLastName).Gender", "Gender")
			.put("Driver Information (NBFirstName NB NBLastName).Marital Status", "Marital Status")
			.put("Driver Information (NBFirstName NB NBLastName).Occupation", "Occupation")
			.put("Driver Information (NBFirstName NB NBLastName).License Type", "License Type")
			.put("Driver Information (NBFirstName NB NBLastName).License State", "License State")
			.put("Driver Information (NBFirstName NB NBLastName).License Number", "License Number")
			.put("Driver Information (NBFirstName NB NBLastName).Age First Licensed", "Age First Licensed")
			.put("Driver Information (NBFirstName NB NBLastName).Affinity Group", "Affinity Group")
			.put("Driver Information (NBFirstName NB NBLastName).Most Recent GPA", "Most Recent GPA")
			.put("Driver Information (NBFirstName NB NBLastName).Smart Driver Course Completion Date", "Smart Driver Course Completion Date")
			.put("Driver Information (NBFirstName NB NBLastName).Smart Driver Course Certificate Number", "Smart Driver Course Certificate Number")
			.put("Driver Information (NBFirstName NB NBLastName).Defensive Driver Course Completed?", "Defensive Driver Course Completed?")
			.put("Driver Information (NBFirstName NB NBLastName).Defensive Driver Course Completion Date", "Defensive Driver Course Completion Date")
			.put("Driver Information (NBFirstName NB NBLastName).Defensive Driver Course Certificate Number", "Defensive Driver Course Certificate Number")
			.put("Driver Information (NBFirstName NB NBLastName).Filing State", "Filing State")
			.put("Driver Information (NBFirstName NB NBLastName).Date First Licensed", "Date First Licensed")
			//TODO return when Activity section is done
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Type", "Type")
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Description", "Description")
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Occurrence Date", "Occurrence Date")
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Loss Payment Amount", "Loss Payment Amount")
			//TODO should be returned after implementing story for Clean up for Activity Information
			//.put("Activity Information (Hit and Run, 05/10/2020, Not included in Rating).Include in Points and/or Tier?", "Include in Points and/or Tier?")
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Not Included in Points and/or Tier - Reason Codes", "Not Included in Points and/or Tier - Reason Codes")
			//Vehicle Information
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Usage", "Usage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).VIN", "VIN")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Existing Damage", "Existing Damage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Existing Damage Description", "Existing Damage Description")
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged", "Salvaged")
						.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "Is the vehicle used in any commercial business operations?")*/
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Business Use Description", "Business Use Description")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Miles One-way to Work or School", "Miles One-way to Work or School")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Anti-theft", "Anti-theft")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> namedInsuredInformation = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Claims Report Order.First Name", CURRENT)
					.put("AAA Claims Report Order.Last Name", CURRENT)
					.put("AAAMvr Report Order.First Name",CURRENT)
					.put("AAAMvr Report Order.Last Name",CURRENT)
					.put("Driver Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Prefix", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Middle Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Last Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Suffix", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Social Security Number", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Base Date",CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Residential Address", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Move-In Date", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Mailing Address", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Residence", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> namedInsuredInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Prefix")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Move-In Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Prior Address")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Residence")
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> namedInsuredInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.put("AAA Claims Report Order", "First Name")
			.put("AAAMvr Report Order", "First Name")
			//end
			.put("Driver Information (VIFirstName VI VILastName)", "First Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Middle Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Last Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Suffix")
			.put("Driver Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "First Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Middle Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Last Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Suffix")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Social Security Number")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Residential Address")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Mailing Address")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> aaaProductOwnedManual = ArrayListMultimap.create(
					ImmutableListMultimap.<String, String>builder()
							.put("AAA Membership Order.Last Name", CURRENT)
							.put("AAA Membership Order.Membership Number", CURRENT)
							.put("AAA Membership Order.Member Since", CURRENT)
							.put("AAA Membership Order.Order Date", CURRENT)
							.put("AAA Membership Order.Receipt Date", CURRENT)
							.put("AAA Membership Order.Status", CURRENT)
							.put("AAA Membership Order.Membership Total Years", CURRENT)
							.put("AAA Products Owned.Current AAA Member", CURRENT)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.build());

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> aaaProductOwnedAutomatic = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Products Owned.Current AAA Member", CURRENT)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> aaaProductOwnedVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> aaaProductOwnedVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Current AAA Member")
			.put("AAA Products Owned", "Override Type")
			.put("AAA Products Owned", "Member Since Date")
			.put("AAA Products Owned", "Membership Number")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> contactInformation = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAAPolicy Issue Summary.Insured Email", AVAILABLE)
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> contactInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("AAAPolicy Issue Summary", "Insured Email")
			.put("Contact Information", "Email")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> contactInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> currentCarrierInformation = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> currentCarrierInfrmationVersion1 = ImmutableListMultimap.<String, String>builder()
					.build();

	protected static final ArrayListMultimap<String, String> policyInformationManual = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Source of Business", CURRENT)
					.put("Policy Information.Override ASD Level", AVAILABLE)
					.put("Policy Information.Channel Type", CURRENT)
					.put("Policy Information.TollFree Number", AVAILABLE)
					.build());

	protected static final ArrayListMultimap<String, String> policyInformationAutomatic = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Source of Business", CURRENT)
					.put("Policy Information.Advance Shopping Discount Override", AVAILABLE)
					.put("Policy Information.Channel Type", CURRENT)
					.put("Policy Information.TollFree Number", AVAILABLE)
					.build());


	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> policyInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Override ASD Level")
			.put("Policy Information","Advance Shopping Discount Override")
			.put("Policy Information","ASD Overriden By")
			.put("Policy Information","Authorized by")
			.put("Policy Information","TollFree Number")
			.put("Policy Information","Suppress Print")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> policyInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Source of Business")
			.put("Policy Information","Source Policy #")
			.put("Policy Information","Channel Type")
			.put("Policy Information","Agency")
			.put("Policy Information","Agency of Record")
			.put("Policy Information","Sales Channel")
			.put("Policy Information","Agent")
			.put("Policy Information","Agent of Record")
			.put("Policy Information","Agent Number")
			.build();

	//TODO should be deleted when Policy Information.Renewal Term Premium - Old Rater is fixed, RenewalMerge test need to have policyInformationVersion1, not policyInformationRenewal
	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> policyInformationRenewalVersion1= ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Source of Business")
			.put("Policy Information","Source Policy #")
			.put("Policy Information","Renewal Term Premium - Old Rater")
			.put("Policy Information","Channel Type")
			.put("Policy Information","Agency")
			.put("Policy Information","Agency of Record")
			.put("Policy Information","Sales Channel")
			.put("Policy Information","Agent")
			.put("Policy Information","Agent of Record")
			.put("Policy Information","Agent Number")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> driverInformationManual = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Claims Report Order.Date of Birth", AVAILABLE)
					.put("AAA Claims Report Order.License Number", AVAILABLE)
					.put("AAA Claims Report Order.License State", AVAILABLE)
					.put("AAA Claims Report Order.Gender", AVAILABLE)
					.put("AAAADBCoverage.Automobile Death Benefit", CURRENT)
					.put("AAAMvr Report Order.Date of Birth", AVAILABLE)
					.put("AAAMvr Report Order.License Number", AVAILABLE)
					.put("AAAMvr Report Order.License State", AVAILABLE)
					.put("AAAMvr Report Order.Gender", AVAILABLE)
					.put("AZ_ADBEEndorsement Form", CURRENT)
					.put("AZ_SR22FREndorsement Form", CURRENT)
					//TODO return when Activity section is done
/*					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Type", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Description", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Occurrence Date", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Loss Payment Amount", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Claim Points", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Violation points", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Include in Points and/or Tier?", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Not Included in Points and/or Tier - Reason Codes", AVAILABLE)*/
					.put("Driver Information (NBFirstName NB NBLastName).Date of Birth", CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> driverInformationAutomatic = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					//TODO return when Activity section is done
/*					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Type", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Description", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Occurrence Date", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Loss Payment Amount", AVAILABLE)
					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Not Included in Points and/or Tier - Reason Codes", AVAILABLE)*/
					.put("Driver Information (NBFirstName NB NBLastName).Date of Birth", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> driverInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Claims Report Order", "Automobile Death Benefit")
			.put("AAAMvr Report Order", "Automobile Death Benefit")
			//TODO return when Activity section is done
/*			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Type")
			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Description")
			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Occurrence Date")
			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Loss Payment Amount")
			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Claim Points")
			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Violation points")
			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Include in Points and/or Tier?")
			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Not Included in Points and/or Tier - Reason Codes")*/
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> driverInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("AAAADBCoverage", "Automobile Death Benefit")
			.put("AZ_ADBEEndorsement Form", "Automobile Death Benefit")
			.put("AZ_SR22FREndorsement Form", "Automobile Death Benefit")
			.put("Driver Information (NBFirstName NB NBLastName)", "Date of Birth")
			.put("Driver Information (NBFirstName NB NBLastName)", "Age")
			.put("Driver Information (NBFirstName NB NBLastName)", "Gender")
			.put("Driver Information (NBFirstName NB NBLastName)", "Marital Status")
			.put("Driver Information (NBFirstName NB NBLastName)", "Occupation")
			.put("Driver Information (NBFirstName NB NBLastName)", "License Type")
			.put("Driver Information (NBFirstName NB NBLastName)", "License State")
			.put("Driver Information (NBFirstName NB NBLastName)", "License Number")
			.put("Driver Information (NBFirstName NB NBLastName)", "Age First Licensed")
			.put("Driver Information (NBFirstName NB NBLastName)", "Date First Licensed")
			.put("Driver Information (NBFirstName NB NBLastName)", "Total Years Driving Experience")
			.put("Driver Information (NBFirstName NB NBLastName)", "Affinity Group")
			.put("Driver Information (NBFirstName NB NBLastName)", "Most Recent GPA")
			.put("Driver Information (NBFirstName NB NBLastName)", "Smart Driver Course Completed?")
			.put("Driver Information (NBFirstName NB NBLastName)", "Smart Driver Course Completion Date")
			.put("Driver Information (NBFirstName NB NBLastName)", "Smart Driver Course Certificate Number")
			.put("Driver Information (NBFirstName NB NBLastName)", "Defensive Driver Course Completed?")
			.put("Driver Information (NBFirstName NB NBLastName)", "Defensive Driver Course Completion Date")
			.put("Driver Information (NBFirstName NB NBLastName)", "Defensive Driver Course Certificate Number")
			.put("Driver Information (NBFirstName NB NBLastName)", "ADB Coverage")
			.put("Driver Information (NBFirstName NB NBLastName)", "Financial Responsibility Filing Needed")
			.put("Driver Information (NBFirstName NB NBLastName)", "Filing State")
			.put("Driver Information (NBFirstName NB NBLastName)", "Form Type")
			.build();


	protected static final ArrayListMultimap<String, String> vehicleInformationManual = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Additional Interest Information (First Name version2, Second Name version2).First Name", AVAILABLE)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Usage", CURRENT)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Enroll in Usage Based Insurance?", CURRENT)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Garaging Address", AVAILABLE)
					.build());

	protected static final ArrayListMultimap<String, String> vehicleInformationAutomatic = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Additional Interest Information (First Name version2, Second Name version2).First Name", AVAILABLE)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Usage", CURRENT)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Garaging Address", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> vehicleInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN)", "Is Garaging different from Residential?")
			.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN)", "Garaging Address")
			//Additional Interest Information
			.put("Additional Interest Information (First Name version2, Second Name version2)", "First Name")
			.put("Additional Interest Information (First Name version2, Second Name version2)", "Second Name")
			.put("Additional Interest Information (First Name version2, Second Name version2)", "Additional Interest Address")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> vehicleInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information section
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Usage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "VIN")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Year")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Make")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Model")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Series")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Body Style")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Existing Damage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Existing Damage Description")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Salvaged?")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Is the vehicle used in any commercial business operations?")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Business Use Description")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Miles One-way to Work or School")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Air Bags")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Anti-theft")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Alternative Fuel Vehicle")
			//Usage Based Insurance section
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Enroll in Usage Based Insurance?")
			//Ownership address
/*			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Ownership Type")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "First Name")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Vehicle Ownership Address")*/
			.build();

}