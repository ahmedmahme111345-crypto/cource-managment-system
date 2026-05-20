package school.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import school.model.*;
import school.service.AdminService;
import school.service.CourseService;
import school.store.DataStore;

import java.time.LocalDate;
import java.util.List;

public class AdminView extends VBox {

    private final TextArea output = new TextArea();

    public AdminView(User admin, Runnable onLogout) {
        setSpacing(8);
        setPadding(new Insets(20));

        output.setEditable(false);
        output.setPrefHeight(200);

        // User management
        Button addUserBtn    = new Button("Add User");
        Button updateUserBtn = new Button("Update User");
        Button deleteUserBtn = new Button("Delete User");
        Button listStuBtn    = new Button("List Students");
        Button listInsBtn    = new Button("List Instructors");
        Button listAdmBtn    = new Button("List Admins");

        // Course management
        Button addCourseBtn    = new Button("Add Course");
        Button updateCourseBtn = new Button("Update Course");
        Button deleteCourseBtn = new Button("Delete Course");
        Button listCoursesBtn  = new Button("List All Courses");
        Button nearStartBtn    = new Button("Courses Near Start");
        Button nearEndBtn      = new Button("Courses Near End");

        Button logoutBtn = new Button("Logout");

        // ── User actions ─────────────────────────────────────────────────
        addUserBtn.setOnAction(e -> {
            String name  = ask("Name:");
            String email = ask("Email:");
            String pass  = ask("Password:");
            String role  = ask("Role (STUDENT/INSTRUCTOR/ADMIN):");
            if (name == null || email == null || pass == null || role == null) return;
            try {
                Role r = Role.valueOf(role.toUpperCase().trim());
                List<User> users = DataStore.loadUsers();
                if (users.stream().anyMatch(u -> u.email.equalsIgnoreCase(email))) {
                    output.appendText("Email already exists.\n"); return;
                }
                User u = new User();
                u.id = DataStore.nextUserId(); u.name = name;
                u.email = email; u.password = pass; u.role = r;
                users.add(u);
                DataStore.saveUsers(users);
                output.appendText("User added. ID=" + u.id + "\n");
            } catch (Exception ex) { output.appendText("Invalid role.\n"); }
        });

        updateUserBtn.setOnAction(e -> {
            output.clear();
            capture(() -> { AdminService.listUsers(Role.STUDENT); AdminService.listUsers(Role.INSTRUCTOR); AdminService.listUsers(Role.ADMIN); });
            String id = ask("User ID to update:");
            if (id == null) return;
            try {
                int uid = Integer.parseInt(id);
                List<User> users = DataStore.loadUsers();
                User target = users.stream().filter(u -> u.id == uid).findFirst().orElse(null);
                if (target == null) { output.appendText("Not found.\n"); return; }
                String name  = ask("New name (blank = keep '" + target.name + "'):");
                String email = ask("New email (blank = keep):");
                String pass  = ask("New password (blank = keep):");
                if (name  != null && !name.isBlank())  target.name     = name;
                if (email != null && !email.isBlank()) target.email    = email;
                if (pass  != null && !pass.isBlank())  target.password = pass;
                DataStore.saveUsers(users);
                output.appendText("User updated.\n");
            } catch (NumberFormatException ex) { output.appendText("Invalid ID.\n"); }
        });

        deleteUserBtn.setOnAction(e -> {
            String id = ask("User ID to delete:");
            if (id == null) return;
            try {
                List<User> users = DataStore.loadUsers();
                boolean removed = users.removeIf(u -> u.id == Integer.parseInt(id));
                if (removed) { DataStore.saveUsers(users); output.appendText("User deleted.\n"); }
                else output.appendText("Not found.\n");
            } catch (NumberFormatException ex) { output.appendText("Invalid ID.\n"); }
        });

        listStuBtn.setOnAction(e -> { output.clear(); capture(() -> AdminService.listUsers(Role.STUDENT)); });
        listInsBtn.setOnAction(e -> { output.clear(); capture(() -> AdminService.listUsers(Role.INSTRUCTOR)); });
        listAdmBtn.setOnAction(e -> { output.clear(); capture(() -> AdminService.listUsers(Role.ADMIN)); });

        // ── Course actions ────────────────────────────────────────────────
        addCourseBtn.setOnAction(e -> {
            String name   = ask("Course name:");
            String parent = ask("Parent course (or None):");
            String instr  = ask("Instructor ID:");
            String room   = ask("Room:");
            String branch = ask("Branch:");
            String price  = ask("Price:");
            String days   = ask("Days (e.g. Sun-Tue):");
            String start  = ask("Start date (YYYY-MM-DD):");
            String end    = ask("End date (YYYY-MM-DD):");
            if (name==null||instr==null||price==null||start==null||end==null) return;
            try {
                Course c = new Course();
                c.id = DataStore.nextCourseId();
                c.name = name; c.parentCourse = (parent==null||parent.isBlank()) ? "None" : parent;
                c.instructorId = Integer.parseInt(instr.trim());
                c.room = room==null?"":room; c.branch = branch==null?"":branch;
                c.price = Double.parseDouble(price.trim());
                c.days = days==null?"":days;
                c.startDate = LocalDate.parse(start.trim());
                c.endDate   = LocalDate.parse(end.trim());
                List<Course> courses = DataStore.loadCourses();
                courses.add(c); DataStore.saveCourses(courses);
                output.appendText("Course added. ID=" + c.id + "\n");
            } catch (Exception ex) { output.appendText("Error: " + ex.getMessage() + "\n"); }
        });

        updateCourseBtn.setOnAction(e -> {
            output.clear(); capture(CourseService::listCourses);
            String id = ask("Course ID to update:");
            if (id == null) return;
            try {
                int cid = Integer.parseInt(id);
                List<Course> courses = DataStore.loadCourses();
                Course c = courses.stream().filter(x -> x.id == cid).findFirst().orElse(null);
                if (c == null) { output.appendText("Not found.\n"); return; }
                String name   = ask("New name (blank = keep '" + c.name + "'):");
                String room   = ask("New room (blank = keep):");
                String branch = ask("New branch (blank = keep):");
                String price  = ask("New price (blank = keep):");
                String days   = ask("New days (blank = keep):");
                if (name   != null && !name.isBlank())   c.name   = name;
                if (room   != null && !room.isBlank())   c.room   = room;
                if (branch != null && !branch.isBlank()) c.branch = branch;
                if (price  != null && !price.isBlank())  { try { c.price = Double.parseDouble(price.trim()); } catch (NumberFormatException ignored) {} }
                if (days   != null && !days.isBlank())   c.days   = days;
                DataStore.saveCourses(courses);
                output.appendText("Course updated.\n");
            } catch (NumberFormatException ex) { output.appendText("Invalid ID.\n"); }
        });

        deleteCourseBtn.setOnAction(e -> {
            String id = ask("Course ID to delete:");
            if (id == null) return;
            try {
                int cid = Integer.parseInt(id);
                List<Course> courses = DataStore.loadCourses();
                boolean removed = courses.removeIf(c -> c.id == cid);
                if (removed) {
                    DataStore.saveCourses(courses);
                    List<Enrollment> enr = DataStore.loadEnrollments();
                    enr.removeIf(en -> en.courseId == cid);
                    DataStore.saveEnrollments(enr);
                    output.appendText("Course deleted.\n");
                } else output.appendText("Not found.\n");
            } catch (NumberFormatException ex) { output.appendText("Invalid ID.\n"); }
        });

        listCoursesBtn.setOnAction(e -> { output.clear(); capture(CourseService::listCourses); });
        nearStartBtn.setOnAction(e   -> { output.clear(); capture(CourseService::upcomingStartReport); });
        nearEndBtn.setOnAction(e     -> { output.clear(); capture(CourseService::upcomingEndReport); });

        logoutBtn.setOnAction(e -> onLogout.run());

        getChildren().addAll(
            new Label("Logged in as: " + admin.name + " (Admin)"),
            new Label("--- User Management ---"),
            addUserBtn, updateUserBtn, deleteUserBtn, listStuBtn, listInsBtn, listAdmBtn,
            new Label("--- Course Management ---"),
            addCourseBtn, updateCourseBtn, deleteCourseBtn, listCoursesBtn, nearStartBtn, nearEndBtn,
            logoutBtn,
            new Label("Output:"), output
        );
    }

    private void capture(Runnable r) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream old = System.out;
        System.setOut(new java.io.PrintStream(buf));
        r.run();
        System.setOut(old);
        output.appendText(buf.toString());
    }

    private String ask(String prompt) {
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText(null);
        d.setContentText(prompt);
        return d.showAndWait().orElse(null);
    }
}
