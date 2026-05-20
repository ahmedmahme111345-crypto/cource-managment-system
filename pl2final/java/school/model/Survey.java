package school.model;

public class Survey {
    public int    studentId;
    public int    courseId;
    public String feedback;

    @Override
    public String toString() {
        return studentId + "," + courseId + "," + feedback.replace(",", ";");
    }
}
