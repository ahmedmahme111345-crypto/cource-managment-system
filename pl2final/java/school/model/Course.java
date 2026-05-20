package school.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// =========================================
// 📘 COURSE MODEL (Represents a course)
// =========================================
static class Course {
    int         id;              // Course ID
    String      name;            // Course name
    String      parentCourse;    // Parent course (if any)
    int         instructorId;    // Instructor ID
    String      room;            // Room name/number
    String      branch;          // Branch location
    double      price;           // Course price
    String      days;            // Days of the course (e.g. Sun-Tue)
    LocalDate   startDate;       // Start date
    LocalDate   endDate;         // End date

    // List of student IDs enrolled in the course
    List<Integer> studentIds = new ArrayList<>();

    // Convert course object to CSV format for storage
    @Override
    public String toString() {
        String students = String.join("|",
                studentIds.stream().map(String::valueOf).toArray(String[]::new));

        return id + "," + name + "," + parentCourse + "," + instructorId + ","
                + room + "," + branch + "," + price + "," + days + ","
                + startDate + "," + endDate + "," + students;
    }
}
