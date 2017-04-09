import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	ArrayList <Transaction> predictionObjs = new ArrayList<Transaction>();
	
	// Contains transaction totals per month <String, Float> -> <Month, totals>
	// TreeMap is sorted by Key but not thread safe -- assume we don't care for this
	// exercise. If we did care we would have to access in synchronized fashion
	TreeMap<String, MonthlyTotals> monthlyData = new TreeMap<String, MonthlyTotals>();
		
	// Same as above EXCLUDING Credit Card transactions
	TreeMap<String, MonthlyTotals> monthlyDataNCC = new TreeMap<String, MonthlyTotals>();
	
	// Mounthly data excluding donuts
	TreeMap<String, MonthlyTotals> monthlyDataND = new TreeMap<String, MonthlyTotals>();
	
	// All Credit Card Transactions
	TreeMap<String, MonthlyTotals> ccMonthlyData = new TreeMap<String, MonthlyTotals>();	
	
	// this variable holds the 'typcial' month
	private MonthlyTotals avgMonth;
	
	// Predicted overall spending including from checking and credit card
	private float dollarPrediction;
	
	
	public static final int TRANSACTIONS = 1;
	public static final int PREDICATIONS = 2;
		
	void initialize() {
		
		loadData();
		
		parseStrings("data.txt",TRANSACTIONS);
		
		createDataObjs();
		
		populateMonthlyTotals();
		
		populateMonthlyTotalsNCC();
		
		populateCCMonthlyTotals();
		
		populateMonthlyTotalsND();
		
		selectAvgMonth();
		
		getPredictions();
		
		parseStrings("predictions.txt",PREDICATIONS);
		
		createPredictionObjs();
		
	}
	
	/**
	 * Retrieves data from database via API Call
	 * Save data to text file
	 */
	private void loadData (){
		URL url = null;
		try {
			url = new URL("https://2016.api.levelmoney.com/api/v2/core/get-all-transactions");
			
			String query = "{\"args\":{\"uid\":1110590645,\"token\":\"1CCCB6092E87B5552B78ADB68278A5FE\",\"api-token\":\"AppTokenForInterview\",\"json-strict-mode\":false,\"json-verbose-response\":false}}";
			
			URLConnection connection = url.openConnection();
			
			//use post mode
			connection.setDoOutput(true);
			connection.setAllowUserInteraction(false);
			
			//send query
	        PrintStream ps = new PrintStream(connection.getOutputStream());
	        ps.print(query);
	        ps.close();
	        
	        //get result
	        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
	        BufferedReader br = new BufferedReader(isr);
	        PrintWriter out = new PrintWriter(new FileWriter("data.txt"));
	        String l = null;
	        while ((l=br.readLine())!=null) {
	            out.println(l);
	        }
	        out.close();
	        br.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		} 
		
	}

	/**
	 * Retrieves data from database via REST API Call
	 * Save data to text file
	 * This method returns 'no-error' but no data.
	 * The eval method on the API website did not return any data either.
	 */
	private void getPredictions (){
		URL url = null;
		try {
			url = new URL("https://2016.api.levelmoney.com/api/v2/core/projected-transactions-for-month");
			
			String query = "{\"args\":{\"uid\":1110590645,\"token\":\"462FDCDDB86D73FAF7FEC6F95BED9688\",\"api-token\":\"AppTokenForInterview\",\"json-strict-mode\":false,\"json-verbose-response\":false},\"year\":2017,\"month\":4}"; 			
			
			
			URLConnection connection = url.openConnection();
			
			//use post mode
			connection.setDoOutput(true);
			connection.setAllowUserInteraction(false);
			
			//send query
	        PrintStream ps = new PrintStream(connection.getOutputStream());
	        ps.print(query);
	        ps.close();
	        
	        //get result
	        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
	        BufferedReader br = new BufferedReader(isr);
	        PrintWriter out = new PrintWriter(new FileWriter("predictions.txt"));
	        String l = null;
	        while ((l=br.readLine())!=null) {
	            out.println(l);
	        }
	        out.close();
	        br.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
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
	
	void createPredictionObjs(){		
		Transaction data;
		for ( int i = 0; i < predictionStrings.size(); i++){
			String str = predictionStrings.get(i);
			data = new Transaction(str);
			this.predictionObjs.add(data);
		}
	}
	
	/**
	 * scan transactionObjs sum monthly spending and income for the month
	 *
	 */
	void populateMonthlyTotals (){
		 java.util.Iterator<Transaction> it = transactionObjs.iterator();
		 Transaction trans = null;
		 MonthlyTotals total = null;
		 
		 while (it.hasNext()) {
			 trans =  it.next();
			 String key = trans.getDate();
			 total = new MonthlyTotals ( trans.getDate(), trans.getSpent(), trans.getIncome(), trans.getAcctName());
			 if ( this.monthlyData.containsKey(key)){
				 //if this month has an entry already
				 //get that object add the income and spent values
				 //to this object and put it in the array
				 MonthlyTotals t = this.monthlyData.get(key);
				 t.incrementIncome(trans.getIncome());
				 t.incrementSpent(trans.getSpent());
				 this.monthlyData.put(key, t);
			 } else {
				 this.monthlyData.put(key, total);
			 }
		 }
		
	}
	
	void populateMonthlyTotalsNCC (){
		 java.util.Iterator<Transaction> it = transactionObjs.iterator();
		 Transaction trans = null;
		 MonthlyTotals total = null;
		 
		 while (it.hasNext()) {
			 trans =  it.next();
			 
			 if (trans.getIsCc()) continue;
			 
			 String key = trans.getDate();
			 total = new MonthlyTotals ( trans.getDate(), trans.getSpent(), trans.getIncome(), trans.getAcctName());
			 if ( this.monthlyDataNCC.containsKey(key)){
				 //if this month has an entry already
				 //get that object add the income and spent values
				 //to this object and put it in the array
				 MonthlyTotals t = this.monthlyDataNCC.get(key);
				 t.incrementIncome(trans.getIncome());
				 t.incrementSpent(trans.getSpent());
				 this.monthlyDataNCC.put(key, t);
			 } else {
				 this.monthlyDataNCC.put(key, total);
			 }
		 }
		
	}
	
	void populateMonthlyTotalsND (){
		 java.util.Iterator<Transaction> it = transactionObjs.iterator();
		 Transaction trans = null;
		 MonthlyTotals total = null;
		 
		 while (it.hasNext()) {
			 trans =  it.next();
			 
			 if (trans.getIsDonut()) continue;
			 
			 String key = trans.getDate();
			 total = new MonthlyTotals ( trans.getDate(), trans.getSpent(), trans.getIncome(), trans.getAcctName());
			 if ( this.monthlyDataND.containsKey(key)){
				 //if this month has an entry already
				 //get that object add the income and spent values
				 //to this object and put it in the array
				 MonthlyTotals t = this.monthlyDataND.get(key);
				 t.incrementIncome(trans.getIncome());
				 t.incrementSpent(trans.getSpent());
				 this.monthlyDataND.put(key, t);
			 } else {
				 this.monthlyDataND.put(key, total);
			 }
		 }
		
	}
	
	
	
	
	void populateCCMonthlyTotals (){
		 java.util.Iterator<Transaction> it = transactionObjs.iterator();
		 Transaction trans = null;
		 MonthlyTotals total = null;
		 
		 while (it.hasNext()) {
			 trans =  it.next();
			 
			 if (trans.getIsCc()) continue;
			 
			 String key = trans.getDate();
			 total = new MonthlyTotals ( trans.getDate(), trans.getSpent(), trans.getIncome(), trans.getAcctName());
			 if ( this.ccMonthlyData.containsKey(key)){
				 //if this month has an entry already
				 //get that object add the income and spent values
				 //to this object and put it in the array
				 MonthlyTotals t = this.ccMonthlyData.get(key);
				 t.incrementIncome(trans.getIncome());
				 t.incrementSpent(trans.getSpent());
				 this.ccMonthlyData.put(key, t);
			 } else {
				 this.ccMonthlyData.put(key, total);
			 }
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
	    		
	    		
	
	void setDollarPrediction (float value){
		this.dollarPrediction = value;
	}
	
	float getDollarPredicition(){
		return this.dollarPrediction;
	}
	
	/**
	  * prints data in transactionObjs
	  * if skipDonuts is true suppress donut transactions
	  * if skipCC is true suppress CC transactions
	  */
	 void printAllData(boolean skipDonuts, boolean skipCC, int flag) {

		 System.out.println("Entered printAllData flag = "+flag);
		 java.util.Iterator<Transaction> it = predictionObjs.iterator();
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
		 for (Map.Entry<String, MonthlyTotals> entry : ccMonthlyData.entrySet()) {
			 MonthlyTotals data = (MonthlyTotals) entry.getValue();
			 System.out.print(" "+data.getDate()+"   Spent $");
			 System.out.printf("%.2f",data.getSpent());
			 System.out.print("   Income $");
			 System.out.printf("%.2f",data.getIncome());
			 System.out.println();		 
		 }
	 }
	 
	 void displayMonthlyTotalsND(){
		 System.out.println("     == Monthly Expenses and Income (omit Donuts) ==");
		 for (Map.Entry<String, MonthlyTotals> entry : monthlyDataND.entrySet()) {
			 MonthlyTotals data = (MonthlyTotals) entry.getValue();
			 System.out.print(" "+data.getDate()+"   Spent $");
			 System.out.printf("%.2f",data.getSpent());
			 System.out.print("   Income $");
			 System.out.printf("%.2f",data.getIncome());
			 System.out.println();		 
		 }
	 }
		
	 void displayMonthlyTotals(){
		 System.out.println("     == Monthly Expenses and Income ==");
		 for (Map.Entry<String, MonthlyTotals> entry : monthlyData.entrySet()) {
			 MonthlyTotals data = (MonthlyTotals) entry.getValue();
			 System.out.print(" "+data.getDate()+"   Spent $");
			 System.out.printf("%.2f",data.getSpent());
			 System.out.print("   Income $");
			 System.out.printf("%.2f",data.getIncome());
			 System.out.println();		 
		 }
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