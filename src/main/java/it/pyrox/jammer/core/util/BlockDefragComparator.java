package it.pyrox.jammer.core.util;

import java.util.Comparator;
import java.util.Map;

import it.pyrox.jammer.core.enums.SaveTypeEnum;
import it.pyrox.jammer.core.model.Block;

public class BlockDefragComparator implements Comparator<Block> {
	
	private static final Map<SaveTypeEnum, Integer> priorityMap = Map.of(SaveTypeEnum.INITIAL, 1,
																		 SaveTypeEnum.MIDDLE_LINK, 1,
																		 SaveTypeEnum.END_LINK, 1,
																		 SaveTypeEnum.INITIAL_DELETED, 2,
																		 SaveTypeEnum.MIDDLE_LINK_DELETED, 2,
																		 SaveTypeEnum.END_LINK_DELETED, 2,
																		 SaveTypeEnum.CORRUPTED, 3,
																		 SaveTypeEnum.FORMATTED, 4);
	
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
			int value1 = priorityMap.get(b1.getSaveType());
			int value2 = priorityMap.get(b2.getSaveType());
			result = value1 - value2;
		}
		
		return result;
	}

}
