package com.se.part.search.services.export.threading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.export.ExportRow;

@Service
public abstract class BaseCategoryManager
{
	@Async
	public abstract Future<Map<String, ExportRow>> exportCategoryResult(List<String> actorComIDs, Map<String, ExportRow> concurrentMap, CountDownLatch latch);

	@Value("#{environment['exporting.wait.period']}")
	protected int defaultWait;

	protected int calculateNumberOfThreads(int listSize, int batchSize)
	{
		int threadNumber = listSize / batchSize;
		if(listSize % batchSize == 0)
		{
			return threadNumber;
		}
		return threadNumber + 1;
	}
}
