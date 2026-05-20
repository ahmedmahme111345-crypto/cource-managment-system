package school.service;

import school.model.*;
import school.store.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentService {

    public static void menu(User student, Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== STUDENT MENU ===");
            System.out.println("1. View my grades");
            System.out.println("2. View all available courses");
            System.out.println("3. Enroll in a course");
            System.out.println("4. Submit survey for a course");
            System.out.println("5. Update my information");
            System.out.println("0. Logout");
            System.out.print("Choice: ");
            int ch = InputHelper.intInput(sc);
            switch (ch) {
                case 1 -> viewGrades(student.id);
                case 2 -> CourseService.listCourses();
                case 3 -> enrollInCourse(student.id, sc);
                case 4 -> submitSurvey(student.id, sc);
                case 5 -> updateInfo(student, sc);
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void viewGrades(int studentId) {
        System.out.println("--- Your Grades ---");
        List<Course> courses = DataStore.loadCourses();
        boolean any = false;
        for (Enrollment e : DataStore.loadEnrollments()) {
            if (e.studentId == studentId) {
                String cName = courses.stream()
                        .filter(c -> c.id == e.courseId)
                        .map(c -> c.name)
                        .findFirst().orElse("Course #" + e.courseId);
                String grade = e.grade == null ? "Not graded yet" : String.valueOf(e.grade);
                System.out.println("  " + cName + " => " + grade);
                any = true;
            }
        }
        if (!any) System.out.println("  (no enrollments)");
    }

    public static void enrollInCourse(int studentId, Scanner sc) {
        CourseService.listCourses();
        System.out.print("Course ID to enroll in: ");
        int courseId = InputHelper.intInput(sc);
        CourseService.enroll(studentId, courseId);
    }

    public static void submitSurvey(int studentId, Scanner sc) {
        System.out.println("--- Your enrolled courses ---");
        List<Course> courses = DataStore.loadCourses();
        List<Enrollment> myEnrollments = new ArrayList<>();
        for (Enrollment e : DataStore.loadEnrollments())
            if (e.studentId == studentId) myEnrollments.add(e);

        if (myEnrollments.isEmpty()) { System.out.println("  (not enrolled in any course)"); return; }

        for (Enrollment e : myEnrollments) {
            String cName = courses.stream().filter(c -> c.id == e.courseId)
                    .map(c -> c.name).findFirst().orElse("#" + e.courseId);
            System.out.println("  [" + e.courseId + "] " + cName);
        }

        System.out.print("Course ID for survey: ");
        int courseId = InputHelper.intInput(sc);

        boolean enrolled = myEnrollments.stream().anyMatch(e -> e.courseId == courseId);
        if (!enrolled) { System.out.println("You are not enrolled in that course."); return; }

        System.out.print("Your feedback: ");
        String feedback = sc.nextLine();

        List<Survey> surveys = DataStore.loadSurveys();
        Survey s = new Survey();
        s.studentId = studentId;
        s.courseId  = courseId;
        s.feedback  = feedback;
        surveys.add(s);
        DataStore.saveSurveys(surveys);
        System.out.println("Survey submitted. Thank you!");
    }

    public static void updateInfo(User student, Scanner sc) {
        System.out.print("New name (blank = keep '" + student.name + "'): ");
        String n = sc.nextLine();
        if (!n.isBlank()) student.name = n;

        System.out.print("New email (blank = keep): ");
        String e = sc.nextLine();
        if (!e.isBlank()) student.email = e;

        System.out.print("New password (blank = keep): ");
        String p = sc.nextLine();
        if (!p.isBlank()) student.password = p;

        List<User> users = DataStore.loadUsers();
        for (int i = 0; i < users.size(); i++)
            if (users.get(i).id == student.id) { users.set(i, student); break; }
        DataStore.saveUsers(users);
        System.out.println("Information updated.");
    }
}
