package school.service;

import school.model.*;
import school.store.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InstructorService {

    public static void menu(User instructor, Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== INSTRUCTOR MENU ===");
            System.out.println("1. View my courses");
            System.out.println("2. View students in a course");
            System.out.println("3. Add / update grade for a student");
            System.out.println("4. Publish grades (confirm & save)");
            System.out.println("5. View surveys for my courses");
            System.out.println("0. Logout");
            System.out.print("Choice: ");
            int ch = InputHelper.intInput(sc);
            switch (ch) {
                case 1 -> listMyCourses(instructor.id);
                case 2 -> listStudentsInCourse(instructor.id, sc);
                case 3 -> addOrUpdateGrade(instructor.id, sc);
                case 4 -> publishGrades(instructor.id);
                case 5 -> viewSurveys(instructor.id);
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void listMyCourses(int instructorId) {
        System.out.println("--- Your Courses ---");
        boolean any = false;
        for (Course c : DataStore.loadCourses()) {
            if (c.instructorId == instructorId) {
                System.out.printf("  [%d] %-20s | %s -> %s | Room: %s%n",
                        c.id, c.name, c.startDate, c.endDate, c.room);
                any = true;
            }
        }
        if (!any) System.out.println("  (no courses assigned)");
    }

    public static void listStudentsInCourse(int instructorId, Scanner sc) {
        listMyCourses(instructorId);
        System.out.print("Course ID: ");
        int courseId = InputHelper.intInput(sc);

        Course course = DataStore.loadCourses().stream()
                .filter(c -> c.id == courseId && c.instructorId == instructorId)
                .findFirst().orElse(null);
        if (course == null) { System.out.println("Course not found or not yours."); return; }

        List<User> users = DataStore.loadUsers();
        System.out.println("--- Students in " + course.name + " ---");
        boolean any = false;
        for (Enrollment e : DataStore.loadEnrollments()) {
            if (e.courseId == courseId) {
                String sName = users.stream().filter(u -> u.id == e.studentId)
                        .map(u -> u.name).findFirst().orElse("ID " + e.studentId);
                String grade = e.grade == null ? "ungraded" : String.valueOf(e.grade);
                System.out.println("  [" + e.studentId + "] " + sName + " => " + grade);
                any = true;
            }
        }
        if (!any) System.out.println("  (no students enrolled)");
    }

    public static void addOrUpdateGrade(int instructorId, Scanner sc) {
        listMyCourses(instructorId);
        System.out.print("Course ID: ");
        int courseId = InputHelper.intInput(sc);

        Course course = DataStore.loadCourses().stream()
                .filter(c -> c.id == courseId && c.instructorId == instructorId)
                .findFirst().orElse(null);
        if (course == null) { System.out.println("Course not found or not yours."); return; }

        System.out.print("Student ID: ");
        int studentId = InputHelper.intInput(sc);
        System.out.print("Grade: ");
        double grade = InputHelper.doubleInput(sc);

        List<Enrollment> list = DataStore.loadEnrollments();
        boolean found = false;
        for (Enrollment e : list) {
            if (e.studentId == studentId && e.courseId == courseId) {
                e.grade = grade;
                found = true;
                break;
            }
        }
        if (!found) {
            Enrollment e = new Enrollment();
            e.studentId = studentId;
            e.courseId  = courseId;
            e.grade     = grade;
            list.add(e);
        }
        DataStore.saveEnrollments(list);
        System.out.println("Grade saved (not yet published). Use 'Publish grades' to confirm.");
    }

    public static void publishGrades(int instructorId) {
        System.out.println("--- Publishing grades ---");
        List<Course> myCourses = new ArrayList<>();
        for (Course c : DataStore.loadCourses())
            if (c.instructorId == instructorId) myCourses.add(c);

        List<User> users = DataStore.loadUsers();
        for (Enrollment e : DataStore.loadEnrollments()) {
            boolean mine = myCourses.stream().anyMatch(c -> c.id == e.courseId);
            if (mine && e.grade != null) {
                String sName = users.stream().filter(u -> u.id == e.studentId)
                        .map(u -> u.name).findFirst().orElse("ID " + e.studentId);
                String cName = myCourses.stream().filter(c -> c.id == e.courseId)
                        .map(c -> c.name).findFirst().orElse("#" + e.courseId);
                System.out.println("  Published: " + sName + " | " + cName + " => " + e.grade);
            }
        }
        System.out.println("Grades published successfully.");
    }

    public static void viewSurveys(int instructorId) {
        System.out.println("--- Survey Feedback for Your Courses ---");
        List<Course> myCourses = new ArrayList<>();
        for (Course c : DataStore.loadCourses())
            if (c.instructorId == instructorId) myCourses.add(c);

        boolean any = false;
        for (Survey s : DataStore.loadSurveys()) {
            myCourses.stream().filter(c -> c.id == s.courseId).findFirst().ifPresent(c -> {
                System.out.println("  Course: " + c.name
                        + " | Student ID: " + s.studentId
                        + " | Feedback: " + s.feedback);
            });
            any = true;
        }
        if (!any) System.out.println("  (no surveys yet)");
    }
}
