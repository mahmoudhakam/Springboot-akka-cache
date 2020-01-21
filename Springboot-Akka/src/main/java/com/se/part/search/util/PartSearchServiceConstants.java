package com.se.part.search.util;

public class PartSearchServiceConstants
{
	public static final String MAX_NUMBER_OF_THREADS = "max.number.of.threads";
	public static final String NUMBER_OF_THREADS = "number.of.threads";
	public static final String PARTS_CORE = "part.parts.core.url";
	public static final String TAXONOMY_CORE = "part.taxonomy.core.url";
	public static final String PARAMETRIC_CORE = "part.parametric.core.url";
	public static final String PARTS_LOOKUP_CORE = "part.lookup.core.url";
	public static final String PARTS_PASSIVE_CORE = "part.passive.core.url";
	public static final String MAN_CORE = "part.man.core.url";
	public static final String PARTS_ALIAS_CORE = "part.partsAlias.core.url";
	public static final String PART_ACL_CORE = "part.acl.core.url";
	public static final String ALERT_CORE = "part.alert.parametric.core.url";

	public static final String ACCEPT_HEADER = "Accept";
	public static final String XML_FORMAT = "xml";
	public static final String DEFAULT_FORMAT = "json";
	public static final String REQUEST_FORMAT = "fmt";
	public static final String AUTH_TOKEN = "seToken";
	public static final String MAX_PAGE_SIZE = "50";

	public static final String API_DATE_FROMAT = "MM-dd-yyyy";
	public static final String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static class REGEX
	{
		public static final String REGEX_NUMBER_ONLY = "^[0-9]*$";
		public static final String REGEX_DATE_API = "^((0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])-[12]\\d{3})$";
	}

	public static class Parameters
	{
		public static final String COM_ID = "comIDs";
		public static final String PAGE_NUMBER = "pageNumber";
		public static final String PAGE_SIZE = "pageSize";
		public static final String SE_TOKEN = "seToken";
		public static final String PART_NUMBER = "partNumber";
		public static final String SEARCH_MODE = "mode";
		public static final String RESPONSE_FORMAT = "fmt";
		public static final String WILDCARD_SINGLE = "wildcardSingle";
		public static final String WILDCARD_MULTI = "wildCardMulti";
		public static final String KEYWORD = "keyword";
		public static final String USER_NAME = "userName";
		public static final String API_KEY = "apiKey";
		public static final String DEBUG_MODE = "debugMode";
		public static final String FROM = "from";
		public static final String TO = "to";
		public static final String START = "start";
		public static final String EXCLUDED_PARTS = "excluded_comids";
		public static final String MAN_ID = "man_id";
		public static final String SIMILAR_TYPE = "similar_id";
		public static final String BOM_ID = "bomID";

	}

	public static class SummaryCoreFields
	{
		public static final String COM_ID = "COM_ID";
		public static final String ROW_COM_DESC = "ROW_COM_DESC";
		public static final String COM_PARTNUM = "COM_PARTNUM";
		public static final String ECCN = "ECCN";
		public static final String HTSUSA = "HTSUSA";
		public static final String MAN_NAME = "MAN_NAME";
		public static final String MAN_ID = "MAN_ID";
		public static final String PDF_URL = "PDF_URL";
		public static final String PL_NAME = "PL_NAME";
		public static final String SCHEDULEB = "SCHEDULEB";
		public static final String TAX_PATH = "TAX_PATH";
		public static final String UNSPSC = "UNSPSC";
		public static final String PDF_URL_DS = "PDF_URL_DS";
		public static final String NAN_PARTNUM_EXACT = "NAN_PARTNUM_EXACT";
		public static final String LC_STATE = "LC_STATE";
		public static final String ROHS = "ROHS";
		public static final String ROHS_VERSION = "ROHS_VERSION";
		public static final String IMAGE_URL = "IMAGE_URL";
	}

	public static class ParametricCoreFields
	{
		public static final String PART_ID = "PART_ID";
		public static final String FET_VALUE = "*_VALUE";
		public static final String PL_ID = "PL_ID";
	}

	public static class TaxonomyCoreFields
	{
		public static final String PL_ID = "PLID";
		public static final String HCOLNAME = "HCOLNAME";
		public static final String FEATURENAME = "FEATURENAME";
		public static final String UNIT = "UNIT";
		public static final String EXPERT_SHEET_ORDER = "EXPERT_SHEET_ORDER";
	}

	public static class ManufaturerCoreFields
	{
		public static final String MAN_ID = "MAN_ID";
		public static final String MAN_NAME = "MAN_NAME";
		public static final String MAN_SEARCH = "MAN_SEARCH";
		public static final String MAN_CODE = "MAN_CODE";
	}

	public static class LookupCoreFields
	{
		public static final String NEW_COM_ID = "NEW_COM_ID";
		public static final String NAN_PARTNUM = "NAN_PARTNUM";
		public static final String MAN_ID = "MAN_ID";
	}

	public static class PassiveCoreFields
	{
		public static final String NAN_PARTNUM = "NAN_PARTNUM";
		public static final String MAN_ID = "MAN_ID";
	}

	public static class AlertCoreFields
	{
		public static final String DD_DATE = "DD_DATE";
		public static final String CATEGORY_ID_DML = "CATEGORY_ID_DML";
		public static final String COM_ID = "COM_ID";
		public static final String SEQ_ID = "SEQ_ID";
		public static final String PL_ID = "PL_ID";
		public static final String MAN_ID = "MAN_ID";
		public static final String SWITCH_ID = "SWITCH_ID";
		public static final String OLD_VAL = "OLD_VAL";
		public static final String NEW_VAL = "NEW_VAL";
		public static final String DML_FET_NAME = "DML_FET_NAME";

	}

}
