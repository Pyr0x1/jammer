package it.pyrox.jammer.core.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.ibm.icu.text.Transliterator;

import it.pyrox.jammer.core.enums.RegionEnum;
import it.pyrox.jammer.core.enums.SaveTypeEnum;
import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.core.model.RawBlock;
import it.pyrox.jammer.core.util.Constants;

public class BlockController {
	
	private BlockController() {}
	
	public static Block createParsedBlock(RawBlock rawBlock, int slotNumber) {
		Block block = new Block(rawBlock);
		parse(block, slotNumber);
		return block;
	}
	
	public static void parse(Block block, int slotNumber) {
		block.setIndex(slotNumber);
		block.setNextLinkIndex(parseNextLinkIndex(block));
		block.setCountryCode(parseCountryCode(block));
		block.setProductCode(parseProductCode(block));
		block.setIdentifier(parseIdentifier(block));
		block.setSaveType(parseSaveType(block));
		block.setSaveSize(parseSaveSize(block));
		block.setRawIndex(parseRawIndex(block));
		block.setTitle(parseTitle(block));
		block.setNumFrames(parseNumFrames(block));
		block.setColorPalette(parsePalette(block));
		block.setIcons(parseIcons(block));
	}
	
	public static void serialize(Block block) {
		serializeIndex(block);
		serializeNextLinkIndex(block);
		serializeSaveType(block);
		calculateChecksum(block);
	}
	
	private static int parseNextLinkIndex(RawBlock rawBlock) {
		int linkIndex = 0;
		linkIndex += rawBlock.getHeader()[8];
		linkIndex = linkIndex << 24;
		linkIndex = linkIndex >>> 24;
		return linkIndex;
	}
	
	private static RegionEnum parseCountryCode(RawBlock rawBlock) {
		byte[] countryCode = new byte[2];
		countryCode[0] = rawBlock.getHeader()[10];
		countryCode[1] = rawBlock.getHeader()[11];
		String regionCode = new String(countryCode, StandardCharsets.US_ASCII);
		RegionEnum regionEnum = RegionEnum.getEnumByValue(regionCode);
		return regionEnum;
	}
	
	private static String parseProductCode(RawBlock rawBlock) {
		byte[] productCode = new byte[10];
		for (int i = 0; i < productCode.length; i++)
			productCode[i] = rawBlock.getHeader()[12 + i];
		return new String(productCode, StandardCharsets.US_ASCII);
	}
	
	private static String parseIdentifier(RawBlock rawBlock) {
		byte[] identifier = new byte[8];
		for (int i = 0; i < identifier.length; i++)
			identifier[i] = rawBlock.getHeader()[22 + i];
		return new String(identifier, StandardCharsets.US_ASCII);
	}
	
	private static int parseRawIndex(RawBlock rawBlock) {
		int index = 0;
		index += rawBlock.getSave()[3];
		index = index << 24;
		index = index >>> 24;
		return index;		
	}
	
