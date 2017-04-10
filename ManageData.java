import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
 

public class ManageData {
	
	// Load the user data from a file into the following ArrayList
	// This separates the data into one String per Transaction
	ArrayList <String> transactionStrings = new ArrayList<String>();
	
	// data from GetProjectedTransactionsForMonth
	ArrayList <String> predictionStrings = new ArrayList<String>();

	// This array contains a Data Object for EVERY transaction 
	ArrayList <Transaction> transactionObjs = new ArrayList<Transaction>();
	
	//Array of projected objects
	ArrayList <Transaction> projectedObjs = new ArrayList<Transaction>();
	
	// Contains transaction totals per month <String, Float> -> <Month, totals>
	// TreeMap is sorted by Key but not thread safe -- assume we don't care for this
	// exercise. If we did care we would have to access in synchronized fashion
	TreeMap<String, MonthlyTotals> monthlyData = new TreeMap<String, MonthlyTotals>();
		
	// Same as above EXCLUDING Credit Card transactions
	TreeMap<String, MonthlyTotals> monthlyDataNCC = new TreeMap<String, MonthlyTotals>();
	
	// Mounthly data excluding donuts
	TreeMap<String, MonthlyTotals> monthlyDataND = new TreeMap<String, MonthlyTotals>();
	
	// Monthly Credit Card Transactions
	TreeMap<String, MonthlyTotals> ccMonthlyData = new TreeMap<String, MonthlyTotals>();	
	
	// this variable holds the 'typcial' month
	private MonthlyTotals avgMonth;
	
	private MonthlyTotals currentProjectedMonthly;
	
	// Contains the monthlyData plus the projected Transactions for the current month
	TreeMap<String, MonthlyTotals> mthDataPlusProjectedData = new TreeMap<String, MonthlyTotals>();
	
	// Calculated Projected transactions for the Current Month
	private MonthlyTotals projectedTransactions;
	
	public static final int TRANSACTIONS = 1;
	public static final int PROJECTIONS = 2;
	Util utils = null;
		
	void initialize() {
		
		utils = new Util();
		
		loadData();
		
		parseStrings("data.txt",TRANSACTIONS);
		
		createDataObjs();
		
		populateMonthlyTotals();
		
		populateMonthlyTotalsNCC();
		
		populateCCMonthlyTotals();
		
		populateMonthlyTotalsND();
		
		loadProjections();
		
		parseStrings("predictions.txt",PROJECTIONS);
		
		createProjectedObjs();
		
		currentMonthlyProjected();
		
		selectAvgMonth();
		
		detectAvgMonth();
			
	}
	
	/**
	 * Retrieves data from database via API Call
	 * Save data to text file
	 */
	private void loadData (){

		try {
			String filename = "data.txt";
			String query = "{\"args\":{\"uid\":1110590645,\"token\":\"1CCCB6092E87B5552B78ADB68278A5FE\",\"api-token\":\"AppTokenForInterview\",\"json-strict-mode\":false,\"json-verbose-response\":false}}";
			URL url = new URL("https://2016.api.levelmoney.com/api/v2/core/get-all-transactions");
			
			utils.loadData(url, query, filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Retrieves data from database via REST API Call
	 * Save data to text file
	 * This method returns 'no-error' but no data.
	 * The eval method on the API website did not return any data either.
	 */
	private void loadProjections (){

		try {
			URL url = new URL("https://2016.api.levelmoney.com/api/v2/core/projected-transactions-for-month");
			String query = "{\"args\":{\"uid\":1110590645,\"token\":\"462FDCDDB86D73FAF7FEC6F95BED9688\",\"api-token\":\"AppTokenForInterview\",\"json-strict-mode\":false,\"json-verbose-response\":false},\"year\":2017,\"month\":4}"; 			
			String filename = ("predictions.txt");
			
			utils.loadData(url, query, filename);			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}  
		
	}
	
	/**
	 * parse data from file and split into individual strings for each transaction
	 * adding each string to the ArrayList specified by flag
	 * Currently only support TRANSACTIONS = 1 and PREDICATIONS = 2
	 */
	void parseStrings(String filename, int flag) {
				
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));
			String str = in.readLine();
			if (str != null){
				String substr;
				String newStr = str;
				while (newStr.indexOf("amount") != -1) {
					substr = newStr.substring(newStr.indexOf("amount")-1, newStr.indexOf("Z\"")+1);
					if (flag == TRANSACTIONS){
						transactionStrings.add(substr);
					} else { // assume PREDICATIONS
						predictionStrings.add(substr);
					}
					newStr = newStr.substring(newStr.indexOf("Z\"")+1);
				}
			}
		
		} catch (IOException e) {
			System.out.println("IOException "+e.getLocalizedMessage());
		} finally {
			try {
				if (in != null){
					in.close();
				}
			} catch (IOException e) {
				System.out.println("IOException "+e.getLocalizedMessage());
			}
		}
		
	}
	
