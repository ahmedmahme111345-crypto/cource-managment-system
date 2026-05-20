package school.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import school.model.*;
import school.service.InstructorService;
import school.store.DataStore;

import java.util.List;

public class InstructorView extends VBox {

    private final TextArea output = new TextArea();

    public InstructorView(User instructor, Runnable onLogout) {
        setSpacing(8);
        setPadding(new Insets(20));

        output.setEditable(false);
        output.setPrefHeight(200);

        Button myCoursesBtn = new Button("View My Courses");
        Button studentsBtn  = new Button("View Students in a Course");
        Button gradeBtn     = new Button("Add / Update Grade");
        Button publishBtn   = new Button("Publish Grades");
        Button surveysBtn   = new Button("View Surveys");
        Button logoutBtn    = new Button("Logout");

        myCoursesBtn.setOnAction(e -> {
            output.clear();
            capture(() -> InstructorService.listMyCourses(instructor.id));
        });

        studentsBtn.setOnAction(e -> {
            output.clear();
            capture(() -> InstructorService.listMyCourses(instructor.id));
            String id = ask("Course ID:");
            if (id == null) return;
            try {
                int courseId = Integer.parseInt(id);
                Course course = DataStore.loadCourses().stream()
                    .filter(c -> c.id == courseId && c.instructorId == instructor.id)
                    .findFirst().orElse(null);
                if (course == null) { output.appendText("Course not found or not yours.\n"); return; }
                List<User> users = DataStore.loadUsers();
                output.appendText("--- Students in " + course.name + " ---\n");
                DataStore.loadEnrollments().stream()
                    .filter(en -> en.courseId == courseId)
                    .forEach(en -> {
                        String name  = users.stream().filter(u -> u.id == en.studentId)
                            .map(u -> u.name).findFirst().orElse("ID " + en.studentId);
                        String grade = en.grade == null ? "ungraded" : String.valueOf(en.grade);
                        output.appendText("  [" + en.studentId + "] " + name + " => " + grade + "\n");
                    });
            } catch (NumberFormatException ex) { output.appendText("Invalid ID.\n"); }
        });

        gradeBtn.setOnAction(e -> {
            output.clear();
            String cId = ask("Course ID:");
            String sId = ask("Student ID:");
            String g   = ask("Grade:");
            if (cId == null || sId == null || g == null) return;
            try {
                int    courseId  = Integer.parseInt(cId);
                int    studentId = Integer.parseInt(sId);
                double grade     = Double.parseDouble(g);
                Course course = DataStore.loadCourses().stream()
                    .filter(c -> c.id == courseId && c.instructorId == instructor.id)
                    .findFirst().orElse(null);
                if (course == null) { output.appendText("Course not found or not yours.\n"); return; }
                List<Enrollment> list = DataStore.loadEnrollments();
                boolean found = false;
                for (Enrollment en : list) {
                    if (en.studentId == studentId && en.courseId == courseId) {
                        en.grade = grade; found = true; break;
                    }
                }
                if (!found) {
                    Enrollment en = new Enrollment();
                    en.studentId = studentId; en.courseId = courseId; en.grade = grade;
                    list.add(en);
                }
                DataStore.saveEnrollments(list);
                output.appendText("Grade saved.\n");
            } catch (NumberFormatException ex) { output.appendText("Invalid input.\n"); }
        });

        publishBtn.setOnAction(e -> {
            output.clear();
            capture(() -> InstructorService.publishGrades(instructor.id));
        });

        surveysBtn.setOnAction(e -> {
            output.clear();
            capture(() -> InstructorService.viewSurveys(instructor.id));
        });

        logoutBtn.setOnAction(e -> onLogout.run());

        getChildren().addAll(
            new Label("Logged in as: " + instructor.name + " (Instructor)"),
            myCoursesBtn, studentsBtn, gradeBtn, publishBtn, surveysBtn, logoutBtn,
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
