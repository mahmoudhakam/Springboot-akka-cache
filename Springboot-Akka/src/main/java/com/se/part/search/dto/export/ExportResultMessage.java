package com.se.part.search.dto.export;

import java.util.Map;

public class ExportResultMessage
{
	public static class ExportResponseMessage
	{
		private final String categoryName;
		private final Map response;
		private final String requestId;

		public ExportResponseMessage(String categoryName, Map response, String requestId)
		{
			super();
			this.categoryName = categoryName;
			this.response = response;
			this.requestId = requestId;
		}

		public String getCategoryName()
		{
			return categoryName;
		}

		public Map getResponse()
		{
			return response;
		}

		public String getRequestId()
		{
			return requestId;
		}

	}
}
