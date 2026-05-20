package school;

import school.model.User;
import school.service.*;
import school.store.DataStore;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DataStore.ensureFiles();
        SeedData.seedIfEmpty();

        Scanner sc = new Scanner(System.in);

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = AuthService.login(email, password);

        if (user == null) {
            System.out.println("Invalid credentials.");
            return;
        }

        System.out.println("\nWelcome, " + user.name + " [" + user.role + "]\n");

        switch (user.role) {
            case ADMIN      -> AdminService.menu(user, sc);
            case STUDENT    -> StudentService.menu(user, sc);
            case INSTRUCTOR -> InstructorService.menu(user, sc);
        }

        System.out.println("Goodbye, " + user.name + "!");
    }
}
