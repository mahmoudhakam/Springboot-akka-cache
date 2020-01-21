package com.se.part.search.dto.export;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.se.part.search.dto.keyword.Constants;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExportRow
{
	@JsonProperty(Constants.PARAMETRIC_STRATEGY)
	private List<FeatureNameValueDTO> features;
	@JsonProperty(Constants.MANUFACTURER_STRATEGY)
	private List<FeatureNameValueDTO> manufacturers;
	@JsonProperty(Constants.PACKAGE_STRATEGY)
	private List<FeatureNameValueDTO> packageResult;
	@JsonProperty(Constants.PCN_STRATEGY)
	private List<PCNResponseDTO> pcnList;
	@JsonProperty(Constants.PACKAGING_STRATEGY)
	private List<FeatureNameValueDTO> packagingResult;
	@JsonProperty(Constants.RISK_STRATEGY)
	private List<FeatureNameValueDTO> riskResult;
	@JsonProperty(Constants.CLASSIFICATION_STRATEGY)
	private List<FeatureNameValueDTO> classificationData;
	@JsonProperty(Constants.REACH_STRATEGY)
	private List<FeatureNameValueDTO> reachData;
	@JsonProperty(Constants.QUALIFICATIONS_STRATEGY)
	private List<FeatureNameValueDTO> qualificationData;
	@JsonProperty(Constants.RAREELEMENTS_STRATEGY)
	private List<FeatureNameValueDTO> rareElementsData;
	@JsonProperty(Constants.CHINAROHS_STRATEGY)
	private List<FeatureNameValueDTO> chinaROHSData;
	@JsonProperty(Constants.WEEE_STRATEGY)
	private List<FeatureNameValueDTO> weeeData;
	@JsonProperty(Constants.COOS_STRATEGY)
	private List<FeatureNameValueDTO> countryOfOriginData;
	@JsonProperty(Constants.PRICE_STRATEGY)
	private List<FeatureNameValueDTO> priceData;
	@JsonProperty(Constants.ROHS_STRATEGY)
	private List<FeatureNameValueDTO> rohsData;
	@JsonProperty(Constants.SUMMARY_STRATEGY)
	private List<FeatureNameValueDTO> summaryData;
	@JsonProperty(Constants.CONFLICT_MINIRALS_STRATEGY)
	private List<FeatureNameValueDTO> conflictMiniralsData;
	@JsonProperty(Constants.GENERAL_PASSIVE_STRATEGY)
	private List<FeatureNameValueDTO> generalPassiveData;

	public List<FeatureNameValueDTO> getPackagingResult()
	{
		return packagingResult;
	}

	public void setPackagingResult(List<FeatureNameValueDTO> packagingResult)
	{
		this.packagingResult = packagingResult;
	}

	public List<FeatureNameValueDTO> getFeatures()
	{
		return features;
	}

	public void setFeatures(List<FeatureNameValueDTO> features)
	{
		this.features = features;
	}

	public List<FeatureNameValueDTO> getManufacturers()
	{
		return manufacturers;
	}

	public void setManufacturers(List<FeatureNameValueDTO> manufacturers)
	{
		this.manufacturers = manufacturers;
	}

	public List<FeatureNameValueDTO> getPackageResult()
	{
		return packageResult;
	}

	public void setPackageResult(List<FeatureNameValueDTO> packageResult)
	{
		this.packageResult = packageResult;
	}

	public ExportRow()
	{
	}

	public List<PCNResponseDTO> getPcnList()
	{
		return pcnList;
	}

	public void setPcnList(List<PCNResponseDTO> pcnList)
	{
		this.pcnList = pcnList;
	}

	public List<FeatureNameValueDTO> getRiskResult()
	{
		return riskResult;
	}

	public void setRiskResult(List<FeatureNameValueDTO> riskResult)
	{
		this.riskResult = riskResult;
	}

	public List<FeatureNameValueDTO> getClassificationData()
	{
		return classificationData;
	}

	public void setClassificationData(List<FeatureNameValueDTO> classificationData)
	{
		this.classificationData = classificationData;
	}

	public List<FeatureNameValueDTO> getReachData()
	{
		return reachData;
	}

	public void setReachData(List<FeatureNameValueDTO> reachData)
	{
		this.reachData = reachData;
	}

	public List<FeatureNameValueDTO> getQualificationData()
	{
		return qualificationData;
	}

	public void setQualificationData(List<FeatureNameValueDTO> qualificationData)
	{
		this.qualificationData = qualificationData;
	}

	public List<FeatureNameValueDTO> getRareElementsData()
	{
		return rareElementsData;
	}

	public void setRareElementsData(List<FeatureNameValueDTO> rareElementsData)
	{
		this.rareElementsData = rareElementsData;
	}

	public List<FeatureNameValueDTO> getChinaROHSData()
	{
		return chinaROHSData;
	}

	public void setChinaROHSData(List<FeatureNameValueDTO> chinaROHSData)
	{
		this.chinaROHSData = chinaROHSData;
	}

	public List<FeatureNameValueDTO> getWeeeData()
	{
		return weeeData;
	}

	public void setWeeeData(List<FeatureNameValueDTO> weeeData)
	{
		this.weeeData = weeeData;
	}

	public List<FeatureNameValueDTO> getCountryOfOriginData()
	{
		return countryOfOriginData;
	}

	public void setCountryOfOriginData(List<FeatureNameValueDTO> countryOfOriginData)
	{
		this.countryOfOriginData = countryOfOriginData;
	}

	public List<FeatureNameValueDTO> getPriceData()
	{
		return priceData;
	}

	public void setPriceData(List<FeatureNameValueDTO> priceData)
	{
		this.priceData = priceData;
	}

	public List<FeatureNameValueDTO> getRohsData()
	{
		return rohsData;
	}

	public void setRohsData(List<FeatureNameValueDTO> rohsData)
	{
		this.rohsData = rohsData;
	}

	public List<FeatureNameValueDTO> getSummaryData()
	{
		return summaryData;
	}

	public void setSummaryData(List<FeatureNameValueDTO> summaryData)
	{
		this.summaryData = summaryData;
	}

	public List<FeatureNameValueDTO> getConflictMiniralsData()
	{
		return conflictMiniralsData;
	}

	public void setConflictMiniralsData(List<FeatureNameValueDTO> conflictMiniralsData)
	{
		this.conflictMiniralsData = conflictMiniralsData;
	}

	public List<FeatureNameValueDTO> getGeneralPassiveData()
	{
		return generalPassiveData;
	}

	public void setGeneralPassiveData(List<FeatureNameValueDTO> generalPassiveData)
	{
		this.generalPassiveData = generalPassiveData;
	}

}
