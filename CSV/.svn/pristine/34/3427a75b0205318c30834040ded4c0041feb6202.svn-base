package com.app.csv;

import java.util.Map;
import java.util.TreeMap;

public class DiagnosticMetricsCsv {
	
	
	public int maxRowId;

	public String header = "case_id,earliness_avg,true_avg_100,true_avg_1000,adaption_rate_avg,adapt_per_ep,costs_avg,cost_per_ep,rewards_avg,reward_per_ep,true_per_ep,position_adaptation_per_ep,case_length_per_ep,";
	

	public Map<Integer, DiagnosticMetricsRow> rows;
	
	public DiagnosticMetricsCsv(int caseLength) {
		this.rows = new TreeMap<Integer, DiagnosticMetricsRow>();
		for(int i = 1; i <= caseLength; i ++) {
			header += "step_" + i +"_probability,";
		}
		header += "costs_avg_100, rewards_avg_all";
		this.maxRowId = -1;
	}
	
	public void insertRow(int id, DiagnosticMetricsRow row) {
		if(row != null) {
			if(id <= maxRowId && id > -1) {
				id = maxRowId;
			}
			rows.put(id, row);
			maxRowId = id;
		}
	}
	
	public void setRevardsAvgAll(double x) {
		for(int i : rows.keySet()) {
			rows.get(i).revardsAvgAll=x;
		}
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
