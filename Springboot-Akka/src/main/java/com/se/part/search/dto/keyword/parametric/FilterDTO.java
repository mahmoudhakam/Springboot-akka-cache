package com.se.part.search.dto.keyword.parametric;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class FilterDTO implements Serializable
{
	private String categoryName; // MainName or SubName or PlName
	private String mainCategoryName;
	private String categoryId; // Main id or Sub id or Pl id
	private int level;
	private int startPage;
	private int pageSize;
	private String sortQuery;
	private List<FeatureDTO> selectedFilters;
	private boolean groupByDatasheet;
	private boolean hideByDatasheet;
	private int numberOfProductsPerDatasheet;
	private int perGroupCount;
	private String datasheetId;
	private String manId;
	private List<String> datasheetIds;
	private Set<String> subIds;
	private Set<String> mainNames;
	private String selectedFiltersJson;
	private String keyword;
	private String datasheetCreationStartDate;
	private String datasheetCreationEndDate;
	private List<String> partIds;
	private boolean sheetView;
	private String groupField;
	private List<String> manIds;
	private boolean pingdomMonitoring;
	private String partDetailsComId;
	private boolean mergeResults;
	private String lastFilter;
	private String mainOperator;
	private boolean autoComplete;
	private boolean debug;
	private boolean collectFacetsEnabled;

	public String getLastFilter()
	{
		return lastFilter;
	}

	public void setLastFilter(String lastFilter)
	{
		this.lastFilter = lastFilter;
	}

	public boolean isMergeResults()
	{
		return mergeResults;
	}

	public boolean shouldMergeResults()
	{
		return mergeResults;
	}

	public void setMergeResults(boolean mergeResults)
	{
		this.mergeResults = mergeResults;
	}

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	public String getMainCategoryName()
	{
		return mainCategoryName;
	}

	public void setMainCategoryName(String mainCategoryName)
	{
		this.mainCategoryName = mainCategoryName;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getStartPage()
	{
		if(startPage < 1)
			return 1;
		return startPage;
	}

	public void setStartPage(int startPage)
	{
		this.startPage = startPage;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = Math.min(pageSize, 1000000);
	}

	public List<FeatureDTO> getSelectedFilters()
	{
		return selectedFilters;
	}

	public void setSelectedFilters(List<FeatureDTO> selectedFilters)
	{
		this.selectedFilters = selectedFilters;
	}

	public boolean isGroupByDatasheet()
	{
		return groupByDatasheet;
	}

	public void setGroupByDatasheet(boolean groupByDatasheet)
	{
		this.groupByDatasheet = groupByDatasheet;
	}

	public int getNumberOfProductsPerDatasheet()
	{
		return numberOfProductsPerDatasheet;
	}

	public void setNumberOfProductsPerDatasheet(int numberOfProductsPerDatasheet)
	{
		this.numberOfProductsPerDatasheet = numberOfProductsPerDatasheet;
	}

	public int getPerGroupCount()
	{
		return perGroupCount;
	}

	public void setPerGroupCount(int perGroupCount)
	{
		this.perGroupCount = perGroupCount;
	}

	public String getDatasheetId()
	{
		return datasheetId;
	}

	public void setDatasheetId(String datasheetId)
	{
		this.datasheetId = datasheetId;
	}

	public String getManId()
	{
		return manId;
	}

	public void setManId(String manId)
	{
		this.manId = manId;
	}

	public void setDatasheetIds(List<String> datasheetIds)
	{
		this.datasheetIds = datasheetIds;
	}

	public List<String> getDatasheetIds()
	{
		return datasheetIds;
	}

	public Set<String> getSubIds()
	{
		return subIds;
	}

	public void setSubIds(Set<String> subIds)
	{
		this.subIds = subIds;
	}

	public String getSelectedFiltersJson()
	{
		return selectedFiltersJson;
	}

	public void setSelectedFiltersJson(String selectedFiltersJson)
	{
		this.selectedFiltersJson = selectedFiltersJson;
	}

	public String getKeyword()
	{

		return keyword;
	}

	public String getEscapedKeyword()
	{
		return keyword.replaceAll("[-\\\\/\\{\\(\\)\\|\\[\\]\\}\\'\t:;$_,`*<>±%@%^&??~!" + '"' + "]", "").trim();
	}

	public String getEscapedString(String keywordToEscape)
	{
		return keywordToEscape.replaceAll("[-\\\\/\\{\\(\\)\\|\\[\\]\\}\\'\t:;$_,`*<>±%@%^&??~!" + '"' + "]", "").trim();
	}

	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	public String getSortQuery()
	{
		return sortQuery;
	}

	public void setSortQuery(String sortQuery)
	{
		this.sortQuery = sortQuery;
	}

	public String getDatasheetCreationStartDate()
	{
		return datasheetCreationStartDate;
	}

	public void setDatasheetCreationStartDate(String datasheetCreationStartDate)
	{
		this.datasheetCreationStartDate = datasheetCreationStartDate;
	}

	public String getDatasheetCreationEndDate()
	{
		return datasheetCreationEndDate;
	}

	public void setDatasheetCreationEndDate(String datasheetCreationEndtDate)
	{
		this.datasheetCreationEndDate = datasheetCreationEndtDate;
	}

	public List<String> getPartIds()
	{
		return partIds;
	}

	public void setPartIds(List<String> partIds)
	{
		this.partIds = partIds;
	}

	public boolean isSheetView()
	{
		return sheetView;
	}

	public void setSheetView(boolean sheetView)
	{
		this.sheetView = sheetView;
	}

	public String getGroupField()
	{
		return groupField;
	}

	public void setGroupField(String groupField)
	{
		this.groupField = groupField;
	}

	public List<String> getManIds()
	{
		return manIds;
	}

	public void setManIds(List<String> manIds)
	{
		this.manIds = manIds;
	}

	public boolean isPingdomMonitoring()
	{
		return pingdomMonitoring;
	}

	public void setPingdomMonitoring(boolean pingdomMonitoring)
	{
		this.pingdomMonitoring = pingdomMonitoring;
	}

	public boolean isHideByDatasheet()
	{
		return hideByDatasheet;
	}

	public void setHideByDatasheet(boolean hideByDatasheet)
	{
		this.hideByDatasheet = hideByDatasheet;
	}

	public String getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}

	public String getPartDetailsComId()
	{
		return partDetailsComId;
	}

	public void setPartDetailsComId(String partDetailsComId)
	{
		this.partDetailsComId = partDetailsComId;
	}

	public Set<String> getMainNames()
	{
		return mainNames;
	}

	public void setMainNames(Set<String> mainNames)
	{
		this.mainNames = mainNames;
	}

	public String getMainOperator()
	{
		return mainOperator;
	}

	public void setMainOperator(String mainOperator)
	{
		this.mainOperator = mainOperator;
	}

	public boolean isAutoComplete()
	{
		return autoComplete;
	}

	public void setAutoComplete(boolean autoComplete)
	{
		this.autoComplete = autoComplete;
	}

	@Override
	public String toString()
	{
		return "FilterDTO [categoryName=" + categoryName + ", categoryId=" + categoryId + ", level=" + level + ", startPage=" + startPage + ", pageSize=" + pageSize + ", sortQuery=" + sortQuery + ", partDetailsComId=" + partDetailsComId
				+ ", selectedFilters=" + selectedFilters + ", groupByDatasheet=" + groupByDatasheet + ", hideByDatasheet=" + hideByDatasheet + ", numberOfProductsPerDatasheet=" + numberOfProductsPerDatasheet + ", perGroupCount=" + perGroupCount
				+ ", datasheetId=" + datasheetId + ", manId=" + manId + ", datasheetIds=" + datasheetIds + ", subIds=" + subIds + ", mainNames=" + mainNames + ", selectedFiltersJson=" + selectedFiltersJson + ", keyword=" + keyword
				+ ", datasheetCreationStartDate=" + datasheetCreationStartDate + ", datasheetCreationEndDate=" + datasheetCreationEndDate + ", partIds=" + partIds + ", sheetView=" + sheetView + ", groupField=" + groupField + ", manIds=" + manIds
				+ ", mainOperator=" + mainOperator + ", autoComplete=" + autoComplete + ", pingdomMonitoring=" + pingdomMonitoring + "]";
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public boolean isCollectFacetsEnabled()
	{
		return collectFacetsEnabled;
	}

	public void setCollectFacetsEnabled(boolean collectFacetsEnabled)
	{
		this.collectFacetsEnabled = collectFacetsEnabled;
	}
}
