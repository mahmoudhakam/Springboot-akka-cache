package com.se.part.search.services.export.threading;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.se.part.search.dto.export.PCNExportDTO;
import com.se.part.search.dto.export.PCNListDTO;
import com.se.part.search.dto.export.PCNResponseDTO;
import com.se.part.search.services.PartSearchHelperService;
import com.se.part.search.services.export.ExportHttpClient;

@Service
public class PCNExporter
{
	@Value("#{environment['export.pcnSection.url']}")
	private String url;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PartSearchHelperService helperService;
	@Autowired
	private RestTemplate restTemplate;

	public void export(List<String> actorComIDs, Map<String, List<PCNResponseDTO>> pcnConcurrentMap)
	{
		long start = System.currentTimeMillis();
		Collection<String> parts = actorComIDs;
		try
		{
			ExportHttpClient<PCNExportDTO> httpClient = new ExportHttpClient<>();
			HttpHeaders headers = getRequestHeaders();
			PCNExportRequest requestBody = getRequestBody(parts);
			HttpEntity entity = new HttpEntity<>(requestBody, headers);
			URI uri = helperService.getTargetEndpoint(url, null);
			ResponseEntity<PCNExportDTO> response = httpClient.callRestEndpoint(HttpMethod.POST, uri, entity, restTemplate, new ParameterizedTypeReference<PCNExportDTO>() {
			});
			if(response.getStatusCode() == HttpStatus.OK)
			{
				PCNExportDTO result = response.getBody();
				if(result != null && (result.getPcnMap() != null && !result.getPcnMap().isEmpty()))
				{
					Map<String, PCNListDTO> pcnMap = result.getPcnMap();
					flushPCNDataToPCNMap(pcnMap, pcnConcurrentMap);
					appendNotFoundParts(pcnConcurrentMap, parts);
				}
				else
				{
					returnEmptyMap(parts, pcnConcurrentMap);
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error during exporting pcn", e);
			returnEmptyMap(parts, pcnConcurrentMap);
		}
		long end = System.currentTimeMillis() - start;
		logger.info("PCN Export has been finished in:{} ms", end);
	}

	private void returnEmptyMap(Collection<String> parts, Map<String, List<PCNResponseDTO>> pcnConcurrentMap)
	{
		parts.forEach(p -> {
			pcnConcurrentMap.put(p, new ArrayList<>());
		});
	}

	private void flushPCNDataToPCNMap(Map<String, PCNListDTO> pcnMap, Map<String, List<PCNResponseDTO>> pcnConcurrentMap)
	{
		pcnMap.forEach((k, v) -> {
			String comID = k.split("\\|")[0];
			pcnConcurrentMap.put(comID, v.getPcnList());
		});
	}

	private void mergePCResult(Map<String, PCNListDTO> responseMap, Map<String, List<PCNResponseDTO>> finalResult)
	{
		// response map key >> comId|ManId
		responseMap.entrySet().forEach(e -> {
			String[] key = e.getKey().split("\\|");
			String comId = key[0];
			List<PCNResponseDTO> pcnList = e.getValue().getPcnList();
			List<PCNResponseDTO> finalPCNList = finalResult.get(comId);
			if(finalPCNList != null)
			{
				finalPCNList.addAll(pcnList);
			}
		});
	}

	private void appendNotFoundParts(Map<String, List<PCNResponseDTO>> finalResult, Collection<String> parts)
	{
		parts.forEach(p -> {
			finalResult.computeIfAbsent(p, k -> new ArrayList<>());
		});
	}

	private PCNExportRequest getRequestBody(Collection<String> parts)
	{
		PCNExportRequest requestBody = new PCNExporter.PCNExportRequest();
		Collection<COMManDTO> list = new LinkedList<>();
		parts.forEach(p -> {
			list.add(new COMManDTO(p));
		});
		requestBody.setComManDto(list);
		return requestBody;
	}

	private HttpHeaders getRequestHeaders()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public class PCNExportRequest
	{
		private Collection<COMManDTO> comManDto;

		public Collection<COMManDTO> getComManDto()
		{
			return comManDto;
		}

		public void setComManDto(Collection<COMManDTO> comManDto)
		{
			this.comManDto = comManDto;
		}
	}

	public class COMManDTO
	{
		private String comId;

		public COMManDTO(String comId)
		{
			this.comId = comId;
		}

		public String getComId()
		{
			return comId;
		}

		public void setComId(String comId)
		{
			this.comId = comId;
		}
	}

	public static class PCNExportResultMessage
	{
		private final String categoryName;
		private final Map<String, List<PCNResponseDTO>> response;
		private final String requestId;

		public PCNExportResultMessage(String categoryName, Map<String, List<PCNResponseDTO>> response, String requestId)
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

		public Map<String, List<PCNResponseDTO>> getResponse()
		{
			return response;
		}

		public String getRequestId()
		{
			return requestId;
		}

	}

}
