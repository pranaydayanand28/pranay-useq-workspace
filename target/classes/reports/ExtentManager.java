package resource.reports;

import com.relevantcodes.extentreports.ExtentTest;

       /* The code is a class that manages threads.
        It has a single instance variable, which is the thread local object for ExtentTest objects.
        The getExtTest() method returns an ExtentTest object from the thread local and assignCategory() assigns a category to it.
        The setExtent test() method sets the value of the thread local to be whatever passed in as its argument, in this case an instance of ExtentTest .
        The code sets a new instance of the ThreadLocal class to a new thread local variable named exTest.
        The code then assigns the value of this thread local variable to an instance of the ExtentManager class and returns it.
        Lastly, the code creates a static method that can be called from any other class in order to retrieve an instance of the ExtentTest object.*/

public class ExtentManager {

    private ExtentManager() {}

    public static final ThreadLocal<ExtentTest> exTest= new ThreadLocal<>();

    public static ExtentTest getExtTest(String category)
    {
        return exTest.get().assignCategory(category);
    }

    public static ExtentTest getExtTest()
    {
        return exTest.get();
    }

    public static void setExtentTest(ExtentTest test)
    {
        exTest.set(test);
    }
}