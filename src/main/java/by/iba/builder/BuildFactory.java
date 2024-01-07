package by.iba.builder;

public class BuildFactory {

    private static final String USER = "user";
    private static final String PERSON = "persons";
    private static final String MESSAGE = "Unknown Builder name!";

    public static Builder create(String builderName) {
        return switch (builderName) {
            case USER -> new UserBuilder();
            case PERSON -> new PersonBuilder();
            default -> throw new IllegalArgumentException(MESSAGE);
        };
    }
}
