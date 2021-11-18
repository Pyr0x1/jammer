package it.pyrox.jammer.core.enums;

public enum SaveType {
	FORMATTED((byte) 0xA0),
	INITIAL((byte) 0x51),
	MIDDLE_LINK((byte) 0x52),
	END_LINK((byte) 0x53),
	INITIAL_DELETED((byte) 0xA1),
	MIDDLE_LINK_DELETED((byte) 0xA2),
	END_LINK_DELETED((byte) 0xA3),
	CORRUPTED((byte) 0xFF);
	
	private byte value;
	
	SaveType(byte value) {
		this.value = value;
	}

	public static SaveType getEnumByValue(byte value) {
		for (SaveType saveType : SaveType.values()) {
			if (saveType.getValue() == value) {
				return saveType;
			}
		}
		
		// If there is no match, return corrupted save type
		return SaveType.CORRUPTED;
	}
	
	public byte getValue() {
		return value;
	}
}
