package test.surfexample;

public class TCTwelve {

	public static void main(String[] args) {

		try {
			long startTime = System.currentTimeMillis();
			long stopTime = System.currentTimeMillis();
			long timeTaken = stopTime - startTime;
			long timeInSecs = ((timeTaken / 1000));
			double speed = 45 / timeInSecs;
			System.out.println("Speed is:" + speed);
		} catch (ArithmeticException e) {
			// handle the exception

		}
	}

}
