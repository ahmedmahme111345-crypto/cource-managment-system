package school.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import school.model.*;
import school.service.CourseService;
import school.service.StudentService;
import school.store.DataStore;

import java.util.List;

public class StudentView extends VBox {

    private final TextArea output = new TextArea();

    public StudentView(User student, Runnable onLogout) {
        setSpacing(8);
        setPadding(new Insets(20));

        output.setEditable(false);
        output.setPrefHeight(200);

        Button gradesBtn  = new Button("View My Grades");
        Button coursesBtn = new Button("View All Courses");
        Button enrollBtn  = new Button("Enroll in a Course");
        Button surveyBtn  = new Button("Submit Survey");
        Button updateBtn  = new Button("Update My Info");
        Button logoutBtn  = new Button("Logout");

        gradesBtn.setOnAction(e -> {
            output.clear();
            capture(() -> StudentService.viewGrades(student.id));
        });

        coursesBtn.setOnAction(e -> {
            output.clear();
            capture(CourseService::listCourses);
        });

        enrollBtn.setOnAction(e -> {
            output.clear();
            capture(CourseService::listCourses);
            String id = ask("Course ID to enroll in:");
            if (id == null) return;
            try { capture(() -> CourseService.enroll(student.id, Integer.parseInt(id))); }
            catch (NumberFormatException ex) { output.appendText("Invalid ID.\n"); }
        });

        surveyBtn.setOnAction(e -> {
            output.clear();
            List<Enrollment> mine = DataStore.loadEnrollments().stream()
                .filter(en -> en.studentId == student.id).toList();
            List<Course> courses = DataStore.loadCourses();
            mine.forEach(en -> {
                String name = courses.stream().filter(c -> c.id == en.courseId)
                    .map(c -> c.name).findFirst().orElse("#" + en.courseId);
                output.appendText("[" + en.courseId + "] " + name + "\n");
            });
            String id       = ask("Course ID:");
            String feedback = ask("Your feedback:");
            if (id == null || feedback == null) return;
            try {
                int courseId = Integer.parseInt(id);
                boolean enrolled = mine.stream().anyMatch(en -> en.courseId == courseId);
                if (!enrolled) { output.appendText("Not enrolled in that course.\n"); return; }
                Survey s = new Survey();
                s.studentId = student.id; s.courseId = courseId; s.feedback = feedback;
                List<Survey> surveys = DataStore.loadSurveys();
                surveys.add(s);
                DataStore.saveSurveys(surveys);
                output.appendText("Survey submitted.\n");
            } catch (NumberFormatException ex) { output.appendText("Invalid ID.\n"); }
        });

        updateBtn.setOnAction(e -> {
            String name  = ask("New name (blank = keep):");
            String email = ask("New email (blank = keep):");
            String pw    = ask("New password (blank = keep):");
            if (name  != null && !name.isBlank())  student.name     = name;
            if (email != null && !email.isBlank()) student.email    = email;
            if (pw    != null && !pw.isBlank())    student.password = pw;
            List<User> users = DataStore.loadUsers();
            for (int i = 0; i < users.size(); i++)
                if (users.get(i).id == student.id) { users.set(i, student); break; }
            DataStore.saveUsers(users);
            output.appendText("Info updated.\n");
        });

        logoutBtn.setOnAction(e -> onLogout.run());

        getChildren().addAll(
            new Label("Logged in as: " + student.name + " (Student)"),
            gradesBtn, coursesBtn, enrollBtn, surveyBtn, updateBtn, logoutBtn,
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
        TextInputDialog d = new TextInputDialog(); // small window
        d.setHeaderText(null);                   // no header only body
        d.setContentText(prompt);                // prompt message 
        return d.showAndWait().orElse(null);     // wait for your respond if you shoose cencel make null
    }
}
