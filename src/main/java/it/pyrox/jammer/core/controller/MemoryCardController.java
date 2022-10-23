package it.pyrox.jammer.core.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.pyrox.jammer.core.enums.SaveType;
import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.core.model.MemoryCard;
import it.pyrox.jammer.core.model.RawBlock;
import it.pyrox.jammer.core.util.Constants;

/**
 * Factory class used to get an instance of a MemoryCard from a file 
 * or from a byte array representing the memory card content
 */
public class MemoryCardController {
	
	private MemoryCardController() {}
	
	/**
	 * 
	 * @param filename The path of the file to be opened as MemoryCard
	 * @return A new MemoryCard object created from a file
	 * @throws IOException If file does not exist or if parsing fails
	 */
	public static MemoryCard getInstance(File file) throws IOException {
		return parse(read(file));
	}
	
	/**
	 * 
	 * @param rawMemoryCard A byte array containing data about MemoryCard
	 * @return a new MemoryCard object created from a byte array
	 * @throws IOException If file does not exist or if parsing fails
	 */
	public static MemoryCard getInstance(byte[] rawMemoryCard) throws IOException {
		return parse(rawMemoryCard);
	}
	
	public static void format(MemoryCard memoryCard) {
		for (int i = 0; i < Constants.NUM_BLOCKS; i++) {
			format(memoryCard, i);
		}
	}
	
	public static void format(MemoryCard memoryCard, int blockIndex) {
		List<Block> linkedBlocks = findLinkedBlocks(memoryCard, blockIndex);
		for (Block block : linkedBlocks) {
			BlockController.format(block);
			BlockController.parse(block, blockIndex);
		}
	}
	
	public static void toggleSaveTypeDeleted(MemoryCard memoryCard, int blockIndex) {
		List<Block> linkedBlocks = findLinkedBlocks(memoryCard, blockIndex);
		for (Block block : linkedBlocks) {
			BlockController.toggleSaveTypeDeleted(block);
		}
	}
	
	public static List<Block> findLinkedBlocks(MemoryCard memoryCard, int blockIndex) {
		Block referenceBlock = memoryCard.getBlockAt(blockIndex);
		List<Block> linkedBlockList = new ArrayList<Block>();
		if (referenceBlock == null || 
			SaveType.CORRUPTED.equals(referenceBlock.getSaveType()) ||
			SaveType.FORMATTED.equals(referenceBlock.getSaveType())) {
			return linkedBlockList;
		}
		else {
			Block firstBlock = findFirstLinkedBlock(memoryCard, blockIndex);
			if (firstBlock != null) {
				linkedBlockList.add(firstBlock);
			}
			// Continue the logic only if there are other linked blocks
			if (BlockController.isNextLinkValid(firstBlock)) {						
				List<Block> nextLinkedBlocksList = findNextLinkedBlocks(memoryCard, firstBlock.getIndex());
				if (nextLinkedBlocksList != null && !nextLinkedBlocksList.isEmpty()) {
					for (Block block : nextLinkedBlocksList) {
						if (!linkedBlockList.contains(block)) {
							linkedBlockList.add(block);
						}
					}
				}
			}
		}		
		return linkedBlockList;
	}
	
	private static List<Block> findNextLinkedBlocks(MemoryCard memoryCard, int blockIndex) {
		List<Block> linkedBlockList = new ArrayList<Block>();
		int index = blockIndex;
		for (int i = 0; i < Constants.NUM_BLOCKS; i++) {		
			Block tmpBlock = memoryCard.getBlockAt(index);
			if (!linkedBlockList.contains(tmpBlock)) {
				linkedBlockList.add(tmpBlock);
			}
			index = tmpBlock.getNextLinkIndex();			
			if (!BlockController.isNextLinkValid(tmpBlock)) {
				break;
			}
		}
		return linkedBlockList;
	}
	
	private static Block findFirstLinkedBlock(MemoryCard memoryCard, int blockIndex) {
		Block firstBlock = memoryCard.getBlockAt(blockIndex);

		for (int i = 0; i < Constants.NUM_BLOCKS; i++) {
			for (int j = 0; j < Constants.NUM_BLOCKS; j++) {
				Block tmpBlock = memoryCard.getBlockAt(j);
				if (tmpBlock.getNextLinkIndex() == firstBlock.getIndex()) {
					firstBlock = tmpBlock;
					break;
				}
			}
		}
		
		return firstBlock;
	}
	
	private static byte[] read(File file) throws IOException {
		byte[] rawMemoryCard = new byte[Constants.MEMCARD_SIZE];
		
		if (file == null) {
			throw new FileNotFoundException("Passed a null filename string");
		}

		BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
		int totalBytesRead = 0;
		if (file.length() != rawMemoryCard.length) {
			input.close();
			throw new IOException("Wrong type of file (length isn't as espected)");
		}
		while (totalBytesRead < rawMemoryCard.length) {
          int bytesRemaining = rawMemoryCard.length - totalBytesRead;
          int bytesRead = input.read(rawMemoryCard, totalBytesRead, bytesRemaining); 
          if (bytesRead > 0) {
            totalBytesRead = totalBytesRead + bytesRead;
          }
        }
		input.close();
		return rawMemoryCard;
	}
	
	private static MemoryCard parse(byte[] rawMemoryCard) throws IOException {
		if (isSignatureRight(rawMemoryCard)) {
			MemoryCard memoryCard = new MemoryCard();
			byte[] tmpHeader;
			byte[] tmpSave;
			for (int slotNumber = 0; slotNumber < Constants.NUM_BLOCKS; slotNumber++) {
				tmpHeader = new byte[Constants.HEADER_SIZE];
				tmpSave = new byte[Constants.SAVE_SIZE];
				for (int currentByte = 0; currentByte < Constants.HEADER_SIZE; currentByte++) {
					tmpHeader[currentByte] = rawMemoryCard[Constants.HEADER_SIZE + (slotNumber * Constants.HEADER_SIZE) + currentByte];
				}
				for (int currentByte = 0; currentByte < Constants.SAVE_SIZE; currentByte++) {
	                tmpSave[currentByte] = rawMemoryCard[Constants.SAVE_SIZE + (slotNumber * Constants.SAVE_SIZE) + currentByte];
				}
				RawBlock rawBlock = new RawBlock(tmpHeader, tmpSave);
				Block block = BlockController.createParsedBlock(rawBlock, slotNumber);
				memoryCard.setBlockAt(slotNumber, block);
			}
			return memoryCard;
		}
		else {
			throw new IOException("Wrong type of file (memory card expected signature missing)");
		}
	}
	
	private static boolean isSignatureRight(byte[] rawMemoryCard) {
		return rawMemoryCard[0] == (byte) 0x4D && rawMemoryCard[1] == (byte) 0x43;			
	}
}
