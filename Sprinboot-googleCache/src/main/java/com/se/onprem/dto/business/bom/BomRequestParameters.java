package com.se.onprem.dto.business.bom;

public final class BomRequestParameters {
	private final String bomData;
	private final String bomName;
	private final String bomId;
	private final int rowId;

	public BomRequestParameters(String bomData, String bomName, String bomId, int rowId) {
		this.bomData = bomData;
		this.bomName = bomName;
		this.bomId = bomId;
		this.rowId = rowId;
	}

	public String getBomData() {
		return bomData;
	}

	public String getBomName() {
		return bomName;
	}

	public String getBomId() {
		return bomId;
	}

	public int getRowId() {
		return rowId;
	}
}