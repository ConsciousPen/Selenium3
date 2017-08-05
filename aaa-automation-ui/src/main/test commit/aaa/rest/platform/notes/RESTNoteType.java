package aaa.rest.platform.notes;

public enum RESTNoteType {

    CUSTOMER("Customer"), QUOTE("Quote"), POLICY("Policy"), CLAIM("claim");

    private String noteType;

    RESTNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String get() {
        return noteType;
    }
}
