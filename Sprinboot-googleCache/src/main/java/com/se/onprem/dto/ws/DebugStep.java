package com.se.onprem.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebugStep
{
	private String stepName;
	private String query;
	private long time;

}
