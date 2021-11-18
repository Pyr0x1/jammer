package it.pyrox.jammer.core.model;

import it.pyrox.jammer.core.util.Constants;

public class RawBlock {
	private byte[] header;
	private byte[] save;
	
	public RawBlock() {
		header = new byte[Constants.HEADER_SIZE];
		save = new byte[Constants.SAVE_SIZE];
		
		clearRawData();
	}
	
	public RawBlock(byte[] header, byte[] save) {
		this.header = header;
		this.save = save;
	}

	public byte[] getHeader() {
		return header;
	}
	
	public void setHeader(byte[] header) {
		this.header = header;
	}
	
	public byte[] getSave() {
		return save;
	}
	
	public void setSave(byte[] save) {
		this.save = save;
	}
	
	public void clearRawData() {
		for (int i = 0; i < Constants.HEADER_SIZE; i++) {
			header[i] = (byte) 0x00;
		}
		
		for (int i = 0; i < Constants.SAVE_SIZE; i++) {
			save[i] = (byte) 0x00;
		}
	}
}
