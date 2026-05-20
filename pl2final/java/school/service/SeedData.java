package school.service;

import school.model.*;
import school.store.DataStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SeedData {

    public static void seedIfEmpty() {
        if (!DataStore.loadUsers().isEmpty()) return;

        System.out.println("[INFO] No data found. Creating sample seed data...");

        List<User> users = new ArrayList<>();
        String[][] ud = {
            {"1", "Admin User",   "admin@school.com", "admin123", "ADMIN"},
            {"2", "Dr. Sara",     "sara@school.com",  "sara123",  "INSTRUCTOR"},
            {"3", "Ali Ahmed",    "ali@school.com",   "ali123",   "STUDENT"},
            {"4", "Mona Hassan",  "mona@school.com",  "mona123",  "STUDENT"},
        };
        for (String[] r : ud) {
            User u = new User();
            u.id = Integer.parseInt(r[0]); u.name = r[1]; u.email = r[2];
            u.password = r[3]; u.role = Role.valueOf(r[4]);
            users.add(u);
        }
        DataStore.saveUsers(users);

        List<Course> courses = new ArrayList<>();

        Course c1 = new Course();
        c1.id = 1; c1.name = "Mathematics"; c1.parentCourse = "None";
        c1.instructorId = 2; c1.room = "A1"; c1.branch = "Cairo";
        c1.price = 500; c1.days = "Sun-Tue";
        c1.startDate = LocalDate.now().plusDays(3);
        c1.endDate   = LocalDate.now().plusDays(60);
        c1.studentIds.add(3); c1.studentIds.add(4);
        courses.add(c1);

        Course c2 = new Course();
        c2.id = 2; c2.name = "Physics"; c2.parentCourse = "None";
        c2.instructorId = 2; c2.room = "B2"; c2.branch = "Cairo";
        c2.price = 600; c2.days = "Mon-Wed";
        c2.startDate = LocalDate.now().plusDays(10);
        c2.endDate   = LocalDate.now().plusDays(70);
        courses.add(c2);
        DataStore.saveCourses(courses);

        List<Enrollment> enr = new ArrayList<>();
        Enrollment e1 = new Enrollment(); e1.studentId = 3; e1.courseId = 1; e1.grade = 88.0; enr.add(e1);
        Enrollment e2 = new Enrollment(); e2.studentId = 4; e2.courseId = 1; e2.grade = null;  enr.add(e2);
        DataStore.saveEnrollments(enr);

        System.out.println("[INFO] Seed complete. Login with:");
        System.out.println("       admin@school.com  / admin123");
        System.out.println("       sara@school.com   / sara123  (instructor)");
        System.out.println("       ali@school.com    / ali123   (student)");
        System.out.println();
    }
}
