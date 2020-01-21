package com.se.onprem.strategy.bom;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.onprem.dto.business.bom.BOMDto;
import com.se.onprem.dto.business.bom.BOMMesssage;
import com.se.onprem.dto.business.bom.BOMRow;
import com.se.onprem.dto.business.bom.BOMSaveResult;
import com.se.onprem.util.SolrWriter;

@Service
public class BomDelete implements IBOMActions {

	@Autowired
	private SolrClient bomCore;
	@Autowired
	private SolrClient bomPartsCore;

	@Override
	public BOMMesssage doAction(BOMMesssage message) {

		BOMMesssage saveResult = new BOMMesssage();
		try {

			UpdateResponse bomCoreResponse = bomCore.deleteByQuery("BOM_ID:" + message.getBomId());
			UpdateResponse bomPartsCoreResponse = bomPartsCore.deleteByQuery("BOM_ID:" + message.getBomId());

			bomCore.commit();
			bomPartsCore.commit();
			saveResult.setBomDeleted(bomCoreResponse.getStatus() == 0 && bomPartsCoreResponse.getStatus() == 0);

		} catch (SolrServerException | IOException e) {

			e.printStackTrace();
		}

		return saveResult;

	}

}
