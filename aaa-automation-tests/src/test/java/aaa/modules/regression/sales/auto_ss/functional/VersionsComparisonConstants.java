package aaa.modules.regression.sales.auto_ss.functional;

import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

public class VersionsComparisonConstants {

	//fields that have DB values, but not UI values on Comparison page
	static final Map<String, String> COMPARISON_PAGE_DIFFERENT_VALUES = ImmutableMap.<String, String>builder()
			.put("Membership Override", "Override")
			.put("Arizonas Best Choice Insurance - 500001406","500001406")
			.put("Advantage Auto Ins Agency, LLC - Magnolia - 500023745","500023745")
			.put("Independent Agents","2514")
			.put("SubProducer","2515")
			.put("Zilliah J Wade","500012749")
			.put("HouseAgent Magnolia","500034992")
			.build();

	//TODO refactor it by taking values from UI
	//values that we don't have in Test Date, but that are used in comparison
	static final Multimap<String, String> PREDEFINED_EXPECTED_VALUES = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			//TODO should be deleted/updated when Report tab is fixed
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Residential Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Prior Address", "VII prior address 1, VII prior address 2, Red Rock, AZ, 85245", "VI prior address 1, VI prior address 2, Phoenix, AZ, 85085")
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Mailing Address", "VII mailing address 1, VII mailing address 2, Red Rock, AZ, 85245", "VI mailing address 1, VI mailing address 2, Phoenix, AZ, 85085")
			.putAll("Drivers.VIIFirstName VII VIILastName.Age", "57","55")
			.putAll("Vehicles.2003, MERCEDES-BENZ, SL500R.Garaging Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//.putAll("Vehicle Information (2008, ACURA, MDX, WAGON 4 DOOR).Garaging Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//AAA Product Owned
			.putAll("Reports.AAA Membership report.Member Since Date", "2005-01-01", "")
			.putAll("Reports.AAA Membership report.Order Date", "","")
			.putAll("Reports.AAA Membership report.Receipt Date", "", "")
			.putAll("Reports.AAA Membership report.Status", "Active","")
			//Current Currier Information
			.putAll("Current Carrier Information.Months with Carrier","12","4")
			//Policy Information
			.putAll("Policy Information.Override ASD Level","false","true")
			.putAll("Policy Information.Agency of Record","500023745","500001406")
			.putAll("Policy Information.Agent of Record","500034992","500012749")
			.putAll("Policy Information.Agent Number","500034992","500012749")
			//Driver Tab
			//data gather data
			.putAll("Drivers.VIFirstName VI VILastName.Age", "18","55")
			.putAll("Drivers.VIFirstName VI VILastName.Total Years Driving Experience", "3","39")
			.putAll("Drivers.VIFirstName VI VILastName.Smart Driver Course Completed?", "true","false")
			.putAll("Drivers.VIFirstName VI VILastName.Defensive Driver Course Completed?", "No","Yes")
			.putAll("Drivers.VIFirstName VI VILastName.ADB Coverage", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Financial Responsibility Filing Needed", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Form Type", "","SR22")
			.putAll("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Claim Points","0","")
			.putAll("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Violation points","","0")
			.putAll("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Include in Points and/or Tier?","true","false")
			//endorsement/renewal data
			.putAll("Drivers.NBFirstName NB NBLastName.Age", "18","55")
			.putAll("Drivers.NBFirstName NB NBLastName.Total Years Driving Experience", "3","39")
			.putAll("Drivers.NBFirstName NB NBLastName.Smart Driver Course Completed?", "true","false")
			.putAll("Drivers.NBFirstName NB NBLastName.Defensive Driver Course Completed?", "No","Yes")
			.putAll("Drivers.NBFirstName NB NBLastName.ADB Coverage", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Financial Responsibility Filing Needed", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Form Type", "","SR22")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Claim Points","0","")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Violation points","","0")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Include in Points and/or Tier?","true","false")
			//Vehicle Information
			.putAll("Vehicles.1998, DODGE, CARAVAN.Year","1998","2003")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Make","DODG","MERZ")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Model","CARAVAN","SL500R")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Series","GRAND CARAVAN SE/SPORT","SL500R")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Body Style","SPORT VAN","ROADSTER")
			.putAll("Vehicles.1998, DODGE, CARAVAN.BI Symbol", "AX","AD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.PD Symbol", "AX","AD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.UM Symbol", "AX","AD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.MP Symbol", "AX","AD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Comp Symbol", "12","60")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Coll Symbol", "12","60")
			//TODO should be return to TD when True/False vs Yes/No is fixed
			.putAll("Vehicles.1998, DODGE, CARAVAN.Salvaged?", "true","false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Is the vehicle used in any commercial business operations?", "","false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Air Bags","Both Front","Both Front and Side")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Alternative Fuel Vehicle","true","false")
			//Usage Based Insurance
			.putAll("Vehicles.1998, DODGE, CARAVAN.Enroll in Usage Based Insurance?", "false","true")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Vehicle Eligibility Response", "","Vehicle Eligible")
			.putAll("Vehicles.1998, DODGE, CARAVAN.AAA UBI Device Status", "","Active")
			//TODO should be returned when dates are fixed
			.putAll("Vehicles.1998, DODGE, CARAVAN.AAA UBI Device Status Date", "08/15/2018","01/08/2015")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Device Voucher Number", "","1111113")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Safety Score", "","15")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Safety Score Date", "08/15/2018","01/09/2015")
			//Garaging address
			.putAll("Vehicles.1998, DODGE, CARAVAN.Is Garaging different from Residential?", "true", "false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Garaging Address", "VII garaging address 1, VII garaging address 2, Phoenix, AZ, 85003","VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//Ownership address
			.putAll("Vehicles.1998, DODGE, CARAVAN.Ownership Type", "Financed","Owned")
			.putAll("Vehicles.1998, DODGE, CARAVAN.First Name", "LI011","")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Vehicle Ownership Address", "674, MINNEAPOLIS, MN, 55440-0674","")
			//Additional Interest Information
			.putAll("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version1, Second Name version1).First Name", "First Name version2","First Name version1")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version1, Second Name version1).Second Name", "Second Name version2","Second Name version1")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version1, Second Name version1).Additional Interest Address", "VII interest address 1, VII interest address 2, Red Rock, AZ, 85245","interest address 1, VI interest address 2, Phoenix, AZ, 85085")
			.putAll("Policy Options.Payment Plan","semiAnnual6SS","annualSS")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> UI_FIELDS_TO_TD_MAPPING = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap PREDEFINED_EXPECTED_VALUES)
			.put("Drivers.VIIFirstName VII VIILastName.First Name", "First Name")
			.put("Drivers.VIIFirstName VII VIILastName.Middle Name", "Middle Name")
			.put("Drivers.VIIFirstName VII VIILastName.Last Name", "Last Name")
			.put("Drivers.VIIFirstName VII VIILastName.Suffix", "Suffix")
			.put("Drivers.VIIFirstName VII VIILastName.Date of Birth", "Date of Birth")
			.put("Drivers.VIIFirstName VII VIILastName.Base Date", "Base Date")
			.put("Drivers.VIIFirstName VII VIILastName.Date First Licensed", "Date First Licensed")
			.put("Drivers.VIIFirstName VII VIILastName.Total Years Driving Experience", "Total Years Driving Experience")
			.put("Named Insureds.VIIFirstName VII VIILastName.Prefix", "Prefix")
			.put("Named Insureds.VIIFirstName VII VIILastName.First Name", "First Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Middle Name", "Middle Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Last Name", "Last Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Suffix", "Suffix")
			.put("Named Insureds.VIIFirstName VII VIILastName.Social Security Number", "Social Security Number")
			.put("Named Insureds.VIIFirstName VII VIILastName.Insured Date of Birth", "Insured Date of Birth")
			.put("Named Insureds.VIIFirstName VII VIILastName.Base Date", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName.Move-In Date", "Move-In Date")
			.put("Named Insureds.VIIFirstName VII VIILastName.Residence", "Residence")
			//AAA Product Owned
			.put("AAA Products Owned.Current AAA Member", "Current AAA Member")
			.put("AAA Products Owned.Membership Number", "Membership Number")
			//.put("AAA Products Owned.Last name", "Last name")
			//TODO Should be return after implementation story - Clean up for AAA Product Owned
			/*			.put("AAA Products Owned.Override Type", "Override Type")
						.put("AAA Products Owned.Member Since Date", "Member Since Date")*/
			.put("AAA Products Owned.Motorcycle Policy #", "Motorcycle Policy #")
			.put("AAA Products Owned.Life Policy #", "Life Policy #")
			.put("AAA Products Owned.Home Policy #", "Home Motorcycle Policy #")
			.put("AAA Products Owned.Renters Policy #", "Renters Policy #")
			.put("AAA Products Owned.Condo Policy #", "Condo Policy #")
			.put("AAA Products Owned.PUP Policy #", "PUP Motorcycle Policy #")
			.put("Reports.AAA Membership report.Membership No.", "Membership Number")
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
			.put("Named Insureds.VIFirstName VI VILastName.Insured Date of Birth", "Insured Date of Birth")
			.put("Drivers.VIFirstName VI VILastName.Date of Birth", "Date of Birth")
			.put("Drivers.VIFirstName VI VILastName.Gender", "Gender")
			.put("Drivers.VIFirstName VI VILastName.Marital Status", "Marital Status")
			.put("Drivers.VIFirstName VI VILastName.Occupation", "Occupation")
			.put("Drivers.VIFirstName VI VILastName.License Type", "License Type")
			.put("Drivers.VIFirstName VI VILastName.License State", "License State")
			.put("Drivers.VIFirstName VI VILastName.License Number", "License Number")
			.put("Drivers.VIFirstName VI VILastName.Age First Licensed", "Age First Licensed")
			.put("Drivers.VIFirstName VI VILastName.Affinity Group", "Affinity Group")
			.put("Drivers.VIFirstName VI VILastName.Most Recent GPA", "Most Recent GPA")
			.put("Drivers.VIFirstName VI VILastName.Smart Driver Course Completion Date", "Smart Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName.Smart Driver Course Certificate Number", "Smart Driver Course Certificate Number")
			.put("Drivers.VIFirstName VI VILastName.Defensive Driver Course Completed?", "Defensive Driver Course Completed?")
			.put("Drivers.VIFirstName VI VILastName.Defensive Driver Course Completion Date", "Defensive Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName.Defensive Driver Course Certificate Number", "Defensive Driver Course Certificate Number")
			.put("Drivers.VIFirstName VI VILastName.Filing State", "Filing State")
			.put("Drivers.VIFirstName VI VILastName.Date First Licensed", "Date First Licensed")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Type", "Type")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Description", "Description")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Occurrence Date", "Occurrence Date")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Loss Payment Amount", "Loss Payment Amount")
			//TODO should be returned after implementing story for Clean up for Activity Information
			//.put("Activity Information (Hit and Run, 05/10/2020, Not included in Rating).Include in Points and/or Tier?", "Include in Points and/or Tier?")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Not Included in Points and/or Tier - Reason Codes", "Not Included in Points and/or Tier - Reason Codes")
			//endorsement/renewal data
			.put("Named Insureds.NBFirstName NB NBLastName.Insured Date of Birth", "Insured Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName.Date of Birth", "Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName.Gender", "Gender")
			.put("Drivers.NBFirstName NB NBLastName.Marital Status", "Marital Status")
			.put("Drivers.NBFirstName NB NBLastName.Occupation", "Occupation")
			.put("Drivers.NBFirstName NB NBLastName.License Type", "License Type")
			.put("Drivers.NBFirstName NB NBLastName.License State", "License State")
			.put("Drivers.NBFirstName NB NBLastName.License Number", "License Number")
			.put("Drivers.NBFirstName NB NBLastName.Age First Licensed", "Age First Licensed")
			.put("Drivers.NBFirstName NB NBLastName.Affinity Group", "Affinity Group")
			.put("Drivers.NBFirstName NB NBLastName.Most Recent GPA", "Most Recent GPA")
			.put("Drivers.NBFirstName NB NBLastName.Smart Driver Course Completion Date", "Smart Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName.Smart Driver Course Certificate Number", "Smart Driver Course Certificate Number")
			.put("Drivers.NBFirstName NB NBLastName.Defensive Driver Course Completed?", "Defensive Driver Course Completed?")
			.put("Drivers.NBFirstName NB NBLastName.Defensive Driver Course Completion Date", "Defensive Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName.Defensive Driver Course Certificate Number", "Defensive Driver Course Certificate Number")
			.put("Drivers.NBFirstName NB NBLastName.Filing State", "Filing State")
			.put("Drivers.NBFirstName NB NBLastName.Date First Licensed", "Date First Licensed")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Type", "Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Description", "Description")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Occurrence Date", "Occurrence Date")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Loss Payment Amount", "Loss Payment Amount")
			//TODO should be returned after implementing story for Clean up for Activity Information
			//.put("Activity Information (Hit and Run, 05/10/2020, Not included in Rating).Include in Points and/or Tier?", "Include in Points and/or Tier?")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating).Not Included in Points and/or Tier - Reason Codes", "Not Included in Points and/or Tier - Reason Codes")			//Vehicle Information
			.put("Vehicles.1998, DODGE, CARAVAN.Usage", "Usage")
			.put("Vehicles.1998, DODGE, CARAVAN.VIN", "VIN")
			.put("Vehicles.1998, DODGE, CARAVAN.Existing Damage", "Existing Damage")
			.put("Vehicles.1998, DODGE, CARAVAN.Existing Damage Description", "Existing Damage Description")
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged", "Salvaged")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "Is the vehicle used in any commercial business operations?")*/
			.put("Vehicles.1998, DODGE, CARAVAN.Business Use Description", "Business Use Description")
			.put("Vehicles.1998, DODGE, CARAVAN.Miles One-way to Work or School", "Miles One-way to Work or School")
			.put("Vehicles.1998, DODGE, CARAVAN.Anti-theft", "Anti-theft")
			//Usage Based Insurance
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Usage Based Insurance Program.Enroll in Usage Based Insurance?", "Enroll in Usage Based Insurance?")*/
			//Garaging address
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is Garaging different from Residential?", "Is Garaging different from Residential?")*/
			.put("Policy Options.Policy Term","Policy Term")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for data gather comparison
	static final Multimap<String, String> DATA_GATHER_NAMED_INSURED_INFORMATION = ImmutableListMultimap.<String, String>builder()
			//TODO should be deleted/updated when Report tab is fixed
			//start
