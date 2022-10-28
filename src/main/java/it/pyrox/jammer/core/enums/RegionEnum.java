package it.pyrox.jammer.core.enums;

public enum RegionEnum {
	AMERICA("BA"), 
	EUROPE("BE"), 
	JAPAN("BI");
	
	private String code;

	private RegionEnum(String code) {
		this.code = code;
	}

	public static RegionEnum getEnumByValue(String regionCode) {
		RegionEnum result = null;
		for (RegionEnum region : RegionEnum.values()) {
			if (region.getCode().equals(regionCode)) {
				result = region;
				break;
			}
		}
		return result;
	}
	
	public String getCode() {
		return code;
	}
}
