package commonutils;
import resource.reports.ExtentReport;

public interface IConst {

	String CONFIG_PATH_STAG = "src/main/java/com/allconfig/configAPI.properties";
	String CONFIG_PATH_Linux_STAG = "/home/jenkins/workspace/smallcaseAPIAutomation/src/main/java/com/allconfig/configAPI.properties";
	String CONFIG_PATH_DEV = "src/main/java/com/allconfig/configAPIDEV.properties";
	String CONFIG_PATH_Linux_DEV = "/home/jenkins/workspace/smallcaseAPIAutomation/src/main/java/com/allconfig/configAPIDEV.properties";
	String CONFIG_PATH_PROD = "src/main/java/com/allconfig/configAPIPROD.properties";
	String CONFIG_PATH_Linux_PROD = "/home/jenkins/workspace/smallcaseAPIAutomation/src/main/java/com/allconfig/configAPIPROD.properties";

	String CONFIG_PATH_PREVIEW = "src/main/java/com/allconfig/configAPIPreview.properties";
	String CONFIG_PATH_Linux_PREVIEW = "/home/jenkins/workspace/smallcaseAPIAutomation/src/main/java/com/allconfig/configAPIPreview.properties";
	String All_Console_Logs_file_Path = "/allConsole.logs";
	String QUERY_PARAMS_FILE = System.getProperty("user.dir") + "/src/main/java/resource/testData/QueryParams.json";
	// String XL_PATH = "";
	String extentConfigFilePath = "config/extent_config.xml";
	String extentReportPath = System.getProperty("user.dir")+"/test-output/extentreports/"+ ExtentReport.buildNumber+"/extentreport";;
	String StockQuery = "./src/main/java/resource/testData/Stockquery.xlsx";
	String scid_stag = "./src/main/java/resource/testData/scid_stag.xlsx";
	String scid_prod = "./src/main/java/resource/testData/scid_prod.xlsx";
	String CREATED_STOCK_PICKS = "src/main/java/resource/testData/CreatedSmallcasePicks.json";

	//Test Data File paths
	String smallcaseLoginResponseJSONSchema = "/src/main/java/resource/testData/JSONSchemas/smallcaseLogin.json";
	String excelSheetPath = "/src/main/java/resource/testData/PayloadParamSheet.xlsx";
	String getUserResponseJSONSchema = "/src/main/java/resource/testData/JSONSchemas/getUser.json";
	String DASHBOARD_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/dashboard.json";
	String DISCOVER_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/discover.json";
	String COLLECTION_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/collection.json";
	String CHECK_STATUS_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/checkStatus.json";
	String BLOG_PROXY_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/blogProxy.json";
	String GET_COLLECTION_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/getCollection.json";
	String BROKER_LOGIN_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/brokerLogin.json";
	String REQUEST_TOKEN_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/reqToken.json";
	String WATCHLIST_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/watchlist.json";
	String SEARCH_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/search.json";
	String INVESTMENT_INSIGHTS_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/InvestmentInsights.json";
	String HIGHLIGHTED_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/Highlighted.json";
	String FUNDS_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/funds.json";
	String PRICEANDCHANGE_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/priceAndChange.json";
	String OFFERS_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/offers.json";
	String GET_DRAFT_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/getDraft.json";
	String GET_DRAFTS_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/getDrafts.json";
	String GET_NEXUMLOGIN_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/nexumLogin.json";
	String GET_NEXUMUSERCREDIT_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/nexumUserCredit.json";
	String GET_NEXUMUSERSTATUS_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/nexumUserStatus.json";
	String GET_NEXUMHOLDING_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/nexumHolding.json";
	String GET_NEXULOANPDF_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/nexumLoanPdf.json";
	String GET_NEXUMLOANSUMMARY_SCHEMA = "/src/main/java/resource/testData/JSONSchemas/nexumloanSummary.json";
	//Excel sheet Names

	//Excel sheet Names
	String brokerLoginPayload = "brokerAuthLogin Payload";
	String integrationBrokers = "Integration Brokers";

	String createPayload = "src/main/java/resource/testData/createPayload.json";
	String createDid = "src/main/java/resource/testData/did.json";
	String createScid = "src/main/java/resource/testData/scid.json";
	String buycreatePayload ="src/main/java/resource/testData/scid.json";

}