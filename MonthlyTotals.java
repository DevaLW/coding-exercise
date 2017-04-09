

public class MonthlyTotals {
	
	private String date; //month
	private float spent;
	private float income;
	private String acctName;

	MonthlyTotals(){
	}
	
	MonthlyTotals (String date,float spent,float income,String acctName){
		this.date = date;
		this.spent = spent;
		this.income = income;
		this.acctName = acctName;	
	}
	
	String getDate () {
		return this.date;
	}
	void setDate (String value) {
		this.date = value;
	}
	String getAcctName () {
		return this.acctName;
	}
	void setAcctName (String value) {
		this.acctName = value;
	}
	float getSpent () {
		return this.spent;
	}
	void setSpent (float value) {
		this.spent = value;
	}
	float getIncome () {
		return this.income;
	}
	void setIncome (float value) {
		this.income = value;
	}	
	float incrementSpent(float value) {
		this.spent += value;
		return spent;
	}
	float incrementIncome(float value) {
		this.income += value;
		return income;
	}
	
	void printRequestedFormat(boolean first){
		if (first){
			System.out.print("{\"");
		} else {
			System.out.print("\"");
		}
		System.out.print(this.date+"\":{\"spent\":\"$");
		System.out.printf("%.2f",this.spent);
		System.out.print("\",\"income\":\"$");
		System.out.printf("%.2f",this.income);
		System.out.print("\"},");
		System.out.println();		
	}
	
}