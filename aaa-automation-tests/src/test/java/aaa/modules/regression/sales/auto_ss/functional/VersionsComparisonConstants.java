package aaa.modules.regression.sales.auto_ss.functional;

import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

public class VersionsComparisonConstants {

	//fields that have DB values, but not UI values on Comparison page
	static final Map<String, String> comparisonPageDifferentValues = ImmutableMap.<String, String>builder()
			.put("Membership Override", "Override")
			.put("Arizonas Best Choice Insurance - 500001406","500001406")
			.put("Advantage Auto Ins Agency, LLC - Magnolia - 500023745","500023745")
			.put("Independent Agents","2514")
			.put("SubProducer","2515")
			.put("Zilliah J Wade","500012749")
			.put("HouseAgent Magnolia","500034992")
			.put("A Student","astud")
			.build();

	//TODO refactor it by taking values from UI
	//values that we don't have in Test Date, but that are used in comparison
	static final Multimap<String, String> predefinedExpectedValues = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			//TODO should be deleted/updated when Report tab is fixed
			.putAll("Named Insured Information (VIFirstName VI VILastName).Residential Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			.putAll("Named Insured Information (VIFirstName VI VILastName).Prior Address", "VII prior address 1, VII prior address 2, Red Rock, AZ, 85245", "VI prior address 1, VI prior address 2, Phoenix, AZ, 85085")
			.putAll("Named Insured Information (VIFirstName VI VILastName).Mailing Address", "VII mailing address 1, VII mailing address 2, Red Rock, AZ, 85245", "VI mailing address 1, VI mailing address 2, Phoenix, AZ, 85085")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Garaging Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//.putAll("Vehicle Information (2008, ACURA, MDX, WAGON 4 DOOR).Garaging Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//Current Currier Information
			.putAll("Current Carrier Information.Months with Carrier","12","4")
			//Policy Information
			.putAll("Policy Information.Override ASD Level","false","true")
			.putAll("Policy Information.Agency of Record","500023745","500001406")
			.putAll("Policy Information.Agent of Record","500034992","500012749")
			.putAll("Policy Information.Agent Number","500034992","500012749")
			//Driver Tab
			//data gather data
			.putAll("Driver Information (VIFirstName VI VILastName).Age", "18","55")
			.putAll("Driver Information (VIFirstName VI VILastName).Total Years Driving Experience", "3","39")
			.putAll("Driver Information (VIFirstName VI VILastName).Smart Driver Course Completed?", "true","false")
			.putAll("Driver Information (VIFirstName VI VILastName).Defensive Driver Course Completed?", "No","Yes")
			.putAll("Driver Information (VIFirstName VI VILastName).ADB Coverage", "false","true")
			.putAll("Driver Information (VIFirstName VI VILastName).Financial Responsibility Filing Needed", "false","true")
			.putAll("Driver Information (VIFirstName VI VILastName).Form Type", "","SR22")
			//endorsement/renewal data
			.putAll("Driver Information (NBFirstName NB NBLastName).Age", "18","55")
			.putAll("Driver Information (NBFirstName NB NBLastName).Total Years Driving Experience", "3","39")
			.putAll("Driver Information (NBFirstName NB NBLastName).Smart Driver Course Completed?", "true","false")
			.putAll("Driver Information (NBFirstName NB NBLastName).Defensive Driver Course Completed?", "No","Yes")
			.putAll("Driver Information (NBFirstName NB NBLastName).ADB Coverage", "false","true")
			.putAll("Driver Information (NBFirstName NB NBLastName).Financial Responsibility Filing Needed", "false","true")
			.putAll("Driver Information (NBFirstName NB NBLastName).Form Type", "","SR22")
			.putAll("Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Claim Points","0","")
			.putAll("Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Violation points","","0")
			.putAll("Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Include in Points and/or Tier?","true","false")
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
			//Usage Based Insurance
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Enroll in Usage Based Insurance?", "false","true")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Vehicle Eligibility Response", "","Vehicle Eligible")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).AAA UBI Device Status", "","Active")
			//TODO should be returned when dates are fixed
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).AAA UBI Device Status Date", "08/15/2018","01/08/2015")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Device Voucher Number", "","1111113")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Safety Score", "","15")
			//TODO should be returned when dates are fixed
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Safety Score Date", "08/15/2018","01/09/2015")
			//Garaging address
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is Garaging different from Residential?", "true", "false")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Garaging Address", "VII garaging address 1, VII garaging address 2, Red Rock, AZ, 85245","VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//Ownership address
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Ownership Type", "Financed","Owned")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).First Name", "LI011","")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Vehicle Ownership Address", "VII ownership address 1, VII ownership address 2, Gold Canyon, AZ, 55440-0674","")
			//Additional Interest Information
			.putAll("Additional Interest Information (First Name version1, Second Name version1).First Name", "First Name version2","First Name version1")
			.putAll("Additional Interest Information (First Name version1, Second Name version1).Second Name", "Second Name version2","Second Name version1")
			.putAll("Additional Interest Information (First Name version1, Second Name version1).Additional Interest Address", "interest address 1, VII interest address 2, Red Rock, AZ, 85245","VI interest address 1, VI interest address 2, Phoenix, AZ, 85085")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> uiFieldsToTDMapping = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap predefinedExpectedValues)
			.put("Driver Information (VIFirstName VI VILastName).First Name", "First Name")
			.put("Driver Information (VIFirstName VI VILastName).Middle Name", "Middle Name")
			.put("Driver Information (VIFirstName VI VILastName).Last Name", "Last Name")
			.put("Driver Information (VIFirstName VI VILastName).Suffix", "Suffix")
			.put("Driver Information (VIFirstName VI VILastName).Base Date", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName).Prefix", "Prefix")
			.put("Named Insured Information (VIFirstName VI VILastName).First Name", "First Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Middle Name", "Middle Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Last Name", "Last Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Suffix", "Suffix")
			.put("Named Insured Information (VIFirstName VI VILastName).Social Security Number", "Social Security Number")
			.put("Named Insured Information (VIFirstName VI VILastName).Base Date", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName).Move-In Date", "Move-In Date")
			.put("Named Insured Information (VIFirstName VI VILastName).Residence", "Residence")
			//AAA Product Owned
			.put("AAA Products Owned.Current AAA Member", "Current AAA Member")
			.put("AAA Products Owned.Membership Number", "Membership Number")
			//.put("AAA Products Owned.Last name", "Last name")
			//TODO Should be return after implementation story - Clean up for AAA Product Owned
			/*			.put("AAA Products Owned.Override Type", "Override Type")
						.put("AAA Products Owned.Member Since Date", "Member Since Date")*/
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
			//Driver Tab
			//data gather
			.put("Driver Information (VIFirstName VI VILastName).Date of Birth", "Date of Birth")
			.put("Driver Information (VIFirstName VI VILastName).Gender", "Gender")
			.put("Driver Information (VIFirstName VI VILastName).Marital Status", "Marital Status")
			.put("Driver Information (VIFirstName VI VILastName).Occupation", "Occupation")
			.put("Driver Information (VIFirstName VI VILastName).License Type", "License Type")
			.put("Driver Information (VIFirstName VI VILastName).License State", "License State")
			.put("Driver Information (VIFirstName VI VILastName).License Number", "License Number")
			.put("Driver Information (VIFirstName VI VILastName).Age First Licensed", "Age First Licensed")
			.put("Driver Information (VIFirstName VI VILastName).Affinity Group", "Affinity Group")
			.put("Driver Information (VIFirstName VI VILastName).Most Recent GPA", "Most Recent GPA")
			.put("Driver Information (VIFirstName VI VILastName).Smart Driver Course Completion Date", "Smart Driver Course Completion Date")
			.put("Driver Information (VIFirstName VI VILastName).Smart Driver Course Certificate Number", "Smart Driver Course Certificate Number")
			.put("Driver Information (VIFirstName VI VILastName).Defensive Driver Course Completed?", "Defensive Driver Course Completed?")
			.put("Driver Information (VIFirstName VI VILastName).Defensive Driver Course Completion Date", "Defensive Driver Course Completion Date")
			.put("Driver Information (VIFirstName VI VILastName).Defensive Driver Course Certificate Number", "Defensive Driver Course Certificate Number")
			.put("Driver Information (VIFirstName VI VILastName).Filing State", "Filing State")
			.put("Driver Information (VIFirstName VI VILastName).Date First Licensed", "Date First Licensed")
			//endorsement/renewal data
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
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Type", "Type")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Description", "Description")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Occurrence Date", "Occurrence Date")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Loss Payment Amount", "Loss Payment Amount")
			//TODO should be returned after implementing story for Clean up for Activity Information
			//.put("Activity Information (Hit and Run, 05/10/2020, Not included in Rating).Include in Points and/or Tier?", "Include in Points and/or Tier?")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Not Included in Points and/or Tier - Reason Codes", "Not Included in Points and/or Tier - Reason Codes")
			//Vehicle Information
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Usage", "Usage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).VIN", "VIN")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Existing Damage", "Existing Damage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Existing Damage Description", "Existing Damage Description")
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged", "Salvaged")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "Is the vehicle used in any commercial business operations?")*/
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Business Use Description", "Business Use Description")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Miles One-way to Work or School", "Miles One-way to Work or School")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Anti-theft", "Anti-theft")
			//Usage Based Insurance
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Usage Based Insurance Program.Enroll in Usage Based Insurance?", "Enroll in Usage Based Insurance?")*/
			//Garaging address
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is Garaging different from Residential?", "Is Garaging different from Residential?")*/
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for data gather comparison
	static final Multimap<String, String> dataGatherNamedInsuredInformation = ImmutableListMultimap.<String, String>builder()
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.put("AAA Claims Report Order", "First Name")
			.put("AAA Credit History Order", "Order Date")
			.put("AAAMvr Report Order", "Receipt Date")
			.put("AAACredit Score Info", "First Name")
			//end
			.put("Driver Information (VIFirstName VI VILastName)", "First Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Middle Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Last Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Suffix")
			.put("Driver Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Prefix")
			.put("Named Insured Information (VIFirstName VI VILastName)", "First Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Middle Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Last Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Suffix")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Social Security Number")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Residential Address")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Move-In Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Prior Address")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Mailing Address")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Residence")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Garaging Address")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for endorsement/renewal comparison
	public static final Multimap<String, String> endorsementRenewalNamedInsuredInformation;
	static {
		Multimap<String, String> endorsementModified = ArrayListMultimap.create(dataGatherNamedInsuredInformation);
		endorsementModified.remove("AAA Credit History Order", "Order Date");
		endorsementModified.remove("AAACredit Score Info", "First Name");
		endorsementModified.remove("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Garaging Address");
		endorsementRenewalNamedInsuredInformation = ImmutableListMultimap.copyOf(endorsementModified);
	}

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> aaaProductOwned = ImmutableListMultimap.<String, String>builder()
			.put("AAA Membership Order", "Last Name")
			.put("AAA Credit History Order","Order Date")
			.put("AAA Products Owned", "Current AAA Member")
			//TODO should be returned after Clean up stories for AAA Product Owned section
