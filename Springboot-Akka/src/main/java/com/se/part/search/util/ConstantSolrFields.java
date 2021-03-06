package com.se.part.search.util;

public interface ConstantSolrFields
{
	String PL_NAME = "PL_NAME";
	String PL_ID = "PL_ID";
	String TAX_PL_NAME = "PLNAME";
	String SOLR_TAXONOMY_CORENAME = "para.solr.taxonomyCoreName";

	String TAX_PL_TYPE_NAME = "TYPENAME";
	String TAX_MAIN_NAME = "MAINNAME";
	String TAX_MAIN_ID = "MAINID";
	String TAX_SUB_NAME = "SUBNAME";
	String TAX_SUB_ID = "SUBID";
	String TAX_PL_ID = "PLID";
	String TAX_SUBSEARCHABLE_FLAG = "SUBSEARCHABLEFLAG";
	String TAX_MAINSEARCHABLE_FLAG = "MAINSEARCHABLEFLAG";

	String TAX_FET_NAME = "FEATURENAME";
	String TAX_FET_DEFINITION = "FEATUREDEFINITION";
	String TAX_FET_HCOLNAME = "HCOLNAME";
	String TAX_FET_UNIT = "UNIT";
	String TAX_SORT_TYPE = "SORTTYPE";
	String TAX_FET_ID = "FETID";
	String TAX_SUB_EMBEDED_UI_ID = "SUBUIID";
	String TAX_MAIN_EMBEDED_UI_ID = "MAINUIID";
	String TAX_EMBEDED_UI_ID = "UIID";
	String TAX_DISPLAY_ORDER = "DISPLAYORDER";
	String TAX_SUB_CATEGORY_DISPLAY_ORDER = "SUB_CATEGORY_DISPLAY_ORDER";
	String TAX_MAIN_CATEGORY_DISPLAY_ORDER = "MAIN_CATEGORY_DISPLAY_ORDER";

	String NAN_PN = "NAN_PN";
	String PART_DESCRIPTION = "PART_DESCRIPTION";
	String PART_SUMMARY_DESCRIPTION = "ROW_COM_DESC";
	String MAN_NAME_TOKEN = "MAN_NAME_TOKEN";
	String INV_COUNT_ORIGINAL = "INV_COUNT_ORIGINAL";
	String COM_PART_NUM = "COM_PARTNUM";
	String MAN_NAME = "MAN_NAME";
	String LIFE_CYCLE = "LIFE_CYCLE";
	String LIFE_CYCLE_SUMMARY = "LC_STATE";
	String STAR_RATING = "STAR_RATING";
	String ROHS = "ROHS";
	String VENDOR = "Manufacturer";
	String FOOTPRINT = "HAS_FOOTPRINT";
	String PART_STATUS = "PART_STATUS";
	String ROHS_VERSION = "ROHS_VERSION";
	String MAN_ID = "MAN_ID";
	String COM_ID = "COM_ID";
	String SMALL_IMAGE = "SMALL_IMAGE";
	String IMAGE_URL = "IMAGE_URL";
	String PART_ID = "PART_ID";

	String DATASHEET_ID = "PDF_ID";
	String LARGE_IMAGE = "LARGE_IMAGE";
	String DATASHEET_URL = "PDF_URL";
	String ONLINE_DATASHEET_URL = "ONLINE_PDF_URL";

	String MAIN_CAT_IDS = "MAIN_CAT_IDS";
	String MAIN_CAT_NAMES = "MAIN_CAT";
	String SUB_CAT_IDS = "SUB_CAT_IDS";
	String SUB_CAT_NAMES = "SUB_CAT";

	String MAN_NAME_EXACT = "MAN_NAME_EXACT";
	String NAN_PARTNUM_EXACT = "NAN_PARTNUM_EXACT";
	String PART_RATING = "PART_RATING";

	String NAN_PARTNUM = "NAN_PN";

	String IS_CUSTOM_PART = "IS_CUSTOM";

	String PASSIVE_CORE_NAN_PARTNUM = "NAN_PARTNUM";
	String PASSIVE_CORE_MAN_NAME = "MAN_NAME";
	String PASSIVE_CORE_PART_NUMBER = "COM_PARTNUM";
	String PASSIVE_CORE_DESCRIPTION = "DESCRIPTION";
	String PASSIVE_CORE_PART_ID = "COM_ID";
	String MAN_ORDER = "MAN_ORDER";
	String TAX_PL_TYPE_SORT = "PL_TYPE_SORT";
	String MAIN_CAT_NAMES_TOKNIZD = "MAIN_CAT_NAMES_TOKNIZD";
	String SUB_CAT_NAMES_TOKNIZD = "SUB_CAT_NAMES_TOKNIZD";
	String INV_COM_ID = "COM_ID";
	String INV_QUANTITY = "QUANTITY";
	String INV_DEST_NAME = "DEST_NAME";
	String INV_BUYNOW_LINK_DS = "BUYNOW_LINK";
	String MAIN_CAT_NAMES_EXACT = "MAIN_CAT_EXACT";
	String SUB_CAT_NAMES_EXACT = "SUB_CAT_EXACT";
	String MAIN_CAT_PARAMETRIC = "MAIN_CAT_NAMES";
	String TAX_PACKAGE_FLAG = "PACKAGEFLAG";
	public static final String MAN_SEARCH = "MAN_SEARCH";
	public static final String FAMILY = "FAMILY";
	public static final String MAN_CODE = "MAN_CODE";
	String SUGGEST_HANDLER = "/suggest";
	public static final String AUTO_COMPLETE_DICTIONARY_NAME = "auto_complete";
	public static final String NAN_AUTO_COMPLETE_DICTIONARY_NAME = "auto_complete_nan";
}
