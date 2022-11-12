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
import javafx.stage.WindowEvent;

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
	private Button formatButton;
	
	@FXML
	private Button copyButton;
	
	@FXML
	private Button copyAllButton;
	
	// All stackPanes with ImageViews will be saved in this map for faster retrieval
	private Map<String, StackPane> imageViewPaneMap;
	
	// This is set after the initialize method
	private Stage stage;
	
	private MemoryCard memoryCard1;
	
	private MemoryCard memoryCard2;
	
	private File memoryCard1File;
	
	private File memoryCard2File;
	
	private boolean isMemoryCardChanged;
		
	private String lastFileChooserDirectory;
	
	private List<Block> selectedBlocks;
	
	private MemoryCard selectedMemoryCard;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Load map with imageViews from both panes
		imageViewPaneMap = new HashMap<String, StackPane>();
		for (Node node : tilePane1.getChildren()) {			
			imageViewPaneMap.put(node.getId(), (StackPane) node);
		}
		for (Node node : tilePane2.getChildren()) {
			imageViewPaneMap.put(node.getId(), (StackPane) node);
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
	private void formatSaveFile(final ActionEvent event) {
		if (selectedBlocks != null && !selectedBlocks.isEmpty()) {
			if (isFormatConfirmationDialogOk()) {
				TilePane tilePaneTmp = selectedMemoryCard.equals(memoryCard1) ? tilePane1 : tilePane2;
				// Change icon first and format then, otherwise block index is reset
				for (Block block : selectedBlocks) {
					Pane imagePane = imageViewPaneMap.get(getBlockIdFromIndex(tilePaneTmp, block.getIndex()));
					if (imagePane != null) {
						imagePane.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, false);
						imagePane.getStyleClass().remove("selected");
						imagePane.setOnMouseClicked(null);
						ImageView imageView = (ImageView) imagePane.getChildren().get(0);
						imageView.setImage(getDefaultImage());
					}
				}			
				MemoryCardController.format(selectedMemoryCard, selectedBlocks.get(0).getIndex());
				formatButton.setDisable(true);
				selectedBlocks = null;
				isMemoryCardChanged = true;
			}
		}
	}
	
	@FXML
	private void exit(final ActionEvent event) {
		// Needed to consume event in close handler
		stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
		Platform.exit();
	}
	
	public void setStageCloseHandler() {
		stage.setOnCloseRequest(event -> {
			if (isMemoryCardChanged && !isExitConfirmationDialogWithChangesOk()) {
				event.consume();
			}
		});
	}
	
	private void toggleSaveFileDeletedState(boolean isDelete) {
		if (selectedBlocks != null && !selectedBlocks.isEmpty()) {
			MemoryCardController.toggleSaveTypeDeleted(selectedMemoryCard, selectedBlocks.get(0).getIndex());
			TilePane tilePaneTmp = selectedMemoryCard.equals(memoryCard1) ? tilePane1 : tilePane2;
			for (Block block : selectedBlocks) {
				Pane imagePane = imageViewPaneMap.get(getBlockIdFromIndex(tilePaneTmp, block.getIndex()));
				if (imagePane != null) {
					ImageView imageView = (ImageView) imagePane.getChildren().get(0);
					toggleImageOpacity(imageView, SaveTypeEnum.isDeleted(block.getSaveType()));
				}
			}
			toggleDeleteRestoreButtons(isDelete);
			isMemoryCardChanged = true;
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
					isMemoryCardChanged = false;
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
	
	private boolean isExitConfirmationDialogWithChangesOk() {
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, Locale.getDefault());
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(bundle.getString("dialog.confirmation.exit.title"));
		alert.setHeaderText(null);
		alert.setContentText(bundle.getString("dialog.confirmation.exit.content"));

		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}
	
	private boolean isFormatConfirmationDialogOk() {
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, Locale.getDefault());
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(bundle.getString("dialog.confirmation.format.title"));
		alert.setHeaderText(null);
		alert.setContentText(bundle.getString("dialog.confirmation.format.content"));

		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}
	
	private void loadMemoryCardBlocks(MemoryCard memoryCard, TilePane tilePane) {
		for (Node imagePane : tilePane.getChildren()) {
			resetImagePane((StackPane) imagePane);
		}
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
	
	private void resetImagePane(StackPane imagePane) {
		imagePane.getStyleClass().clear();
		imagePane.pseudoClassStateChanged(Constants.PSEUDO_CLASS_CHECKED, false);
		imagePane.setOnMouseClicked(null);
	}
	
	private void setBlockImage(TilePane tilePaneTmp, Image image, List<Block> blockList, int index, boolean clickable) {
		StackPane imagePane = imageViewPaneMap.get(getBlockIdFromIndex(tilePaneTmp, index));
		ImageView imageView = (ImageView) imagePane.getChildren().get(0);
		imageView.setImage(image);
		if (clickable) {
			Optional<Block> optionalBlock = blockList.stream().filter(e -> index == e.getIndex()).findFirst();		
			if (optionalBlock.isPresent() && SaveTypeEnum.isDeleted(optionalBlock.get().getSaveType())) {
				toggleImageOpacity(imageView, true);
			}
			imagePane.getStyleClass().add("selected");			
			imagePane.setOnMouseClicked(event -> {
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
			Pane linkedPane = imageViewPaneMap.get(getBlockIdFromIndex(tilePaneTmp, block.getIndex()));
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
		String[] splittedId = id.split(Constants.IMAGE_PANE_BASE_ID + "_")[1].split("_");
		return Integer.parseInt(splittedId[1]);
	}
	
	private String getBlockIdFromIndex(TilePane tilePaneTmp, int index) {
		String tilePaneIndex = "";
		if (tilePane1.equals(tilePaneTmp)) {
			tilePaneIndex = "1";
		}
		else if (tilePane2.equals(tilePaneTmp)) {
			tilePaneIndex = "2";
		}
		return Constants.IMAGE_PANE_BASE_ID + "_" + tilePaneIndex + "_" + index;
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
	
	private void enableAllButtons() {
		deleteButton.setDisable(false);
		restoreButton.setDisable(false);
		formatButton.setDisable(false);
		copyButton.setDisable(false);
		copyAllButton.setDisable(false);
	}
	
	private void disableAllButtons() {
		deleteButton.setDisable(true);
		restoreButton.setDisable(true);
		formatButton.setDisable(true);
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