	private static String parseTitle(RawBlock rawBlock) {
		byte[] title = new byte[Constants.TITLE_SIZE];
		for (int i = 0; i < title.length; i++) {
			title[i] = rawBlock.getSave()[4 + i];
		}
		String titleString = new String(title, Charset.forName(Constants.SHIFT_JIS));
		// Remove characters after first null otherwise there are problems with labels
		if (titleString.indexOf(0) != -1) {
			titleString = titleString.substring(0, titleString.indexOf(0) + 1);			
		}
		Transliterator transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
		titleString = transliterator.transliterate(titleString);
		if (titleString.length() < Constants.TITLE_SIZE) {
			// Right pad the string with spaces so it is of fixed length
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < Constants.TITLE_SIZE; i++) {
				stringBuilder.append(" ");
			}
			titleString = titleString + stringBuilder.substring(titleString.length());
		}
		return titleString;
	}
	
	private static SaveTypeEnum parseSaveType(RawBlock rawBlock) {
		byte rawSaveType = rawBlock.getHeader()[0];
		return SaveTypeEnum.getEnumByValue(rawSaveType);
	}
	
	private static int parseSaveSize(RawBlock rawBlock) {
		int size = 0;
		byte[] sizeTmp = new byte[3];
		for (int i = 0; i < sizeTmp.length; i++) {
			sizeTmp[i] = rawBlock.getHeader()[4 + i];
		}
		size += sizeTmp[2];
		size = size << 24;
		size = size >>> 24;
		size += sizeTmp[1] << 8;
		size = size << 16;
		size = size >>> 16;
		size += sizeTmp[0] << 16;
		size = size << 8;
		size = size >>> 8;
		return size;
	}
	
	private static int parseNumFrames(RawBlock rawBlock) {
		byte numFrames = rawBlock.getSave()[2];
		switch(numFrames) {
		case 0x11:
			return 1;
		case 0x12:
			return 2;
		case 0x13:
			return 3;
		default:
			return 0;
		}
	}
	
	private static Color[] parsePalette(RawBlock rawBlock) {
		int red = 0;
		int green = 0;
		int blue = 0;
		int mask = 0;
		int counter = 0;
		Color[] colorPalette = new Color[Constants.NUM_COLORS];
		
		for (int byteCount = 0; byteCount < Constants.NUM_COLORS * 2; byteCount += 2) {
			red = ((rawBlock.getSave()[byteCount + 96] & 0x1F) << 3);
			green = ((rawBlock.getSave()[byteCount + 97] & 0x3) << 6) | ((rawBlock.getSave()[byteCount + 96] & 0xE0) >> 2);
			blue = ((rawBlock.getSave()[byteCount + 97] & 0x7C) << 1);
			mask = (rawBlock.getSave()[byteCount + 97] & 0x80);
			if ((red | green | blue | mask) == 0)
				colorPalette[counter] = new Color(Color.TRANSLUCENT);
			else
				colorPalette[counter] = new Color(red, green, blue);
			counter++;
		}
		return colorPalette;
	}
	
	private static BufferedImage[] parseIcons(Block block) {
		int numFrames = block.getNumFrames();
		BufferedImage[] icons = new BufferedImage[numFrames];
		for (int iconNumber = 0; iconNumber < numFrames; iconNumber++) {
			icons[iconNumber] = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			int byteCount = Constants.HEADER_SIZE + (Constants.HEADER_SIZE * iconNumber);
			int[][] pixelsMat = new int[16][16];
			byte colorIndex = 0;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x += 2) {
                	colorIndex = (byte) (block.getSave()[byteCount] & 0xF);
                    pixelsMat[x][y] = block.getColorPalette()[colorIndex].getRGB();
                    colorIndex = (byte) ((block.getSave()[byteCount] >> 4) & 0xF);
                    pixelsMat[x + 1][y] = block.getColorPalette()[colorIndex].getRGB();
                    byteCount++;
                }
            }
            int[] pixelArray = new int[16 * 16];
            int k = 0;
            for (int i = 0; i < 16; i++) {
            	for (int j = 0; j < 16; j++) {
            		pixelArray[k] = pixelsMat[j][i];
            		k++;
            	}
            }
            WritableRaster raster = icons[iconNumber].getRaster();
            raster.setDataElements(0, 0, 16, 16, pixelArray);
		}
		return icons;
	}
	
	private static void serializeSaveType(Block block) {
		block.getHeader()[0] = block.getSaveType().getValue();
	}
	
	private static void serializeIndex(Block block) {
		// Overwrites raw index with the index set while reading the card
		int index = block.getIndex();
		block.getHeader()[3] = (byte) index;
	}
	
	private static void serializeNextLinkIndex(Block block) {
		int nextLinkIndex = block.getNextLinkIndex();
		block.getHeader()[8] = (byte) nextLinkIndex;
	}
	
	private static void calculateChecksum(Block block) {
		byte checkSum = 0x00;
		
		for (int i = 0; i < Constants.HEADER_SIZE - 2; i++) {
			checkSum ^= block.getHeader()[i];
		}
		
		block.getHeader()[Constants.HEADER_SIZE - 1] = checkSum;
	}
	
	public static boolean isNextLinkValid(Block block) {
		int index = block.getNextLinkIndex();
		// If next link index is 0xFF there is no other linked block
		return index >= 0 && index < Constants.NUM_BLOCKS;		
	}
	
	public static void format(Block block) {
		block.clearRawData();
		// Place default values in header
		block.getHeader()[0] = (byte) 0xA0;
		block.getHeader()[8] = (byte) 0xFF;
		block.getHeader()[9] = (byte) 0xFF;        
	}
	
	public static void toggleSaveTypeDeleted(Block block) {
		switch (block.getSaveType()) {
			case INITIAL:
				block.setSaveType(SaveTypeEnum.INITIAL_DELETED);
				break;
			case MIDDLE_LINK:
				block.setSaveType(SaveTypeEnum.MIDDLE_LINK_DELETED);
				break;
			case END_LINK:
				block.setSaveType(SaveTypeEnum.END_LINK_DELETED);
				break;
			case INITIAL_DELETED:
				block.setSaveType(SaveTypeEnum.INITIAL);
				break;
			case MIDDLE_LINK_DELETED:
				block.setSaveType(SaveTypeEnum.MIDDLE_LINK);
				break;
			case END_LINK_DELETED:
				block.setSaveType(SaveTypeEnum.END_LINK);
				break;
			default:
				break;
		}
	}
}
