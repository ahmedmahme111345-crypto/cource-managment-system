package school.store;

import school.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//-----------------------
// Data storage helper functions
// ------------------------
public class DataStore {

    public static List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",");
                User u = new User();
                u.id       = Integer.parseInt(p[0]);
                u.name     = p[1];
                u.email    = p[2];
                u.password = p[3];
                u.role     = Role.valueOf(p[4]);
                list.add(u);
            }
        } catch (Exception ignored) {}
        return list;
    }
// --------------------------------
// saves users into users.txt (quite literally the same logic as any other save function)
// --------------------------------------
    public static void saveUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("users.txt"))) {
            for (User u : users) pw.println(u);
        } catch (Exception ignored) {}
    }
// -----------------------------
// tracks users ids to be able to make them follow the correct sequence
// ---------------------------------
    public static int nextUserId() {
        return loadUsers().stream().mapToInt(u -> u.id).max().orElse(0) + 1;
    }

    public static List<Course> loadCourses() {
        List<Course> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("courses.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",", -1);
                Course c = new Course();
                c.id           = Integer.parseInt(p[0]);
                c.name         = p[1];
                c.parentCourse = p[2];
                c.instructorId = Integer.parseInt(p[3]);
                c.room         = p[4];
                c.branch       = p[5];
                c.price        = Double.parseDouble(p[6]);
                c.days         = p[7];
                c.startDate    = LocalDate.parse(p[8]);
                c.endDate      = LocalDate.parse(p[9]);
                if (p.length > 10 && !p[10].isEmpty()) {
                    for (String s : p[10].split("\\|"))           //handles the pipe character || for correct parsing of ids
                        c.studentIds.add(Integer.parseInt(s));
                }
                list.add(c);
            }
        } catch (Exception ignored) {}
        return list;
    }

    public static void saveCourses(List<Course> courses) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("courses.txt"))) {
            for (Course c : courses) pw.println(c);
        } catch (Exception ignored) {}
    }

// -----------------------------
// tracks courses ids to be able to make them follow the correct sequence
// ---------------------------------
    public static int nextCourseId() {
        return loadCourses().stream().mapToInt(c -> c.id).max().orElse(0) + 1;
    }
// ------------------------
// loads enrollments follows the same logic as the other load functions
// but adjusted for the enrollments class
// ---------------------------------
    public static List<Enrollment> loadEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("enrollments.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",", -1);
                Enrollment e = new Enrollment();
                e.studentId = Integer.parseInt(p[0]);
                e.courseId  = Integer.parseInt(p[1]);
                e.grade     = (p.length > 2 && !p[2].isEmpty()) ? Double.parseDouble(p[2]) : null;
                list.add(e);
            }
        } catch (Exception ignored) {}
        return list;
    }

    public static void saveEnrollments(List<Enrollment> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("enrollments.txt"))) {
            for (Enrollment e : list) pw.println(e);
        } catch (Exception ignored) {}
    }
// ---------------------------
// loads the surveys from survey.txt
// ----------------------
    public static List<Survey> loadSurveys() {
        List<Survey> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("surveys.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",", 3);
                Survey s = new Survey();
                s.studentId = Integer.parseInt(p[0]);
                s.courseId  = Integer.parseInt(p[1]);
                s.feedback  = p.length > 2 ? p[2] : "";
                list.add(s);
            }
        } catch (Exception ignored) {}
        return list;
    }
// -------------------------
// saves the survey from the student into surveys.txt
// --------------------------------------
    public static void saveSurveys(List<Survey> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("surveys.txt"))) {
            for (Survey s : list) pw.println(s);
        } catch (Exception ignored) {}
    }

//------------------------------
// makes our files
// -------------------------------------
    public static void ensureFiles() {
        String[] files = {"users.txt", "courses.txt", "enrollments.txt", "surveys.txt"};
        for (String f : files) {
            try { new File(f).createNewFile(); } catch (Exception ignored) {}
        }
    }
}
