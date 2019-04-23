package aaa.modules.regression.sales.auto_ca.choice.functional;

import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

public class VersionsComparisonConstants {

	//fields that have DB values, but not UI values on Comparison page
	static final Map<String, String> COMPARISON_PAGE_DIFFERENT_VALUES = ImmutableMap.<String, String>builder()
			.put("Membership Override", "Override")
			.put("SMTestCC - 400000092","400000092")
			.put("SMTestNCNU - 400001005","400001005")
			.put("IE DSC-District Sales and Marketing","2512")
			.put("Captive Branch Agents","2507")
			.put("Zilliah J Wade","500012749")
			.put("HouseAgent Magnolia","500034992")
			.build();

	//TODO refactor it by taking values from UI
	//values that we don't have in Test Date, but that are used in comparison
	static final Multimap<String, String> PREDEFINED_EXPECTED_VALUES = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			//TODO should be deleted/updated when Report tab is fixed
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Residential Address", "VII residence address 1, VII residence address 2, Culver City, Los Angeles, CA, 90232", "VI residence address 1, VI residence address 2, HUNTINGTON PARK, Los Angeles, CA, 90255")
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Prior Address", "VII prior address 1, VII prior address 2, Culver City, CA, 90232", "VI prior address 1, VI prior address 2, HUNTINGTON PARK, CA, 90255")
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Mailing Address", "VII mailing address 1, VII mailing address 2, Culver City, Los Angeles, CA, 90232", "VI mailing address 1, VI mailing address 2, HUNTINGTON PARK, Los Angeles, CA, 90255")
			.putAll("Vehicles.2003, MERCEDES-BENZ, SL500R.Garaging Address", "VII residence address 1, VII residence address 2, Culver City, CA, 90232", "VI residence address 1, VI residence address 2, HUNTINGTON PARK, CA, 90255")
			//.putAll("Vehicle Information (2008, ACURA, MDX, WAGON 4 DOOR).Garaging Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//Current Currier Information
			.putAll("Current Carrier Information.Months with Carrier","12","4")
			//Policy Information
			.putAll("Policy Information.Agency Location","400001105","")
			.putAll("Policy Information.Agency of Record","400001005","400000092")
			.putAll("Policy Information.Agent","400010108","400004092")
			.putAll("Policy Information.Agent of Record","400010108","400004092")
			.putAll("Policy Information.Agent Number","400010108","400004092")
			//Third Party
			.putAll("Policy Information.Third Party Designee Address","VII third party address 1, VII third party address 2, Culver City, CA, 90232","VI third party address 1, VI third party address 2, HUNTINGTON PARK, CA, 90255")
			//Driver Tab
			//data gather data
			.putAll("Drivers.VIFirstName VI VILastName.Age", "18","55")
			.putAll("Drivers.VIFirstName VI VILastName.Permit Before License", "true","false")
			.putAll("Drivers.VIFirstName VI VILastName.Date First Licensed", "06/20/2018","07/20/2018")
			.putAll("Drivers.VIFirstName VI VILastName.Licensed in US/Canada for 18 or More Months?", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Total Years Driving Experience", "3","38")
			.putAll("Drivers.VIFirstName VI VILastName.New Driver Course Completed", "true","false")
			.putAll("Drivers.VIFirstName VI VILastName.Mature driver course completed within 36 months", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Good Driver Discount", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Smoker: Cigarettes, cigars or pipes", "true","false")
			.putAll("Drivers.VIFirstName VI VILastName.Financial Responsibility Filling Needed", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Violation Points","3","0")
			.putAll("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Conviction Points","1","0")
			.putAll("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Include in Points and/or YAF?","true","false")
			.putAll("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Not Included in Points and/or YAF - Reason Codes","","")
			//endorsement/renewal data
			.putAll("Drivers.NBFirstName NB NBLastName.Age", "18","55")
			.putAll("Drivers.NBFirstName NB NBLastName.Permit Before License", "true","false")
			.putAll("Drivers.NBFirstName NB NBLastName.Date First Licensed", "06/20/2018","07/20/2018")
			.putAll("Drivers.NBFirstName NB NBLastName.Licensed in US/Canada for 18 or More Months?", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Total Years Driving Experience", "3","38")
			.putAll("Drivers.NBFirstName NB NBLastName.New Driver Course Completed", "true","false")
			.putAll("Drivers.NBFirstName NB NBLastName.Mature driver course completed within 36 months", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Good Driver Discount", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Smoker: Cigarettes, cigars or pipes", "true","false")
			.putAll("Drivers.NBFirstName NB NBLastName.Financial Responsibility Filling Needed", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Conviction Points","1","")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Violation Points","3","0")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Include in Points and/or YAF?","true","false")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Not Included in Points and/or YAF - Reason Codes","","")
			//Vehicle Information
			.putAll("Vehicles.1998, DODGE, CARAVAN.Year","1998","2003")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Make","DODG","MERZ")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Model","CARAVAN","SL500R")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Series","GRAND CARAVAN SE/SPORT","SL500R")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Body Style","SPORT VAN","ROADSTER")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Comp Symbol", "7","27")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Coll Symbol", "7","27")
			//TODO should be return to TD when True/False vs Yes/No is fixed
			.putAll("Vehicles.1998, DODGE, CARAVAN.Salvaged?", "true","false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Is the vehicle used in any commercial business operations?", "","false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Anti-theft", "NONE","STD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Air Bags","000E","000K")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Alternative Fuel Vehicle","true","false")
			//Garaging address
			.putAll("Vehicles.1998, DODGE, CARAVAN.Is Garaging different from Residential?", "true", "false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Garaging Address", "VII garaging address 1, VII garaging address 2, Red Rock, AZ, 85245","VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			//Ownership address
			.putAll("Vehicles.1998, DODGE, CARAVAN.Ownership Type", "Financed","Owned")
			.putAll("Vehicles.1998, DODGE, CARAVAN.First Name", "LI011","")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Vehicle Ownership Address", "PO BOX 674, MINNEAPOLIS, MN, 55440-0674","")
			//Additional Interest Information
			.putAll("Additional Interest Information (First Name version1, Second Name version1).First Name", "First Name version2","First Name version1")
			.putAll("Additional Interest Information (First Name version1, Second Name version1).Second Name", "Second Name version2","Second Name version1")
			.putAll("Additional Interest Information (First Name version1, Second Name version1).Additional Interest Address", "VII interest address 1, VII interest address 2, Red Rock, AZ, 85245","interest address 1, VI interest address 2, Phoenix, AZ, 85085")
			//Assignment Tab Information
			.putAll("Vehicles.2003, MERCEDES-BENZ, SL500R.Manually Rated Driver", "","NBFirstName NBLastName")
			.putAll("Policy Options.Payment Plan","standart6CAC","annualCAC")
			.putAll("Coverages.Medical Payments","$5,000","$2,000")
			.putAll("Coverages.Property Damage Liability","$100,000","$50,000")
			.putAll("Coverages.Bodily Injury Liability","$100,000/$300,000","$50,000/$100,000")
			.putAll("Coverages.Uninsured Motorists Bodily Injury","$100,000/$300,000","$50,000/$100,000")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> UI_FIELDS_TO_TD_MAPPING = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap PREDEFINED_EXPECTED_VALUES)
			.put("Drivers.VIIFirstName VII VIILastName.First Name", "First Name")
			.put("Drivers.VIIFirstName VII VIILastName.Middle Name", "Middle Name")
			.put("Drivers.VIIFirstName VII VIILastName.Last Name", "Last Name")
			.put("Drivers.VIIFirstName VII VIILastName.Suffix", "Suffix")
			.put("Drivers.VIIFirstName VII VIILastName.Base Date", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName.Title", "Title")
			.put("Named Insureds.VIIFirstName VII VIILastName.First Name", "First Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Middle Name", "Middle Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Last Name", "Last Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Suffix", "Suffix")
			.put("Named Insureds.VIIFirstName VII VIILastName.Base Date", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName.Move-In Date", "Move-In Date")
			//AAA Product Owned
			.put("AAA Membership.Current AAA Member", "Current AAA Member")
			.put("AAA Membership.Membership Number", "Membership Number")
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
			.put("Policy Information.Channel Type","Channel Type")
			.put("Policy Information.Agency","Agency")
			.put("Policy Information.Sales Channel","Sales Channel")
			//.put("Policy Information.Agent","Agent")
			.put("Policy Information.Authorized by","Authorized by")
			.put("Policy Information.TollFree Number","TollFree Number")
			.put("Policy Information.Language Preference","Language Preference")
			.put("Policy Information.Suppress Print","Suppress Print")
			//Third Party
			.put("Policy Information.Third Party Designee Name","Name")
			//Driver Tab
			//data gather
			.put("Drivers.VIFirstName VI VILastName.Date of Birth", "Date of Birth")
			.put("Drivers.VIFirstName VI VILastName.Gender", "Gender")
			.put("Drivers.VIFirstName VI VILastName.Marital Status", "Marital Status")
			.put("Drivers.VIFirstName VI VILastName.Occupation", "Occupation")
			.put("Drivers.VIFirstName VI VILastName.Age First Licensed", "Age First Licensed")
			.put("Drivers.VIFirstName VI VILastName.License Type", "License Type")
			.put("Drivers.VIFirstName VI VILastName.First US/Canada License Date", "First US/Canada License Date")
			.put("Drivers.VIFirstName VI VILastName.License State", "License State")
			.put("Drivers.VIFirstName VI VILastName.License #", "License #")
			.put("Drivers.VIFirstName VI VILastName.Employee Benefit Type", "Employee Benefit Type")
			.put("Drivers.VIFirstName VI VILastName.Most Recent GPA", "Most Recent GPA")
			.put("Drivers.VIFirstName VI VILastName.New Driver Course Completion Date", "New Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName.New Driver Certification Number", "New Driver Certification Number")
			.put("Drivers.VIFirstName VI VILastName.Mature Driver Course Completion Date", "Mature Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName.Filling Type", "Filling Type")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Type", "Type")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Description", "Description")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Occurrence Date", "Occurrence Date")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Conviction Date", "Conviction Date")
			//endorsement/renewal data
			.put("Drivers.NBFirstName NB NBLastName.Date of Birth", "Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName.Gender", "Gender")
			.put("Drivers.NBFirstName NB NBLastName.Marital Status", "Marital Status")
			.put("Drivers.NBFirstName NB NBLastName.Occupation", "Occupation")
			.put("Drivers.NBFirstName NB NBLastName.Age First Licensed", "Age First Licensed")
			.put("Drivers.NBFirstName NB NBLastName.License Type", "License Type")
			.put("Drivers.NBFirstName NB NBLastName.First US/Canada License Date", "First US/Canada License Date")
			.put("Drivers.NBFirstName NB NBLastName.License State", "License State")
			.put("Drivers.NBFirstName NB NBLastName.License #", "License #")
			.put("Drivers.NBFirstName NB NBLastName.Employee Benefit Type", "Employee Benefit Type")
			.put("Drivers.NBFirstName NB NBLastName.Most Recent GPA", "Most Recent GPA")
			.put("Drivers.NBFirstName NB NBLastName.New Driver Course Completion Date", "New Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName.New Driver Certification Number", "New Driver Certification Number")
			.put("Drivers.NBFirstName NB NBLastName.Mature Driver Course Completion Date", "Mature Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName.Filling Type", "Filling Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Type", "Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Description", "Description")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Occurrence Date", "Occurrence Date")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Conviction Date", "Conviction Date")
			//TODO should be returned after implementing story for Clean up for Activity Information
			//.put("Activity Information (Hit and run, 05/10/2020, Not included in Rating).Include in Points and/or YAF?", "Include in Points and/or Tier?")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating).Not Included in Points and/or YAF - Reason Codes", "Not Included in Points and/or YAF - Reason Codes")
			//Vehicle Information
			.put("Vehicles.1998, DODGE, CARAVAN.Usage", "Usage")
			.put("Vehicles.1998, DODGE, CARAVAN.VIN", "VIN")
			.put("Vehicles.1998, DODGE, CARAVAN.Existing Damage", "Existing Damage")
			.put("Vehicles.1998, DODGE, CARAVAN.Existing Damage Description", "Existing Damage Description")
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Vehicles.1998, DODGE, CARAVAN.Salvaged", "Salvaged")
			.put("Vehicles.1998, DODGE, CARAVAN.Is the vehicle used in any commercial business operations?", "Is the vehicle used in any commercial business operations?")*/
			.put("Vehicles.1998, DODGE, CARAVAN.Business Use Description", "Business Use Description")
			.put("Vehicles.1998, DODGE, CARAVAN.Miles One-way to Work or School", "Miles One-way to Work or School")
			.put("Vehicles.1998, DODGE, CARAVAN.Odometer Reading", "Odometer Reading")
			.put("Vehicles.1998, DODGE, CARAVAN.Odometer Reading Date", "Odometer Reading Date")
			.put("Vehicles.1998, DODGE, CARAVAN.Customer Declared Annual Miles", "Customer Declared Annual Miles")
			.put("Vehicles.1998, DODGE, CARAVAN.Anti-theft Recovery Device", "Anti-theft Recovery Device")
			.put("Vehicles.1998, DODGE, CARAVAN.Primary Use", "Primary Use")
			//Usage Based Insurance
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Usage Based Insurance Program.Enroll in Usage Based Insurance?", "Enroll in Usage Based Insurance?")*/
			//Garaging address
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is Garaging different from Residential?", "Is Garaging different from Residential?")*/
			//Assignment Tab Information
			.put("Vehicles.2003, MERCEDES-BENZ, SL500R.Primary Driver", "Primary Driver")
			.put("Policy Options.Policy Term","Policy Term")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for data gather comparison
	static final Multimap<String, String> DATA_GATHER_NAMED_INSURED_INFORMATION = ImmutableListMultimap.<String, String>builder()
			.put("Drivers.VIIFirstName VII VIILastName", "First Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Middle Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Last Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Suffix")
			.put("Drivers.VIIFirstName VII VIILastName", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Title")
			.put("Named Insureds.VIIFirstName VII VIILastName", "First Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Middle Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Last Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Suffix")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Residential Address")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Move-In Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Prior Address")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Mailing Address")
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
			.put("AA59 Existing Damage Endorsement Form", "Last Name")
			.put("AAA Membership", "Current AAA Member")
			//TODO should be returned after Clean up stories for AAA Product Owned section
			/*			.put("AAA Products Owned", "Override Type")*/
			.put("AAA Products Owned", "Motorcycle Policy #")
			.put("AAA Products Owned", "Life Policy #")
			.put("AAA Products Owned", "Home Policy #")
			.put("AAA Products Owned", "Renters Policy #")
			.put("AAA Products Owned", "Condo Policy #")
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
			.put("AA59 Existing Damage Endorsement Form","Source of Business")
			.put("Policy Information","Source of Business")
			.put("Policy Information","Source Policy #")
			.put("Policy Information","Channel Type")
			.put("Policy Information","Agency")
			.put("Policy Information","Agency of Record")
			.put("Policy Information","Agency Location")
			.put("Policy Information","Sales Channel")
			.put("Policy Information","Agent")
			.put("Policy Information","Agent of Record")
			.put("Policy Information","Agent Number")
			.put("Policy Information","Authorized by")
			.put("Policy Information","TollFree Number")
			.put("Policy Information","Language Preference")
			.put("Policy Information","Suppress Print")
			.build();

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> THIRD_PARTY_DESIGNEE = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Third Party Designee Name")
			.put("Policy Information","Third Party Designee Address")
			.build();

	//all components/attributes that should be on Comparison page for Driver Information section
	static final Multimap<String, String> DATA_GATHER_DRIVER_INFORMATION = ImmutableListMultimap.<String, String>builder()
			.put("Forms.CIPCS22Endorsement Form", "Automobile Death Benefit")
			.put("Drivers.VIFirstName VI VILastName", "Date of Birth")
			.put("Drivers.VIFirstName VI VILastName", "Age")
			.put("Drivers.VIFirstName VI VILastName", "Gender")
			.put("Drivers.VIFirstName VI VILastName", "Marital Status")
			.put("Drivers.VIFirstName VI VILastName", "Occupation")
			.put("Drivers.VIFirstName VI VILastName", "Age First Licensed")
			.put("Drivers.VIFirstName VI VILastName", "Permit Before License")
			.put("Drivers.VIFirstName VI VILastName", "Total Years Driving Experience")
			.put("Drivers.VIFirstName VI VILastName", "Date First Licensed")
			.put("Drivers.VIFirstName VI VILastName", "Licensed in US/Canada for 18 or More Months?")
			.put("Drivers.VIFirstName VI VILastName", "First US/Canada License Date")
			.put("Drivers.VIFirstName VI VILastName", "License #")
			.put("Drivers.VIFirstName VI VILastName", "Employee Benefit Type")
			.put("Drivers.VIFirstName VI VILastName", "Most Recent GPA")
			.put("Drivers.VIFirstName VI VILastName", "Good Driver Discount")
			.put("Drivers.VIFirstName VI VILastName", "New Driver Course Completed")
			.put("Drivers.VIFirstName VI VILastName", "New Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName", "New Driver Certification Number")
			.put("Drivers.VIFirstName VI VILastName", "Mature driver course completed within 36 months")
			.put("Drivers.VIFirstName VI VILastName", "Mature Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName", "Smoker: Cigarettes, cigars or pipes")
			.put("Drivers.VIFirstName VI VILastName", "Financial Responsibility Filling Needed")
			.put("Drivers.VIFirstName VI VILastName", "Filling Type")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Type")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Occurrence Date")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Description")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Conviction Date")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Violation Points")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Include in Points and/or YAF?")
			.put("Drivers.VIFirstName VI VILastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Not Included in Points and/or YAF - Reason Codes")
			.build();

	//all components/attributes that should be on Comparison page for Driver Information section
	static final Multimap<String, String> ENDORSEMENT_DRIVER_INFORMATION = ImmutableListMultimap.<String, String>builder()
			.put("Drivers.NBFirstName NB NBLastName", "Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName", "Age")
			.put("Drivers.NBFirstName NB NBLastName", "Gender")
			.put("Drivers.NBFirstName NB NBLastName", "Marital Status")
			.put("Drivers.NBFirstName NB NBLastName", "Occupation")
			.put("Drivers.NBFirstName NB NBLastName", "Age First Licensed")
			.put("Drivers.NBFirstName NB NBLastName", "Permit Before License")
			.put("Drivers.NBFirstName NB NBLastName", "Total Years Driving Experience")
			.put("Drivers.NBFirstName NB NBLastName", "Date First Licensed")
			.put("Drivers.NBFirstName NB NBLastName", "Licensed in US/Canada for 18 or More Months?")
			.put("Drivers.NBFirstName NB NBLastName", "First US/Canada License Date")
			.put("Drivers.NBFirstName NB NBLastName", "License #")
			.put("Drivers.NBFirstName NB NBLastName", "Employee Benefit Type")
			.put("Drivers.NBFirstName NB NBLastName", "Most Recent GPA")
			.put("Drivers.NBFirstName NB NBLastName", "Good Driver Discount")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Course Completed")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Certification Number")
			.put("Drivers.NBFirstName NB NBLastName", "Mature driver course completed within 36 months")
			.put("Drivers.NBFirstName NB NBLastName", "Mature Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName", "Smoker: Cigarettes, cigars or pipes")
			.put("Drivers.NBFirstName NB NBLastName", "Financial Responsibility Filling Needed")
			.put("Drivers.NBFirstName NB NBLastName", "Filling Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Occurrence Date")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Description")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Conviction Date")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Violation Points")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Include in Points and/or YAF?")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Not Included in Points and/or YAF - Reason Codes")
			.build();

	//all components/attributes that should be on Comparison page for Driver Information section
	static final Multimap<String, String> RENEWAL_DRIVER_INFORMATION = ImmutableListMultimap.<String, String>builder()
			.put("Drivers.NBFirstName NB NBLastName", "Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName", "Age")
			.put("Drivers.NBFirstName NB NBLastName", "Gender")
			.put("Drivers.NBFirstName NB NBLastName", "Marital Status")
			.put("Drivers.NBFirstName NB NBLastName", "Occupation")
			.put("Drivers.NBFirstName NB NBLastName", "Age First Licensed")
			.put("Drivers.NBFirstName NB NBLastName", "Permit Before License")
			.put("Drivers.NBFirstName NB NBLastName", "Total Years Driving Experience")
			.put("Drivers.NBFirstName NB NBLastName", "Date First Licensed")
			.put("Drivers.NBFirstName NB NBLastName", "Licensed in US/Canada for 18 or More Months?")
			.put("Drivers.NBFirstName NB NBLastName", "First US/Canada License Date")
			.put("Drivers.NBFirstName NB NBLastName", "License #")
			.put("Drivers.NBFirstName NB NBLastName", "Employee Benefit Type")
			.put("Drivers.NBFirstName NB NBLastName", "Most Recent GPA")
			.put("Drivers.NBFirstName NB NBLastName", "Good Driver Discount")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Course Completed")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Certification Number")
			.put("Drivers.NBFirstName NB NBLastName", "Mature driver course completed within 36 months")
			.put("Drivers.NBFirstName NB NBLastName", "Mature Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName", "Smoker: Cigarettes, cigars or pipes")
			.put("Drivers.NBFirstName NB NBLastName", "Financial Responsibility Filling Needed")
			.put("Drivers.NBFirstName NB NBLastName", "Filling Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Type")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Occurrence Date")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Description")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Conviction Date")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Violation Points")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Include in Points and/or YAF?")
			.put("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Hit and run, 07/20/2018, Not included in Rating)", "Not Included in Points and/or YAF - Reason Codes")
			.build();

	//all components/attributes that should be on Comparison page for Vehicle Information section
	static final Multimap<String, String> VEHICLE_INFORMATION = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information section
			.put("Vehicles.1998, DODGE, CARAVAN", "VIN")
			.put("Vehicles.1998, DODGE, CARAVAN", "Year")
			.put("Vehicles.1998, DODGE, CARAVAN", "Make")
			.put("Vehicles.1998, DODGE, CARAVAN", "Model")
			.put("Vehicles.1998, DODGE, CARAVAN", "Series")
			.put("Vehicles.1998, DODGE, CARAVAN", "Body Style")
			.put("Vehicles.1998, DODGE, CARAVAN", "Comp Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Coll Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Primary Use")
			.put("Vehicles.1998, DODGE, CARAVAN", "Is the vehicle used in any commercial business operations?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Business Use Description")
			.put("Vehicles.1998, DODGE, CARAVAN", "Air Bags")
			.put("Vehicles.1998, DODGE, CARAVAN", "Anti-theft")
			.put("Vehicles.1998, DODGE, CARAVAN", "Alternative Fuel Vehicle")
			.put("Vehicles.1998, DODGE, CARAVAN", "Anti-theft Recovery Device")
			.put("Vehicles.1998, DODGE, CARAVAN", "Salvaged?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Miles One-way to Work or School")
			.put("Vehicles.1998, DODGE, CARAVAN", "Odometer Reading")
			.put("Vehicles.1998, DODGE, CARAVAN", "Odometer Reading Date")
			.put("Vehicles.1998, DODGE, CARAVAN", "Customer Declared Annual Miles")
			//Garaging address
			.put("Vehicles.1998, DODGE, CARAVAN", "Is Garaging different from Residential?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Garaging Address")
			//Ownership address
			.put("Vehicles.1998, DODGE, CARAVAN", "Ownership Type")
			.put("Vehicles.1998, DODGE, CARAVAN", "First Name")
			.put("Vehicles.1998, DODGE, CARAVAN", "Vehicle Ownership Address")
			//coverages
			.put("Vehicles.1998, DODGE, CARAVAN.Coverages", "Enhanced Transportation Expense Coverage")
			.put("Vehicles.1998, DODGE, CARAVAN.Coverages", "Limit per day")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for endorsement/renewal comparison
	public static final Multimap<String, String> ENDORSEMENT_RENEWAL_VEHICLE_INFORMATION;
	static {
		Multimap<String, String> endorsementModified = ArrayListMultimap.create(VEHICLE_INFORMATION);
		endorsementModified.remove("Vehicles.1998, DODGE, CARAVAN.Coverages", "Enhanced Transportation Expense Coverage");
		endorsementModified.remove("Vehicles.1998, DODGE, CARAVAN.Coverages", "Limit per day");
		ENDORSEMENT_RENEWAL_VEHICLE_INFORMATION = ImmutableListMultimap.copyOf(endorsementModified);
	}


	//all components/attributes that should be on Comparison page for Assignment section for NB and endorsements
	static final Multimap<String, String> ASSIGNMENT = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information section
			.put("Vehicles.2003, MERCEDES-BENZ, SL500R", "Primary Driver")
			.build();

	//all components/attributes that should be on Comparison page for Assignment section
	static final Multimap<String, String> RENEWAL_ASSIGNMENT = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information section
			.put("Vehicles.2003, MERCEDES-BENZ, SL500R", "Primary Driver")
			.put("Vehicles.2003, MERCEDES-BENZ, SL500R", "Manually Rated Driver")
			.build();

	//all components/attributes that should be on Comparison page for Premium and Coverages section for Quote Compare
	static final Multimap<String, String> DATA_GATHER_PREMIUM_AND_COVERAGES = ImmutableListMultimap.<String, String>builder()
			.put("Policy Options","Payment Plan")
			.put("Policy Options", "Policy Term")
			.put("Coverages","Medical Payments")
			.put("Coverages","Property Damage Liability")
			.put("Coverages","Bodily Injury Liability")
			.put("Coverages","Uninsured Motorists Bodily Injury")
			.build();

	//all components/attributes that should be on Comparison page for Premium and Coverages section for Quote Compare
	static final Multimap<String, String> ENDORSEMENT_RENEWAl_PREMIUM_AND_COVERAGES = ImmutableListMultimap.<String, String>builder()
			.put("Coverages","Medical Payments")
			.put("Coverages","Property Damage Liability")
			.put("Coverages","Bodily Injury Liability")
			.put("Coverages","Uninsured Motorists Bodily Injury")
			.build();
}