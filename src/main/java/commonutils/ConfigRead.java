package commonutils;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class ConfigRead implements IConst {

    public static String getPropertyValue(String propertyName){
        String propertyValue = "";
        Properties prop = new Properties();

        String runEnvironment = System.getProperty("env");

        if (runEnvironment == null) {
            // Default to staging environment if not specified
            runEnvironment = "staging";
        }

        switch (runEnvironment) {
            case "staging":
                try {
                    String OS = System.getProperty("os.name");
                    if (OS.contains("Window")) {
                        prop.load(new FileInputStream(CONFIG_PATH_STAG));
                    } else if (OS.contains("Mac")) {
                        prop.load(new FileInputStream(CONFIG_PATH_STAG));
                    } else {

                        prop.load(new FileInputStream(System.getProperty("user.dir")+"/src/main/java/com/allconfig/configAPI.properties"));

                    }
                    propertyValue = prop.getProperty(propertyName);
                } catch (Exception e) {
                    System.out.println("Exception occurred while reading from property file");
                }
                break;
            case "production":
                try {
                    String OS = System.getProperty("os.name");
                    if (OS.contains("Window")) {
                        prop.load(new FileInputStream(CONFIG_PATH_PROD));
                    } else if (OS.contains("Mac")) {
                        prop.load(new FileInputStream(CONFIG_PATH_PROD));
                    } else {
                        prop.load(new FileInputStream(System.getProperty("user.dir")+"/src/main/java/com/allconfig/configAPIPROD.properties"));
                    }
                    propertyValue = prop.getProperty(propertyName);
                } catch (Exception e) {
                    System.out.println("Exception occurred while reading from property file");
                }
                break;
            case "development":
                try {
                    String OS = System.getProperty("os.name");
                    if (OS.contains("Window")) {
                        prop.load(new FileInputStream(CONFIG_PATH_DEV));
                    } else if (OS.contains("Mac")) {
                        prop.load(new FileInputStream(CONFIG_PATH_DEV));
                    } else {
                        prop.load(new FileInputStream(System.getProperty("user.dir")+"/src/main/java/com/allconfig/configAPIDEV.properties"));
                    }
                    propertyValue = prop.getProperty(propertyName);
                } catch (Exception e) {
                    System.out.println("Exception occurred while reading from property file");
                }
                break;
            case "preview":
                try {
                    String OS = System.getProperty("os.name");
                    if (OS.contains("Window") || OS.contains("Mac")) {
                        prop.load(new FileInputStream(CONFIG_PATH_PREVIEW));
                    } else {
                        prop.load(new FileInputStream(System.getProperty("user.dir")+"/src/main/java/com/allconfig/configAPIPreview.properties"));
                        String prNumber = System.getenv("pr_number");
                        if (Objects.equals(prNumber, "")) {
                            System.out.println("Pull Request Number environment variable is not set.");
                        } else {
                            String otpServiceUrl = prop.getProperty("otp_service_url");
                            String updatedUrl = otpServiceUrl.replace("{{pr_number}}", prNumber);
                            prop.setProperty("otp_service_url", updatedUrl);
                            System.out.println("Updated OTP Service URL: " + updatedUrl);
                            String see = prop.getProperty("provider");
                            System.out.println("see it  " + see);

                        }
                    }
                    propertyValue = prop.getProperty(propertyName);

                } catch (Exception e) {
                    System.out.println("Exception occurred while reading from property file");
                }
                break;

        }
        return propertyValue;
    }

    /**
     * Read all logs & in run time generate logs report
     */

    public static void allConsoleLog() throws FileNotFoundException {
        System.setOut(new PrintStream(new FileOutputStream(All_Console_Logs_file_Path)));
    }

}
