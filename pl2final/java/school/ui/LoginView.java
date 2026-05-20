package school.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import school.model.User;
import school.service.AuthService;

import java.util.function.Consumer;

public class LoginView extends VBox {

    public LoginView(Consumer<User> onLogin) {
        setSpacing(10);
        setPadding(new Insets(30));

        Label title    = new Label("School Management System");
        TextField email = new TextField();
        email.setPromptText("Email");
        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");
        Label error = new Label();
        Button btn  = new Button("Login");

        btn.setOnAction(e -> {
            User user = AuthService.login(email.getText().trim(), pass.getText());
            if (user == null) { error.setText("Invalid credentials."); }
            else              { onLogin.accept(user); }
        });

        getChildren().addAll(title, email, pass, btn, error);
    }
}
