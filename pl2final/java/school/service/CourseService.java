package school.service;

import school.model.*;
import school.store.DataStore;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

// =========================================
// 📚 COURSE SERVICE (Handles course operations)
// =========================================
static class CourseService {

    // ==============================
    // 🎯 Main menu for course module
    // ==============================
    static void menu(Scanner sc) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== COURSES MENU ===");
            System.out.println("1. Add course");
            System.out.println("2. Update course");
            System.out.println("3. Delete course");
            System.out.println("4. List all courses");
            System.out.println("5. List all instructors");
            System.out.println("6. List all students");
            System.out.println("7. Report: courses near start");
            System.out.println("8. Report: courses near end");
            System.out.println("0. Back");

            System.out.print("Choice: ");
            int ch = intInput(sc);

            switch (ch) {
                case 1 -> addCourse(sc);
                case 2 -> updateCourse(sc);
                case 3 -> deleteCourse(sc);
                case 4 -> listCourses();
                case 5 -> AdminService.listUsers(Role.INSTRUCTOR);
                case 6 -> AdminService.listUsers(Role.STUDENT);
                case 7 -> upcomingStartReport();
                case 8 -> upcomingEndReport();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }


    // =========================================
    // 📊 Report: Courses starting within 7 days
    // =========================================
    static void upcomingStartReport() {
        System.out.println("--- Courses Starting Within 7 Days ---");

        LocalDate now = LocalDate.now();
        boolean any = false;

        for (Course c : DataStore.loadCourses()) {
            long days = ChronoUnit.DAYS.between(now, c.startDate);

            if (days >= 0 && days <= 7) {
                System.out.println("  " + c.name + " | Starts: " + c.startDate
                        + " | Branch: " + c.branch + " | Room: " + c.room);
                any = true;
            }
        }

        if (!any) System.out.println("  (none)");
    }


    // =========================================
    // 📊 Report: Courses ending within 7 days
    // =========================================
    static void upcomingEndReport() {
        System.out.println("--- Courses Ending Within 7 Days ---");

        LocalDate now = LocalDate.now();
        boolean any = false;

        for (Course c : DataStore.loadCourses()) {
            long days = ChronoUnit.DAYS.between(now, c.endDate);

            if (days >= 0 && days <= 7) {
                System.out.println("  " + c.name + " | Ends: " + c.endDate
                        + " | Branch: " + c.branch);
                any = true;
            }
        }

        if (!any) System.out.println("  (none)");
    }


    // =========================================
    // ➕ Add a new course
    // =========================================
    static void addCourse(Scanner sc) {

        System.out.print("Course name: ");
        String name = sc.nextLine();

        System.out.print("Parent course (or None): ");
        String parent = sc.nextLine();

        System.out.print("Instructor ID: ");
        int instrId = intInput(sc);

        System.out.print("Room: ");
        String room = sc.nextLine();

        System.out.print("Branch: ");
        String branch = sc.nextLine();

        System.out.print("Price: ");
        double price = doubleInput(sc);

        System.out.print("Days (e.g. Sun-Tue): ");
        String days = sc.nextLine();

        System.out.print("Start date (YYYY-MM-DD): ");
        LocalDate start = dateInput(sc);

        System.out.print("End date   (YYYY-MM-DD): ");
        LocalDate end = dateInput(sc);

        List<Course> courses = DataStore.loadCourses();

        Course c = new Course();
        c.id           = DataStore.nextCourseId();
        c.name         = name;
        c.parentCourse = parent.isBlank() ? "None" : parent;
        c.instructorId = instrId;
        c.room         = room;
        c.branch       = branch;
        c.price        = price;
        c.days         = days;
        c.startDate    = start;
        c.endDate      = end;

        courses.add(c);
        DataStore.saveCourses(courses);

        System.out.println("Course added. ID=" + c.id);
    }


    // =========================================
    // ✏️ Update an existing course
    // =========================================
    static void updateCourse(Scanner sc) {

        listCourses();

        System.out.print("Course ID to update: ");
        int id = intInput(sc);

        List<Course> courses = DataStore.loadCourses();

        Course c = courses.stream()
                .filter(x -> x.id == id)
                .findFirst()
                .orElse(null);

        if (c == null) {
            System.out.println("Not found.");
            return;
        }

        System.out.print("New name (blank = keep '" + c.name + "'): ");
        String n = sc.nextLine();
        if (!n.isBlank()) c.name = n;

        System.out.print("New room (blank = keep '" + c.room + "'): ");
        String r = sc.nextLine();
        if (!r.isBlank()) c.room = r;

        System.out.print("New branch (blank = keep '" + c.branch + "'): ");
        String b = sc.nextLine();
        if (!b.isBlank()) c.branch = b;

        System.out.print("New price (0 = keep " + c.price + "): ");
        double p = doubleInput(sc);
        if (p > 0) c.price = p;

        System.out.print("New days (blank = keep '" + c.days + "'): ");
        String d = sc.nextLine();
        if (!d.isBlank()) c.days = d;

        DataStore.saveCourses(courses);

        System.out.println("Course updated.");
    }


    // =========================================
    // ❌ Delete a course
    // =========================================
    static void deleteCourse(Scanner sc) {

        listCourses();

        System.out.print("Course ID to delete: ");
        int id = intInput(sc);

        List<Course> courses = DataStore.loadCourses();

        boolean removed = courses.removeIf(c -> c.id == id);

        if (removed) {
            DataStore.saveCourses(courses);

            // Remove related enrollments
            List<Enrollment> enr = DataStore.loadEnrollments();
            enr.removeIf(e -> e.courseId == id);
            DataStore.saveEnrollments(enr);

            System.out.println("Course deleted.");
        } else {
            System.out.println("Not found.");
        }
    }


    // =========================================
    // 📋 List all courses
    // =========================================
    static void listCourses() {

        System.out.println("--- All Courses ---");

        List<User> users = DataStore.loadUsers();
        boolean any = false;

        for (Course c : DataStore.loadCourses()) {

            // Get instructor name using instructor ID
            String instrName = users.stream()
                    .filter(u -> u.id == c.instructorId)
                    .map(u -> u.name)
                    .findFirst()
                    .orElse("Unknown");

            System.out.printf(
                    "  [%d] %-20s | Instructor: %-15s | %s -> %s | Room: %s | Branch: %s | Price: %.0f%n",
                    c.id, c.name, instrName, c.startDate, c.endDate, c.room, c.branch, c.price
            );

            any = true;
        }

        if (!any) System.out.println("  (no courses)");
    }


    // =========================================
    // 🎓 Enroll a student in a course
    // =========================================
    static void enroll(int studentId, int courseId) {

        List<Enrollment> list = DataStore.loadEnrollments();

        // Check if already enrolled
        boolean exists = list.stream()
                .anyMatch(e -> e.studentId == studentId && e.courseId == courseId);

        if (exists) {
            System.out.println("Already enrolled.");
            return;
        }

        // Add student to course
        List<Course> courses = DataStore.loadCourses();

        courses.stream()
                .filter(c -> c.id == courseId)
                .findFirst()
                .ifPresent(c -> {
                    if (!c.studentIds.contains(studentId))
                        c.studentIds.add(studentId);
                });

        DataStore.saveCourses(courses);

        // Create new enrollment record
        Enrollment e = new Enrollment();
        e.studentId = studentId;
        e.courseId  = courseId;

        list.add(e);
        DataStore.saveEnrollments(list);

        System.out.println("Enrolled successfully.");
    }
}
