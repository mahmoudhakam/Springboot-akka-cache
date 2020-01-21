package com.se.part.search.dto.export;

import java.util.List;

public class BOMExportResultMessages
{
	public static class PartFoundExportResult
	{
		private final List<BOMResultParent> results;
		private final String categoryName;

		public PartFoundExportResult(List<BOMResultParent> results, String categoryName)
		{
			super();
			this.results = results;
			this.categoryName = categoryName;
		}

		public List<BOMResultParent> getResults()
		{
			return results;
		}

		public String getCategoryName()
		{
			return categoryName;
		}
	}

	public static class PartsNotFoundExportResult
	{
		private final List<BOMResultParent> results;
		private final String categoryName;

		public PartsNotFoundExportResult(List<BOMResultParent> results, String categoryName)
		{
			super();
			this.results = results;
			this.categoryName = categoryName;
		}

		public List<BOMResultParent> getResults()
		{
			return results;
		}

		public String getCategoryName()
		{
			return categoryName;
		}

	}
}
