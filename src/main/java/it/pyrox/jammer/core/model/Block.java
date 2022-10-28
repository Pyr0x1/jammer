package it.pyrox.jammer.core.model;

import java.awt.Color;
import java.awt.image.BufferedImage;

import it.pyrox.jammer.core.enums.RegionEnum;
import it.pyrox.jammer.core.enums.SaveTypeEnum;

public class Block extends RawBlock {
	
	public enum SAVE_TYPE {FORMATTED,
						   INITIAL,
						   MIDDLE_LINK,
						   END_LINK,
						   INITIAL_DELETED,
						   MIDDLE_LINK_DELETED,
						   END_LINK_DELETED,
						   CORRUPTED};
	
	private int nextLinkIndex; 
	private RegionEnum countryCode;
	private String productCode;
	private String identifier;
	private int rawIndex; // Index parsed from raw save data block (seems often wrong, don't use it)
	private int index; // Index saved "manually" when reading memory card
	private String title;
	private SaveTypeEnum saveType;
	private int saveSize;
	private int numFrames;
	private Color colorPalette[];
	private BufferedImage icons[];

	public Block(byte[] header, byte[] save) {
		super(header, save);
	}
	
	public Block(RawBlock rawBlock) {
		super(rawBlock.getHeader(), rawBlock.getSave());
	}
	
	public int getNextLinkIndex() {
		return nextLinkIndex;
	}

	public void setNextLinkIndex(int nextLinkIndex) {
		this.nextLinkIndex = nextLinkIndex;
	}

	public RegionEnum getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(RegionEnum countryCode) {
		this.countryCode = countryCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getRawIndex() {
		return rawIndex;
	}

	public void setRawIndex(int rawIndex) {
		this.rawIndex = rawIndex;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public SaveTypeEnum getSaveType() {
		return saveType;
	}

	public void setSaveType(SaveTypeEnum saveType) {
		this.saveType = saveType;
	}

	public int getSaveSize() {
		return saveSize;
	}

	public void setSaveSize(int saveSize) {
		this.saveSize = saveSize;
	}

	public int getNumFrames() {
		return numFrames;
	}

	public void setNumFrames(int numFrames) {
		this.numFrames = numFrames;
	}

	public Color[] getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(Color[] colorPalette) {
		this.colorPalette = colorPalette;
	}

	public BufferedImage[] getIcons() {
		return icons;
	}

	public void setIcons(BufferedImage[] icons) {
		this.icons = icons;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("|");
		stringBuilder.append(countryCode != null ? countryCode.getCode() : "  ");
		stringBuilder.append("|");
		stringBuilder.append(productCode);
		stringBuilder.append("|");
		stringBuilder.append(identifier);
		stringBuilder.append("|");
		stringBuilder.append(title);
		stringBuilder.append("|");
		stringBuilder.append(saveType);
		stringBuilder.append("|");
		stringBuilder.append(nextLinkIndex);
		stringBuilder.append("|");
		stringBuilder.append(saveSize);
		stringBuilder.append("|");
		stringBuilder.append(numFrames);
		stringBuilder.append("|");
		return stringBuilder.toString();
	}
}
