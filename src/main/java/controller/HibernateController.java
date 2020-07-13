package controller;

import config.HibernateConfiguration;
import model.Post;
import model.Role;
import model.RoleEnum;
import model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class HibernateController {
    public Role findRoleByName(RoleEnum roleEnum){
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        Query query = session.createQuery("SELECT r FROM Role r WHERE r.roleName=:roleName");
        query.setParameter("roleName", roleEnum);
        query.setMaxResults(1);
        Role role = (Role) query.uniqueResult();
        transaction.commit();
        session.close();
        return role;
    }
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public void addUser(String name, String lastName, String email, String password, RoleEnum roleEnum) throws NoSuchAlgorithmException {
        // otwarcie sesji
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        // rozpoczęcie transakcji
        Transaction transaction =session.beginTransaction();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5"); // algorytm do szyfrowania
        byte [] encodedPassword = messageDigest.digest(password.getBytes());
        User user = new User(name,lastName,email, bytesToHex(encodedPassword));
        // przypisanie roli do użytkownika
        Set<Role> roles = user.getRoles();
        roles.add(findRoleByName(roleEnum));
        user.setRoles(roles);
        session.save(user);
        transaction.commit();
        session.close();
    }
    public User loginUser(String login, String password) throws NoSuchAlgorithmException {
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5"); // algorytm do szyfrowania
        byte [] encodedPassword = messageDigest.digest(password.getBytes());
        Query query = session.createQuery(
                "SELECT u FROM User u WHERE u.userEmail=:login AND u.userPassword=:password");
        query.setString("login", login);
        query.setString("password", bytesToHex(encodedPassword));
        query.setMaxResults(1);
        User user = (User) query.uniqueResult();
        transaction.commit();
        session.close();
        return user;
    }
    public void addPost(Post post){
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        session.save(post);
        transaction.commit();
        session.close();
    }
    public void findUserPosts(User user){
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        Query query = session.createQuery("SELECT p FROM Post p WHERE p.user=:user");
        query.setParameter("user", user);
        List<Post> userPosts = query.list();
        userPosts.stream()
                .forEach(post -> System.out.println(
                        post.getPostId() + " " +post.getPostTitle()));
        transaction.commit();
        session.close();
    }
    public Post findUserPostById(int postId, User user){
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        Query query = session.createQuery("SELECT p FROM Post p WHERE p.user=:user AND p.postId=:id");
        query.setParameter("user", user);
        query.setInteger("id", postId);
        query.setMaxResults(1);
        Post post = (Post) query.uniqueResult();
        transaction.commit();
        session.close();
        return post;
    }
    public void updatePostTitle(Post oldPost, String newTitle){
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        oldPost.setPostTitle(newTitle);
        session.saveOrUpdate(oldPost);      // UPDATE
        transaction.commit();
        session.close();
    }
    public void deleteUserPostById(int postId, User user){
        Post postToDelete = findUserPostById(postId, user);
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        session.delete(postToDelete);
        transaction.commit();
        session.close();
    }
    public void printAllPosts(){
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();

        Query query = session.createQuery("SELECT p FROM Post p");
        List<Post> userPosts = query.list();
        userPosts.stream()
                .forEach(post -> System.out.printf(
                        "| %2d | %20s | %20s | %20s | %15s |\n",
                        post.getPostId(),
                        post.getPostTitle(),
                        post.getUser().getUserName() + " " + post.getUser().getUserLastName(),
                        post.getPostCategory(),
                        post.getPostRegistration()));
        transaction.commit();
        session.close();
    }
    // ----------------
    public User getUserById(int userId){
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        Query query = session.createQuery("SELECT u FROM User u WHERE u.userId=:id");
        query.setInteger("id", userId);
        query.setMaxResults(1);
        User user = (User) query.uniqueResult();
        transaction.commit();
        session.close();
        return user;
    }
    public void updateUserPassword(int userId, String newPassword) throws NoSuchAlgorithmException {
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        User user = getUserById(userId);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5"); // algorytm do szyfrowania
        byte [] encodedPassword = messageDigest.digest(newPassword.getBytes());
        user.setUserPassword(bytesToHex(encodedPassword));
        session.saveOrUpdate(user);
        transaction.commit();
        session.close();
    }
    public void deleteUserById(int userId){
        Session session = HibernateConfiguration.getSessionFactory().openSession();
        Transaction transaction =session.beginTransaction();
        session.delete(getUserById(userId));
        transaction.commit();
        session.close();
    }
}
