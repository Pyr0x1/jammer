package it.pyrox.jammer.desktop.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import it.pyrox.jammer.core.controller.MemoryCardController;
import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.core.model.MemoryCard;
import it.pyrox.jammer.desktop.util.Constants;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainApplicationController implements Initializable {
	
	@FXML
	private VBox container;
	
	@FXML
	private TilePane tilePane1;
	
	@FXML
	private VBox actionButtonsContainer;
	
	@FXML
	private TilePane tilePane2;
	
	@FXML
	private Label blockDescriptionLabel;
	
	private MemoryCard memoryCard1;
	
	private MemoryCard memoryCard2;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {				
		for (int i = 0; i < 2; i++) {			
			TilePane tilePaneTmp = tilePane1;
			if (i > 0) {
				tilePaneTmp = tilePane2;				
			}			
			for (int j = 0; j < Constants.NUM_BLOCK_ROWS; j++) {				
				for (int k = 0; k < Constants.NUM_BLOCK_COLS; k++) {
					// default empty image
					Image image = new Image(getClass().getResourceAsStream("/" + Constants.EMPTY_BLOCK_PNG_FILE), 32, 32, true, false);		
					setBlockImage(tilePaneTmp, image, getLinearIndex(j, k), false);		
				}				
			}
		}
	}
	
	@FXML
	private void loadMemoryCard1(final ActionEvent event) {
		loadMemoryCard(event, 1);
	}
	
	@FXML
	private void loadMemoryCard2(final ActionEvent event) {
		loadMemoryCard(event, 2);
	}
	
	private void loadMemoryCard(final ActionEvent actionEvent, int memoryCardSlot) {
		TilePane tilePaneTmp = memoryCardSlot == 1 ? tilePane1 : tilePane2;
	    Stage stage = (Stage) tilePane1.getScene().getWindow();
	    ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, Locale.getDefault());
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(bundle.getString("file.type.mcr"), "*.mcr");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(stage);
		MemoryCard memoryCardTmp = null;
		try {			
			memoryCardTmp = MemoryCardController.getInstance(file);
			// save open memory cards in the controller
			if (memoryCardSlot == 1) {
				memoryCard1 = memoryCardTmp;
			}
			else if (memoryCardSlot == 2) {
				memoryCard2 = memoryCardTmp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		tilePaneTmp.getChildren().clear();
		for (int j = 0; j < Constants.NUM_BLOCK_ROWS; j++) {	
			for (int k = 0; k < Constants.NUM_BLOCK_COLS; k++) {
				// default empty image
				Image defaultImage = new Image(getClass().getResourceAsStream("/" + Constants.EMPTY_BLOCK_PNG_FILE), 32, 32, true, false);
				Image blockImage = getBlockImage(memoryCardTmp, getLinearIndex(j, k));
				if (blockImage != null) {
					setBlockImage(tilePaneTmp, blockImage, getLinearIndex(j, k), true);
				}
				else {
					setBlockImage(tilePaneTmp, defaultImage, getLinearIndex(j, k), false);
				}
			}				
		}
	}
	
	@FXML
	private void exit(final ActionEvent event) {
		Platform.exit();
	}
	
	/**
	 * Custom method to create an Image based on a BufferedImage without antialising
	 * when resizing. Needed because Image constructor with smooth parameter can't be used
	 * in this case because image is not from a URL and setting the smooth parameter
	 * in the enclosing ImageView doesn't work
	 * 
	 * @param bufferedImage The input image
	 * @param scale The scale factor for the result image
	 * @return
	 */
	private Image getScaledAntialisedImageFromBufferedImage(BufferedImage bufferedImage, int scale) {
		Image imageFromSwing = SwingFXUtils.toFXImage(bufferedImage, null);
		int width = (int) imageFromSwing.getWidth();
		int height = (int) imageFromSwing.getHeight();
		WritableImage resultImage = new WritableImage(width * scale, height * scale);
		PixelReader pixelReader = imageFromSwing.getPixelReader();
		PixelWriter pixelWriter = resultImage.getPixelWriter();
		for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = pixelReader.getColor(i, j);
                for (int k = 0; k < scale; k++) {
                	for (int w = 0; w < scale; w++) {                		
                		pixelWriter.setColor(i * scale + k, j * scale + w, color);
                	}
                }
            }
        }		
		return resultImage;
	}
	
	private void setBlockImage(TilePane tilePaneTmp, Image image, int index, boolean clickable) {
		ImageView imageViewTmp = new ImageView(image);				
		StackPane tmpPane = new StackPane();
		tmpPane.getChildren().add(imageViewTmp);
		tmpPane.setPadding(new Insets(5));
		tmpPane.setId(getBlockIdFromIndex(index));
		if (clickable) {
			tmpPane.getStyleClass().add("selected");
			tmpPane.setOnMouseClicked(event -> {
				MemoryCard memoryCard = null;
				Pane clickedPane = (Pane) event.getSource();
				boolean selected = clickedPane.getPseudoClassStates().contains(Constants.PSEUDO_CLASS_CHECKED);
				clickedPane.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, !selected);
				if (clickedPane.getParent().equals(tilePane1)) {
					memoryCard = memoryCard1;
				}
				else if (clickedPane.getParent().equals(tilePane2)) {
					memoryCard = memoryCard2;
				}
				int clickedIndex = getBlockIndexFromPane(clickedPane);
				// Select linked blocks
				List<Block> blockList = MemoryCardController.findLinkedBlocks(memoryCard, clickedIndex);
				for (Block block : blockList) {
					Pane linkedPane = findImagePaneById(tilePaneTmp, getBlockIdFromIndex(block.getIndex()));					
					linkedPane.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, !selected);					
				}
				// Update block description
				blockDescriptionLabel.setText(blockList.get(0).getTitle());
			});
		}
		tilePaneTmp.getChildren().add(tmpPane);
	}
	
	private int getLinearIndex(int row, int col) {
		return row * Constants.NUM_BLOCK_COLS + col;
	}
	
	private int getBlockIndexFromPane(Pane clickedPane) {
		String id = clickedPane.getId();
		String[] splittedId = id.split(Constants.IMAGE_PANE_BASE_ID);
		return Integer.parseInt(splittedId[1]);
	}
	
	private String getBlockIdFromIndex(int index) {
		return Constants.IMAGE_PANE_BASE_ID + index;
	}
	
	private Pane findImagePaneById(TilePane tilePane, String id) {
		Pane result = null;
		for (Node node : tilePane.getChildren()) {
			if (id.equals(((Pane) node).getId())) {
				result = (Pane) node;
				break;
			}
		}
		return result;
	}
	
	private Image getBlockImage(MemoryCard memoryCard, int index) {
		Image result = null;
		List<Block> blockList = MemoryCardController.findLinkedBlocks(memoryCard, index);
		for (Block block : blockList) {
			BufferedImage[] icons = block.getIcons();
			if (icons != null && icons.length > 0) {
				result = getScaledAntialisedImageFromBufferedImage(icons[0], 2);
				break;
			}
		}
		return result;
	}
}
