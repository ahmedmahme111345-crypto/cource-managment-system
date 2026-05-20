package school.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import school.model.User;
import school.service.SeedData;
import school.store.DataStore;

public class App extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        DataStore.ensureFiles();
        SeedData.seedIfEmpty();
        stage.setTitle("SchoolMS");
        showLogin();
        stage.show();
    }

    private void showLogin() {
        stage.setScene(new Scene(new LoginView(this::showDashboard), 400, 300));
    }

    private void showDashboard(User user) {
        switch (user.role) {
            case ADMIN      -> stage.setScene(new Scene(new AdminView(user,      this::showLogin), 500, 500));
            case STUDENT    -> stage.setScene(new Scene(new StudentView(user,    this::showLogin), 500, 500));
            case INSTRUCTOR -> stage.setScene(new Scene(new InstructorView(user, this::showLogin), 500, 500));
        }
    }

    public static void main(String[] args) { launch(args); }
}
