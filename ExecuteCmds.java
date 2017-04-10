

public class ExecuteCmds{
	  
	ManageData manageData = null;
	
	ExecuteCmds(){
			
		//Constructor for UserData parses the data file. 
		//Resulting in one string per transaction
		//Saving those strings in ArrayList transactionStrings
		manageData = new ManageData();
		
		//initializes required data structures
		manageData.initialize();		

	}
	
	void getProjectedMonthlyData(){
		MonthlyTotals mt = manageData.getProjectedMonthly();
		mt.printRequestedFormat(true);
	}
	
	/**
	 * Requirement 2:
	 * Determine how much money the user spends and makes in each of the months 
	 * for which we have data, and in the "average" month. 
	 * What "average" means is up to you.
	 * 
	**/
	void displayMonthlyData (){
		manageData.displayMonthlyTotals();
	}
	
	/**
	 * Display monthly transactions Excluding Donuts
	 */
	void displayMthlyDataNCC (){
		manageData.displayMonthlyTotalsNCC();
	}
	

	/**
	 * Display monthly transactions Excluding Donuts
	 */
	void displayMonthlyDataND (){
		manageData.displayMonthlyTotalsND();
	}
	
	
	/**
	 * Prints all data -- one entry per transaction
	 * @param data
	 */
	void displayAllData (boolean suppressDonuts, boolean suppressCC) {
		manageData.printAllData(suppressDonuts, suppressCC, ManageData.TRANSACTIONS);
	}
	
	/**
	 * Display only credit card data
	 */
	void displayCcData () {
		manageData.printCCData();
	}
	
	

	/**
	 * Prints all data -- one entry per transaction
	 * @param data
	 */
	void displayFormatRequested () {
		manageData.printRequestedFormat();
	}
	
	
	void displayHelp(){
		
		System.out.println("	--monthly-totals | -mt");
		System.out.println("	Display monthly totals.");
		System.out.println();
		
		System.out.println("	--display-requested-format | -drf");
		System.out.println("	Display monthly totals in the original requested format.");
		System.out.println();
			
		System.out.println("	--cc-transactions | -cct");
		System.out.println("	Display monthly totals for credit card transactions.");
		System.out.println();
			
		System.out.println("	--ignore-donuts |  -id");
		System.out.println("	Display monthly spending omitting cost of donuts");
		System.out.println();
		
		System.out.println("	--ignore-cc-payments |  -icc");
		System.out.println("	Display monthly data for cash only transactions - omit credit card transactions");
		System.out.println();
					
		System.out.println("	--crystal-ball |  -cb");
		System.out.println("	Display projected for current month");
		System.out.println();

		System.out.println("	--display-user-data |  -dud");
		System.out.println("	Display ALL user data EACH transaction listed separately.");
		System.out.println();
		
		System.out.println("	--help |  -h");
		System.out.println("	Display help");
		System.out.println();

		System.out.println("	--quit |  -q");
		System.out.println("	Exit the program");
		System.out.println();

		
	}
	
}