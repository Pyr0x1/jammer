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
		Locale locale = Locale.getDefault();
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, locale);
		URL url = this.getClass().getResource("/" + Constants.FXML_FILE);		
		FXMLLoader loader = new FXMLLoader(url, bundle);
        
        VBox vbox = loader.<VBox>load();
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(Constants.CSS_FILE);
        primaryStage.setScene(scene);
        setStageProperties(primaryStage);
        primaryStage.show();		
	}
	
	private void setStageProperties(Stage primaryStage) {
		primaryStage.setTitle(Constants.TITLE);
		primaryStage.setResizable(false);
	}
}