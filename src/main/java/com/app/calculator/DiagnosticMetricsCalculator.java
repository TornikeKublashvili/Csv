package com.app.calculator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.app.Application;
import com.app.csv.DiagnosticMetricsCsv;
import com.app.csv.DiagnosticMetricsRow;

@Component
public class DiagnosticMetricsCalculator {
	
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	
	public DiagnosticMetricsCsv addTwoCsv(DiagnosticMetricsCsv first, DiagnosticMetricsCsv second, int caseLength) {	
		DiagnosticMetricsCsv out = new DiagnosticMetricsCsv(caseLength);
		for(int id : first.rows.keySet()) {
			DiagnosticMetricsRow rowFromFirstCsv = first.rows.get(id);
			DiagnosticMetricsRow rowFromSecondCsv = second.rows.get(id);
			if(rowFromFirstCsv != null && rowFromSecondCsv != null) {
				out.insertRow(id, addTwoRows(id, rowFromFirstCsv, rowFromSecondCsv));
			}
		}
		return out;	
	}
	
	public double calculateCostPerEp(DiagnosticMetricsRow row, double k) {
		double alpha = 1 - (row.positionAdaptationPerEp-1)/(Math.max(row.caseLengthPerEp-1, 1));
		if(row.truePerEp > 0) { 										//Adaptionsentscheidung war richtig 
			if(row.adaptionPerEp > 0){									//angepasst
				return  100*k*(alpha) + (1-alpha)*(100+k*100);
			}
			else {														//nicht angepasst
				return 0;
			}
		}
		else {															//Adaptionsentscheidung war falsch 
			if (row.adaptionPerEp > 0) {								//angepasst
				return 100*k*alpha*2 + (1-alpha)*100*k;
			}
			else {														//nicht angepasst
				return 100;
			}	
		}
	}
	
	public double calculateCostPerEp_old(DiagnosticMetricsRow row, double k) {
		double alpha = (row.positionAdaptationPerEp)/(row.caseLengthPerEp-1);
		if(row.truePerEp > 0) {
			if(row.adaptionPerEp > 0){
				return  100*(alpha) + 100*k;
			}
			else {
				return 0;
			}
		}
		else {
			if (row.adaptionPerEp > 0) {
				return 100*(1-alpha)*k + 100*k;
			}
			else {
				return 100;
			}	
		}
	}
	
	public DiagnosticMetricsRow addTwoRows(int id, DiagnosticMetricsRow first, DiagnosticMetricsRow second) {
		try {	
			double caseProbabilitiesAvg []= new double[first.caseProbabilities.length];
			double caseProbabilitiesFirst[] = first.caseProbabilities;
			double caseProbabilitiesSecond[] = second.caseProbabilities;
			for (int i = 0; i < caseProbabilitiesAvg.length; i ++) {
				caseProbabilitiesAvg[i] = (caseProbabilitiesFirst[i]+caseProbabilitiesSecond[i]);
			}
			return new DiagnosticMetricsRow(id, first.caseId, (first.earlinessAvg+second.earlinessAvg), (first.trueAvg100+second.trueAvg100), 
					(first.trueAvg1000+second.trueAvg1000), (first.adaptionRateAvg+second.adaptionRateAvg), (first.adaptionPerEp+second.adaptionPerEp), (first.costAvg+second.costAvg),
					(first.costPerEp+second.costPerEp), (first.rewardsAvg+second.rewardsAvg), (first.rewardPerEp+second.rewardPerEp), (first.truePerEp+second.truePerEp),
						(first.positionAdaptationPerEp+second.positionAdaptationPerEp),  (first.caseLengthPerEp+second.caseLengthPerEp), caseProbabilitiesAvg, (first.costAvg100+second.costAvg100), (first.revardsAvgAll+second.revardsAvgAll));
		}
		catch(Exception e) {
			logger.error(id + " " + e.toString());
		}
		return null;
	}
	
