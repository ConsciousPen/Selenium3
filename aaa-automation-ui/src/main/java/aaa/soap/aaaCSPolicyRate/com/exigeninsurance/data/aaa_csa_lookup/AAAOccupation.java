
package aaa.soap.aaaCSPolicyRate.com.exigeninsurance.data.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAOccupation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAOccupation"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Accountant"/&gt;
 *     &lt;enumeration value="Agent"/&gt;
 *     &lt;enumeration value="Architect"/&gt;
 *     &lt;enumeration value="Broadcaster"/&gt;
 *     &lt;enumeration value="BusinessOwner"/&gt;
 *     &lt;enumeration value="CALicensedPhy"/&gt;
 *     &lt;enumeration value="CPA"/&gt;
 *     &lt;enumeration value="CivilService"/&gt;
 *     &lt;enumeration value="Columnist"/&gt;
 *     &lt;enumeration value="Dentist"/&gt;
 *     &lt;enumeration value="Driver"/&gt;
 *     &lt;enumeration value="Editor"/&gt;
 *     &lt;enumeration value="Educator"/&gt;
 *     &lt;enumeration value="Engineer"/&gt;
 *     &lt;enumeration value="Entertainer"/&gt;
 *     &lt;enumeration value="FarmOwner"/&gt;
 *     &lt;enumeration value="Farmer"/&gt;
 *     &lt;enumeration value="FinancialCVR"/&gt;
 *     &lt;enumeration value="Fire Fighter"/&gt;
 *     &lt;enumeration value="ForeignDiplomat"/&gt;
 *     &lt;enumeration value="ForeignNational"/&gt;
 *     &lt;enumeration value="FulltimeClergy"/&gt;
 *     &lt;enumeration value="GolfProf"/&gt;
 *     &lt;enumeration value="HealthCareProf"/&gt;
 *     &lt;enumeration value="HomeMaint"/&gt;
 *     &lt;enumeration value="Homemaker"/&gt;
 *     &lt;enumeration value="InHomeDayCare"/&gt;
 *     &lt;enumeration value="Insurance"/&gt;
 *     &lt;enumeration value="Laborer"/&gt;
 *     &lt;enumeration value="LawEnforcement"/&gt;
 *     &lt;enumeration value="Legal"/&gt;
 *     &lt;enumeration value="Manager"/&gt;
 *     &lt;enumeration value="Manufacturing"/&gt;
 *     &lt;enumeration value="Military"/&gt;
 *     &lt;enumeration value="NewsReporter"/&gt;
 *     &lt;enumeration value="OfficeAdmin"/&gt;
 *     &lt;enumeration value="Other"/&gt;
 *     &lt;enumeration value="OutHomeDayCare"/&gt;
 *     &lt;enumeration value="Paramedic"/&gt;
 *     &lt;enumeration value="Paramedic/EMT"/&gt;
 *     &lt;enumeration value="Pharmacist"/&gt;
 *     &lt;enumeration value="Pilot"/&gt;
 *     &lt;enumeration value="PolOfficial"/&gt;
 *     &lt;enumeration value="PoliceOfficer"/&gt;
 *     &lt;enumeration value="ProfAthlete"/&gt;
 *     &lt;enumeration value="ProfDriver"/&gt;
 *     &lt;enumeration value="ProfJournalist"/&gt;
 *     &lt;enumeration value="ProfLecturer"/&gt;
 *     &lt;enumeration value="ProfPilot"/&gt;
 *     &lt;enumeration value="Professional"/&gt;
 *     &lt;enumeration value="Programmer"/&gt;
 *     &lt;enumeration value="Publisher"/&gt;
 *     &lt;enumeration value="RadioTVPers"/&gt;
 *     &lt;enumeration value="Real Estate"/&gt;
 *     &lt;enumeration value="RestFoodservice"/&gt;
 *     &lt;enumeration value="Retired"/&gt;
 *     &lt;enumeration value="SalesInside"/&gt;
 *     &lt;enumeration value="SalesOutside"/&gt;
 *     &lt;enumeration value="Scientist"/&gt;
 *     &lt;enumeration value="ScriptWriter"/&gt;
 *     &lt;enumeration value="Self Employed"/&gt;
 *     &lt;enumeration value="SelfEmplContr"/&gt;
 *     &lt;enumeration value="SelfEmplMaint"/&gt;
 *     &lt;enumeration value="Student"/&gt;
 *     &lt;enumeration value="Teacher"/&gt;
 *     &lt;enumeration value="TeamManager"/&gt;
 *     &lt;enumeration value="TeamOwner"/&gt;
 *     &lt;enumeration value="Telecaster"/&gt;
 *     &lt;enumeration value="Unemployed"/&gt;
 *     &lt;enumeration value="Veterinarian"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AAAOccupation", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAOccupation {

    @XmlEnumValue("Accountant")
    ACCOUNTANT("Accountant"),
    @XmlEnumValue("Agent")
    AGENT("Agent"),
    @XmlEnumValue("Architect")
    ARCHITECT("Architect"),
    @XmlEnumValue("Broadcaster")
    BROADCASTER("Broadcaster"),
    @XmlEnumValue("BusinessOwner")
    BUSINESS_OWNER("BusinessOwner"),
    @XmlEnumValue("CALicensedPhy")
    CA_LICENSED_PHY("CALicensedPhy"),
    CPA("CPA"),
    @XmlEnumValue("CivilService")
    CIVIL_SERVICE("CivilService"),
    @XmlEnumValue("Columnist")
    COLUMNIST("Columnist"),
    @XmlEnumValue("Dentist")
    DENTIST("Dentist"),
    @XmlEnumValue("Driver")
    DRIVER("Driver"),
    @XmlEnumValue("Editor")
    EDITOR("Editor"),
    @XmlEnumValue("Educator")
    EDUCATOR("Educator"),
    @XmlEnumValue("Engineer")
    ENGINEER("Engineer"),
    @XmlEnumValue("Entertainer")
    ENTERTAINER("Entertainer"),
    @XmlEnumValue("FarmOwner")
    FARM_OWNER("FarmOwner"),
    @XmlEnumValue("Farmer")
    FARMER("Farmer"),
    @XmlEnumValue("FinancialCVR")
    FINANCIAL_CVR("FinancialCVR"),
    @XmlEnumValue("Fire Fighter")
    FIRE_FIGHTER("Fire Fighter"),
    @XmlEnumValue("ForeignDiplomat")
    FOREIGN_DIPLOMAT("ForeignDiplomat"),
    @XmlEnumValue("ForeignNational")
    FOREIGN_NATIONAL("ForeignNational"),
    @XmlEnumValue("FulltimeClergy")
    FULLTIME_CLERGY("FulltimeClergy"),
    @XmlEnumValue("GolfProf")
    GOLF_PROF("GolfProf"),
    @XmlEnumValue("HealthCareProf")
    HEALTH_CARE_PROF("HealthCareProf"),
    @XmlEnumValue("HomeMaint")
    HOME_MAINT("HomeMaint"),
    @XmlEnumValue("Homemaker")
    HOMEMAKER("Homemaker"),
    @XmlEnumValue("InHomeDayCare")
    IN_HOME_DAY_CARE("InHomeDayCare"),
    @XmlEnumValue("Insurance")
    INSURANCE("Insurance"),
    @XmlEnumValue("Laborer")
    LABORER("Laborer"),
    @XmlEnumValue("LawEnforcement")
    LAW_ENFORCEMENT("LawEnforcement"),
    @XmlEnumValue("Legal")
    LEGAL("Legal"),
    @XmlEnumValue("Manager")
    MANAGER("Manager"),
    @XmlEnumValue("Manufacturing")
    MANUFACTURING("Manufacturing"),
    @XmlEnumValue("Military")
    MILITARY("Military"),
    @XmlEnumValue("NewsReporter")
    NEWS_REPORTER("NewsReporter"),
    @XmlEnumValue("OfficeAdmin")
    OFFICE_ADMIN("OfficeAdmin"),
    @XmlEnumValue("Other")
    OTHER("Other"),
    @XmlEnumValue("OutHomeDayCare")
    OUT_HOME_DAY_CARE("OutHomeDayCare"),
    @XmlEnumValue("Paramedic")
    PARAMEDIC("Paramedic"),
    @XmlEnumValue("Paramedic/EMT")
    PARAMEDIC_EMT("Paramedic/EMT"),
    @XmlEnumValue("Pharmacist")
    PHARMACIST("Pharmacist"),
    @XmlEnumValue("Pilot")
    PILOT("Pilot"),
    @XmlEnumValue("PolOfficial")
    POL_OFFICIAL("PolOfficial"),
    @XmlEnumValue("PoliceOfficer")
    POLICE_OFFICER("PoliceOfficer"),
    @XmlEnumValue("ProfAthlete")
    PROF_ATHLETE("ProfAthlete"),
    @XmlEnumValue("ProfDriver")
    PROF_DRIVER("ProfDriver"),
    @XmlEnumValue("ProfJournalist")
    PROF_JOURNALIST("ProfJournalist"),
    @XmlEnumValue("ProfLecturer")
    PROF_LECTURER("ProfLecturer"),
    @XmlEnumValue("ProfPilot")
    PROF_PILOT("ProfPilot"),
    @XmlEnumValue("Professional")
    PROFESSIONAL("Professional"),
    @XmlEnumValue("Programmer")
    PROGRAMMER("Programmer"),
    @XmlEnumValue("Publisher")
    PUBLISHER("Publisher"),
    @XmlEnumValue("RadioTVPers")
    RADIO_TV_PERS("RadioTVPers"),
    @XmlEnumValue("Real Estate")
    REAL_ESTATE("Real Estate"),
    @XmlEnumValue("RestFoodservice")
    REST_FOODSERVICE("RestFoodservice"),
    @XmlEnumValue("Retired")
    RETIRED("Retired"),
    @XmlEnumValue("SalesInside")
    SALES_INSIDE("SalesInside"),
    @XmlEnumValue("SalesOutside")
    SALES_OUTSIDE("SalesOutside"),
    @XmlEnumValue("Scientist")
    SCIENTIST("Scientist"),
    @XmlEnumValue("ScriptWriter")
    SCRIPT_WRITER("ScriptWriter"),
    @XmlEnumValue("Self Employed")
    SELF_EMPLOYED("Self Employed"),
    @XmlEnumValue("SelfEmplContr")
    SELF_EMPL_CONTR("SelfEmplContr"),
    @XmlEnumValue("SelfEmplMaint")
    SELF_EMPL_MAINT("SelfEmplMaint"),
    @XmlEnumValue("Student")
    STUDENT("Student"),
    @XmlEnumValue("Teacher")
    TEACHER("Teacher"),
    @XmlEnumValue("TeamManager")
    TEAM_MANAGER("TeamManager"),
    @XmlEnumValue("TeamOwner")
    TEAM_OWNER("TeamOwner"),
    @XmlEnumValue("Telecaster")
    TELECASTER("Telecaster"),
    @XmlEnumValue("Unemployed")
    UNEMPLOYED("Unemployed"),
    @XmlEnumValue("Veterinarian")
    VETERINARIAN("Veterinarian");
    private final String value;

    AAAOccupation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAOccupation fromValue(String v) {
        for (AAAOccupation c: AAAOccupation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
