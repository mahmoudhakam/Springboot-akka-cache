package com.se.part.search.services.export.threading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.export.ExportRow;
import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.keyword.Constants;

@Service(Constants.MANUFACTURER_STRATEGY + Constants.BEAN)
public class ManufacturerExportBean extends BaseCategoryManager
{
	@Value("#{environment['exporting.batch.parametric']}")
	private int exportingThreadsBatch;
	private Map<String, List<FeatureNameValueDTO>> manufacturerConcurrentMap = null;
	@Autowired
	public ExportingThreadAsync exportingThread;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Future<Map<String, ExportRow>> exportCategoryResult(List<String> comIDs, Map<String, ExportRow> concurrentMap, CountDownLatch latch)
	{
		try
		{
			long start = System.currentTimeMillis();
			logger.info("Start exporting:{}", Constants.MANUFACTURER_STRATEGY + Constants.BEAN);
			manufacturerConcurrentMap = new ConcurrentHashMap<>();
			int threadNumber = calculateNumberOfThreads(comIDs.size(), exportingThreadsBatch);
			CountDownLatch catlatch = new CountDownLatch(threadNumber);
			for(int i = 0; i < comIDs.size(); i += exportingThreadsBatch)
			{
				List<String> actorComIDs = comIDs.subList(i, Math.min(i + exportingThreadsBatch, comIDs.size()));
				exportingThread.getManufacturerExportResult(manufacturerConcurrentMap, actorComIDs, catlatch);
			}
			catlatch.await(defaultWait, TimeUnit.MILLISECONDS);
			manufacturerConcurrentMap.entrySet().forEach(e -> {
				String comId = e.getKey();
				ExportRow row = concurrentMap.get(comId);
				row.setManufacturers(e.getValue());
			});
			long end = System.currentTimeMillis() - start;
			logger.info("Exporting:{} ends in:{}", Constants.MANUFACTURER_STRATEGY + Constants.BEAN, end);
		}
		catch(Exception e2)
		{
			logger.error("Error during exporting Manufacturer", e2);
		}
		finally
		{
			latch.countDown();
		}
		return new AsyncResult(concurrentMap);
	}

}
