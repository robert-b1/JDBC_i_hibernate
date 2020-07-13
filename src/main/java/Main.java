import config.HibernateConfiguration;
import controller.HibernateController;
import model.CategoryEnum;
import model.Post;
import model.RoleEnum;
import model.User;
import org.hibernate.Session;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        HibernateController hc = new HibernateController();
        Scanner scanner = new Scanner(System.in);
        System.out.println("APLIKACJA BLOGA");
        boolean isLogged = false;
        User user = null;
        while (true) {
            if (!isLogged) {
                System.out.println("Podaj e-mail:");
                String login = scanner.nextLine();
                System.out.println("Podaj hasło:");
                String password = scanner.nextLine();
                user = hc.loginUser(login, password);
                if(user != null){
                    isLogged = true;
                }
            } else {

                hc.printAllPosts();
                System.out.println("Co chcesz zrobić?");
                if(user.getRoles().contains(hc.findRoleByName(RoleEnum.ROLE_USER))) {
                    System.out.println("1. Dodaj posta \n2. Zmień tutuł posta \n3. Usuń posta\n");
                }
                if(user.getRoles().contains(hc.findRoleByName(RoleEnum.ROLE_ADMIN))){
                    System.out.println("A1. Dodaj użytkownika \nA2. Dodaj administratora" +
                            "\nA3. Zmień hasło użytkownika/administratora \nA4. Usuń użytkownika/administratora\n");
                }
                System.out.println("Q. Wyjście");
                String decision = scanner.nextLine();
                if (decision.equals("1") && user.getRoles().contains(hc.findRoleByName(RoleEnum.ROLE_USER))) {
                    System.out.println("Podaj tytuł: ");
                    String title = scanner.nextLine();
                    System.out.println("Podaj treść: ");
                    String content = scanner.nextLine();
                    System.out.println("Wybierz kategorię: ");
                    Arrays.stream(CategoryEnum.values()).forEach(System.out::println);
                    CategoryEnum categoryEnum = CategoryEnum.valueOf(scanner.nextLine());
                    Date dateTime = new Date();
                    Post post = new Post(title, content, categoryEnum, dateTime, user);
                    hc.addPost(post);
                } else if (decision.equals("2") && user.getRoles().contains(hc.findRoleByName(RoleEnum.ROLE_USER))) {
                    hc.findUserPosts(user);
                    System.out.println("Podaj id posta któremu chcesz zmienić tytuł");
                    try {
                        int postId = scanner.nextInt();
                        scanner.nextLine();
                        Post oldPost = hc.findUserPostById(postId, user);
                        System.out.println("Podaj nowy tytuł posta");
                        String newTitle = scanner.nextLine();
                        hc.updatePostTitle(oldPost, newTitle);
                    }catch (InputMismatchException ex){
                        System.out.println("Błędna wartość id");
                        scanner.nextLine();
                    }
                } else if (decision.equals("3") && user.getRoles().contains(hc.findRoleByName(RoleEnum.ROLE_USER))) {
                    hc.findUserPosts(user);
                    System.out.println("Podaj id posta do usunięcia");
                    try {
                        int postId = scanner.nextInt();
                        hc.deleteUserPostById(postId, user);
                    } catch (InputMismatchException ex) {
                        System.out.println("Błędny wartość id");
                        scanner.nextLine();
                    }
                } else if ((decision.equals("A1") || decision.equals("A2")) && user.getRoles().contains(hc.findRoleByName(RoleEnum.ROLE_ADMIN))){
                    System.out.println("Podaj imię: ");
                    String name = scanner.nextLine();
                    System.out.println("Podaj nazwisko: ");
                    String lastname = scanner.nextLine();
                    System.out.println("Podaj adres e-mail: ");
                    String email = scanner.nextLine();
                    System.out.println("Podaj hasło: ");
                    String password = scanner.nextLine();
                    if(decision.equals("A1")) {
                        hc.addUser(name, lastname, email, password, RoleEnum.ROLE_USER);
                    } else {
                        hc.addUser(name, lastname, email, password, RoleEnum.ROLE_ADMIN);
                    }
                } else if (decision.equals("A3") && user.getRoles().contains(hc.findRoleByName(RoleEnum.ROLE_ADMIN))){
                    try {
                        System.out.println("Podaj id użytkownika, któremu chcesz zmienić hasło");
                        int userId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Podaj nowe hasło");
                        String password = scanner.nextLine();
                        hc.updateUserPassword(userId, password);
                    } catch (InputMismatchException ex){
                        System.out.println("Błędna wartość id");
                        scanner.nextLine();
                    }
                } else if (decision.equals("A4") && user.getRoles().contains(hc.findRoleByName(RoleEnum.ROLE_ADMIN))){
                    try {
                        System.out.println("Podaj id użytkownika, któremu chcesz zmienić hasło");
                        int userId = scanner.nextInt();
                        hc.deleteUserById(userId);
                    } catch (InputMismatchException ex){
                        System.out.println("Błędna wartość id");
                    }
                } else if (decision.toUpperCase().equals("Q")){
                    break;
                } else {
                    System.out.println("Błędny wybór");
                }
            }

        }
    }
}

