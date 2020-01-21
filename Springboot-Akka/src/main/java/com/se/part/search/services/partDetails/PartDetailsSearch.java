// package com.se.part.search.services.partDetails;
//
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.Comparator;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.LinkedHashMap;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.Future;
// import java.util.concurrent.TimeUnit;
//
// import org.apache.solr.client.solrj.SolrClient;
// import org.apache.solr.client.solrj.SolrQuery;
// import org.apache.solr.client.solrj.SolrQuery.ORDER;
// import org.apache.solr.client.solrj.SolrServerException;
// import org.apache.solr.client.solrj.response.QueryResponse;
// import org.apache.solr.common.SolrDocumentList;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
//
// import com.se.part.search.dto.ArrowSearchStep;
// import com.se.part.search.dto.ParentSearchRequest;
// import com.se.part.search.dto.partDetails.ArrowFeatureDTO;
// import com.se.part.search.dto.partDetails.PartDetailsDTO;
// import com.se.part.search.dto.partDetails.PartDetailsRequest;
// import com.se.part.search.dto.partDetails.SummaryDataDTO;
// import com.se.part.search.services.PartSearchHelperService;
// import com.se.part.search.strategies.PartSearchStrategy;
// import com.se.part.search.util.PartSearchServiceConstants;
//
// @Service
// public class PartDetailsSearch implements PartSearchStrategy
// {
// private Logger logger = LoggerFactory.getLogger(this.getClass());
// private SolrClient partsSummarySolrServer;
// private SolrClient taxonomySolrServer;
// private SolrClient parametricSolrServer;
// private PartSearchHelperService helperService;
// private Map<String, List<ArrowFeatureDTO>> plTaxFeatures = null;
//
// // @PostConstruct
// void init()
// {
// if(plTaxFeatures == null)
// {
// try
// {
// logger.info("Start Caching pl features tax ");
// plTaxFeatures = cachePlTaxonomyFeatures();
// }
// catch(Exception e)
// {
// logger.error("Error during caching taxonomy tree", e);
// }
// }
// }
//
// @Autowired
// public PartDetailsSearch(SolrClient partsSummarySolrServer, SolrClient taxonomySolrServer, SolrClient parametricSolrServer, PartSearchHelperService
// helperService)
// {
// this.partsSummarySolrServer = partsSummarySolrServer;
// this.taxonomySolrServer = taxonomySolrServer;
// this.parametricSolrServer = parametricSolrServer;
// this.helperService = helperService;
// }
//
// private Map<String, List<ArrowFeatureDTO>> cachePlTaxonomyFeatures() throws SolrServerException, IOException
// {
// SolrQuery plTaxFeaturesQuery = formatePlTaxFeaturesQuery(0l);
// QueryResponse response = helperService.executeSorlQuery(plTaxFeaturesQuery, taxonomySolrServer);
// plTaxFeaturesQuery = formatePlTaxFeaturesQuery(response.getResults().getNumFound());
// response = helperService.executeSorlQuery(plTaxFeaturesQuery, taxonomySolrServer);
// logger.info("Getting pl Features Query:{} takes about:{}", plTaxFeaturesQuery, response.getElapsedTime());
// if(!response.getResults().isEmpty())
// {
// Map<String, List<ArrowFeatureDTO>> result = fillPlFeaturesTaxonomyTree(response.getResults());
// plTaxFeatures = sortedPlFeaturesTaxonomyByExpertSheetOrder(result);
// }
// return plTaxFeatures;
// }
//
// private Map<String, List<ArrowFeatureDTO>> sortedPlFeaturesTaxonomyByExpertSheetOrder(Map<String, List<ArrowFeatureDTO>> result)
// {
// result.entrySet().forEach(entry -> entry.getValue().sort(Comparator.comparing(ArrowFeatureDTO::getExpertSheetOrder)));
// return result;
// }
//
// private Map<String, List<ArrowFeatureDTO>> fillPlFeaturesTaxonomyTree(SolrDocumentList taxFeatures)
// {
// Map<String, List<ArrowFeatureDTO>> result = new HashMap<>();
// taxFeatures.forEach(d -> {
// String plID = (String) d.getFieldValue(PartSearchServiceConstants.TaxonomyCoreFields.PL_ID);
// String featureName = (String) d.getFieldValue(PartSearchServiceConstants.TaxonomyCoreFields.FEATURENAME);
// int expertSheetOrder = (int) d.getFieldValue(PartSearchServiceConstants.TaxonomyCoreFields.EXPERT_SHEET_ORDER);
// String hColName = (String) d.getFieldValue(PartSearchServiceConstants.TaxonomyCoreFields.HCOLNAME);
// String unit = (String) d.getFieldValue(PartSearchServiceConstants.TaxonomyCoreFields.UNIT);
// List<ArrowFeatureDTO> listOfFeatures = result.get(plID);
// if(listOfFeatures == null)
// {
// listOfFeatures = new ArrayList<>();
// }
// ArrowFeatureDTO feature = new ArrowFeatureDTO();
// feature.setFetName(featureName);
// feature.setUnit(unit != null ? unit : "");
// feature.setExpertSheetOrder(expertSheetOrder);
// feature.setHcolName(hColName);
// listOfFeatures.add(feature);
// result.put(plID, listOfFeatures);
// });
//
// return result;
// }
//
// private SolrQuery formatePlTaxFeaturesQuery(long rows)
// {
// SolrQuery query = new SolrQuery("SHEETVIEWFLAG:(1 3) AND PACKAGEFLAG:0");
// query.setRows((int) rows);
// return query;
// }
//
// @Override
// public Object searchArrowRequest(ParentSearchRequest request, PartSearchStrategy searchStrategy, List<ArrowSearchStep> steps) throws
// InterruptedException
// {
// PartDetailsRequest partDetailsRequest = (PartDetailsRequest) request;
// List<String> comIDs = helperService.splitByDelimeter(partDetailsRequest.getComIDs(), ",");
// CountDownLatch latch = new CountDownLatch(2);
// Future<Map<String, SummaryDataDTO>> futureSummaryData = helperService.getPartSummaryData(comIDs, searchStrategy, latch, partsSummarySolrServer,
// parametricSolrServer, taxonomySolrServer, partDetailsRequest.getPageNumber(),
// partDetailsRequest.getPageSize(), steps);
// Future<Map<String, List<ArrowFeatureDTO>>> futureParametricData = helperService.getParametricData(comIDs, searchStrategy, latch,
// partsSummarySolrServer, parametricSolrServer, taxonomySolrServer, partDetailsRequest.getPageNumber(),
// partDetailsRequest.getPageSize(), steps, plTaxFeatures);
// try
// {
// boolean isCompleted = latch.await(5, TimeUnit.SECONDS);
// if(!isCompleted)
// {
// return null;
// }
// }
// catch(InterruptedException e)
// {
// logger.error("Interruption:", e);
// throw e;
// }
// List<PartDetailsDTO> parts = new ArrayList<>();
// try
// {
// Map<String, SummaryDataDTO> summaryDataMap = futureSummaryData.get();
// Map<String, List<ArrowFeatureDTO>> parametricDataMap = futureParametricData.get();
// summaryDataMap.entrySet().forEach(e -> {
// PartDetailsDTO partDetailsDTO = new PartDetailsDTO();
// String comId = e.getKey();
// partDetailsDTO.setComID(comId);
// partDetailsDTO.setSummaryData(summaryDataMap.get(comId));
// partDetailsDTO.setFeatures(parametricDataMap.get(comId));
// parts.add(partDetailsDTO);
// });
// }
// catch(InterruptedException | ExecutionException e)
// {
// logger.error("Interruption execption:", e);
// }
// return parts;
// }
//
// public SolrQuery formatePartsSummaryQuery(List<String> comIDs, String pageNumber, String pageSize)
// {
// SolrQuery query = new SolrQuery();
//
// StringBuilder q = new StringBuilder();
// q.append(PartSearchServiceConstants.SummaryCoreFields.COM_ID + ":(");
// StringBuilder delimeter = new StringBuilder();
//
// comIDs.forEach(c -> {
// q.append(delimeter);
// q.append(c);
// delimeter.setLength(0);
// delimeter.append(" OR ");
// });
// q.append(")");
//
// query.set("fl",
// PartSearchServiceConstants.SummaryCoreFields.COM_ID + "," + PartSearchServiceConstants.SummaryCoreFields.COM_PARTNUM + "," +
// PartSearchServiceConstants.SummaryCoreFields.ECCN + "," + PartSearchServiceConstants.SummaryCoreFields.HTSUSA
// + "," + PartSearchServiceConstants.SummaryCoreFields.MAN_NAME + "," + PartSearchServiceConstants.SummaryCoreFields.PDF_URL + "," +
// PartSearchServiceConstants.SummaryCoreFields.PDF_URL_DS + ","
// + PartSearchServiceConstants.SummaryCoreFields.PL_NAME + "," + PartSearchServiceConstants.SummaryCoreFields.ROW_COM_DESC + "," +
// PartSearchServiceConstants.SummaryCoreFields.SCHEDULEB + ","
// + PartSearchServiceConstants.SummaryCoreFields.TAX_PATH + "," + PartSearchServiceConstants.SummaryCoreFields.UNSPSC);
//
// query.set("q", q.toString());
// query.setStart(helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize)));
// query.setRows(Integer.parseInt(pageSize));
// return query;
// }
//
// public Map<String, SummaryDataDTO> fillSummaryData(SolrDocumentList results)
// {
// Map<String, SummaryDataDTO> result = new HashMap<>();
// results.forEach(d -> {
// SummaryDataDTO dto = new SummaryDataDTO();
// dto.setComID((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.COM_ID));
// dto.setComPartNumber((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.COM_PARTNUM));
// dto.seteCCN((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.ECCN));
// dto.sethTSUSA((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.HTSUSA));
// dto.setManName((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.MAN_NAME));
// dto.setPdfURL((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.PDF_URL));
// dto.setOnlineSupplierDS((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.PDF_URL_DS));
// dto.setPlName((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.PL_NAME));
// dto.setRowComDesc((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.ROW_COM_DESC));
// dto.setScheduleB((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.SCHEDULEB));
// dto.setTaxPath((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.TAX_PATH));
// dto.setuNSPSC((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.UNSPSC));
//
// result.put((String) d.getFieldValue(PartSearchServiceConstants.SummaryCoreFields.COM_ID), dto);
// });
// return result;
// }
//
// public SolrQuery formateParametricCoreQuery(List<String> comIDs, String pageNumber, String pageSize)
// {
// SolrQuery query = new SolrQuery();
//
// StringBuilder q = new StringBuilder();
// q.append(PartSearchServiceConstants.ParametricCoreFields.PART_ID + ":(");
// StringBuilder delimeter = new StringBuilder();
//
// comIDs.forEach(c -> {
// q.append(delimeter);
// q.append(c);
// delimeter.setLength(0);
// delimeter.append(" OR ");
// });
// q.append(")");
//
// // fl=PART_ID,*_VALUE
// query.set("fl", PartSearchServiceConstants.ParametricCoreFields.PART_ID + "," + PartSearchServiceConstants.ParametricCoreFields.FET_VALUE + "," +
// PartSearchServiceConstants.ParametricCoreFields.PL_ID);
//
// query.set("q", q.toString());
// query.setStart(helperService.calculateSolrStartingPage(Integer.parseInt(pageNumber), Integer.parseInt(pageSize)));
// query.setRows(Integer.parseInt(pageSize));
// return query;
// }
//
// // http://172.24.32.150:1986/solr/Parametric_Taxonomies/select?q=(HCOLNAME:(C_2) AND (PLID:1156)) OR (HCOLNAME:(C_1198) AND (PLID:1372)) OR
// // (HCOLNAME:(C_1927) AND
// // (PLID:1156))&wt=json&fl=PLID,HCOLNAME,FEATURENAME,UNIT
// public SolrQuery formateTaxonmoyQuery(Collection<List<ArrowFeatureDTO>> collection)
// {
// SolrQuery query = new SolrQuery();
// Set<String> featurePLQuery = new HashSet<>();
// collection.forEach(f -> {
// f.forEach(d -> {
// String fetQuery = "(" + PartSearchServiceConstants.TaxonomyCoreFields.HCOLNAME + ":" + d.getHcolName() + " AND " +
// PartSearchServiceConstants.TaxonomyCoreFields.PL_ID + ":" + d.getPlID() + ")";
// featurePLQuery.add(fetQuery);
// });
// });
//
// StringBuilder delimeter = new StringBuilder();
// StringBuilder q = new StringBuilder();
// q.append("(");
// featurePLQuery.forEach(e -> {
// q.append(delimeter);
// q.append(e);
// delimeter.setLength(0);
// delimeter.append(" OR ");
// });
// q.append(")");
// q.append(" AND SHEETVIEWFLAG:(1 3) AND PACKAGEFLAG:0");
//
// query.set("fl", PartSearchServiceConstants.TaxonomyCoreFields.PL_ID + "," + PartSearchServiceConstants.TaxonomyCoreFields.HCOLNAME + "," +
// PartSearchServiceConstants.TaxonomyCoreFields.FEATURENAME + ","
// + PartSearchServiceConstants.TaxonomyCoreFields.UNIT);
// query.set("q", q.toString());
// query.setSort(PartSearchServiceConstants.TaxonomyCoreFields.EXPERT_SHEET_ORDER, ORDER.asc);
// query.setRows(featurePLQuery.size());
//
// return query;
// }
//
// public Map<String, List<ArrowFeatureDTO>> getParametricFeatures(SolrDocumentList partsParametricData, Map<String, List<ArrowFeatureDTO>>
// plTaxFeatures)
// {
// Map<String, List<ArrowFeatureDTO>> result = new HashMap<>();
// partsParametricData.forEach(part -> {
// String comID = (String) part.getFieldValue(PartSearchServiceConstants.ParametricCoreFields.PART_ID);
// String plID = (String) part.getFieldValue(PartSearchServiceConstants.ParametricCoreFields.PL_ID);
// List<ArrowFeatureDTO> taxFeatures = plTaxFeatures.get(plID);
// if(taxFeatures != null)
// {
// List<ArrowFeatureDTO> completeFeatures = new LinkedList<>();
// taxFeatures.forEach(singleFeature -> {
// ArrowFeatureDTO completeFeature = new ArrowFeatureDTO();
// completeFeature.setUnit(singleFeature.getUnit());
// completeFeature.setFetName(singleFeature.getFetName());
// Object fetValueObject = part.getFieldValue(singleFeature.getHcolName() + "_VALUE");
// completeFeature.setFeatureValue(fetValueObject == null ? "" : fetValueObject.toString());
// completeFeatures.add(completeFeature);
// });
// result.put(comID, completeFeatures);
// }
// });
// return result;
// }
//
// public Map<String, List<ArrowFeatureDTO>> completeFeatures(Map<String, List<ArrowFeatureDTO>> features, SolrDocumentList taxFeatureDocuments)
// {
// Map<String, List<ArrowFeatureDTO>> featuresCompleted = new LinkedHashMap<>();
// Map<String, ArrowFeatureDTO> featureAliasToFeatureNameMap = new HashMap<>();
// /**
// * looping features got from taxonomy
// */
// taxFeatureDocuments.forEach(d -> {
// String fetName = (String) d.getFieldValue(PartSearchServiceConstants.TaxonomyCoreFields.FEATURENAME);
// String unit = (String) d.getFieldValue(PartSearchServiceConstants.TaxonomyCoreFields.UNIT);
// String hColName = (String) d.getFieldValue(PartSearchServiceConstants.TaxonomyCoreFields.HCOLNAME);
// ArrowFeatureDTO taxonomyFeature = new ArrowFeatureDTO();
// taxonomyFeature.setFetName(fetName);
// taxonomyFeature.setHcolName(hColName);
// taxonomyFeature.setUnit(unit);
// featureAliasToFeatureNameMap.put(hColName, taxonomyFeature);
// });
// /**
// * mapping features (com_id vs features) got from parametric
// */
// features.entrySet().forEach(f -> f.getValue().forEach(feature -> {
// if(featureAliasToFeatureNameMap.containsKey(feature.getHcolName()))
// {
// List<ArrowFeatureDTO> addedFeatures = featuresCompleted.get(f.getKey());// com_id
// if(addedFeatures == null)
// {
// addedFeatures = new LinkedList<>();
// }
// ArrowFeatureDTO taxonomyFeature = featureAliasToFeatureNameMap.get(feature.getHcolName());
// feature.setFetName(taxonomyFeature.getFetName());
// feature.setUnit(taxonomyFeature.getUnit());
// addedFeatures.add(feature);
// featuresCompleted.put(f.getKey(), addedFeatures);
// }
// }));
// return featuresCompleted;
// }
// }
