package aaa.rest;

public enum RESTServiceUser {

    ALL_RIGHTS("qa", "qa"),
    NOTES_RS_NO_ADD_USER_PRIVILEGE("TestUserForRestNotes2", "qa"),
    NOTES_RS_NO_SET_AS_CONFIDENTIAL_PRIVILEGE("TestUserForRestNotes1", "qa"),
    NOTES_RS_NO_NOTES_INQUIRY_ROLE("TestUserForRestNotes3", "qa"),
    NOTES_RS_NO_UPDATE_NOTES_ROLE("TestUserForRestNotes4", "qa"),
    NOTES_RS_BOB_ROLE_1("BOBNotesRSUser1", "qa"),
    NOTES_RS_BOB_ROLE_2("BOBNotesRSUser2", "qa"),
    NOTES_RS_BOB_ROLE_3("BOBNotesRSUser3", "qa"),
    NOTES_RS_BOB_ROLE_4("BOBNotesRSUser4", "qa"),
    NOTES_RS_BOB_ROLE_5("BOBNotesRSUser5", "qa"),
    NOTES_RS_BOB_ROLE_6("BOBNotesRSUser6", "qa"),
    MY_WORK_RS_USER_1("MyWorkRSUsr1", "qa"),
    MY_WORK_RS_USER_2("MyWorkRSUsr2", "qa"),
    MY_WORK_RS_USER_3("MyWorkRSUsr3", "qa"),
    PARTY_SEARCH_RS_USER_1("PSRUser1", "qa"),
    PARTY_SEARCH_RS_USER_2("PSRUser2", "qa"),
    PARTY_SEARCH_RS_USER_3("PSRUser3", "qa"),
    PARTY_SEARCH_RS_USER_4("PSRUser4", "qa"),
    PARTY_SEARCH_RS_USER_5("PSRUser5", "qa"),
    TASKS_RS_USER_SAME_AS_QA_USER("TestUserForRestTasks", "qa"),
    TASKS_RS_USER("TestUserForRestTasks1", "qa"),
    TASKS_RS_USER_NO_CREATE_MT_PRIV("CTRUser1","qa"),
    TASKS_RS_BAM_USER1("TABUsr1","qa"),
    TASKS_RS_BAM_USER2("TABUsr2","qa"),
    TASKS_RS_ASSIGN_USER1("TRATUsr1","qa"),
    TASKS_RS_ASSIGN_USER2("TRATUsr2","qa"),
    TASKS_RS_ASSIGN_USER3("TRATUsr3","qa"),
    TASKS_RS_ASSIGN_USER4("TRATUsr4","qa"),
    TASKS_RS_ASSIGN_USER5("TRATUsr5","qa"),
    TASKS_RS_ASSIGN_USER6("TRATUsr6","qa"),
    TASKS_RS_ASSIGN_USER7("TRATUsr7","qa"),
    TASKS_RS_ASSIGN_USER8("TRATUsr8","qa"),
    CUSTOMER_RS_USER("CCRUser1", "qa");

    private String login;
    private String password;

    RESTServiceUser(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