/*			.put("AAA Products Owned", "Override Type")
			.put("AAA Products Owned", "Member Since Date")*/
			.put("AAA Products Owned", "Membership Number")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.build();

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> contactInformation = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.put("Contact Information", "Email")
			.build();

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> currentCarrierInformation = ImmutableListMultimap.<String, String>builder()
			//TODO Should be return after implementation story - Clean up for Current Carrier
			//.put("Current Carrier Information","More than 6 months Total Insurance Experience")
			.put("Current Carrier Information","Agent Entered Current/Prior Carrier")
			//TODO Should be return after implementation story - Clean up for Current Carrier
			//.put("Current Carrier Information","Agent Entered Other Carrier")
			.put("Current Carrier Information","Agent Entered Inception Date")
			.put("Current Carrier Information","Agent Entered Expiration Date")
			.put("Current Carrier Information","Agent Entered Policy Number")
			.put("Current Carrier Information","Days Lapsed")
			.put("Current Carrier Information","Months with Carrier")
			.put("Current Carrier Information","Agent Entered BI Limits")
			.build();

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> policyInformation = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Source of Business")
			.put("Policy Information","Source Policy #")
			.put("Policy Information","Override ASD Level")
			.put("Policy Information","Advance Shopping Discount Override")
			.put("Policy Information","ASD Overriden By")
			.put("Policy Information","Channel Type")
			.put("Policy Information","Agency")
			.put("Policy Information","Agency of Record")
			.put("Policy Information","Sales Channel")
			.put("Policy Information","Agent")
			.put("Policy Information","Agent of Record")
			.put("Policy Information","Agent Number")
			.put("Policy Information","Authorized by")
			.put("Policy Information","TollFree Number")
			.put("Policy Information","Suppress Print")
			.build();

	//all components/attributes that should be on Comparison page for Driver Information section
	static final Multimap<String, String> dataGatherDriverInformation = ImmutableListMultimap.<String, String>builder()
			.put("AAAADBCoverage", "Automobile Death Benefit")
			.put("AZ_ADBEEndorsement Form", "Automobile Death Benefit")
			.put("AZ_SR22FREndorsement Form", "Automobile Death Benefit")
			.put("Driver Information (VIFirstName VI VILastName)", "Date of Birth")
			.put("Driver Information (VIFirstName VI VILastName)", "Age")
			.put("Driver Information (VIFirstName VI VILastName)", "Gender")
			.put("Driver Information (VIFirstName VI VILastName)", "Marital Status")
			.put("Driver Information (VIFirstName VI VILastName)", "Occupation")
			.put("Driver Information (VIFirstName VI VILastName)", "License Type")
			.put("Driver Information (VIFirstName VI VILastName)", "License State")
			.put("Driver Information (VIFirstName VI VILastName)", "License Number")
			.put("Driver Information (VIFirstName VI VILastName)", "Age First Licensed")
			.put("Driver Information (VIFirstName VI VILastName)", "Date First Licensed")
			.put("Driver Information (VIFirstName VI VILastName)", "Total Years Driving Experience")
			.put("Driver Information (VIFirstName VI VILastName)", "Affinity Group")
			.put("Driver Information (VIFirstName VI VILastName)", "Most Recent GPA")
			.put("Driver Information (VIFirstName VI VILastName)", "Smart Driver Course Completed?")
			.put("Driver Information (VIFirstName VI VILastName)", "Smart Driver Course Completion Date")
			.put("Driver Information (VIFirstName VI VILastName)", "Smart Driver Course Certificate Number")
			.put("Driver Information (VIFirstName VI VILastName)", "Defensive Driver Course Completed?")
			.put("Driver Information (VIFirstName VI VILastName)", "Defensive Driver Course Completion Date")
			.put("Driver Information (VIFirstName VI VILastName)", "Defensive Driver Course Certificate Number")
			.put("Driver Information (VIFirstName VI VILastName)", "ADB Coverage")
			.put("Driver Information (VIFirstName VI VILastName)", "Financial Responsibility Filing Needed")
			.put("Driver Information (VIFirstName VI VILastName)", "Form Type")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Type")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Description")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Occurrence Date")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Loss Payment Amount")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Claim Points")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Violation points")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Include in Points and/or Tier?")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Not Included in Points and/or Tier - Reason Codes")
			.build();

	//all components/attributes that should be on Comparison page for Driver Information section
	static final Multimap<String, String> endorsementRenewalDriverInformation = ImmutableListMultimap.<String, String>builder()
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
			.put("Driver Information (NBFirstName NB NBLastName)", "Form Type")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Type")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Description")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Occurrence Date")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Loss Payment Amount")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Claim Points")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Violation points")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Include in Points and/or Tier?")
			.put("Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Not Included in Points and/or Tier - Reason Codes")
			.build();

	//all components/attributes that should be on Comparison page for Vehicle Information section
	static final Multimap<String, String> dataGatherVehicleInformation = ImmutableListMultimap.<String, String>builder()
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
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Vehicle Eligibility Response")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "AAA UBI Device Status")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "AAA UBI Device Status Date")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Device Voucher Number")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Safety Score")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Safety Score Date")
			//Garaging address
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Is Garaging different from Residential?")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Garaging Address")
			//Ownership address
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Ownership Type")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "First Name")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Vehicle Ownership Address")
			//Additional Interest Information
			.put("Additional Interest Information (First Name version1, Second Name version1)", "First Name")
			.put("Additional Interest Information (First Name version1, Second Name version1)", "Second Name")
			.put("Additional Interest Information (First Name version1, Second Name version1)", "Additional Interest Address")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for endorsement/renewal comparison
	public static final Multimap<String, String> endorsementRenewalVehicleInformation;
	static {
		Multimap<String, String> endorsementModified = ArrayListMultimap.create(dataGatherVehicleInformation);
		endorsementModified.remove("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Vehicle Eligibility Response");
		endorsementModified.remove("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "AAA UBI Device Status");
		endorsementModified.remove("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "AAA UBI Device Status Date");
		endorsementModified.remove("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Device Voucher Number");
		endorsementModified.remove("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Safety Score");
		endorsementModified.remove("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Safety Score Date");
		endorsementRenewalVehicleInformation = ImmutableListMultimap.copyOf(endorsementModified);
	}
}