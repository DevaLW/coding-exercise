import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Util {

	
	void loadData (URL url, String query, String filename){
		
		try {
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
	        PrintWriter out = new PrintWriter(new FileWriter(filename));
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
	
	void printData (TreeMap<String, MonthlyTotals> transactions) {
		 for (Map.Entry<String, MonthlyTotals> entry : transactions.entrySet()) {
			 MonthlyTotals data = (MonthlyTotals) entry.getValue();
			 System.out.print(" "+data.getDate()+"   Spent $");
			 System.out.printf("%.2f",data.getSpent());
			 System.out.print("   Income $");
			 System.out.printf("%.2f",data.getIncome());
			 System.out.println();		 
		 }
	}

	
	/**
	 * 
	 * If the ccFlag is true skip credit card transactions
	 * if the sdFlag is true skip donut expenses
	 * 
	 * @param transObjs
	 * @param ccFlag, sdFlag
	 * @return TreeMap
	 */
	TreeMap<String, MonthlyTotals> loadObjects(ArrayList <Transaction> transObjs, 
												boolean ccFlag, boolean sdFlag, boolean onlyCcd) {
		
		java.util.Iterator<Transaction> it = transObjs.iterator();
		Transaction trans = null;
		MonthlyTotals total = null;
	 
		TreeMap<String, MonthlyTotals> monthlyData = new TreeMap<String, MonthlyTotals>();
		
		while (it.hasNext()) {
			trans =  it.next();
		 
			if (ccFlag && trans.getIsCc()) continue;
			if (onlyCcd && !trans.getIsCc()) continue;
			if (sdFlag && trans.getIsDonut()) continue;
		 
			String key = trans.getDate();
				total = new MonthlyTotals ( trans.getDate(), trans.getSpent(), trans.getIncome(), trans.getAcctName());
				if ( monthlyData.containsKey(key)){
					//if this month has an entry already
					//get that object add the income and spent values
					//to this object and put it in the array
					MonthlyTotals t = monthlyData.get(key);
					t.incrementIncome(trans.getIncome());
					t.incrementSpent(trans.getSpent());
					monthlyData.put(key, t);
				} else {
					monthlyData.put(key, total);
				}
		}
		return monthlyData;
	 
	}
	
}