	public List<DiagnosticMetricsRow> calculateSumAndAvgFromAllRows(DiagnosticMetricsCsv diagnosticMetricsCsv) {	
		List<DiagnosticMetricsRow> out = new ArrayList<DiagnosticMetricsRow>();
		DiagnosticMetricsRow sum = null;
		int rowCount=0;
		for(int id : diagnosticMetricsCsv.rows.keySet()) {
			DiagnosticMetricsRow row = diagnosticMetricsCsv.rows.get(id);
			if(sum == null) {
				sum = row;
				rowCount++;
			}
			else {
				sum = addTwoRows(sum.rowId, sum, row);
				rowCount++;
			}
		}
		sum.caseId = Integer.MIN_VALUE;
		sum.rowId = Integer.MIN_VALUE;
		out.add(sum);
		
		double caseProbabilitiesAvg []= new double[sum.caseProbabilities.length];
		double caseProbabilitiesSum[] = sum.caseProbabilities;
		for (int i = 0; i < caseProbabilitiesAvg.length; i ++) {
			caseProbabilitiesAvg[i] = caseProbabilitiesSum[i]/rowCount;
		}
		out.add(new DiagnosticMetricsRow(Integer.MIN_VALUE+1,Integer.MIN_VALUE+1, sum.earlinessAvg/rowCount, sum.trueAvg100/rowCount, 
				sum.trueAvg1000/rowCount, sum.adaptionRateAvg/rowCount, sum.adaptionPerEp/rowCount, sum.costAvg/rowCount,
				sum.costPerEp/rowCount, sum.rewardsAvg/rowCount, sum.rewardPerEp/rowCount, sum.truePerEp/rowCount,
				sum.positionAdaptationPerEp/rowCount,  sum.caseLengthPerEp/rowCount, caseProbabilitiesAvg, sum.costAvg100/rowCount, sum.revardsAvgAll/rowCount));	
		return out;	
	}
	public List<DiagnosticMetricsRow> calculateSumAndAvgFromAllRowsBeforeAndAfterConvergence(DiagnosticMetricsCsv diagnosticMetricsCsv) {	
		List<DiagnosticMetricsRow> out = new ArrayList<DiagnosticMetricsRow>();
		DiagnosticMetricsRow sum = null;
		DiagnosticMetricsRow sumAfterConvergence = null;
		int rowCount=0;
		int rowCountAfterConvergence=0;
		boolean convergent = false;
		for(int id : diagnosticMetricsCsv.rows.keySet()) {
			DiagnosticMetricsRow row = diagnosticMetricsCsv.rows.get(id);
			if(sum == null) {
				sum = row;
				rowCount++;
			}
			else {
				sum = addTwoRows(sum.rowId, sum, row);
				rowCount++;
			}
			
			if(!convergent) {
				convergent = row.converget();
			}
			
			if(convergent){
				if(sumAfterConvergence == null) {
					sumAfterConvergence = row;
					rowCountAfterConvergence++;
				}
				else {
					sumAfterConvergence = addTwoRows(sumAfterConvergence.rowId, sumAfterConvergence, row);
					rowCountAfterConvergence++;
				}
			}
		}
		sum.caseId = Integer.MIN_VALUE;
		sum.rowId = Integer.MIN_VALUE;
		out.add(sum);
		
		double caseProbabilitiesAvg []= new double[sum.caseProbabilities.length];
		double caseProbabilitiesSum[] = sum.caseProbabilities;
		for (int i = 0; i < caseProbabilitiesAvg.length; i ++) {
			caseProbabilitiesAvg[i] = caseProbabilitiesSum[i]/rowCount;
		}
		out.add(new DiagnosticMetricsRow(Integer.MIN_VALUE+1,Integer.MIN_VALUE+1, sum.earlinessAvg/rowCount, sum.trueAvg100/rowCount, 
				sum.trueAvg1000/rowCount, sum.adaptionRateAvg/rowCount, sum.adaptionPerEp/rowCount, sum.costAvg/rowCount,
				sum.costPerEp/rowCount, sum.rewardsAvg/rowCount, sum.rewardPerEp/rowCount, sum.truePerEp/rowCount,
				sum.positionAdaptationPerEp/rowCount,  sum.caseLengthPerEp/rowCount, caseProbabilitiesAvg, sum.costAvg100/rowCount, sum.revardsAvgAll/rowCount));
		
		sumAfterConvergence.caseId = Integer.MIN_VALUE+2;
		sumAfterConvergence.rowId = Integer.MIN_VALUE+2;
		out.add(sumAfterConvergence);
		
		double caseProbabilitiesAvgAfterConvergence []= new double[sumAfterConvergence.caseProbabilities.length];
		double caseProbabilitiesSumAfterConvergence[] = sumAfterConvergence.caseProbabilities;
		for (int i = 0; i < caseProbabilitiesAvgAfterConvergence.length; i ++) {
			caseProbabilitiesAvgAfterConvergence[i] = caseProbabilitiesSumAfterConvergence[i]/rowCountAfterConvergence;
		}
		out.add(new DiagnosticMetricsRow(Integer.MIN_VALUE+3,Integer.MIN_VALUE+3, sumAfterConvergence.earlinessAvg/rowCountAfterConvergence, sumAfterConvergence.trueAvg100/rowCountAfterConvergence, 
				sumAfterConvergence.trueAvg1000/rowCountAfterConvergence, sumAfterConvergence.adaptionRateAvg/rowCount, sumAfterConvergence.adaptionPerEp/rowCountAfterConvergence, sumAfterConvergence.costAvg/rowCountAfterConvergence,
				sumAfterConvergence.costPerEp/rowCountAfterConvergence, sumAfterConvergence.rewardsAvg/rowCount, sumAfterConvergence.rewardPerEp/rowCountAfterConvergence, sumAfterConvergence.truePerEp/rowCountAfterConvergence,
				sumAfterConvergence.positionAdaptationPerEp/rowCountAfterConvergence,  sumAfterConvergence.caseLengthPerEp/rowCountAfterConvergence, caseProbabilitiesAvg, sumAfterConvergence.costAvg100/rowCountAfterConvergence, sumAfterConvergence.revardsAvgAll/rowCountAfterConvergence));
		
		return out;	
	}
	
