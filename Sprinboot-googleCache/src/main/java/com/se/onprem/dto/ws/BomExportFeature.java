package com.se.onprem.dto.ws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BomExportFeature
{
	@JsonProperty("FeatureName")
	private String featureName;
	@JsonProperty("FeatureValue")
	private String featureValue;
	@JsonProperty("FeatureUnit")
	private String featureUnit;

	public BomExportFeature(String featureName, String featureValue)
	{
		super();
		this.featureName = featureName;
		this.featureValue = featureValue;
	}

}
