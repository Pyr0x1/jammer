package it.pyrox.jammer.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import it.pyrox.jammer.core.controller.BlockController;
import it.pyrox.jammer.core.model.MemoryCard;
import it.pyrox.jammer.core.model.RawBlock;

public class MemoryCardReader {
	
	private MemoryCardReader() {}
	
	private static byte[] read(File file) throws IOException {
		byte[] rawMemoryCard = new byte[Constants.MEMCARD_SIZE];
		
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
	
	private static byte[] read(String filename) throws IOException {				
		if (filename == null) {
			throw new FileNotFoundException("Passed a null filename string");
		}
		File file = new File(filename);
		return read(file);
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
				memoryCard.setBlockAt(slotNumber, BlockController.createParsedBlock(rawBlock, slotNumber));
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
	
	public static MemoryCard open(String filename) throws IOException {
		return parse(read(filename));
	}
	
	public static MemoryCard open(File file) throws IOException {
		return parse(read(file));
	}
}
