package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Data                   // dodaje gettery, settery i toString
@AllArgsConstructor
@NoArgsConstructor
@Entity            // mapuje klasę User na tabelkę db o nazwie user i kolumnach jak pola klasowe
//@Table("user")   // zmienia nazwę mapowanej tabelki
public class User {
    @Id                 // dodaje klauzule PRIMARY KEY do userId
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // dodaje klauzulę a_i do userId
    @Column(name = "user_id")
    private int userId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_lastname")                         // mapuje pole o nazwie określonej w name
    private String userLastName;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "user_password")
    private String userPassword;
    @Column(name = "user_registration")
    private Date userRegistration = new Date();
    @Column(name = "user_status")
    private boolean userStatus = true;
    // RELACJA MANY TO MANY (N:M) User to Role
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            name = "user_to_role")
    private Set<Role> roles = new HashSet<>();

    // RELACJA ONE TO MANY (1:N) User to Post
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "user"
    )
    private List<Post> posts = new ArrayList<>();

    public User(String userName, String userLastName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }
}
