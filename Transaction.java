
/**
 * The data for a single transaction
 * 
 * @author Deva
 *
 */

public class Transaction {
	
	private String date; 
	private float spent;
	private float income;
	private String acctName;
	private boolean isDonut;
	private boolean isCc;
	
	
	public static final String KK_DONUTS = "Krispy Kreme Donuts";
	public static final String DUNKIN = "DUNKIN #336784";
	public static final String CREDIT_CARD = "-cc";
	
	/**
	 * Takes a string representing a single transaction and creates 
	 * a Data Object from that String. 
	 *  
	 * @param str
	 */
	Transaction (String str){
		
		if (str == null || str.isEmpty()) return;
		
		
		String[] elements = str.split(",");
		String dollarAmt = null;
		String id = null;
		String time = null;
		for (int i=0; i < elements.length; i++){
			if (elements[i].contains("amount")){
				dollarAmt = elements[i];
			} else if (elements[i].contains("account-id")){
				id = elements[i];
			} else if (elements[i].contains("transaction-time")){
				time = elements[i];
			}			
		}
		
		if (str.contains(KK_DONUTS) || str.contains(DUNKIN)) {
			this.isDonut = true;
		}
		
		if (str.contains(CREDIT_CARD)) {
			this.isCc = true;
		}
		
		String text = "\"amount\":";
		
		String s = dollarAmt.substring(text.length(), dollarAmt.length());
		boolean debit = Boolean.FALSE;
		if (s.contains("-")){
			debit = Boolean.TRUE;
			s = s.substring(1);
		}
		Float f = Float.parseFloat(s);
		f = f / 10000;
		if (debit) {
			this.spent += f;
		} else {
			this.income += f;
		}
		
		text = "\"account-id\":\"";
		s = id.substring(text.length(), id.length()-1);
		this.acctName = s;
		
		text = "\"transaction-time\":\"";
		s = time.substring(text.length(), text.length()+7);
		this.date = s;
	
	}
	

	Transaction (String acctName,String date, float spent, float income,boolean isDonut,boolean isCc){
		this.acctName = acctName;
		this.date = date;
		this.spent = spent;
		this.income = income;
		this.isDonut = isDonut;
		this.isCc = isCc;
	}
	
	void setDate(String newDate){
		date = newDate;
	}
	String getDate(){
		return date;
	}
	
	void setSpent(float value){
		spent = value;
	}
	float getSpent(){
		return spent;
	}
	float incrementSpent(float value){
		spent += value;
		return spent;
	}
	
	void setIncome(float value){
		income = value;
	}
	float getIncome(){
		return income;
	}
	float incrementIncome(float value){
		income += value;
		return income;
	}
	
	void setAcctName(String value){
		acctName = value;
	}
	String getAcctName(){
		return acctName;
	}
	
	void setIsDonut(boolean value){
		isDonut = value;
	}
	boolean getIsDonut(){
		return isDonut;
	}
	
	void setIsCc (boolean value){
		isCc = value;
	}
	boolean getIsCc(){
		return isCc;
	}
	
	void printRequestedFormat(boolean first){
		if (first){
			System.out.print("{\"");
		} else {
			System.out.print("\"");
		}
		System.out.print(this.date+"\":{\"spent\":\"$");
		System.out.print(this.spent+"\",\"income\":\"$");
		System.out.print(this.income+"\"}");
		System.out.println();		
	}
	
	void printTransaction() {
		System.out.print(" Date: "+this.date);
		System.out.print("  Spent: $"+this.spent);
		System.out.print("			Income: $"+this.income);
		System.out.println("		Acct: "+this.acctName);
		//System.out.println("=======================================================================");
		//System.out.println();
	}
}