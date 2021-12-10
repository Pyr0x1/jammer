package it.pyrox.jammer.desktop;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import it.pyrox.jammer.desktop.util.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApplication extends Application {
	
	public static void main(String[] args) {
        launch(args);
    }
	
	@Override
	public void start(Stage primaryStage) throws Exception {				
		Locale locale = new Locale("en", "EN");
		ResourceBundle bundle = ResourceBundle.getBundle("locale.strings", locale);
		URL url = this.getClass().getResource("/Application.fxml");		
		FXMLLoader loader = new FXMLLoader(url, bundle);
        
        VBox vbox = loader.<VBox>load();
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("css/style.css");
        primaryStage.setScene(scene);
        setStageProperties(primaryStage);
        primaryStage.show();		
	}
	
	private void setStageProperties(Stage primaryStage) {
		primaryStage.setTitle(Constants.TITLE);
		primaryStage.setResizable(false);
	}
}