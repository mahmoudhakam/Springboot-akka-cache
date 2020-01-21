package com.se.onprem.strategy.bom;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.onprem.dto.business.bom.BOMDto;
import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.business.bom.BOMSaveResult;
import com.se.onprem.util.SolrWriter;

@Service
public class BomSave implements IBOMActions
{

	@Autowired
	private SolrWriter<BOMRow> solrWriter;
	@Autowired
	private SolrWriter<BOMDto> solrBOMWriter;
	@Autowired
	private SolrClient bomCore;
	@Autowired
	private SolrClient bomPartsCore;

	@Override
	public BOMMesssage doAction(BOMMesssage message)
	{
		int rowId = 1;
		BOMMesssage saveResult = new BOMMesssage();

		BOMSaveResult bomSaveResult;
		BOMDto newBom = new BOMDto();
		if(StringUtils.isNotEmpty(message.getBomId()))
		{
			boolean partsSaved = solrWriter.indexParts(setPartsData(message.getBomParts(), Long.valueOf(message.getBomId()), message.getRowId()),
					bomPartsCore);
			bomSaveResult = new BOMSaveResult(partsSaved, false, newBom);
			saveResult.setBomSaveResult(bomSaveResult);
			return saveResult;
		}
		newBom = BOMDto.builder().bomId(System.currentTimeMillis()).bomName(message.getBomName()).indentedBOM(message.getIndented())
				.creationdate(Instant.now()).maxLevel(message.getMaxLevel()).minLevel(message.getMinLevel()).build();
		boolean partsSaved = solrWriter.indexParts(setPartsData(message.getBomParts(), newBom.getBomId(), rowId), bomPartsCore);
		List<BOMDto> boms = new LinkedList<>();
		boms.add(newBom);
		boolean bomsaved = solrBOMWriter.indexParts(boms, bomCore);
		bomSaveResult = new BOMSaveResult(partsSaved, bomsaved, newBom);

		saveResult.setBomSaveResult(bomSaveResult);
		return saveResult;
	}

	private List<BOMRow> setPartsData(List<BOMRow> bomParts, Long bomId, int rowId)
	{
		for(BOMRow part : bomParts)
		{
			part.setBomId(bomId);
			part.setRowKey(rowId + "||" + bomId);
			part.setRowId(rowId++);
		}
		return bomParts;
	}

}