	/**
	 * Create data objects from each string in transactionStrings. 
	 * Send each transaction as a string to the Data constructor 
	 * Add object to ArrayList transactionObjects
	 */
	void createDataObjs(){		
		Transaction data;
		for ( int i = 0; i < transactionStrings.size(); i++){
			String str = transactionStrings.get(i);
			data = new Transaction(str);
			this.transactionObjs.add(data);
		}
	}
	
	void createProjectedObjs(){		
		Transaction data;
		for ( int i = 0; i < predictionStrings.size(); i++){
			String str = predictionStrings.get(i);
			data = new Transaction(str);
			this.projectedObjs.add(data);
		}
	}
	
	/**
	 * scan transactionObjs sum monthly spending and income for the month
	 *
	 */
	void populateMonthlyTotals (){
		this.monthlyData = utils.loadObjects(this.transactionObjs,false, false, false);	
	}
	/**
	 * skip credit card transactions
	 */
	void populateMonthlyTotalsNCC (){
		this.monthlyDataNCC = utils.loadObjects(this.transactionObjs,true, false, false);	
	}
	
	/**
	 * skip donut spending
	 */
	void populateMonthlyTotalsND (){
		this.monthlyDataND = utils.loadObjects(this.transactionObjs,false, true, false);
	}
	
	/**
	 * Load ONLY credit card data
	 */
	void populateCCMonthlyTotals (){
		this.ccMonthlyData = utils.loadObjects(this.transactionObjs,false, false,true);	
	}

	/**
	 * find the current month data and add the projected data 
	 * mthDataPlusProjectedData
	 * (String date,float spent,float income,String acctName){
	 */

	 void detectAvgMonth(){
		 String currentMonth = "2017-04";
		 MonthlyTotals data1 = null;
		 MonthlyTotals data2 = null;

		 for (Map.Entry<String, MonthlyTotals> entry : monthlyData.entrySet()) {
			 data1 = (MonthlyTotals) entry.getValue();
			 data2 = new MonthlyTotals (data1.getDate(),data1.getSpent(),data1.getIncome(),data1.getAcctName());
			 this.mthDataPlusProjectedData.put(data1.getDate(), data2);
		 }
		 data1 = this.mthDataPlusProjectedData.get(currentMonth);
		 data1.incrementIncome(this.currentProjectedMonthly.getIncome());
		 data1.incrementSpent(this.currentProjectedMonthly.getSpent());
		 this.mthDataPlusProjectedData.put(currentMonth,data1);
		 this.projectedTransactions = new MonthlyTotals (data1.getDate(),data1.getSpent(),data1.getIncome(),data1.getAcctName());

	 }
	
	 /**
	  * Calculate the monthly total from projected data
	  * sum all values from API call
	  */
	void currentMonthlyProjected (){
		 java.util.Iterator<Transaction> it = projectedObjs.iterator();
		 Transaction trans = null;

		 float income = 0;
		 float spent = 0;
		 
		 while (it.hasNext()) {
			 trans =  it.next();
			 income += trans.getIncome();
			 spent  += trans.getSpent();			 
		 }
		
		 if (trans != null){
			 currentProjectedMonthly = new MonthlyTotals (trans.getDate(), spent, income, trans.getAcctName());
		 }
		 
	}
	
	
	void selectAvgMonth(){
		// calculate the 'average' monthly spending and income values
		// omit the first and last months as they are only 'partial' months
		float avgIncome = 0;
		float avgSpent = 0;
		float numMonths = monthlyDataNCC.size() - 2; //omitting 1st and last months
		
		String firstMonth = "2014-10"; // by observation I know these are the 1st and last
		String lastMonth = "2017-04";  // months - normally these values would be acquired programmatically 
		
		for (Map.Entry<String, MonthlyTotals> entry : monthlyDataNCC.entrySet()) {
			MonthlyTotals data = (MonthlyTotals) entry.getValue();
			
			if (data.getDate().contains(firstMonth) || data.getDate().contains(lastMonth)){
				continue;
			}
			avgIncome += data.getIncome();
			avgSpent += data.getSpent();			
		}
		avgIncome = avgIncome/numMonths;
		avgSpent = avgSpent/numMonths;		
		
		// find the month that is cloest to that value -- this is an ' average' month
		this.avgMonth = findAvgMonth(avgIncome, avgSpent);
		
	}
	
