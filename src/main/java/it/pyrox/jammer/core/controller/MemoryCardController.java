package it.pyrox.jammer.core.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.pyrox.jammer.core.enums.SaveTypeEnum;
import it.pyrox.jammer.core.exception.NotEnoughSpaceException;
import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.core.model.MemoryCard;
import it.pyrox.jammer.core.util.BlockDefragComparator;
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
		if (memoryCard != null) {
			MemoryCardWriter.save(memoryCard, file);
		}
	}
	
	public static void format(MemoryCard memoryCard) {
		if (memoryCard != null) {
			for (int i = 0; i < Constants.NUM_BLOCKS; i++) {
				format(memoryCard, i);
			}
		}
	}
	
	public static void format(MemoryCard memoryCard, int blockIndex) {
		if (memoryCard != null) {
			List<Block> linkedBlocks = findLinkedBlocks(memoryCard, blockIndex);
			for (Block block : linkedBlocks) {
				BlockController.format(block);
				BlockController.parse(block, blockIndex);
			}
		}
	}
	
	/**
	 * Checks if all blocks in the memory card are formatted
	 * 
	 * @param memoryCard
	 * @return
	 */
	private static boolean isMemoryCardFormatted(MemoryCard memoryCard) {
		boolean result = true;
		
		if (memoryCard != null) {
			for (Block block : memoryCard.getBlocks()) {
				if (!SaveTypeEnum.FORMATTED.equals(block.getSaveType())) {
					result = false;
					break;
				}
			}
		}
		
		return result;
	}
	
	public static void toggleSaveTypeDeleted(MemoryCard memoryCard, int blockIndex) {
		if (memoryCard != null) {
			List<Block> linkedBlocks = findLinkedBlocks(memoryCard, blockIndex);
			for (Block block : linkedBlocks) {
				BlockController.toggleSaveTypeDeleted(block);
			}
		}
	}
	
	public static List<Block> findLinkedBlocks(MemoryCard memoryCard, int blockIndex) {
		List<Block> linkedBlockList = new ArrayList<>();
		if (memoryCard != null) {
			Block referenceBlock = memoryCard.getBlockAt(blockIndex);
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
		}
		return linkedBlockList;
	}
	
	protected static int findFirstEnoughContiguousEmptyBlocks(MemoryCard memoryCard, int neededBlocksSize) {
		int minContiguousEmptyBlocksFirstIndex = -1;
		int minContiguousEmptyBlocksSize = Constants.NUM_BLOCKS;
		int tmpContiguousEmptyBlocksFirstIndex = -1;
		int tmpContiguousEmptyBlocksSize = 0;
		
		if (memoryCard != null) {
			Block[] blocks = memoryCard.getBlocks();
			for (Block block : blocks) {
				int currentIndex = block.getIndex();
				if (SaveTypeEnum.FORMATTED.equals(block.getSaveType())) {
					if (currentIndex == 0 || !SaveTypeEnum.FORMATTED.equals(memoryCard.getBlockAt(currentIndex - 1).getSaveType())) {						
						tmpContiguousEmptyBlocksFirstIndex = currentIndex;
					}
					tmpContiguousEmptyBlocksSize++;
				}
				if (!SaveTypeEnum.FORMATTED.equals(block.getSaveType()) || currentIndex == Constants.NUM_BLOCKS - 1) {
					if (tmpContiguousEmptyBlocksSize >= neededBlocksSize && ((tmpContiguousEmptyBlocksSize < minContiguousEmptyBlocksSize) || tmpContiguousEmptyBlocksSize == Constants.NUM_BLOCKS)) {
						minContiguousEmptyBlocksFirstIndex = tmpContiguousEmptyBlocksFirstIndex;
						minContiguousEmptyBlocksSize = tmpContiguousEmptyBlocksSize;
					}
					tmpContiguousEmptyBlocksFirstIndex = -1;
					tmpContiguousEmptyBlocksSize = 0;
				}
			}
		}
		return minContiguousEmptyBlocksFirstIndex;
	}
	
	// Modified bubble sort to perform also update of link index
	public static void defrag(MemoryCard memoryCard) {
		if (memoryCard != null) {
			Block[] blocks = memoryCard.getBlocks();
			BlockDefragComparator comparator = new BlockDefragComparator();
			for (int i = 0; i < Constants.NUM_BLOCKS; i++) {							
				for (int j = 0; j < Constants.NUM_BLOCKS - 1; j++) {
					Block b1 = blocks[j];
					Block b2 = blocks[j + 1];
					if (comparator.compare(b1, b2) > 0) {
						swapBlocksAndUpdateLinks(blocks, b1, b2, j, j + 1);
					}
				}
			}
		}
	}
	
	public static void copyLinkedBlocks(MemoryCard source, MemoryCard destination, int startingIndexToCopy) throws NotEnoughSpaceException {
		if (source != null && destination != null && startingIndexToCopy >= 0 && startingIndexToCopy < Constants.NUM_BLOCKS) {
			List<Block> linkedBlocks = findLinkedBlocks(source, startingIndexToCopy);
			int indexToCopyAt = findFirstEnoughContiguousEmptyBlocks(destination, linkedBlocks.size());
			if (indexToCopyAt >= 0) {
				for (int i = 0; i < linkedBlocks.size(); i++) {
					Block blockCopy = BlockController.deepCopy(linkedBlocks.get(i));
					blockCopy.setIndex(indexToCopyAt + i);
					// At the moment the copy manages only contiguous free blocks, so the next index will be current + 1
					// Don't update last linked block next index because it will already be ok
					if (i < linkedBlocks.size() - 1) {
						blockCopy.setNextLinkIndex(indexToCopyAt + i + 1);
					}
					destination.setBlockAt(indexToCopyAt + i, blockCopy);
				}
			}
			else {
				throw new NotEnoughSpaceException();
			}
		}
	}
	
	public static void copyAllBlocks(MemoryCard source, MemoryCard destination) throws NotEnoughSpaceException {
		if (source != null && destination != null) {			
			if (isMemoryCardFormatted(destination)) {
				for (int i = 0; i < source.getBlocks().length; i++) {
					Block blockCopy = BlockController.deepCopy(source.getBlocks()[i]);					
					destination.setBlockAt(i, blockCopy);
				}
			}
			else {
				throw new NotEnoughSpaceException();
			}
		}
	}
	
	public static MemoryCard deepCopy(MemoryCard source) {
		MemoryCard memoryCard = new MemoryCard();
		if (source != null) {
			for (Block block : source.getBlocks()) {
				memoryCard.setBlockAt(block.getIndex(), BlockController.deepCopy(block));
			}
		}
		return memoryCard;
	}
	
	private static void swapBlocksAndUpdateLinks(Block[] blocks, Block b1, Block b2, int index1, int index2) {		
		blocks[index1] = b2;
		blocks[index2] = b1;
		blocks[index1].setIndex(index1);
		blocks[index2].setIndex(index2);
		for (Block block : blocks) {
			if (block.getNextLinkIndex() == index1) {
				block.setNextLinkIndex(index2);
			}
			else if (block.getNextLinkIndex() == index2) {
				block.setNextLinkIndex(index1);
			}
		}
	}
	
	private static List<Block> findNextLinkedBlocks(MemoryCard memoryCard, int blockIndex) {
		List<Block> linkedBlockList = new ArrayList<>();
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
