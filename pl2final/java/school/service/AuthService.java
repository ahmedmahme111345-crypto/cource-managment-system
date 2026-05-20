package school.service;

import school.model.User;
import school.store.DataStore;

public class AuthService {

    public static User login(String email, String password) {
        for (User u : DataStore.loadUsers())
            if (u.email.equals(email) && u.password.equals(password))
                return u;
        return null;
    }
}