//			.put("AAA Claims Report Order", "First Name")
//			.put("AAA Credit History Order", "Order Date")
//			.put("AAAMvr Report Order", "Receipt Date")
//			.put("AAACredit Score Info", "First Name")
			//end
			.put("Named Insureds.VIIFirstName VII VIILastName", "Prefix")
			.put("Named Insureds.VIIFirstName VII VIILastName", "First Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Middle Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Last Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Suffix")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Social Security Number")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Insured Date of Birth")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Residential Address")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Move-In Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Prior Address")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Mailing Address")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Residence")
			.put("Drivers.VIIFirstName VII VIILastName", "First Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Middle Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Last Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Suffix")
			.put("Drivers.VIIFirstName VII VIILastName", "Date of Birth")
			.put("Drivers.VIIFirstName VII VIILastName", "Age")
			.put("Drivers.VIIFirstName VII VIILastName", "Base Date")
			.put("Drivers.VIIFirstName VII VIILastName", "Date First Licensed")
			.put("Drivers.VIIFirstName VII VIILastName", "Total Years Driving Experience")
			.put("Vehicles.2003, MERCEDES-BENZ, SL500R", "Garaging Address")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for endorsement/renewal comparison
	public static final Multimap<String, String> ENDORSEMENT_RENEWAL_NAMED_INSURED_INFORMATION;
	static {
		Multimap<String, String> endorsementModified = ArrayListMultimap.create(DATA_GATHER_NAMED_INSURED_INFORMATION);
		endorsementModified.remove("Vehicles.2003, MERCEDES-BENZ, SL500R", "Garaging Address");
		ENDORSEMENT_RENEWAL_NAMED_INSURED_INFORMATION = ImmutableListMultimap.copyOf(endorsementModified);
	}

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> AAA_PRODUCT_OWNED = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Current AAA Member")
			//TODO should be returned after Clean up stories for AAA Product Owned section
