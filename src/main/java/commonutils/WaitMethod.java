package commonutils;

import java.util.concurrent.TimeUnit;

public class WaitMethod {
	
	/**
	 * Wait for certain amount of time before it throws an error
	 */
	public static void wait(int timeInSeconds) throws InterruptedException {
		TimeUnit.SECONDS.sleep(timeInSeconds);
	}

}
