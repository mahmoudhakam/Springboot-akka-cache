package com.se.part.search.services;

import org.springframework.stereotype.Repository;

@Repository
// @Transactional
public class PartSearchloggerService
{
	// private PartSearchHelperService helperService;
	// private PartSearchSolrDelegate solrDelegateService;
	// // private DBLogRepository dbLogger;
	// private Logger logger = LoggerFactory.getLogger(this.getClass());
	//
	// @PersistenceContext
	// private EntityManager entityManager;

	// @Autowired
	// public PartSearchloggerService(PartSearchHelperService helperService, PartSearchSolrDelegate solrDelegateService/* , DBLogRepository dbLogger
	// */)
	// {
	// this.helperService = helperService;
	// // this.dbLogger = dbLogger;
	// this.solrDelegateService = solrDelegateService;
	// }

	// @Async
	// public Future<String> logComIdDB(String methodName) {
	// DBLogModel model = new DBLogModel();
	// model.setRequestID(System.nanoTime() + "");
	// model.setFullURL("localhost:8071/ArrowSearchService/partSearch?partNumber=bav99");
	// model.setOpearationName("partSearch");
	// model.setServiceName("ArrowService");
	// model.setPartNumber("bav99");
	// model.setRemoteAddress("127.0.0.1");
	// model.setRequestDate(new Date(new java.util.Date().getTime()));
	// model.setTimeTaken(20);
	// model.setUserName("Arrow");
	// logger.info("Start logging request:{}", model);
	// dbLogger.save(model);
	// return new AsyncResult<>("Done");
	// }

	// @Async
	// public Future<String> logPartDetail(PartDetailsRequest partDetailsRequest, long end)
	// {
	// List<String> comIDList = helperService.splitByDelimeter(partDetailsRequest.getComIDs(), ",");
	// logger.info("Start logging comIds to db:{}", comIDList);
	// List<DBLogModel> comIDS = new ArrayList<>();
	// comIDList.forEach(c -> {
	// DBLogModel model = new DBLogModel();
	// model.setRequestID("" + System.nanoTime());
	// model.setFullURL(partDetailsRequest.getFullURL());
	// model.setOpearationName("partDetails");
	// model.setServiceName("ArrowSearchService");
	// model.setPartNumber(c);
	// model.setRemoteAddress(partDetailsRequest.getRemoteAddress());
	// model.setRequestDate(new Date(new java.util.Date().getTime()));
	// model.setTimeTaken((int) end);
	// model.setUserName("Arrow");
	// comIDS.add(model);
	// });
	// // dbLogger.save(comIDS);
	// return new AsyncResult<>("Done");
	// }
	//
	// @Async
	// public Future<String> logComIdSolr(String comIDs)
	// {
	// List<String> comIDList = helperService.splitByDelimeter(comIDs, ",");
	// logger.info("Start logging comIds to solr:{}", comIDList);
	// List<ArrowACLDTO> comIDsDTO = new ArrayList<>(comIDList.size());
	// comIDList.forEach(c -> {
	// ArrowACLDTO aclDTO = new ArrowACLDTO();
	// aclDTO.setComID(c);
	// comIDsDTO.add(aclDTO);
	// });
	//
	// comIDsDTO.forEach(a -> {
	// try
	// {
	// UpdateResponse response = solrDelegateService.getPartACLSolrServer().addBean(a);
	// int status = response.getStatus();
	// }
	// catch(Exception e)
	// {
	// logger.error("Error during acl logging", e);
	// }
	// });
	//
	// return new AsyncResult<>("Done");
	// }
	//
	// @Async
	// public Future<String> logPartSearch(PartSearchRequest partSearchRequest, long end)
	// {
	// List<PartInput> parts = null;
	// try
	// {
	// parts = helperService.convertJsonPartsMans(partSearchRequest.getPartNumber());
	// }
	// catch(JSONException e)
	// {
	// }
	// logger.info("Start logging parts to db:{}", parts);
	// if(parts != null)
	// {
	// List<DBLogModel> comIDS = new ArrayList<>();
	// parts.forEach(c -> {
	// DBLogModel model = new DBLogModel();
	// model.setRequestID("" + System.nanoTime());
	// model.setFullURL(partSearchRequest.getFullURL());
	// model.setOpearationName("partSearch");
	// model.setServiceName("ArrowSearchService");
	// model.setPartNumber(c.getPartNumber());
	// model.setRemoteAddress(partSearchRequest.getRemoteAddress());
	// model.setRequestDate(new Date(new java.util.Date().getTime()));
	// model.setTimeTaken((int) end);
	// model.setUserName("Arrow");
	// comIDS.add(model);
	// });
	// // dbLogger.save(comIDS);
	// }
	// return new AsyncResult<>("Done");
	// }
	//
	// @Async
	// public Future<String> logKeywordSearch(KeywordSearchRequest keywordSearchRequest, long end)
	// {
	// logger.info("Start logging keyword to db:{}", keywordSearchRequest.getKeyword());
	// DBLogModel model = new DBLogModel();
	// model.setRequestID("" + System.nanoTime());
	// model.setFullURL(keywordSearchRequest.getFullURL());
	// model.setOpearationName("partDetails");
	// model.setServiceName("ArrowSearchService");
	// model.setPartNumber(keywordSearchRequest.getKeyword());
	// model.setRemoteAddress(keywordSearchRequest.getRemoteAddress());
	// model.setRequestDate(new Date(new java.util.Date().getTime()));
	// model.setTimeTaken((int) end);
	// model.setUserName("Arrow");
	// // dbLogger.save(model);
	// return new AsyncResult<>("Done");
	// }
	//
	// @Async
	// public Future<String> logAlert(AlertRequest alertRequest, long end)
	// {
	// logger.info("Start logging keyword to db:{}", "");
	// DBLogModel model = new DBLogModel();
	// model.setRequestID("" + System.nanoTime());
	// model.setFullURL(alertRequest.getFullURL());
	// model.setOpearationName("alert");
	// model.setServiceName("ArrowSearchService");
	// model.setPartNumber("");
	// model.setRemoteAddress(alertRequest.getRemoteAddress());
	// model.setRequestDate(new Date(new java.util.Date().getTime()));
	// model.setTimeTaken((int) end);
	// model.setUserName("Arrow");
	// // dbLogger.save(model);
	// return new AsyncResult<>("Done");
	// }

}
