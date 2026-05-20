package school.model;
// =========================================
// 📘 ENROLLMENT MODEL (Represents enrollment)
// =========================================
static class Enrollment {
    int    studentId;    // Student ID
    int    courseId;     // Course ID
    Double grade;        // Student grade (nullable)

    // Convert enrollment object to CSV format
    @Override
    public String toString() {
        return studentId + "," + courseId + "," + (grade == null ? "" : grade);
    }
}
