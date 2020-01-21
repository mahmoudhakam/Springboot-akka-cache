package com.se.part.search.bom.messages;

import java.util.Collection;

import com.se.part.search.dto.bom.BOMRow;

public class ValidationResponseMessage
{
	public static final class RespondPartFound
	{
		final long partId;
		final Collection<BOMRow> partsResponse;
		final PartValidationMessage responseType;

		public RespondPartFound(long partId, Collection<BOMRow> responses, PartValidationMessage responseType)
		{
			this.partId = partId;
			this.partsResponse = responses;
			this.responseType = responseType;
		}

		public PartValidationMessage getResponse()
		{
			// TODO Auto-generated method stub
			return responseType;
		}

		public Collection<BOMRow> getPartsResponse()
		{
			return partsResponse;
		}
	}

	public static final class RespondPartNotFound
	{
		final long partId;
		final Collection<BOMRow> partsResponse;
		final BOMRequestType nextStep;
		final PartValidationMessage responseType;
		final boolean  ignoreManufacturer;
		public RespondPartNotFound(long partId, Collection<BOMRow> responses,BOMRequestType nextStep,PartValidationMessage responseType,boolean ignoreManufacturer)

		{
			this.partId = partId;
			this.responseType = responseType;
			this.partsResponse = responses;
			this.nextStep=nextStep;
			this.ignoreManufacturer=ignoreManufacturer;

		}

		public BOMRequestType getNextStep()
		{
			return nextStep;
		}

		public PartValidationMessage getResponse()
		{
			return responseType;
		}

		public Collection<BOMRow> getPartsResponse()
		{
			return partsResponse;
		}
		public boolean isIgnoreManufacturer()
		{
			return ignoreManufacturer;
		}
	}

}
