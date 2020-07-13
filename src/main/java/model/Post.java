package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // dodaje klauzulę a_i do userId
    @Column(name = "post_id")
    private int postId;
    @Column(name = "post_title")
    private String postTitle;
    @Column(name = "post_content")
    private String postContent;
    @Column(name = "post_category")
    @Enumerated
    private CategoryEnum postCategory;
    @Column(name = "post_registaration")
    private Date postRegistration = new Date();
    // RELACJA MANY TO ONE (N:1) Post to User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Post(String postTitle, String postContent, CategoryEnum postCategory, Date postRegistration, User user) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postCategory = postCategory;
        this.postRegistration = postRegistration;
        this.user = user;
    }
}
