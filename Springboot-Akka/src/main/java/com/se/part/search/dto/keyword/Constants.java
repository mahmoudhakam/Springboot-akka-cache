package com.se.part.search.dto.keyword;

public interface Constants
{
	class RequestResponseFormat
	{
		public static final String ACCEPT_HEADER = "Accept";
		public static final String XML_FORMAT = "xml";
		public static final String DEFAULT_FORMAT = "json";
		public static String REQUEST_FORMAT = "fmt";

	}

	String BOM_VALIDATION_STRATEGY_EXACT = "exactValidator";
	String BOM_VALIDATION_STRATEGY_PASSIVE = "passiveValidator";
	String BOM_VALIDATION_STRATEGY_LOOKUP = "lookupValidator";
	String BOM_VALIDATION_STRATEGY_EXACT_PART_ONLY = "exactValidatorPartOnly";
	String BOM_VALIDATION_STRATEGY_BEGINWITH = "beginWithValidator";
	String BOM_VALIDATION_STRATEGY_SIMILAR = "similarValidator";
	String PARA_SERVICE_SEARCH_PROPERTIES = "C:/";
	String SOLR_PARTS_CORE_URL = "para.rest.api.solr.partsCoreUrl";
	String SOLR_TAXONOMY_CORE_URL = "para.rest.api.solr.taxonomyCoreUrl";
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
	String SOLR_FACET_THREADS = "para.facet.threads";
	// String SOLR_SHARDS_PARTS_CORE_URL = "para.rest.api.solr.shards.partsCoreUrl";
	// String KEYWORD_DROP_LIMIT = "keyword.drop.limit";
	String SPONSERED_MFR = "keyword.sponseredMfrs";
	String SOLR_PASSIVE_CORE_URL = "pasive.parts.core.url";
	String SOLR_INV_CORE_URL = "inv.core.url";
	String SOLR_MFR_CORE_URL = "mfr.core.url";
	String SOLR_MAN_CORENAME = "solr.man.corename";
	String SOLR_PARTS_SUMMARY_CORE_URL = "solr.summary.conrename";

	String SOLR_MAN_BASIC_PARTS_CORE_URL = "solr.man.basic.corename";
	String SOLR_DESC_CORE_URL = "solr.desc.corename";
	String AUTOCOMPLETE_PART = "part";
	String AUTOCOMPLETE_MAN = "man";
	String AUTOCOMPLETE_DESC = "desc";
	String AUTOCOMPLETE_CAT = "cat";
	String PARAMETRIC_STRATEGY = "ParametricStrategy";
	String MANUFACTURER_STRATEGY = "ManufacturerStrategy";
	String PACKAGING_STRATEGY = "PackagingStrategy";
	String ACTOR_MANAGER_POSTFIX = "_Manager";
	String PACKAGE_STRATEGY = "PackageStrategy";
	String PCN_STRATEGY = "PCNStrategy";
	String BEAN = "_BEAN";
	String RISK_STRATEGY = "RiskStrategy";
	String CLASSIFICATION_STRATEGY = "ClassificationStrategy";
	String REACH_STRATEGY = "ReachStrategy";
	String QUALIFICATIONS_STRATEGY = "QualificationsStrategy";
	String RAREELEMENTS_STRATEGY = "RareElementsStrategy";
	String CHINAROHS_STRATEGY = "ChinaROHSStrategy";
	String WEEE_STRATEGY = "WeeeStrategy";
	String COOS_STRATEGY = "CountryOfOriginStrategy";
	String PRICE_STRATEGY = "PriceStrategy";
	String ROHS_STRATEGY = "ROHSStrategy";
	String SUMMARY_STRATEGY = "SummaryStrategy";
	String CONFLICT_MINIRALS_STRATEGY = "ConflictMiniralsStrategy";
	String GENERAL_PASSIVE_STRATEGY = "GeneralPassiveStrategy";

	public static class ConflictMiniralsFeatures
	{
		public static final String status = "Conflict Mineral Status";
		public static final String membership = "EICC Membership";
		public static final String template = "EICC-GeSI/CMRT Template";
		public static final String version = "EICC Version";
		public static final String certitificateOfCpmliance = "Certificate of Compliance";
		public static final String policy = "Conflict Minerals Policy";
		public static final String statement = "Conflict Mineral Statement";
		public static final String supplier = "Supplier";
	}

	public static class GeneralFeatures
	{
		public static final String DESCRIPTION = "SiliconExpert Description";
		public static final String URL = "Datasheet URL";
		public static final String LCSTATUS = "Lifecycle Status";
		public static final String ROHSSTATUS = "EU RoHS Status";
		public static final String ROHSVERSION = "RoHS Version";
		public static final String CROSSES_COUNT = "Crosses";
		public static final String PCN_COUNT = "PCN Summary";
		public static final String PL = "Product Line";
		public static final String TAXPATH = "Taxonomy Path";
		public static final String INTRODUCTIONDATE = "Introduction Date";
		public static final String FAMILYNAME = "Family Name";
		public static final String GRADE = "SiliconExpert Grade";
		public static final String PARTIMAGE = "Part Image";
	}

	public static class ROHSFeatures
	{
		public static final String ELV = "ELV";
		public static final String HALOGEN_FREE = "Halogen Free";
		public static final String GREEN = "Green";

		public static final String STATUS_2002_95_EC = "RoHS Status (2002/95/EC)";
		public static final String EXEMPTION_2002_95_EC = "RoHS Exemption (2002/95/EC)";
		public static final String EXEMPTION_TYPE_2002_95_EC = "RoHS Exemption Type (2002/95/EC)";
		public static final String CONVERSION_DATE_2002_95_EC = "Conversion Date (2002/95/EC)";
		public static final String SOURCE_TYPE_2002_95_EC = "RoHS Source Type (2002/95/EC)";
		public static final String SOURCE_DOCUMENT_2002_95_EC = "RoHS Source Document (2002/95/EC)";

		public static final String STATUS_2011_65_EU = "RoHS Status (2011/65/EU)";
		public static final String EXEMPTION_2011_65_EU = "RoHS Exemption (2011/65/EU)";
		public static final String EXEMPTION_TYPE_2011_65_EU = "RoHS Exemption Type (2011/65/EU)";
		public static final String CONVERSION_DATE_2011_65_EU = "Conversion Date (2011/65/EU)";
		public static final String SOURCE_TYPE_2011_65_EU = "RoHS Source Type (2011/65/EU)";
		public static final String SOURCE_DOCUMENT_2011_65_EU = "RoHS Source Document (2011/65/EU)";

		public static final String STATUS_2011_65_EU_2015_863 = "RoHS Status (2011/65/EU, 2015/863)";
		public static final String EXEMPTION_2011_65_EU_2015_863 = "RoHS Exemption (2011/65/EU, 2015/863)";
		public static final String EXEMPTION_TYPE_2011_65_EU_2015_863 = "RoHS Exemption Type (2011/65/EU, 2015/863)";
		public static final String CONVERSION_DATE_2011_65_EU_2015_863 = "Conversion Date (2011/65/EU, 2015/863)";
		public static final String SOURCE_TYPE_2011_65_EU_2015_863 = "RoHS Source Type (2011/65/EU, 2015/863)";
		public static final String SOURCE_DOCUMENT_2011_65_EU_2015_863 = "RoHS Source Document (2011/65/EU, 2015/863)";
	}
}
