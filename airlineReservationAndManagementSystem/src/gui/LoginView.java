package gui;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class LoginView {
	private MainApp mainApp;

    public LoginView(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public Parent getView() {
        VBox layout = new VBox();
        layout.getChildren().add(new Label("Login Ekranı (Yapım Aşamasında)"));
        // Buraya butonlar ve text field'lar gelecek
        return layout;
    }
}
