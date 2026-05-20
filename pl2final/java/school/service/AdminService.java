package school.service;

import school.model.Role;
import school.model.User;
import school.store.DataStore;

import java.util.List;
import java.util.Scanner;

// --------------------
// Admin service module
// --------------------
public class AdminService {

    public static void menu(User user, Scanner sc) {
        boolean logout = false;

        while (!logout) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Manage users (add/update/delete/list)");
            System.out.println("2. Manage courses");
            System.out.println("3. Report: courses near start");
            System.out.println("4. Report: courses near end");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int ch = InputHelper.intInput(sc);

            switch (ch) {
                case 1 -> runUserManagementMenu(sc);
                case 2 -> manageCourses(sc);
                case 3 -> CourseService.upcomingStartReport();
                case 4 -> CourseService.upcomingEndReport();
                case 0 -> logout = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void runUserManagementMenu(Scanner sc) {
        System.out.println("\n-- USER MANAGEMENT --");
        System.out.println("1. Add user");
        System.out.println("2. Update user");
        System.out.println("3. Delete user");
        System.out.println("4. List students");
        System.out.println("5. List instructors");
        System.out.println("6. List admins");
        System.out.print("Choice: ");

        int uc = InputHelper.intInput(sc);

        switch (uc) {
            case 1 -> addUser(sc);
            case 2 -> updateUser(sc);
            case 3 -> deleteUser(sc);
            case 4 -> listUsers(Role.STUDENT);
            case 5 -> listUsers(Role.INSTRUCTOR);
            case 6 -> listUsers(Role.ADMIN);
            default -> System.out.println("Invalid.");
        }
    }

    public static void addUser(Scanner sc) {
        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        System.out.print("Role (STUDENT/INSTRUCTOR/ADMIN): ");
        String roleStr = sc.nextLine();

        Role role;

        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (Exception e) {
            System.out.println("Invalid role.");
            return;
        }

        List<User> users = DataStore.loadUsers();

        boolean dup = users.stream()
                .anyMatch(u -> u.email.equalsIgnoreCase(email));

        if (dup) {
            System.out.println("Email already exists.");
            return;
        }

        User u = new User();
        u.id = DataStore.nextUserId();
        u.name = name;
        u.email = email;
        u.password = pass;
        u.role = role;

        users.add(u);

        DataStore.saveUsers(users);

        System.out.println("User added. ID=" + u.id);
    }

    public static void updateUser(Scanner sc) {
        System.out.print("User ID to update: ");

        int id = InputHelper.intInput(sc);

        List<User> users = DataStore.loadUsers();

        User target = users.stream()
                .filter(u -> u.id == id)
                .findFirst()
                .orElse(null);

        if (target == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("New name (blank = keep '" + target.name + "'): ");
        String n = sc.nextLine();

        if (!n.isBlank()) {
            target.name = n;
        }

        System.out.print("New email (blank = keep): ");
        String e = sc.nextLine();

        if (!e.isBlank()) {
            target.email = e;
        }

        System.out.print("New password (blank = keep): ");
        String p = sc.nextLine();

        if (!p.isBlank()) {
            target.password = p;
        }

        DataStore.saveUsers(users);

        System.out.println("User updated.");
    }

    public static void deleteUser(Scanner sc) {
        System.out.print("User ID to delete: ");

        int id = InputHelper.intInput(sc);

        List<User> users = DataStore.loadUsers();

        boolean removed = users.removeIf(u -> u.id == id);

        if (removed) {
            DataStore.saveUsers(users);
            System.out.println("User deleted.");
        } else {
            System.out.println("User not found.");
        }
    }

    public static void listUsers(Role role) {
        System.out.println("--- " + role + "S ---");

        boolean any = false;

        for (User u : DataStore.loadUsers()) {
            if (u.role == role) {
                System.out.println("  [" + u.id + "] " + u.name + " | " + u.email);
                any = true;
            }
        }

        if (!any) {
            System.out.println("  (none)");
        }
    }

    public static void manageCourses(Scanner sc) {
        CourseService.menu(sc);
    }
}
