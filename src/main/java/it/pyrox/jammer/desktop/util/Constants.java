package it.pyrox.jammer.desktop.util;

import javafx.css.PseudoClass;

public class Constants {
	
	public static final String TITLE = "Jammer";
	public static final String LOCALE_FILE = "locale.strings";
	public static final String FXML_FILE = "Application.fxml";
	public static final String CSS_FILE = "css/style.css";
	public static final String EMPTY_BLOCK_PNG_FILE = "empty-block.png";
	public static final int MIN_STAGE_WIDTH = 340;
	public static final int MIN_STAGE_HEIGHT = 280;
	public static final int NUM_BLOCK_ROWS = 5;
	public static final int NUM_BLOCK_COLS = 3;
	public static final PseudoClass PSEUDO_CLASS_CHECKED = PseudoClass.getPseudoClass("checked");
	public static final String IMAGE_PANE_BASE_ID = "imagePane";
	
	private Constants() {} // Prevents undesired inheritance
}
