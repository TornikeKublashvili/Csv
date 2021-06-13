package com.app.csv;

import java.util.Map;
import java.util.TreeMap;

public class ModelsCsv {
	public int maxRowId;

	public String header = "step,case_id,process_Length,position,actual_duration,predicted_duration,planned_duration,reliability,adaptation_needed,adaptation_predicted,prediction_true,";
	
	public Map<Integer, ModelsRow> rows;
	
	public ModelsCsv () {
		this.rows = new TreeMap<Integer, ModelsRow>();
		this.maxRowId = -1;
	}
	public void insertRow(int id, ModelsRow row) {
		if(row != null) {
			if(id <= maxRowId && id > -1) {
				id = maxRowId;
			}
			rows.put(id, row);
			maxRowId = id;
		}
	}
	
	
	public double getCorrelation() {
	
	    double sumX = 0.0;
	    double sumY = 0.0;
	    double sumXX = 0.0;
	    double sumYY = 0.0;
	    double sumXY = 0.0;
	    int n = 0;	
	    for(Integer i : rows.keySet()) {
	    	ModelsRow row = rows.get(i);
	      double x = row.adaptationNeeded;
	      double y = row.adaptationPredicted;
	
	      sumX += x;
	      sumY += y;
	      sumXX += x * x;
	      sumYY += y * y;
	      sumXY += x * y;
	      n++;
	    }
	    return (n*sumXY - sumX*sumY)/(Math.sqrt((n*sumXX - sumX*sumX)*(n*sumYY - sumY*sumY)));
	}
	
	public double getMCC() {
		
		int truePositive = 0;
		int trueNegative = 0;
		int falsePositive = 0;
		int falseNegative = 0;
	    for(Integer i : rows.keySet()) {
	    	ModelsRow row = rows.get(i);
	    	double needed = row.adaptationNeeded;
	    	double predicted = row.adaptationPredicted;
	    	if(needed == 1.0 && predicted == 1.0 ) {
	    		truePositive ++;
	    	}
	    	else if(needed == 1 && predicted == 0 ) {
	    		falseNegative++;
	    	}
	    	else if(needed == 0 && predicted == 1.0) {
	    		falsePositive++;
	    	}
	    	else if(needed == 0 && predicted == 0) {
	    		trueNegative ++;
	    	}
	    }
	    return ((truePositive*trueNegative) - (falsePositive*falseNegative))/(Math.sqrt(truePositive+falsePositive )*Math.sqrt(truePositive+falseNegative)*Math.sqrt(trueNegative+falsePositive)*Math.sqrt(trueNegative+falseNegative));
	}
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(header).append(System.getProperty("line.separator"));		
		for(int i : rows.keySet()) {
			sb.append(rows.get(i)).append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}
}
