package com.se.part.search.services.export.threading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.se.part.search.dto.export.FeatureNameValueDTO;
import com.se.part.search.dto.export.PCNResponseDTO;
import com.se.part.search.dto.export.ParametricFeatureDTO;
import com.se.part.search.dto.export.risk.RiskDTO;
import com.se.part.search.services.export.threading.workers.RiskExporter;

@Service
public class ExportingThreadAsync
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	ParametricExporter paramerticWorker;
	@Autowired
	PackageExporter packageWorker;
	@Autowired
	PackagingExporter packagingWorker;
	@Autowired
	PCNExporter pcnWorker;
	@Autowired
	ManufacurerExporter manufacturerWorker;
	@Autowired
	RiskExporter riskWorker;

	@Async
	public void getParametricExportResult(Map<String, List<ParametricFeatureDTO>> parametricConcurrentMap, List<String> comIDs, CountDownLatch latch)
	{
		try
		{
			paramerticWorker.export(comIDs, parametricConcurrentMap);
		}
		catch(Exception e)
		{
			logger.error("Error during parametric worker export", e);
		}
		finally
		{
			latch.countDown();
		}
	}

	@Async
	public void getPackageExportResult(Map<String, List<FeatureNameValueDTO>> packageConcurrentMap, List<String> actorComIDs, CountDownLatch catlatch)
	{
		try
		{
			packageWorker.export(actorComIDs, packageConcurrentMap);
		}
		catch(Exception e)
		{
			logger.error("Error during package worker export", e);
		}
		finally
		{
			catlatch.countDown();
		}
	}

	@Async
	public void getPackagingExportResult(Map<String, List<FeatureNameValueDTO>> packagingConcurrentMap, List<String> actorComIDs, CountDownLatch catlatch)
	{
		try
		{
			packagingWorker.export(actorComIDs, packagingConcurrentMap);
		}
		catch(Exception e)
		{
			logger.error("Error during packaging worker export", e);
		}
		finally
		{
			catlatch.countDown();
		}
	}

	@Async
	public void getPCNExportResult(Map<String, List<PCNResponseDTO>> pcnConcurrentMap, List<String> actorComIDs, CountDownLatch catlatch)
	{
		try
		{
			pcnWorker.export(actorComIDs, pcnConcurrentMap);
		}
		catch(Exception e)
		{
			logger.error("Error during pcn worker export", e);
		}
		finally
		{
			catlatch.countDown();
		}
	}

	@Async
	public void getManufacturerExportResult(Map<String, List<FeatureNameValueDTO>> parametricConcurrentMap, List<String> actorComIDs, CountDownLatch catlatch)
	{
		try
		{
			manufacturerWorker.export(actorComIDs, parametricConcurrentMap);
		}
		catch(Exception e)
		{
			logger.error("Error during pcn manufacturer export", e);
		}
		finally
		{
			catlatch.countDown();
		}
	}

	@Async
	public void getRiskExportResult(Map<String, RiskDTO> riskConcurrentMap, List<String> actorComIDs, CountDownLatch catlatch)
	{
		try
		{
			riskWorker.export(actorComIDs, riskConcurrentMap);
		}
		catch(Exception e)
		{
			logger.error("Error during pcn manufacturer export", e);
		}
		finally
		{
			catlatch.countDown();
		}

	}

}
