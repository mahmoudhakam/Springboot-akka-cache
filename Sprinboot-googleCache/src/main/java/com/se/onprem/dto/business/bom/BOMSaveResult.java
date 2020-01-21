package com.se.onprem.dto.business.bom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class BOMSaveResult
{
	private final boolean partsSaved;
	private final boolean bomSaved;
	private final BOMDto savedBOM;

}
