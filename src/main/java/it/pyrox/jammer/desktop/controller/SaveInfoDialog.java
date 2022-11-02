package it.pyrox.jammer.desktop.controller;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import it.pyrox.jammer.core.enums.RegionEnum;
import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.desktop.util.Constants;
import it.pyrox.jammer.desktop.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

public class SaveInfoDialog extends Dialog<Void> {
	
	@FXML
	private Label contentTitle;
	
	@FXML
	private Label contentProductCode;
	
	@FXML
	private Label contentIdentifier;
	
	@FXML
	private Label contentRegion;
	
	@FXML
	private Label contentSlot;
	
	@FXML
	private Label contentSize;
	
	@FXML
	private Label contentIconFrames;
	
	@FXML
	private ImageView contentIcon;
	
	private List<Block> blockList;
	
	public SaveInfoDialog() {
		
	}
	
	public SaveInfoDialog(Window owner, List<Block> blockList) {
		try  {
			this.blockList = blockList;
			Locale locale = Locale.getDefault();
			ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, locale);
			URL url = this.getClass().getResource("/" + Constants.DIALOG_FXML_FILE);	
			FXMLLoader loader = new FXMLLoader(url, bundle);
			loader.setController(this);
	        DialogPane dialogPane = loader.load();     
	        initOwner(owner);
	        setTitle(bundle.getString("dialog.save.info.title"));            
            setDialogPane(dialogPane);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@FXML
    private void initialize() {
		contentTitle.setText(blockList.get(0).getTitle().trim());
		contentProductCode.setText(blockList.get(0).getProductCode());
		contentIdentifier.setText(blockList.get(0).getIdentifier());
		RegionEnum region = blockList.get(0).getCountryCode();
		String regionDescription = getTranscodedRegion(region);
		contentRegion.setText(regionDescription);
		StringBuilder stringBuilder = new StringBuilder("");
		Integer size = 0;
		for (Block block : blockList) {
			stringBuilder.append(block.getIndex() + 1);
			if (!block.equals(blockList.get(blockList.size() - 1))) {
				stringBuilder.append(", ");
			}
			size += block.getSaveSize() / 1000;
		}
		contentSlot.setText(stringBuilder.toString());
		contentSize.setText(size + " " + Constants.KB);
		contentIconFrames.setText(Integer.toString(blockList.get(0).getIcons().length));
		Image image = Utils.getScaledAntialisedImageFromBufferedImage(blockList.get(0).getIcons()[0], 4);
		contentIcon.setImage(image);
    }
	
	private String getTranscodedRegion(RegionEnum region) {
		String result = "";
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.LOCALE_FILE, Locale.getDefault());
		if (RegionEnum.AMERICA.getCode().equals(region.getCode())) {
			result = bundle.getString("region.description.america");
		}
		else if (RegionEnum.EUROPE.getCode().equals(region.getCode())) {
			result = bundle.getString("region.description.europe");
		}
		else if (RegionEnum.JAPAN.getCode().equals(region.getCode())) {
			result = bundle.getString("region.description.japan");
		}
		return result;
	}
}
