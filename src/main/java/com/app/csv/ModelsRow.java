package com.app.csv;

public class ModelsRow {
	public int rowId;
	public double caseId;  					
	public double processLength; 			
	public double position;				
	public double actualDuration;				
	public double predictedDuration;			
	public double plannedDuration;			
	public double reliability;					
	public double adaptationNeeded;				
	public double adaptationPredicted;				
	public double predictionTrue;				

	
	
	public ModelsRow(String line) {
		String [] row = line.split(",");
		this.rowId = Integer.valueOf(row[0]);
		this.caseId = Double.valueOf(row[1]);
		this.processLength = Double.valueOf(row[2]);
		this.position = Double.valueOf(row[3]);
		this.actualDuration = Double.valueOf(row[4]);
		this.predictedDuration = Double.valueOf(row[5]);
		this.plannedDuration = Double.valueOf(row[6]);
		this.reliability = Double.valueOf(row[7]);
		this.adaptationNeeded = Double.valueOf(row[8]);
		this.adaptationPredicted = Double.valueOf(row[9]);
		this.predictionTrue = Double.valueOf(row[10]);;

	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(rowId).append(",")
			.append(caseId).append(",")
			.append(processLength).append(",")
			.append(position).append(",")
			.append(actualDuration).append(",")
			.append(predictedDuration).append(",")
			.append(plannedDuration).append(",")
			.append(reliability).append(",")
			.append(adaptationNeeded).append(",")
			.append(adaptationPredicted).append(",")
			.append(predictionTrue).append(",");
		return sb.toString();
	}
}
