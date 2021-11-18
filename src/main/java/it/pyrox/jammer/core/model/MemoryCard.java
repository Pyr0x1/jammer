package it.pyrox.jammer.core.model;

import it.pyrox.jammer.core.util.Constants;

public class MemoryCard {
	
	private Block blocks[];
	
	public MemoryCard() {
		this.blocks = new Block[Constants.NUM_BLOCKS];
	}

	public Block[] getBlocks() {
		return blocks;
	}

	public void setBlocks(Block[] blocks) {
		this.blocks = blocks;
	}
	
	public Block getBlockAt(int index) {
		if (index >= 0 && index < blocks.length)
			return blocks[index];
		else
			return null;
	}
	
	public void setBlockAt(int index, Block block) {
		if (index >= 0 && index < blocks.length)
			blocks[index] = block;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Block block : blocks) {
			stringBuilder.append(block.toString());
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}
}
