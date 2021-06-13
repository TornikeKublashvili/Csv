package com.app.csv;

public class DiagnosticMetricsRow {
	public int rowId;
	public double caseId;  					//0 - A
	public double earlinessAvg; 			//1 - B
	public double trueAvg100;				//2 - C
	public double trueAvg1000;				//3 - D
	public double adaptionRateAvg;			//4 - E
	public double adaptionPerEp;			//5 - F
	public double costAvg;					//6 - G
	public double costPerEp;				//7 - H
	public double rewardsAvg;				//8 - I
	public double rewardPerEp;				//9 - J
	public double truePerEp;				//10 - K
	public double positionAdaptationPerEp;	//11 - L
	public double caseLengthPerEp;			//12 - M
	public double[] caseProbabilities;		//13-84 N-CF
	public double costAvg100;				//
	public double revardsAvgAll;				//
	
	
	public DiagnosticMetricsRow(int rowId, String line, int caseProbabilitiesLength) {
		this.rowId = rowId;
		
		String [] row = line.split(",");
		this.caseId = Double.valueOf(row[0]);
		this.earlinessAvg = Double.valueOf(row[1]);
		this.trueAvg100 = Double.valueOf(row[2]);
		this.trueAvg1000 = Double.valueOf(row[3]);
		this.adaptionRateAvg = Double.valueOf(row[4]);
		this.adaptionPerEp = Double.valueOf(row[5]);
		this.costAvg = Double.valueOf(row[6]);
		this.costPerEp = Double.valueOf(row[7]);
		this.rewardsAvg = Double.valueOf(row[8]);
		this.rewardPerEp = Double.valueOf(row[9]);
		this.truePerEp = row[10].toLowerCase().equals("true")? 1.0 : row[10].toLowerCase().equals("false")? 0.0: Double.parseDouble(row[10]);
		this.positionAdaptationPerEp = Double.valueOf(row[11])==-1?0.0:Double.valueOf(row[11]);
		this.caseLengthPerEp = Double.valueOf(row[12]);
		this.caseProbabilities = new double[caseProbabilitiesLength];
		for(int i = 0; i < caseProbabilities.length; i ++) {
			caseProbabilities[i] = Double.valueOf(row[i + 13])==-1?0.0:Double.valueOf(row[i + 13]);
		}
		this.costAvg100 = Double.valueOf(row[row.length-2]);
		this.revardsAvgAll = Double.valueOf(row[row.length-1]);
		
	}
	
	public DiagnosticMetricsRow(int rowId, double caseId, double earlinessAvg, double trueAvg100, double trueAvg1000, double adaptionRateAvg,
			double adaptionPerEp, double costAvg, double costPerEp, double rewardsAvg, double rewardPerEp, double truePerEp, double positionAdaptationPerEp,
			double caseLengthPerEp, double caseProbabilities[], double costAvg100, double revardsAvgAll) {
		this.rowId = rowId;
		this.caseId = caseId;
		this.earlinessAvg = earlinessAvg;
		this.trueAvg100 = trueAvg100;
		this.trueAvg1000 = trueAvg1000;
		this.adaptionRateAvg=adaptionRateAvg;
		this.adaptionPerEp = adaptionPerEp;
		this.costAvg = costAvg;
		this.costPerEp = costPerEp;
		this.rewardsAvg = rewardsAvg;
		this.rewardPerEp = rewardPerEp;
		this.truePerEp = truePerEp;
		this.positionAdaptationPerEp = positionAdaptationPerEp;
		this.caseLengthPerEp = caseLengthPerEp;
		this.caseProbabilities = caseProbabilities;
		this.costAvg100 = costAvg100;
		this.revardsAvgAll = revardsAvgAll;
	}
	
	public boolean converget() {
		if(rowId < 100) {
			return false; 
		}
		return rewardsAvg > revardsAvgAll ? true : false;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(caseId).append(",")
			.append(earlinessAvg).append(",")
			.append(trueAvg100).append(",")
			.append(trueAvg1000).append(",")
			.append(adaptionRateAvg).append(",")
			.append(adaptionPerEp).append(",")
			.append(costAvg).append(",")
			.append(costPerEp).append(",")
			.append(rewardsAvg).append(",")
			.append(rewardPerEp).append(",")
			.append(truePerEp).append(",")
			.append(positionAdaptationPerEp).append(",")
			.append(caseLengthPerEp).append(",");
		for (int i = 0; i < caseProbabilities.length; i ++) {
			sb.append(caseProbabilities[i]);
			sb.append(",");
		}
		sb.append(costAvg100).append(",")
			.append(revardsAvgAll).append(",");
		return sb.toString();
	}
}
