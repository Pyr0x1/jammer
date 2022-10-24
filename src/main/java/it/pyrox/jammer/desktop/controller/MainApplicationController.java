package it.pyrox.jammer.desktop.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
	
	private Map<String, StackPane> imagePaneMap1;
	
	private Map<String, StackPane> imagePaneMap2;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {				
		for (int i = 0; i < 2; i++) {			
			TilePane tilePaneTmp = tilePane1;
			imagePaneMap1 = new HashMap<>();
			if (i > 0) {
				tilePaneTmp = tilePane2;
				imagePaneMap2 = new HashMap<>();
			}			
			for (int index = 0; index < Constants.NUM_BLOCKS; index++) {								
				// default empty image
				Image image = getDefaultImage();		
				setBlockImage(tilePaneTmp, image, index, false);									
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
	
	@FXML
	private void exit(final ActionEvent event) {
		Platform.exit();
	}
	
	private void loadMemoryCard(final ActionEvent actionEvent, int memoryCardSlot) {
		TilePane tilePaneTmp = memoryCardSlot == 1 ? tilePane1 : tilePane2;
		File file = openFileChooserDialogAndGetFile();
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
		for (int index = 0; index < Constants.NUM_BLOCKS; index++) {			
			// default empty image
			Image defaultImage = getDefaultImage();
			Image blockImage = getBlockImage(memoryCardTmp, index);
			if (blockImage != null) {
				setBlockImage(tilePaneTmp, blockImage, index, true);
			}
			else {
				setBlockImage(tilePaneTmp, defaultImage, index, false);
			}			
		}
	}
	
	private void setBlockImage(TilePane tilePaneTmp, Image image, int index, boolean clickable) {
		ImageView imageViewTmp = new ImageView(image);				
		StackPane tmpPane = new StackPane();
		tmpPane.getChildren().add(imageViewTmp);
		tmpPane.setPadding(new Insets(5));
		tmpPane.setId(getBlockIdFromIndex(tilePaneTmp, index));
		if (clickable) {
			tmpPane.getStyleClass().add("selected");
			tmpPane.setOnMouseClicked(event -> {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
		            if (event.getClickCount() == 1) {
		            	handleSingleClickedImageBlock(event, tilePaneTmp);
		            }
		            else if (event.getClickCount() == 2) {
		            	handleDoubleClickedImageBlock(event, tilePaneTmp);
		            }
				}
			});
		}
		tilePaneTmp.getChildren().add(tmpPane);
		Map<String, StackPane> mapTmp = getMapFromTilePane(tilePaneTmp);
		mapTmp.put(tmpPane.getId(), tmpPane);
	}
	
	private void handleSingleClickedImageBlock(MouseEvent event, TilePane tilePaneTmp) {
		MemoryCard memoryCard = null;
		Pane clickedPane = (Pane) event.getSource();
		deselectAllImageBlocks(tilePaneTmp);
		clickedPane.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, true);
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
			Pane linkedPane = findImagePaneById(tilePaneTmp, getBlockIdFromIndex(tilePaneTmp, block.getIndex()));				
			linkedPane.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, true);					
		}
		// Update block description
		blockDescriptionLabel.setText(blockList.get(0).getTitle());
	}
	
	private void handleDoubleClickedImageBlock(MouseEvent event, TilePane tilePaneTmp) {
		MemoryCard memoryCard = null;
		Pane clickedPane = (Pane) event.getSource();
		if (clickedPane.getParent().equals(tilePane1)) {
			memoryCard = memoryCard1;
		}
		else if (clickedPane.getParent().equals(tilePane2)) {
			memoryCard = memoryCard2;
		}
		int clickedIndex = getBlockIndexFromPane(clickedPane);
		List<Block> blockList = MemoryCardController.findLinkedBlocks(memoryCard, clickedIndex);
		showSaveInfoDialog(blockList);
	}
	
	private void showSaveInfoDialog(List<Block> blockList) {
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, Locale.getDefault());
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(bundle.getString("dialog.save.info.title"));
		dialog.setHeaderText(null);		
		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		addHeaderLabelInDialogPanelGrid(gridPane, bundle, "dialog.save.info.content.title", 0, 0);		
		gridPane.add(new Label(blockList.get(0).getTitle().trim()), 1, 0);
		addHeaderLabelInDialogPanelGrid(gridPane, bundle, "dialog.save.info.content.product.code", 0, 1);
		gridPane.add(new Label(blockList.get(0).getProductCode()), 1, 1);
		addHeaderLabelInDialogPanelGrid(gridPane, bundle, "dialog.save.info.content.identifier", 0, 2);
		gridPane.add(new Label(blockList.get(0).getIdentifier()), 1, 2);
		addHeaderLabelInDialogPanelGrid(gridPane, bundle, "dialog.save.info.content.region", 0, 3);
		gridPane.add(new Label(blockList.get(0).getCountryCode()), 1, 3);
		addHeaderLabelInDialogPanelGrid(gridPane, bundle, "dialog.save.info.content.slot", 0, 4);
		String slots = "";
		Integer size = 0;
		for (Block block : blockList) {
			slots += block.getIndex() + 1;
			if (!block.equals(blockList.get(blockList.size() - 1))) {
				slots += ", ";
			}
			size += block.getSaveSize() / 1000;
		}
		gridPane.add(new Label(slots), 1, 4);
		addHeaderLabelInDialogPanelGrid(gridPane, bundle, "dialog.save.info.content.size", 0, 5);		
		gridPane.add(new Label(size + " " + Constants.KB), 1, 5);
		addHeaderLabelInDialogPanelGrid(gridPane, bundle, "dialog.save.info.content.icon.frames", 0, 6);		
		gridPane.add(new Label(Integer.toString(blockList.get(0).getIcons().length)), 1, 6);
		Image image = getScaledAntialisedImageFromBufferedImage(blockList.get(0).getIcons()[0], 4);
		ImageView imageView = new ImageView(image);		
		StackPane tmpPane = new StackPane();
		tmpPane.getChildren().add(imageView);
		HBox hBox = new HBox(10);		
		hBox.getChildren().add(imageView);
		hBox.getChildren().add(gridPane);
		dialog.getDialogPane().setContent(hBox);
		dialog.getDialogPane().getButtonTypes().add(new ButtonType(bundle.getString("dialog.save.info.button"), ButtonData.CANCEL_CLOSE));
		dialog.showAndWait();
	}
	
	private void addHeaderLabelInDialogPanelGrid(GridPane gridPane, ResourceBundle bundle, String key, int col, int row) {
		Label label = new Label(bundle.getString(key));
		label.setStyle("-fx-font-weight: bold;");
		gridPane.add(label, col, row);
		GridPane.setHalignment(label, HPos.RIGHT);		
	}
	
	private int getBlockIndexFromPane(Pane clickedPane) {
		String id = clickedPane.getId();
		String[] splittedId = id.split(Constants.IMAGE_PANE_BASE_ID);
		return Integer.parseInt(splittedId[1]);
	}
	
	private String getBlockIdFromIndex(TilePane tilePaneTmp, int index) {
		return tilePaneTmp.getId() + "_" + Constants.IMAGE_PANE_BASE_ID + index;
	}
	
	private Pane findImagePaneById(TilePane tilePane, String id) {
		Map<String, StackPane> mapTmp = getMapFromTilePane(tilePane);
		return mapTmp.get(id);		
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
	
	private Image getDefaultImage() {
		return new Image(getClass().getResourceAsStream("/" + Constants.EMPTY_BLOCK_PNG_FILE), 32, 32, true, false);
	}
	
	private void deselectAllImageBlocks(TilePane tilePaneTmp) {
		for (Node node : tilePaneTmp.getChildren()) {
			node.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, false);
		}
	}
	
	private File openFileChooserDialogAndGetFile() {
		Stage stage = (Stage) tilePane1.getScene().getWindow();
	    ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, Locale.getDefault());
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(bundle.getString("file.type.mcr"), "*.mcr");
		fileChooser.getExtensionFilters().add(extFilter);
		return fileChooser.showOpenDialog(stage);		
	}
	
	private Map<String, StackPane> getMapFromTilePane(TilePane tilePane) {
		Map<String, StackPane> map = null;
		if (tilePane1.equals(tilePane)) {
			map = imagePaneMap1;
		}
		else if (tilePane2.equals(tilePane)) {
			map = imagePaneMap2;
		}
		return map;
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
}
