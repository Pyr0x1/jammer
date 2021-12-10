package it.pyrox.jammer.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import it.pyrox.jammer.desktop.util.Constants;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class MainApplicationController implements Initializable {
	
	@FXML
	private VBox container;
	
	@FXML
	private TilePane tilePane1;
	
	@FXML
	private VBox actionButtonsContainer;
	
	@FXML
	private TilePane tilePane2;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {				
		for (int i = 0; i < 2; i++) {
			TilePane tilePaneTmp = tilePane1;
			if (i > 0) {
				tilePaneTmp = tilePane2;
			}
			for (int j = 0; j < Constants.NUM_BLOCK_ROWS; j++) {
				for (int k = 0; k < Constants.NUM_BLOCK_COLS; k++) {				
					Image image = new Image(getClass().getResourceAsStream("/block.png"), 32, 32, true, false);
					ImageView imageViewTmp = new ImageView(image);															
					tilePaneTmp.getChildren().add(imageViewTmp);					
				}				
			}
		}
	}
}
