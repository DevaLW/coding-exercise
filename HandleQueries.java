import java.util.Scanner;

/*
 * Main Entry point for the program
 * 
 * 
 */

public class HandleQueries {

	// Display all monthly totals the user spends and makes
	// and in the "average" month
	public static final String MONTHLY_TOTALS = "--monthly-totals";
	public static final String SHORT_MONTHLY_TOTALS = "-mt";

	// Display total spending omitting cost of donuts
	public static final String NO_DONUTS = "--ignore-donuts";
	public static final String SHORT_NO_DONUTS = "-id";

	// Predict spending for current month
	public static final String CRYSTAL_BALL = "--crystal-ball";
	public static final String SHORT_CRYSTAL_BALL = "-cb";

	// Display data for cash only transactions
	public static final String NO_CREDIT_CARD = "--ignore-cc-payments";
	public static final String SHORT_NO_CREDIT_CARD = "-icc";

	// Display data for credit card only transactions
	public static final String CREDIT_CARD = "--cc-transactions";
	public static final String SHORT_CREDIT_CARD = "-cct";

	// Display all user data
	public static final String ALL_DATA = "--display-user-data";
	public static final String SHORT_ALL_DATA = "-dud";

	// Original Requested Format
	public static final String ORGINIAL = "--display-requested-format";
	public static final String SHORT_ORGINIAL = "-drf";

	// Exit the program
	public static final String HELP = "--help";
	public static final String SHORT_HELP = "-h";

	// Exit the program
	public static final String EXIT = "--quit";
	public static final String SHORT_EXIT = "-q";

	public static void main(String[] args) {

		System.out.println("Loading Data This will only take a Moment Please Wait........ ");

		// Constructor for this objects initializes everything
		ExecuteCmds execute = new ExecuteCmds();

		Thread inputThread = new Thread(new Runnable() {

			@Override
			public void run() {

				Scanner scan = new Scanner(System.in);
				String input = "";
				System.out.println();
				System.out.println("Valid Commands are:");
				System.out.println();
				execute.displayHelp();
				while (true) {
					System.out.println();
					System.out.println("Enter command: ");
					input = scan.nextLine();

					switch (input) {
					case MONTHLY_TOTALS:
					case SHORT_MONTHLY_TOTALS:
						execute.displayMonthlyData();
						break;
					case NO_CREDIT_CARD:
					case SHORT_NO_CREDIT_CARD:
						execute.displayMthlyDataNCC();
						break;
					case CREDIT_CARD:
					case SHORT_CREDIT_CARD:
						execute.displayCcData();
						break;
					case ALL_DATA:
					case SHORT_ALL_DATA:
						execute.displayAllData(false, false);
						break;
					case NO_DONUTS:
					case SHORT_NO_DONUTS:
						execute.displayMonthlyDataND();
						break;
					case CRYSTAL_BALL:
					case SHORT_CRYSTAL_BALL:
						execute.getProjectedMonthlyData();
						break;
					case ORGINIAL:
					case SHORT_ORGINIAL:
						execute.displayFormatRequested();
						break;
					case HELP:
					case SHORT_HELP:
						execute.displayHelp();
						break;
					case EXIT:
					case SHORT_EXIT:
						System.out.println("exit");
						scan.close();
						return;
					default:
						System.out.println("Invalid input");
						execute.displayHelp();
						break;

					}
				}
			}
		});

		inputThread.start();
	}

}