	public DiagnosticMetricsCsv divideCsv(DiagnosticMetricsCsv csv, int x, int caseLength) {	
		DiagnosticMetricsCsv out = new DiagnosticMetricsCsv(caseLength);
		for(int id : csv.rows.keySet()) {
			DiagnosticMetricsRow row = csv.rows.get(id);
			out.insertRow(id, divideRow(id, row, x));
		}
		return out;	
	}	
	
	public DiagnosticMetricsRow divideRow(int id, DiagnosticMetricsRow row, int x) {
		try {
			double caseProbabilitiesDivided []= new double[row.caseProbabilities.length];
			double caseProbabilitiesOld[] = row.caseProbabilities;
			for (int i = 0; i < caseProbabilitiesDivided.length; i ++) {
				caseProbabilitiesDivided[i] = (caseProbabilitiesOld[i]/x);
			}
			return new DiagnosticMetricsRow(id, row.caseId, row.earlinessAvg/x, row.trueAvg100/x, 
					row.trueAvg1000/x, row.adaptionRateAvg/x, row.adaptionPerEp/x, row.costAvg/x,
					row.costPerEp/x, row.rewardsAvg/x, row.rewardPerEp/x, row.truePerEp/x,
					row.positionAdaptationPerEp/x,  row.caseLengthPerEp/x, caseProbabilitiesDivided, row.costAvg100/x, row.revardsAvgAll/x);
		}
		catch(Exception e) {
			logger.error(id + " " + e.toString());
		}
		return null;
	}
}
