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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
	
	@FXML
	private Button deleteButton;
	
	@FXML
	private Button restoreButton;
	
	@FXML
	private Button copyButton;
	
	@FXML
	private Button copyAllButton;
	
	private Stage stage;
	
	private MemoryCard memoryCard1;
	
	private MemoryCard memoryCard2;
	
	private File memoryCard1File;
	
	private File memoryCard2File;
	
	private Map<String, StackPane> imagePaneMap1;
	
	private Map<String, StackPane> imagePaneMap2;
	
	private String lastFileChooserDirectory;
	
	private List<Block> selectedBlocks;
	
	private MemoryCard selectedMemoryCard;
	
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
		disableAllButtons();
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
	private void saveMemoryCard1(final ActionEvent event) {
		saveMemoryCard(1);
	}
	
	@FXML
	private void saveMemoryCard2(final ActionEvent event) {
		saveMemoryCard(2);
	}
	
	@FXML
	private void deleteSaveFile(final ActionEvent event) {
		toggleSaveFileDeletedState(true);
	}
	
	@FXML
	private void restoreSaveFile(final ActionEvent event) {
		toggleSaveFileDeletedState(false);
	}
	
	@FXML
	private void exit(final ActionEvent event) {
		Platform.exit();
	}
	
	private void toggleSaveFileDeletedState(boolean isDelete) {
		if (selectedBlocks != null && !selectedBlocks.isEmpty()) {
			MemoryCardController.toggleSaveTypeDeleted(selectedMemoryCard, selectedBlocks.get(0).getIndex());
			TilePane tilePaneTmp = selectedMemoryCard.equals(memoryCard1) ? tilePane1 : tilePane2;
			for (Block block : selectedBlocks) {
				Pane imagePane = findImagePaneById(tilePaneTmp, getBlockIdFromIndex(tilePaneTmp, block.getIndex()));
				ImageView imageView = (ImageView) imagePane.getChildren().get(0);
				toggleImageOpacity(imageView, SaveTypeEnum.isDeleted(block.getSaveType()));
			}
			toggleDeleteRestoreButtons(isDelete);
		}
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
				memoryCard1File = file;
			}
			else if (memoryCardSlot == 2) {
				memoryCard2 = memoryCardTmp;
				memoryCard2File = file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadMemoryCardBlocks(memoryCardTmp, tilePaneTmp);
	}
	
	private void saveMemoryCard(int memoryCardSlot) {
		MemoryCard memoryCard = memoryCardSlot == 1 ? memoryCard1 : memoryCard2;
		File memoryCardFile = memoryCardSlot == 1 ? memoryCard1File : memoryCard2File;
		if (memoryCard != null && memoryCardFile != null) {					
			if (isSaveConfirmationDialogOk()) {
				try {
					MemoryCardController.saveInstance(memoryCard, memoryCardFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}	
	
	private boolean isSaveConfirmationDialogOk() {
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, Locale.getDefault());
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(bundle.getString("dialog.confirmation.save.title"));
		alert.setHeaderText(null);
		alert.setContentText(bundle.getString("dialog.confirmation.save.content"));

		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;		
	}
	
	private void loadMemoryCardBlocks(MemoryCard memoryCard, TilePane tilePane) {
		tilePane.getChildren().clear();
		for (int index = 0; index < Constants.NUM_BLOCKS; index++) {			
			// default empty image
			Image defaultImage = getDefaultImage();
			List<Block> blockList = MemoryCardController.findLinkedBlocks(memoryCard, index);
			Image blockImage = getBlockImage(blockList);
			if (blockImage != null) {
				setBlockImage(tilePane, blockImage, blockList, index, true);
			}
			else {
				setBlockImage(tilePane, defaultImage, blockList, index, false);
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
			if (optionalBlock.isPresent() && SaveTypeEnum.isDeleted(optionalBlock.get().getSaveType())) {
				toggleImageOpacity(imageViewTmp, true);
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
		int clickedIndex = getBlockIndexFromClickedImagePane(clickedPane);
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
		// Save currently selected blocks and memory card
		selectedBlocks = blockList;
		selectedMemoryCard = memoryCard;
		// Enables all buttons at first
		enableAllButtons();
		// Update label for Delete/Restore buttons (check only if first block is deleted because linked ones are supposed to be the same)
		toggleDeleteRestoreButtons(SaveTypeEnum.isDeleted(blockList.get(0).getSaveType()));
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
		int clickedIndex = getBlockIndexFromClickedImagePane(clickedPane);
		List<Block> blockList = MemoryCardController.findLinkedBlocks(memoryCard, clickedIndex);
		showSaveInfoDialog(blockList);
	}
	
	private void showSaveInfoDialog(List<Block> blockList) {
		SaveInfoDialog dialog = new SaveInfoDialog(stage, blockList);
		dialog.showAndWait();
	}
	
	private int getBlockIndexFromClickedImagePane(Pane clickedPane) {
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
	
	private void toggleImageOpacity(ImageView imageView, boolean isBlockDeleted) {
		if (isBlockDeleted) {
			imageView.setOpacity(0.5);
		}
		else {
			imageView.setOpacity(1);
		}
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
	
	private void enableAllButtons() {
		deleteButton.setDisable(false);
		restoreButton.setDisable(false);
		copyButton.setDisable(false);
		copyAllButton.setDisable(false);
	}
	
	private void disableAllButtons() {
		deleteButton.setDisable(true);
		restoreButton.setDisable(true);
		copyButton.setDisable(true);
		copyAllButton.setDisable(true);
	}
	
	private void toggleDeleteRestoreButtons(boolean isDeleted) {
		deleteButton.setDisable(isDeleted);
		restoreButton.setDisable(!isDeleted);		
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
