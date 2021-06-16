package com.app.csv;

import java.text.DecimalFormat;
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
	
	public String getAvgReliability() {	
	    double sum = 0.0;
	    double sumTruePositive = 0.0;
	    double sumTrueNegative = 0.0;
	    double sumFalsePositive = 0.0;
	    double sumFalseNegative = 0.0;
	    int n = 0;
	    int nTruePositive = 0;
	    int nTrueNegative = 0;
	    int nFalsePositive = 0;
	    int nFalseNegative = 0;
	    
	    for(Integer i : rows.keySet()) {
			ModelsRow row = rows.get(i);
			sum += row.reliability;
		    n++;
	    	double needed = row.adaptationNeeded;
	    	double predicted = row.adaptationPredicted;
	    	if(needed == 1.0 && predicted == 1.0 ) {
	    		sumTruePositive += row.reliability;
	    		nTruePositive++;
	    	}
	    	else if(needed == 1 && predicted == 0 ) {
	    		sumFalseNegative += row.reliability;
	    		nFalseNegative++;
	    	}
	    	else if(needed == 0 && predicted == 1.0) {
	    		sumFalsePositive += row.reliability;
	    		nFalsePositive++;
	    	}
	    	else if(needed == 0 && predicted == 0) {
	    		sumTrueNegative  += row.reliability;
	    		nTrueNegative++;
	    	}
	    }
	    String sumS = roundedDouble(sum/n, 4).replace(",", "."); 
	    String sumTruePositiveS = roundedDouble(sumTruePositive/nTruePositive, 4).replace(",", ".");
	    String sumTrueNegativeS = roundedDouble(sumTrueNegative/nTrueNegative, 4).replace(",", ".");
	    String sumFalsePositiveS = roundedDouble(sumFalsePositive/nFalsePositive, 4).replace(",", ".");
	    String sumFalseNegativeS = roundedDouble(sumFalseNegative/nFalseNegative, 4).replace(",", ".");
	    return	n + "," + sumS + ","+ nTruePositive +"," + sumTruePositiveS + "," + nTrueNegative + "," + sumTrueNegativeS + "," + nFalsePositive + "," + sumFalsePositiveS + "," + nFalseNegative + "," + sumFalseNegativeS;
	}
	
	public String getConfusionMatrixPercent() {	
	    int n = 0;
	    int nTruePositive = 0;
	    int nTrueNegative = 0;
	    int nFalsePositive = 0;
	    int nFalseNegative = 0;
	    
	    for(Integer i : rows.keySet()) {
			ModelsRow row = rows.get(i);
		    n++;
	    	double needed = row.adaptationNeeded;
	    	double predicted = row.adaptationPredicted;
	    	if(needed == 1.0 && predicted == 1.0 ) {
	    		nTruePositive++;
	    	}
	    	else if(needed == 1 && predicted == 0 ) {
	    		nFalseNegative++;
	    	}
	    	else if(needed == 0 && predicted == 1.0) {
	    		nFalsePositive++;
	    	}
	    	else if(needed == 0 && predicted == 0) {
	    		nTrueNegative++;
	    	}
	    }
	    String sumTruePositiveS = roundedDouble(((double)nTruePositive/n)*100, 2).replace(",", ".");
	    String sumTrueNegativeS = roundedDouble(((double)nTrueNegative/n)*100, 2).replace(",", ".");
	    String sumFalsePositiveS = roundedDouble(((double)nFalsePositive/n)*100, 2).replace(",", ".");
	    String sumFalseNegativeS = roundedDouble(((double)nFalseNegative/n)*100, 2).replace(",", ".");
	    return	n + ","+ nTruePositive +"," + sumTruePositiveS + "%," + nTrueNegative + "," + sumTrueNegativeS + "%," + nFalsePositive + "," + sumFalsePositiveS + "%," + nFalseNegative + "," + sumFalseNegativeS +"%";
	}
	
	public String getCorrelation() {
	
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
	    Double cor = (n*sumXY - sumX*sumY)/(Math.sqrt((n*sumXX - sumX*sumX)*(n*sumYY - sumY*sumY)));
	    return	roundedDouble(cor, 4).replace(",", ".");
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
	
	public String roundedDouble(Double d, int decimal) {
		if (d.isNaN()) {
			return "0";
		}
		String format = "##.";
		for (int i = 0; i < decimal; i ++) {
			format += "#";
		}
        DecimalFormat df = new DecimalFormat(format);
        String value = df.format(d);
        if(!value.contains(",")) {
        	value += ",0";
        }
        
        while(value.substring(value.indexOf(","), value.length()).length() <= decimal) {
        	value+="0"; 
        }
        return value;
	}
}
