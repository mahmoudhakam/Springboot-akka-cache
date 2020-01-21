package com.se.onprem.util;

public interface ParametricConstants {
    class RequestResponseFormat {
        public static final String ACCEPT_HEADER = "Accept";
        public static final String XML_FORMAT = "xml";
        public static final String DEFAULT_FORMAT = "json";
        public static String REQUEST_FORMAT = "fmt";

    }

    class AsyncConfig {
        public static final String MAX_NUMBER_OF_THREADS = "max.number.of.threads";
        public static final String NUMBER_OF_THREADS = "number.of.threads";

    }
    final class ServiceDefaults{
    	public static final int MAX_PARTS_PER_PAGE=25;
    }

    String PARA_SERVICE_SEARCH_PROPERTIES = "C:/";
    String SOLR_PARAMETRIC_VALUES_URL = "para.rest.api.solr.parametricValuesCoreUrl";
    String SOLR_PARAMETRIC_SEARCH_URL = "para.rest.api.solr.searchCoreUrl";
    String SOLR_PARAMETRIC_CORE_URL = "para.rest.api.solr.partsCoreUrl";
    String SOLR_TAXONOMY_CORE_URL = "para.rest.api.solr.taxonomyCoreUrl";
    String SE_PART_SEARCH_API_URL = "se.part.search.api.url";
    String SE_KEYWORD_SEARCH_API_URL = "se.keyword.search.api.url";
    String SE_KEYWORD_AUTOCOMPLETE_API_URL = "se.autocomplete.api.url";
    String SE_KEYWORD_QUERY_API_URL = "se.keyword.query.api.url";
    String SOLR_ACL_CORE_URL = "acl.core.url";
    String PRODUCT_LINE = "Product Line";
    String PRODUCTLINENAME = "productLineName";
    String PRODUCT_LINE_NAME = "product_line_name";
    String FULL_PART = "fullPart";
    String AVAILABILITY = "AVAILABILITY";
    String INVENTORY = "INVENTORY";
    String MFR_NAME = "mfrName";
    String EU_ROHS = "euRohs";
    String PART_STATUS = "partStatus";
    String LIFE_CYCLE_FET_NAME = "LifeCycle";
    String LIFE_CYCLE_PARAMETRIC = "Life Cycle";
    String PART_RATING = "partRating";
    String DESCRIPTION = "Description";
    String SOLR_FACET_METHOD = "para.facet.method";
    String SOLR_SHARDS_PARTS_CORE_URL = "para.rest.api.solr.shards.partsCoreUrl";
    String KEYWORD_DROP_LIMIT = "keyword.drop.limit";
    String SOLR_PASSIVE_CORE_URL = "pasive.parts.core.url";
	String ACL_CORE_URL = "acl.core.url";
	String BOM_CORE_URL = "bom.core.url";
	String BOM_PARTS_CORE_URL = "bom_parts.core.url";
	String BOM_EXPORT_API_URL = "bom.export.api.url";
	String BOM_EXPORT_LOGO_PATH = "bom.export.logo.path";
	
}