/*			.put("AAA Products Owned", "Override Type")
			.put("AAA Products Owned", "Member Since Date")*/
			.put("AAA Products Owned", "Membership Number")
			.put("AAA Products Owned", "Motorcycle Policy #")
			.put("AAA Products Owned", "Life Policy #")
			.put("AAA Products Owned", "Home Policy #")
			.put("AAA Products Owned", "Renters Policy #")
			.put("AAA Products Owned", "Condo Policy #")
			.put("AAA Products Owned", "PUP Policy #")
			.put("Reports.AAA Membership report", "Membership No.")
			.put("Reports.AAA Membership report", "Member Since Date")
			.put("Reports.AAA Membership report", "Order Date")
			.put("Reports.AAA Membership report", "Receipt Date")
			.put("Reports.AAA Membership report", "Status")
			.build();

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> CONTACT_INFORMATION = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.put("Contact Information", "Email")
			.build();

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> CURRENT_CARRIER_INFORMATION = ImmutableListMultimap.<String, String>builder()
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
	static final Multimap<String, String> POLICY_INFORMATION = ImmutableListMultimap.<String, String>builder()
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
	static final Multimap<String, String> DATA_GATHER_DRIVER_INFORMATION = ImmutableListMultimap.<String, String>builder()
			.put("Named Insureds.VIFirstName VI VILastName", "Insured Date of Birth")
			.put("Drivers.VIFirstName VI VILastName", "Date of Birth")
			.put("Drivers.VIFirstName VI VILastName", "Age")
			.put("Drivers.VIFirstName VI VILastName", "Gender")
			.put("Drivers.VIFirstName VI VILastName", "Marital Status")
			.put("Drivers.VIFirstName VI VILastName", "Occupation")
			.put("Drivers.VIFirstName VI VILastName", "License Type")
			.put("Drivers.VIFirstName VI VILastName", "License State")
			.put("Drivers.VIFirstName VI VILastName", "License Number")
			.put("Drivers.VIFirstName VI VILastName", "Age First Licensed")
			.put("Drivers.VIFirstName VI VILastName", "Date First Licensed")
			.put("Drivers.VIFirstName VI VILastName", "Total Years Driving Experience")
			.put("Drivers.VIFirstName VI VILastName", "Affinity Group")
			.put("Drivers.VIFirstName VI VILastName", "Most Recent GPA")
			.put("Drivers.VIFirstName VI VILastName", "Smart Driver Course Completed?")
			.put("Drivers.VIFirstName VI VILastName", "Smart Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName", "Smart Driver Course Certificate Number")
			.put("Drivers.VIFirstName VI VILastName", "Defensive Driver Course Completed?")
			.put("Drivers.VIFirstName VI VILastName", "Defensive Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName", "Defensive Driver Course Certificate Number")
			.put("Drivers.VIFirstName VI VILastName", "ADB Coverage")
			.put("Drivers.VIFirstName VI VILastName", "Financial Responsibility Filing Needed")
			.put("Drivers.VIFirstName VI VILastName", "Form Type")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Type")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Description")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Occurrence Date")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Loss Payment Amount")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Claim Points")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Violation points")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Include in Points and/or Tier?")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Not Included in Points and/or Tier - Reason Codes")
			.build();

	//all components/attributes that should be on Comparison page for Driver Information section
	static final Multimap<String, String> ENDORSEMENT_RENEWAL_DRIVER_INFORMATION = ImmutableListMultimap.<String, String>builder()
			.put("Named Insureds.NBFirstName NB NBLastName", "Insured Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName", "Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName", "Age")
			.put("Drivers.NBFirstName NB NBLastName", "Gender")
			.put("Drivers.NBFirstName NB NBLastName", "Marital Status")
			.put("Drivers.NBFirstName NB NBLastName", "Occupation")
			.put("Drivers.NBFirstName NB NBLastName", "License Type")
			.put("Drivers.NBFirstName NB NBLastName", "License State")
			.put("Drivers.NBFirstName NB NBLastName", "License Number")
			.put("Drivers.NBFirstName NB NBLastName", "Age First Licensed")
			.put("Drivers.NBFirstName NB NBLastName", "Date First Licensed")
			.put("Drivers.NBFirstName NB NBLastName", "Total Years Driving Experience")
			.put("Drivers.NBFirstName NB NBLastName", "Affinity Group")
			.put("Drivers.NBFirstName NB NBLastName", "Most Recent GPA")
			.put("Drivers.NBFirstName NB NBLastName", "Smart Driver Course Completed?")
			.put("Drivers.NBFirstName NB NBLastName", "Smart Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName", "Smart Driver Course Certificate Number")
			.put("Drivers.NBFirstName NB NBLastName", "Defensive Driver Course Completed?")
			.put("Drivers.NBFirstName NB NBLastName", "Defensive Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName", "Defensive Driver Course Certificate Number")
			.put("Drivers.NBFirstName NB NBLastName", "ADB Coverage")
			.put("Drivers.NBFirstName NB NBLastName", "Financial Responsibility Filing Needed")
			.put("Drivers.NBFirstName NB NBLastName", "Form Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Description")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Occurrence Date")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Loss Payment Amount")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Claim Points")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Violation points")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Include in Points and/or Tier?")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and Run, 07/20/2018, Not included in Rating)", "Not Included in Points and/or Tier - Reason Codes")
			.build();

	//all components/attributes that should be on Comparison page for Vehicle Information section
	static final Multimap<String, String> DATA_GATHER_VEHICLE_INFORMATION = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information section
			.put("Vehicles.1998, DODGE, CARAVAN", "Usage")
			.put("Vehicles.1998, DODGE, CARAVAN", "VIN")
			.put("Vehicles.1998, DODGE, CARAVAN", "Year")
			.put("Vehicles.1998, DODGE, CARAVAN", "Make")
			.put("Vehicles.1998, DODGE, CARAVAN", "Model")
			.put("Vehicles.1998, DODGE, CARAVAN", "Series")
			.put("Vehicles.1998, DODGE, CARAVAN", "Body Style")
			.put("Vehicles.1998, DODGE, CARAVAN", "BI Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "PD Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "UM Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "MP Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Comp Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Coll Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Existing Damage")
			.put("Vehicles.1998, DODGE, CARAVAN", "Existing Damage Description")
			.put("Vehicles.1998, DODGE, CARAVAN", "Miles One-way to Work or School")
			.put("Vehicles.1998, DODGE, CARAVAN", "Is the vehicle used in any commercial business operations?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Business Use Description")
			.put("Vehicles.1998, DODGE, CARAVAN", "Salvaged?")

			.put("Vehicles.1998, DODGE, CARAVAN", "Air Bags")
			.put("Vehicles.1998, DODGE, CARAVAN", "Anti-theft")
			.put("Vehicles.1998, DODGE, CARAVAN", "Alternative Fuel Vehicle")
			//Usage Based Insurance section
			.put("Vehicles.1998, DODGE, CARAVAN", "Enroll in Usage Based Insurance?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Vehicle Eligibility Response")
			.put("Vehicles.1998, DODGE, CARAVAN", "AAA UBI Device Status")
			.put("Vehicles.1998, DODGE, CARAVAN", "AAA UBI Device Status Date")
			.put("Vehicles.1998, DODGE, CARAVAN", "Device Voucher Number")
			.put("Vehicles.1998, DODGE, CARAVAN", "Safety Score")
			.put("Vehicles.1998, DODGE, CARAVAN", "Safety Score Date")
			//Garaging address
			.put("Vehicles.1998, DODGE, CARAVAN", "Is Garaging different from Residential?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Garaging Address")
			//Ownership address
			.put("Vehicles.1998, DODGE, CARAVAN", "Ownership Type")
			.put("Vehicles.1998, DODGE, CARAVAN", "First Name")
			.put("Vehicles.1998, DODGE, CARAVAN", "Vehicle Ownership Address")
			//Additional Interest Information
			.put("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version1, Second Name version1)", "First Name")
			.put("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version1, Second Name version1)", "Second Name")
			.put("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version1, Second Name version1)", "Additional Interest Address")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for endorsement/renewal comparison
	public static final Multimap<String, String> ENDORSEMENT_RENEWAL_VEHICLE_INFORMATION;
	static {
		Multimap<String, String> endorsementModified = ArrayListMultimap.create(DATA_GATHER_VEHICLE_INFORMATION);
		endorsementModified.remove("Vehicles.1998, DODGE, CARAVAN", "Vehicle Eligibility Response");
		endorsementModified.remove("Vehicles.1998, DODGE, CARAVAN", "AAA UBI Device Status");
		endorsementModified.remove("Vehicles.1998, DODGE, CARAVAN", "AAA UBI Device Status Date");
		endorsementModified.remove("Vehicles.1998, DODGE, CARAVAN", "Device Voucher Number");
		endorsementModified.remove("Vehicles.1998, DODGE, CARAVAN", "Safety Score");
		endorsementModified.remove("Vehicles.1998, DODGE, CARAVAN", "Safety Score Date");
		ENDORSEMENT_RENEWAL_VEHICLE_INFORMATION = ImmutableListMultimap.copyOf(endorsementModified);
	}

	//all components/attributes that should be on Comparison page for Premium and Coverages section for Quote Compare
	static final Multimap<String, String> DATA_GATHER_PREMIUM_AND_COVERAGES = ImmutableListMultimap.<String, String>builder()
			.put("Policy Options","Payment Plan")
			.put("Policy Options", "Policy Term")
			.build();

}