	MonthlyTotals findAvgMonth(float avgIncome, float avgSpent) {
	
		float closestSpentMatch = 0; //holds closest match
		MonthlyTotals closestSpentMT = null;
		float closestIncomeMatch = 0;//holds closest match
		MonthlyTotals closestIncomeMT = null;

		for (Map.Entry<String, MonthlyTotals> entry : monthlyDataNCC.entrySet()) {
			MonthlyTotals data = (MonthlyTotals) entry.getValue();

			if ((Math.abs((avgSpent - data.getSpent())) < (Math.abs(avgSpent - closestSpentMatch)) )){
				closestSpentMatch = data.getSpent();
				closestSpentMT = data;
			}
			
			if ( (Math.abs((avgIncome - data.getIncome())) < (Math.abs(avgIncome - closestIncomeMatch))) ){
				closestIncomeMatch = data.getIncome();
				closestIncomeMT = data;
			}
			 
		}

		// compare the 2 months - select the one with the least deviation from the month average
		float spentdiff = Math.abs(avgSpent - closestIncomeMT.getSpent());
		float incomediff= Math.abs(avgIncome - closestSpentMT.getIncome());

		if (spentdiff < incomediff){
			return closestIncomeMT;
		} 
		
		return closestSpentMT;
	}   		
    		
	    		
	MonthlyTotals getProjectedMonthly(){
		return this.projectedTransactions;
	}
	
	ArrayList <Transaction> getTransactionObjs(){
		return this.transactionObjs;
	}
	ArrayList <String> getTransactionStr () {
		return this.transactionStrings;
	}
	
	/**
	  * prints data in transactionObjs
	  * if skipDonuts is true suppress donut transactions
	  * if skipCC is true suppress CC transactions
	  */
	 void printAllData(boolean skipDonuts, boolean skipCC, int flag) {

		 System.out.println("Entered printAllData flag = "+flag);
		 java.util.Iterator<Transaction> it = projectedObjs.iterator();
		 if (flag == TRANSACTIONS){
			 it = transactionObjs.iterator();
		 } 
		 
		 Transaction data = null;
		 
		 while (it.hasNext()) {
			 data =  it.next();
		     if (skipDonuts){
		    	 if (data.getIsDonut()) {
		    		 continue;
		    	 }
		     }
		     if (skipCC){
		    	 if (data.getIsCc()) {
		    		 continue;
		    	 }
		     }
			 //System.out.println("	============== Account ID: "+data.getAcctName()+" ==============");
			 data.printTransaction();
		 }
	 }
	 
 
	 void printCCData(){
		 System.out.println("     == Monthly Credit Card Transactions ==");
		 utils.printData(this.ccMonthlyData);
	 }
	 
	 void displayMonthlyTotalsNCC(){
		 System.out.println("== Monthly Expenses and Income (omit credit card transactions) ==");
		 utils.printData(this.monthlyDataNCC);
	 }
	 
	 void displayMonthlyTotalsND(){
		 System.out.println("     == Monthly Expenses and Income (omit Donuts) ==");
		 utils.printData(this.monthlyDataND);
	 }
		
	 void displayMonthlyTotals(){ 
		 System.out.println("     == Monthly Expenses and Income ==");
		 utils.printData(this.monthlyData);
	 }
	 
	 void displayMonthlyPlusProjected(){ 
		 System.out.println("     == Monthly Expenses and Income ==");
		 utils.printData(this.mthDataPlusProjectedData);
	 }

	 void printRequestedFormat() {
		 System.out.println("   == Monthly Expenses and Income ==");
		 boolean first = true;
		 for (Map.Entry<String, MonthlyTotals> entry : monthlyData.entrySet()) {
			 MonthlyTotals data = (MonthlyTotals) entry.getValue();
			 data.printRequestedFormat(first);
			 first = false;
		 }
		 System.out.print("\"average\":{\"spent\":\"$");
		 System.out.printf("%.2f",this.avgMonth.getSpent());
		 System.out.print("\",\"income\":\"$");
		 System.out.printf("%.2f",this.avgMonth.getIncome());
		 System.out.println("\"}}");
		 
	 }
	 
}