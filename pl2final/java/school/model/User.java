package school.model;

public class User {
    public int    id;
    public String name;
    public String email;
    public String password;
    public Role   role;

    @Override
    public String toString() {
        return id + "," + name + "," + email + "," + password + "," + role;
    }
}
