package resource.reports;

import com.relevantcodes.extentreports.ExtentReports;
import commonutils.IConst;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
This class is just defining report structure and finally used to initialize extent report from the base Class
*/

public class ExtentReport {

    public static ExtentReports report=null;
    public static String extentreportpath;
    public static final DateFormat date = new SimpleDateFormat("yyyyMMdd-HHmm");
    public Date dateForm = new Date();
    public static String buildNumber=System.getProperty("buildNumber","");
    public static String location=System.getProperty("user.dir")+"/test-output/extentreports/"+buildNumber+"/";
    public File directory=new File(location);

    //To avoid external initialization
    private ExtentReport() {
        if(!directory.exists()){
            if (directory.mkdirs()){

            }
        }
        extentreportpath=IConst.extentReportPath;
        report=new ExtentReports(extentreportpath + date.format(dateForm) + ".html", true);
        report.loadConfig(new File(IConst.extentConfigFilePath));
    }

    public static void initialize()
    {
        new ExtentReport();
    }
}