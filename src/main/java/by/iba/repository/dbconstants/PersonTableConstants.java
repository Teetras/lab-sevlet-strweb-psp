package by.iba.repository.dbconstants;

public enum PersonTableConstants {
    ID("id"),
    NAME("pname"),
    PHONE("phone"),
    EMAIL("email");

    private String fieldName;

    private PersonTableConstants(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}