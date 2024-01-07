package by.iba.repository.specification;

import java.util.Arrays;
import java.util.List;

public class PersonInsert implements Parameter {
    private String name;
    private String phone;
    private String email;

    public PersonInsert(String name, String phone, String email) {

        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public List<Object> getParameters() {

        return Arrays.asList(name, phone, email);
    }
}
