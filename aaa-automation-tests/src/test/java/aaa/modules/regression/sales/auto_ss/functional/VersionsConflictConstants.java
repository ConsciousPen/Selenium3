package aaa.modules.regression.sales.auto_ss.functional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

public class VersionsConflictConstants {

	private static final String AVAILABLE = "Available";
	private static final String CURRENT = "Current";

	//Values that are not in Test Data, but are used in comparison
	static final Multimap<String, String> PREDEFINED_EXPECTED_VALUES = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Residential Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Prior Address", "VI prior address 1, VI prior address 2, Phoenix, AZ, 85085", "VII prior address 1, VII prior address 2, Red Rock, AZ, 85245")
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Mailing Address", "VII mailing address 1, VII mailing address 2, Red Rock, AZ, 85245", "VI mailing address 1, VI mailing address 2, Phoenix, AZ, 85085")
			.putAll("Drivers.VIIFirstName VII VIILastName.Age", "57", "55")
			.putAll("Vehicles.2003, MERCEDES-BENZ, SL500R.Garaging Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//AAA Products Owned
			.putAll("AAA Products Owned.Override Type", "Term", "Life")
			.putAll("Reports.AAA Membership report.Member Since Date", "2005-01-01", "")
			.putAll("Reports.AAA Membership report.Order Date", "", "")
			.putAll("Reports.AAA Membership report.Receipt Date", "", "")
			.putAll("Reports.AAA Membership report.Status", "Active", "")
			//Policy Information
			.putAll("Policy Information.Renewal Term Premium - Old Rater", "", "2532")
			.putAll("Policy Information.Override ASD Level", "true", "false")
			.putAll("Policy Information.Agency of Record", "500023745", "500001406")
			.putAll("Policy Information.Agent of Record", "500034992", "500012749")
			.putAll("Policy Information.Agent Number", "500034992", "500012749")
			//Current Currier Information
			.putAll("Current Carrier Information.Months with Carrier", "12", "4")
			//Driver Tab
			.putAll("Drivers.NBFirstName NB NBLastName.Age", "18", "55")
			.putAll("Drivers.NBFirstName NB NBLastName.Total Years Driving Experience", "3", "39")
			.putAll("Drivers.NBFirstName NB NBLastName.Smart Driver Course Completed?", "true", "false")
			.putAll("Drivers.NBFirstName NB NBLastName.Defensive Driver Course Completed?", "No", "Yes")
			.putAll("Drivers.NBFirstName NB NBLastName.ADB Coverage", "false", "true")
			.putAll("Drivers.NBFirstName NB NBLastName.Financial Responsibility Filing Needed", "false", "true")
			.putAll("Drivers.NBFirstName NB NBLastName.Filing State", "Utah", "Indiana")
			.putAll("Drivers.NBFirstName NB NBLastName.Form Type", "", "SR22")
			//TODO return when Activity section is done
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Claim Points","","0")
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Violation points","0","")
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Include in Points and/or Tier?","false","true")
			//Vehicle Information
			.putAll("Vehicles.1998, DODGE, CARAVAN.Year", "1998", "2003")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Make", "DODG", "MERZ")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Model", "CARAVAN", "SL500R")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Series", "GRAND CARAVAN SE/SPORT", "SL500R")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Body Style", "SPORT VAN", "ROADSTER")
			.putAll("Vehicles.1998, DODGE, CARAVAN.BI Symbol", "AX", "AD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.PD Symbol", "AX", "AD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.UM Symbol", "AX", "AD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.MP Symbol", "AX", "AD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Comp Symbol", "12", "60")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Coll Symbol", "12", "60")
			//TODO should be return to TD when True/False vs Yes/No is fixed
			.putAll("Vehicles.1998, DODGE, CARAVAN.Salvaged?", "true", "false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Is the vehicle used in any commercial business operations?", "", "false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Air Bags", "Both Front", "Both Front and Side")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Alternative Fuel Vehicle", "true", "false")
			//Garaging address
			.putAll("Vehicles.1998, DODGE, CARAVAN.Is Garaging different from Residential?", "false", "true")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Garaging Address", "NB residence address 1, NB residence address 2, Gold Canyon, AZ, 85118", "VII garaging address 1, VII garaging address 2, Phoenix, AZ, 85003")
			//Usage Based Insurance
			.putAll("Vehicles.1998, DODGE, CARAVAN.Enroll in Usage Based Insurance?", "false", "true")
			//Ownership address
			.putAll("Vehicles.1998, DODGE, CARAVAN.Ownership Type", "Financed", "Owned")
			.putAll("Vehicles.1998, DODGE, CARAVAN.First Name", "LI011", "")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Vehicle Ownership Address", "674, MINNEAPOLIS, MN, 55440-0674", "")
			//Additional Interest Information
			.putAll("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version2, Second Name version2).First Name", "First Name version1", "First Name version2")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version2, Second Name version2).Second Name", "Second Name version1", "Second Name version2")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version2, Second Name version2).Additional Interest Address", "interest address 1, VI interest address 2, Phoenix, AZ, 85085", "interest address 1, VII interest address 2, Red Rock, AZ, 85245")
			//Add Components
			.putAll("Named Insureds.Insured Principal (VIFirstName VI VILastName)", "Insured Principal (VIFirstName VI VILastName)", "")
			.putAll("Named Insureds.Insured Principal (VIIFirstName VII VIILastName)", "Insured Principal (VIIFirstName VII VIILastName)", "")
			.putAll("Drivers.Driver (New Driver Version1)", "Driver (New Driver Version1)", "")
			.putAll("Drivers.Driver (New Driver Version2)", "Driver (New Driver Version2)", "")
			.putAll("Named Insureds.NBFirstName NB NBLastName.AAA Clue Order", "AAA Clue Order", "")
			.putAll("Vehicles.Vehicle (1998, DODGE, CARAVAN)", "Vehicle (1998, DODGE, CARAVAN)", "")
			.putAll("Vehicles.Vehicle (2003, MERCEDES-BENZ, SL500R)", "Vehicle (2003, MERCEDES-BENZ, SL500R)", "")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Accident (Property Damage Only), 05/25/2018, Not included in Rating)", "Activity Information (Accident (Property Damage Only), 05/25/2018, Not included in Rating)", "")
			.putAll("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information.Are there any additional interest(s)?", "true", "false")
			.putAll("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information.First Name", "First Name version1", "")
			.putAll("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information.Second Name", "Second Name version1", "")
			.putAll("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information.Additional Interest Address", "interest address 1, VI interest address 2, Phoenix, AZ, 85085", "")
			.putAll("Named Insureds.NBFirstName NB NBLastName.AAA Clue Order.Order Date", "", "")
			.putAll("Named Insureds.NBFirstName NB NBLastName.AAA Clue Order.Receipt Date", "", "")
			.putAll("Drivers.New Driver2 Version2.Assigned Vehicle", "2011, CHEVROLET, EXPRESS VAN", "1996, MAZDA, MX-5 MIATA")
			.putAll("Drivers.New Driver3 Version3.Assigned Vehicle", "2011, MERCEDES-BENZ, G55AMG", "2011, CHEVROLET, EXPRESS VAN")
			.putAll("Drivers.Driver (Third Driver Version2)", "", "Driver (Third Driver Version2)")
			.putAll("Drivers.Driver (Second Driver Version1)", "", "Driver (Second Driver Version1)")
			.putAll("Vehicles.Vehicle (2011, CHEVROLET, EXPRESS VAN)", "", "Vehicle (2011, CHEVROLET, EXPRESS VAN)")
			.putAll("Vehicles.Vehicle (2011, MERCEDES-BENZ, G55AMG)", "", "Vehicle (2011, MERCEDES-BENZ, G55AMG)")
			.putAll("Named Insureds.Insured Principal (Second VI Insured)", "", "Insured Principal (Second VI Insured)")
			.putAll("Policy Options.Payment Plan", "semiAnnualSS", "quaterlySS")
			//Atomic merge Contact Information
			.putAll("Contact Information.Home Phone Number", "1111111111", "")
			.putAll("Contact Information.Mobile Phone Number", "", "1111111113")
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
			//AAA Products Owned section
			.put("AAA Products Owned.Current AAA Member", "Current AAA Member")
			.put("AAA Products Owned.Membership Number", "Membership Number")
			//we verify it as predefined because during NB we have Term as default
			//.put("AAA Products Owned.Override Type", "Override Type")
			.put("AAA Products Owned.Member Since Date", "Member Since Date")
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
			.put("Current Carrier Information.Agent Entered Current/Prior Carrier", "Agent Entered Current/Prior Carrier")
			//TODO Should be return after implementation story - Clean up for Current Carrier
			//.put("Current Carrier Information.Agent Entered Other Carrier","Agent Entered Other Carrier")
			.put("Current Carrier Information.Agent Entered Inception Date", "Agent Entered Inception Date")
			.put("Current Carrier Information.Agent Entered Expiration Date", "Agent Entered Expiration Date")
			.put("Current Carrier Information.Agent Entered Policy Number", "Agent Entered Policy Number")
			.put("Current Carrier Information.Days Lapsed", "Days Lapsed")
			.put("Current Carrier Information.Months with Carrier", "Months with Carrier")
			.put("Current Carrier Information.Agent Entered BI Limits", "Agent Entered BI Limits")
			//Policy Information
			.put("Policy Information.Source of Business", "Source of Business")
			.put("Policy Information.Source Policy #", "Source Policy #")
			//TODO should be returned when Yes/NO vs true/false is fixed
			//.put("Policy Information.Override ASD Level","Override ASD Level")
			.put("Policy Information.Advance Shopping Discount Override", "Advance Shopping Discount Override")
			.put("Policy Information.ASD Overriden By", "ASD Overriden By")
			.put("Policy Information.Channel Type", "Channel Type")
			.put("Policy Information.Agency", "Agency")
			.put("Policy Information.Sales Channel", "Sales Channel")
			.put("Policy Information.Agent", "Agent")
			.put("Policy Information.Authorized by", "Authorized by")
			.put("Policy Information.TollFree Number", "TollFree Number")
			.put("Policy Information.Suppress Print", "Suppress Print")
			//Driver Information
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
			//Vehicle Information
			.put("Vehicles.1998, DODGE, CARAVAN.Usage", "Usage")
			.put("Vehicles.1998, DODGE, CARAVAN.VIN", "VIN")
			.put("Vehicles.1998, DODGE, CARAVAN.Existing Damage", "Existing Damage")
			.put("Vehicles.1998, DODGE, CARAVAN.Existing Damage Description", "Existing Damage Description")
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged", "Salvaged")
						.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "Is the vehicle used in any commercial business operations?")*/
			.put("Vehicles.1998, DODGE, CARAVAN.Business Use Description", "Business Use Description")
			.put("Vehicles.1998, DODGE, CARAVAN.Miles One-way to Work or School", "Miles One-way to Work or School")
			.put("Vehicles.1998, DODGE, CARAVAN.Anti-theft", "Anti-theft")
			//Assignment
			.put("Drivers.NBFirstName NBLastName.Assigned Vehicle", "Select Vehicle")

			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current,3 Available).
	 */
	protected static final ArrayListMultimap<String, String> NAMED_INSURED_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAA Claims Report Order.First Name", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAA Claims Report Order.Last Name", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAA Claims Report Order.Date of Birth", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAA Claims Report Order.Name on License", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAAMvr Report Order.First Name", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAAMvr Report Order.Last Name", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAAMvr Report Order.Date of Birth", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAAMvr Report Order.Name on License", CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Prefix", AVAILABLE)
					.put("Named Insureds.VIIFirstName VII VIILastName.First Name", CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Social Security Number", CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Base Date", CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Residential Address", CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Move-In Date", AVAILABLE)
					.put("Named Insureds.VIIFirstName VII VIILastName.Mailing Address", CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Residence", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> NAMED_INSURED_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Named Insureds.VIIFirstName VII VIILastName", "Prefix")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Move-In Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Prior Address")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Residence")
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> NAMED_INSURED_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers.VIIFirstName VII VIILastName", "First Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Middle Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Last Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Suffix")
			.put("Drivers.VIIFirstName VII VIILastName", "Date of Birth")
			.put("Drivers.VIIFirstName VII VIILastName", "Age")
			.put("Drivers.VIIFirstName VII VIILastName", "Base Date")
			.put("Drivers.VIIFirstName VII VIILastName", "Date First Licensed")
			.put("Drivers.VIIFirstName VII VIILastName", "Total Years Driving Experience")
			.put("Named Insureds.VIIFirstName VII VIILastName", "First Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Middle Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Last Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Suffix")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Social Security Number")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Insured Date of Birth")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Residential Address")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Mailing Address")
			.build();

	//Maps to select added Named insureds in endorsement and OOSE
	protected static final ArrayListMultimap<String, String> ADD_NAMED_INSURED_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					//TODO next 2 lines should be deleted when defect PT-3834 is fixed
					.put("Vehicle Assignment", CURRENT)
					.put("Vehicle Assignment", CURRENT)
					.put("Named Insureds.Insured Principal (VIFirstName VI VILastName)", AVAILABLE)
					.put("Named Insureds.Insured Principal (VIIFirstName VII VIILastName)", CURRENT)
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Work Phone Number", AVAILABLE)
					.put("Contact Information.Mobile Phone Number", CURRENT)
					.put("Contact Information.Preferred Phone #", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ADD_NAMED_INSURED_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAAPolicy Issue Summary", "Insured Email")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Email")
			.put("Named Insureds", "Insured Principal (VIFirstName VI VILastName)")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ADD_NAMED_INSURED_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.put("Named Insureds", "Insured Principal (VIIFirstName VII VIILastName)")
			.build();
	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> AAA_PRODUCT_OWNED_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Products Owned.Current AAA Member", CURRENT)
					.put("AAA Products Owned.Motorcycle Policy #", AVAILABLE)
					.put("AAA Products Owned.Life Policy #", CURRENT)
					.put("AAA Products Owned.Home Policy #", AVAILABLE)
					.put("AAA Products Owned.Renters Policy #", CURRENT)
					.put("AAA Products Owned.Condo Policy #", AVAILABLE)
					.put("AAA Products Owned.PUP Policy #", CURRENT)
					.build());

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> AAA_PRODUCT_OWNED_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Products Owned.Current AAA Member", CURRENT)
					.put("AAA Products Owned.Motorcycle Policy #", AVAILABLE)
					.put("AAA Products Owned.Life Policy #", CURRENT)
					.put("AAA Products Owned.Home Policy #", AVAILABLE)
					.put("AAA Products Owned.Renters Policy #", CURRENT)
					.put("AAA Products Owned.Condo Policy #", AVAILABLE)
					.put("AAA Products Owned.PUP Policy #", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> AAA_PRODUCT_OWNED_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Motorcycle Policy #")
			.put("AAA Products Owned", "Home Policy #")
			.put("AAA Products Owned", "Condo Policy #")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> AAA_PRODUCT_OWNED_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Current AAA Member")
			.put("AAA Products Owned", "Override Type")
			.put("AAA Products Owned", "Member Since Date")
			.put("AAA Products Owned", "Membership Number")
			.put("AAA Products Owned", "Life Policy #")
			.put("AAA Products Owned", "Renters Policy #")
			.put("AAA Products Owned", "PUP Policy #")
			.put("Reports.AAA Membership report", "Membership No.")
			.put("Reports.AAA Membership report", "Member Since Date")
			.put("Reports.AAA Membership report", "Order Date")
			.put("Reports.AAA Membership report", "Receipt Date")
			.put("Reports.AAA Membership report", "Status")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> CONTACT_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Work Phone Number", AVAILABLE)
					.put("Contact Information.Mobile Phone Number", CURRENT)
					.put("Contact Information.Preferred Phone #", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.build());
	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> CONTACT_INFORMATION_VERSION_4 = ImmutableListMultimap.<String, String>builder()
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> CONTACT_INFORMATION_VERSION_3 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> CONTACT_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAAPolicy Issue Summary", "Insured Email")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Email")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> CONTACT_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> CURRENT_CARRIER_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> CURRENT_CARRIER_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.build();

	protected static final ArrayListMultimap<String, String> POLICY_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Source of Business", CURRENT)
					.put("Policy Information.Override ASD Level", AVAILABLE)
					.put("Policy Information.Channel Type", CURRENT)
					.put("Policy Information.TollFree Number", AVAILABLE)
					.build());

	protected static final ArrayListMultimap<String, String> POLICY_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Source of Business", CURRENT)
					.put("Policy Information.Advance Shopping Discount Override", AVAILABLE)
					.put("Policy Information.Channel Type", CURRENT)
					.put("Policy Information.TollFree Number", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> POLICY_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information", "Override ASD Level")
			.put("Policy Information", "Advance Shopping Discount Override")
			.put("Policy Information", "ASD Overriden By")
			.put("Policy Information", "Authorized by")
			.put("Policy Information", "TollFree Number")
			.put("Policy Information", "Suppress Print")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> policyInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information", "Source of Business")
			.put("Policy Information", "Source Policy #")
			.put("Policy Information", "Channel Type")
			.put("Policy Information", "Agency")
			.put("Policy Information", "Agency of Record")
			.put("Policy Information", "Sales Channel")
			.put("Policy Information", "Agent")
			.put("Policy Information", "Agent of Record")
			.put("Policy Information", "Agent Number")
			.build();

	//TODO should be deleted when Policy Information.Renewal Term Premium - Old Rater is fixed, RenewalMerge test need to have policyInformationVersion1, not policyInformationRenewal
	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> POLICY_INFORMATION_RENEWAL_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information", "Source of Business")
			.put("Policy Information", "Source Policy #")
			.put("Policy Information", "Renewal Term Premium - Old Rater")
			.put("Policy Information", "Channel Type")
			.put("Policy Information", "Agency")
			.put("Policy Information", "Agency of Record")
			.put("Policy Information", "Sales Channel")
			.put("Policy Information", "Agent")
			.put("Policy Information", "Agent of Record")
			.put("Policy Information", "Agent Number")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> DRIVER_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAA Claims Report Order.Date of Birth", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAA Claims Report Order.License Number", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAA Claims Report Order.License State", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAA Claims Report Order.Gender", AVAILABLE)
					.put("Vehicles.2008, ACURA, MDX.Coverages.Automobile Death Benefit", CURRENT)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.Date of Birth", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.License Number", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.License State", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.Gender", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Forms.AZ_ADBEEndorsement Form", CURRENT)
					.put("Drivers.NBFirstName NB NBLastName.Forms.AZ_ADBEEndorsement Form", CURRENT)
					.put("Drivers.NBFirstName NB NBLastName.Forms.AZ_ADBEEndorsement Form", CURRENT)
					.put("Drivers.NBFirstName NB NBLastName.Forms.AZ_SR22FREndorsement Form", CURRENT)
					.put("Drivers.NBFirstName NB NBLastName.Forms.AZ_SR22FREndorsement Form", CURRENT)
					.put("Drivers.NBFirstName NB NBLastName.Forms.AZ_SR22FREndorsement Form", CURRENT)
					.put("Named Insureds.NBFirstName NB NBLastName.Insured Date of Birth", CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> DRIVER_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Named Insureds.NBFirstName NB NBLastName.Insured Date of Birth", CURRENT)

					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> DRIVER_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> DRIVER_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
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
			.put("Drivers.NBFirstName NB NBLastName", "Filing State")
			.put("Drivers.NBFirstName NB NBLastName", "Form Type")
			.build();

	protected static final ArrayListMultimap<String, String> ADD_DRIVER_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Work Phone Number", AVAILABLE)
					.put("Contact Information.Mobile Phone Number", CURRENT)
					.put("Contact Information.Preferred Phone #", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.put("Reports.AAA Clue Order.Order Date", CURRENT)
					.put("Reports.AAA Clue Order.Receipt Date", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ADD_DRIVER_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.put("Drivers", "Driver (New Driver Version2)")
			.put("Vehicles.2008, ACURA, MDX.Coverages.AAAADBCoverage", "Automobile Death Benefit")
			.put("Reports", "AAA Clue Order")
			.put("Reports.AAA Clue Order", "Order Date")
			.put("Reports.AAA Clue Order", "Receipt Date")
			.put("Reports.AAA Clue Order", "Select")
			.build();

	protected static final ArrayListMultimap<String, String> VEHICLE_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version2, Second Name version2).First Name", AVAILABLE)
					.put("Vehicles.1998, DODGE, CARAVAN.Usage", CURRENT)
					.put("Vehicles.1998, DODGE, CARAVAN.Enroll in Usage Based Insurance?", CURRENT)
					.put("Vehicles.1998, DODGE, CARAVAN.Garaging Address", AVAILABLE)
					.put("Vehicles.1998, DODGE, CARAVAN.Ownership Type", CURRENT)
					.put("Vehicles.1998, DODGE, CARAVAN.Forms.AALPXXEndorsement Form", CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> VEHICLE_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version2, Second Name version2).First Name", AVAILABLE)
					.put("Vehicles.1998, DODGE, CARAVAN.Usage", CURRENT)
					.put("Vehicles.1998, DODGE, CARAVAN.Garaging Address", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> VEHICLE_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicles.1998, DODGE, CARAVAN", "Is Garaging different from Residential?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Garaging Address")
			//Additional Interest Information
			.put("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version2, Second Name version2)", "First Name")
			.put("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version2, Second Name version2)", "Second Name")
			.put("Vehicles.1998, DODGE, CARAVAN.Additional Interest.Additional Interest Information (First Name version2, Second Name version2)", "Additional Interest Address")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> VEHICLE_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information section
			.put("Vehicles.1998, DODGE, CARAVAN", "Usage")
			.put("Vehicles.1998, DODGE, CARAVAN", "VIN")
			.put("Vehicles.1998, DODGE, CARAVAN", "Year")
			.put("Vehicles.1998, DODGE, CARAVAN", "Make")
			.put("Vehicles.1998, DODGE, CARAVAN", "Model")
			.put("Vehicles.1998, DODGE, CARAVAN", "Series")
			.put("Vehicles.1998, DODGE, CARAVAN", "Body Style")
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
			//Ownership address
			.put("Vehicles.1998, DODGE, CARAVAN", "Ownership Type")
			.put("Vehicles.1998, DODGE, CARAVAN", "First Name")
			.put("Vehicles.1998, DODGE, CARAVAN", "Vehicle Ownership Address")
			//Techincal data
			.put("Vehicles.1998, DODGE, CARAVAN", "BI Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "PD Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "UM Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "MP Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Comp Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Coll Symbol")
			.build();

	protected static final ArrayListMultimap<String, String> ADD_VEHICLE_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Work Phone Number", AVAILABLE)
					.put("Contact Information.Mobile Phone Number", CURRENT)
					.put("Contact Information.Preferred Phone #", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/endorsement
	static final Multimap<String, String> ADD_VEHICLE_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAAPolicy Issue Summary", "Insured Email")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Email")
			.put("Vehicles", "Vehicle (2003, MERCEDES-BENZ, SL500R)")
			.put("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information", "Are there any additional interest(s)?")
			.put("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information", "First Name")
			.put("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information", "Second Name")
			.put("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information", "Additional Interest Address")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ADD_VEHICLE_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.put("Vehicles", "Vehicle (1998, DODGE, CARAVAN)")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> ASSIGNMENT = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAANYDriver Vehicle Relationship", CURRENT)
					.put("AAANYDriver Vehicle Relationship", CURRENT)
					.put("AAANYDriver Vehicle Relationship", AVAILABLE)
					.put("AAANYDriver Vehicle Relationship", AVAILABLE)
					.put("AAANYDriver Vehicle Relationship", CURRENT)
					.put("AAANYDriver Vehicle Relationship", AVAILABLE)
					.put("Reports.AAA Membership report.Order Date", CURRENT)
					.put("Drivers.NBFirstName NBLastName.Assigned Vehicle", AVAILABLE)
					.put("Drivers.New Driver2 Version2.Assigned Vehicle", AVAILABLE)
					.put("Drivers.New Driver3 Version3.Assigned Vehicle", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/endorsement
	static final Multimap<String, String> ASSIGNMENT_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers.NBFirstName NBLastName", "Assigned Vehicle")
			.put("Drivers.New Driver2 Version2", "Assigned Vehicle")
			.put("Drivers.New Driver3 Version3", "Assigned Vehicle")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ASSIGNMENT_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.build();

	//components/attributes that should be on comparision page Rolled on/OOSE for Removing components
	static final Multimap<String, String> REMOVE_DRIVER_VEHICLE_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers", "Driver (Third Driver Version2)")
			.put("Vehicles", "Vehicle (2011, MERCEDES-BENZ, G55AMG)")
			.build();

	//components/attributes that should be on comparision page Rolled on/Endorsement for Removing components
	static final Multimap<String, String> REMOVE_DRIVER_VEHICLE_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers", "Driver (Second Driver Version1)")
			.put("Drivers", "Driver (Second Driver Version1)")
			.put("Vehicles", "Vehicle (2011, CHEVROLET, EXPRESS VAN)")
			.put("Vehicles", "Vehicle (2011, CHEVROLET, EXPRESS VAN)")
			.build();

	protected static final ArrayListMultimap<String, String> REMOVE_DRIVER_VEHICLE_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAADriver Vehicle Relationship", CURRENT)
					.put("AAADriver Vehicle Relationship", AVAILABLE)
					.put("Drivers.Driver (Second Driver Version1)", CURRENT)
					.put("Drivers.Driver (Second Driver Version1)", CURRENT)
					.put("Drivers.Driver (Third Driver Version2)", AVAILABLE)
					.put("Vehicles.Vehicle (2011, CHEVROLET, EXPRESS VAN)", CURRENT)
					.put("Vehicles.Vehicle (2011, CHEVROLET, EXPRESS VAN)", CURRENT)
					.put("Vehicles.Vehicle (2011, MERCEDES-BENZ, G55AMG)", AVAILABLE)
					.build());

	//components/attributes that should be on comparision page Rolled on/Endorsement for Removing components
	static final Multimap<String, String> REMOVE_NAMED_INSUREDS_RENEWAL_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Named Insureds", "Insured Principal (Second VI Insured)")
			.build();

	protected static final ArrayListMultimap<String, String> REMOVE_NAMED_INSURED = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.build());

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> PREMIUM_AND_COVERAGES = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					//TODO next 2 lines should be deleted when defect PT-3834 is fixed
					.put("Vehicle Assignment", CURRENT)
					.put("Vehicle Assignment", CURRENT)

					.put("Policy Options.Payment Plan", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> PREMIUM_AND_COVERAGES_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Options", "Payment Plan")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> PREMIUM_AND_COVERAGES_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.build();
}

