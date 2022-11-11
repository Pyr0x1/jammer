package it.pyrox.jammer.core.util;

import java.util.Comparator;

import it.pyrox.jammer.core.enums.SaveTypeEnum;
import it.pyrox.jammer.core.model.Block;

public class BlockDefragComparator implements Comparator<Block> {
	
	@Override
	public int compare(Block b1, Block b2) {
		int result = 0;
		
		if (b1 == null && b2 == null) {
			result = 0;
		}
		else if (b1 == null && b2 != null) {
			result = 1;
		}
		else if (b1 != null && b2 == null) {
			result = -1;
		}
		else if (b1 != null && b2 != null) {
			if (SaveTypeEnum.FORMATTED.equals(b1.getSaveType()) && SaveTypeEnum.FORMATTED.equals(b2.getSaveType())) {
				result = 0;
			}
			else if (!SaveTypeEnum.FORMATTED.equals(b1.getSaveType()) && !SaveTypeEnum.FORMATTED.equals(b2.getSaveType())) {
				result = 0;
			}
			else if (!SaveTypeEnum.FORMATTED.equals(b1.getSaveType()) && SaveTypeEnum.FORMATTED.equals(b2.getSaveType())) {
				result = -1;
			}
			else if (SaveTypeEnum.FORMATTED.equals(b1.getSaveType()) && !SaveTypeEnum.FORMATTED.equals(b2.getSaveType())) {
				result = 1;
			}			
		}
		
		return result;
	}

}
