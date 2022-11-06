package it.pyrox.jammer.core.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.pyrox.jammer.core.enums.SaveTypeEnum;
import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.core.model.MemoryCard;
import it.pyrox.jammer.core.util.Constants;
import it.pyrox.jammer.core.util.MemoryCardReader;
import it.pyrox.jammer.core.util.MemoryCardWriter;

/**
 * Factory class used to get an instance of a MemoryCard from a file 
 * or from a byte array representing the memory card content
 */
public class MemoryCardController {
	
	private MemoryCardController() {}
	
	/**
	 * 
	 * @param file The file to be used to open the MemoryCard
	 * @return A new MemoryCard object created from a file
	 * @throws IOException If file does not exist or if parsing fails
	 */
	public static MemoryCard getInstance(File file) throws IOException {
		return MemoryCardReader.open(file);
	}
	
	/**
	 * 
	 * @param memoryCard The MemoryCard to be saved to file
	 * @param file The file where the MemoryCard will be saved
	 * @throws IOException If file cannot be created
	 */
	public static void saveInstance(MemoryCard memoryCard, File file) throws IOException {
		MemoryCardWriter.save(memoryCard, file);
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
			SaveTypeEnum.CORRUPTED.equals(referenceBlock.getSaveType()) ||
			SaveTypeEnum.FORMATTED.equals(referenceBlock.getSaveType())) {
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
}
