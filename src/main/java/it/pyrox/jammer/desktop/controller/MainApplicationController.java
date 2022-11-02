package it.pyrox.jammer.desktop.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import it.pyrox.jammer.core.controller.MemoryCardController;
import it.pyrox.jammer.core.enums.SaveTypeEnum;
import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.core.model.MemoryCard;
import it.pyrox.jammer.desktop.util.Constants;
import it.pyrox.jammer.desktop.util.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
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
	
	private String lastFileChooserDirectory;
	
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
				setBlockImage(tilePaneTmp, image, null, index, false);									
			}
		}				
	}
	
	@FXML
	private void loadMemoryCard1(final ActionEvent event) {
		loadMemoryCard(1);
	}
	
	@FXML
	private void loadMemoryCard2(final ActionEvent event) {
		loadMemoryCard(2);
	}
	
	@FXML
	private void exit(final ActionEvent event) {
		Platform.exit();
	}
	
	private void loadMemoryCard(int memoryCardSlot) {
		TilePane tilePaneTmp = memoryCardSlot == 1 ? tilePane1 : tilePane2;
		File file = openFileChooserDialogAndGetFile();
		if (file == null) {
			return;
		}
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
			List<Block> blockList = MemoryCardController.findLinkedBlocks(memoryCardTmp, index);
			Image blockImage = getBlockImage(blockList);
			if (blockImage != null) {
				setBlockImage(tilePaneTmp, blockImage, blockList, index, true);
			}
			else {
				setBlockImage(tilePaneTmp, defaultImage, blockList, index, false);
			}			
		}
	}
	
	private void setBlockImage(TilePane tilePaneTmp, Image image, List<Block> blockList, int index, boolean clickable) {
		ImageView imageViewTmp = new ImageView(image);				
		StackPane tmpPane = new StackPane();
		tmpPane.getChildren().add(imageViewTmp);
		tmpPane.setPadding(new Insets(5));
		tmpPane.setId(getBlockIdFromIndex(tilePaneTmp, index));
		if (clickable) {
			Optional<Block> optionalBlock = blockList.stream().filter(e -> index == e.getIndex()).findFirst();			
			if (optionalBlock.isPresent() &&
				(SaveTypeEnum.INITIAL_DELETED.equals(optionalBlock.get().getSaveType()) ||
				SaveTypeEnum.MIDDLE_LINK_DELETED.equals(optionalBlock.get().getSaveType()) ||
				SaveTypeEnum.END_LINK_DELETED.equals(optionalBlock.get().getSaveType()))) {
				imageViewTmp.setOpacity(0.5);
			}			
			tmpPane.getStyleClass().add("selected");
			tmpPane.setOnMouseClicked(event -> {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
		            if (event.getClickCount() == 1) {
		            	handleSingleClickedImageBlock(event, tilePaneTmp);
		            }
		            else if (event.getClickCount() == 2) {
		            	handleDoubleClickedImageBlock(event);
		            }
				}
			});
		}
		tilePaneTmp.getChildren().add(tmpPane);
		Map<String, StackPane> mapTmp = getMapFromTilePane(tilePaneTmp);
		if (mapTmp != null) {
			mapTmp.put(tmpPane.getId(), tmpPane);
		}
	}
	
	private void handleSingleClickedImageBlock(MouseEvent event, TilePane tilePaneTmp) {
		MemoryCard memoryCard = null;
		Pane clickedPane = (Pane) event.getSource();
		deselectAllImageBlocks();
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
			if (linkedPane != null) {
				linkedPane.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, true);
			}
		}
		// Update block description
		blockDescriptionLabel.setText(blockList.get(0).getTitle());
	}
	
	private void handleDoubleClickedImageBlock(MouseEvent event) {
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
		Stage stage = (Stage) tilePane1.getScene().getWindow();
		SaveInfoDialog dialog = new SaveInfoDialog(stage, blockList);
		dialog.showAndWait();
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
		return mapTmp != null ? mapTmp.get(id) : null;		
	}
	
	private Image getBlockImage(List<Block> blockList) {
		Image result = null;		
		for (Block block : blockList) {
			BufferedImage[] icons = block.getIcons();
			if (icons != null && icons.length > 0) {
				result = Utils.getScaledAntialisedImageFromBufferedImage(icons[0], 2);
				break;
			}
		}
		return result;
	}
	
	private Image getDefaultImage() {
		return new Image(getClass().getResourceAsStream("/" + Constants.EMPTY_BLOCK_PNG_FILE), 32, 32, true, false);
	}
	
	private void deselectAllImageBlocks() {
		for (int i = 0; i < 2; i++) {			
			TilePane tilePaneTmp = tilePane1;			
			if (i > 0) {
				tilePaneTmp = tilePane2;
			}
			for (Node node : tilePaneTmp.getChildren()) {
				node.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, false);
			}
		}
	}
	
	private File openFileChooserDialogAndGetFile() {
		Stage stage = (Stage) tilePane1.getScene().getWindow();
	    ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, Locale.getDefault());
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(bundle.getString("file.type.mcr"), "*.mcr");
		fileChooser.getExtensionFilters().add(extFilter);
		if (lastFileChooserDirectory != null) {
			fileChooser.setInitialDirectory(new File(lastFileChooserDirectory));
		}
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) { 
			lastFileChooserDirectory = file.getParent();
		}
		return file;
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
}
