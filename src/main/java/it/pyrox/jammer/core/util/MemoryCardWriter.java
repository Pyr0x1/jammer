package it.pyrox.jammer.core.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.core.model.MemoryCard;

public class MemoryCardWriter {
	
	private MemoryCardWriter() {}
	
	private static void write(byte[] rawMemoryCard, File file) throws IOException {
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
		output.write(rawMemoryCard);
		output.close();
	}
	
	private static byte[] serialize(MemoryCard memoryCard) {
		byte[] rawMemoryCard = new byte[Constants.MEMCARD_SIZE];
		recreateSignature(rawMemoryCard);
		for (int slotNumber = 0; slotNumber < Constants.NUM_BLOCKS; slotNumber++) {
			Block tmpBlock = memoryCard.getBlockAt(slotNumber);
			for (int currentByte = 0; currentByte < Constants.HEADER_SIZE; currentByte++) {
				rawMemoryCard[Constants.HEADER_SIZE + (slotNumber * Constants.HEADER_SIZE) + currentByte] = tmpBlock.getHeader()[currentByte];
			}
			for (int currentByte = 0; currentByte < Constants.SAVE_SIZE; currentByte++) {
				rawMemoryCard[Constants.SAVE_SIZE + (slotNumber * Constants.SAVE_SIZE) + currentByte] = tmpBlock.getSave()[currentByte];
			}						
		}
		return rawMemoryCard;
	}
	
	public static void save(MemoryCard memoryCard, File file) throws IOException {
		write(serialize(memoryCard), file);
	}
	
	private static void recreateSignature(byte[] rawMemoryCard) {
		rawMemoryCard[0] = 0x4D; 	// M
        rawMemoryCard[1] = 0x43; 	// C
        rawMemoryCard[127] = 0x0E; 	// precalculated XOR
	}
}
