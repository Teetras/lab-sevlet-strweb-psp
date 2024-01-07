package by.iba.model;

import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Serializable {

    private int id;
    private String name;
    private String phone;
    private String email;

    public Person(Person person) {
        this.name = person.name;
        this.phone = person.phone;
        this.email = person.email;
    }

    public Person(String nname, String nphone, String nemail) {
        this.name = nname;
        this.phone = nphone;
        this.email = nemail;
    }
}

