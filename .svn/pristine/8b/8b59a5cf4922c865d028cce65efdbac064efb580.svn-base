package com.app;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.app.calculator.DiagnosticMetricsCalculator;
import com.app.csv.DiagnosticMetricsCsv;
import com.app.csv.DiagnosticMetricsRow;
import com.app.helper.CsvHelper;
import com.app.helper.FileHelper;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);
		
	@Autowired
	private ApplicationSettings  appSettings;
	
	@Autowired
	private CsvHelper csvHelper;
	
	@Autowired
	private FileHelper fileHelper;
	
	@Autowired
	private DiagnosticMetricsCalculator diagnosticMetricsCalculator;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String...strings) throws Exception {
		onStart();

	}
	
	public void onStart() {
		/**
		Step 1
		we take a raw data from all 100 experiments and generate new data with k Factor (0.1-0.5)
		**/
		//calculateCosts();
		//calculateCostsNew();
		
		/**
		Step 2
		we read all diagnostic_metrics.csv from all folders which are in input folder and contains "MasterStateMasterReward" in the name 
		and merge them as a new csv which contains average value from all csv files and save it in output directory
		**/
		//mergeAllCsv(); 
		
		/**
		Step 3
		we read all diagnostic_metrics.csv from all folders which are in input folder and contains "MasterStateMasterReward" in the name,
		calculate average values from all columns and save it as a new row. we save the new csv in the output directory.	
		 */
		calculateSumAndAvgFromAllRowsAndSaveAsTheNewRows();
		
		
	}
	
//	public void calculateCosts() {
//		
//		for(int j = 1; j < 6; j ++) {
//			List <File> allFolders = fileHelper.getAllSubdirectories(appSettings.csvSearchDirectory, appSettings.csvInputFolderName);
//			for (File folder : allFolders) {
//				String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
//				logger.info("found: " + csvPath);
//				List <String> lines = csvHelper.getLinesFromCsvData(csvHelper.readCsvDataFromPath(csvPath));
//				
//				DiagnosticMetricsCsv diagnosticMetricsCsv = new DiagnosticMetricsCsv(appSettings.caseProbabilitiesLength);
//				double costSum = 0;
//				for(int i = 0; i < lines.size(); i ++) {
//					if(i > 0) { //ignore header
//						DiagnosticMetricsRow row = new DiagnosticMetricsRow(i, lines.get(i), appSettings.caseProbabilitiesLength);
//						double costPerEpisode = diagnosticMetricsCalculator.calculateCostPerEp(row, j/10.0);
//						costSum += costPerEpisode;
//						row.costPerEp = costPerEpisode;
//						row.costAvg = costSum/i;
//						diagnosticMetricsCsv.insertRow(i,row); 
//					}
//				}
//				File f = new File(csvPath);
//				String outputDirPath = appSettings.csvOutputDirectory + "\\" + (j/10.0) + getDir(f.getParentFile().getAbsolutePath());	
//				csvHelper.saveCsv(outputDirPath + "\\" + appSettings.csvOutputFileName, diagnosticMetricsCsv.toString());
//			}
//		}
//	}
	
	public void calculateCostsNew() {
		
		for(int j = 1; j < 6; j ++) {
			List <File> allFolders = fileHelper.getAllSubdirectories(appSettings.csvSearchDirectory, appSettings.csvInputFolderName);
			for (File folder : allFolders) {
				String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
				logger.info("found: " + csvPath);
				List <String> lines = csvHelper.getLinesFromCsvData(csvHelper.readCsvDataFromPath(csvPath));
				
				DiagnosticMetricsCsv diagnosticMetricsCsv = new DiagnosticMetricsCsv(appSettings.caseProbabilitiesLength);
				double costSum = 0;
				double cost100 [] = new double [100];
				double revardsSum = 0;
				for(int i = 0; i < lines.size(); i ++) {
					if(i > 0) { //ignore header
						DiagnosticMetricsRow row = new DiagnosticMetricsRow(i, lines.get(i), appSettings.caseProbabilitiesLength);
						double costPerEpisode = diagnosticMetricsCalculator.calculateCostPerEp(row, j/10.0);
						costSum += costPerEpisode;
						cost100[i%100] = costPerEpisode;
						revardsSum += row.rewardPerEp;
						row.costPerEp = costPerEpisode;
						row.costAvg = costSum/i;
						row.costAvg100 = i > 100?getAvg(cost100):costSum/i;	
						diagnosticMetricsCsv.insertRow(i, row); 
					}
				}
				diagnosticMetricsCsv.setRevardsAvgAll(revardsSum/lines.size());
				File f = new File(csvPath);
				String outputDirPath = appSettings.csvOutputDirectory + "\\" + (j/10.0) + getDir(f.getParentFile().getAbsolutePath());	
				csvHelper.saveCsv(outputDirPath + "\\" + appSettings.csvOutputFileName, diagnosticMetricsCsv.toString());
			}
		}
	}
	
	public double getAvg(double [] arr) {
		double out = 0;
		for(double d : arr){
			out += d;
		}
		return out/arr.length;
	}
	
	public void calculateSumAndAvgFromAllRowsAndSaveAsTheNewRows() {
		for(int j = 1; j < 6; j ++) {
			List <File> allFolders = fileHelper.getAllSubdirectories(appSettings.csvSearchDirectory + "\\" + (j/10.0), appSettings.csvInputFolderName);
			for (File folder : allFolders) {
				String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
				logger.info("found: " + csvPath);
				List <String> lines = csvHelper.getLinesFromCsvData(csvHelper.readCsvDataFromPath(csvPath));
				
				DiagnosticMetricsCsv diagnosticMetricsCsv = new DiagnosticMetricsCsv(appSettings.caseProbabilitiesLength);
				for(int i = 0; i < lines.size(); i ++) {
					if(i > 0) { //ignore header
						diagnosticMetricsCsv.insertRow(i, new DiagnosticMetricsRow(i, lines.get(i), appSettings.caseProbabilitiesLength)); 
					}
				}
				List<DiagnosticMetricsRow> sumAndAvg = diagnosticMetricsCalculator.calculateSumAndAvgFromAllRows(diagnosticMetricsCsv);
				for(DiagnosticMetricsRow row : sumAndAvg) {
					diagnosticMetricsCsv.insertRow(row.rowId, row);
				}
				
				File f = new File(csvPath);
				String outputDirPath = appSettings.csvOutputDirectory + "\\" + (j/10.0) + getDir(f.getParentFile().getAbsolutePath());	
				csvHelper.saveCsv(outputDirPath + "\\" + appSettings.csvOutputFileName, diagnosticMetricsCsv.toString());
			}	
		}
	}
	
	public void mergeAllCsv() {
		for(int j = 1; j < 6; j ++) {
			DiagnosticMetricsCsv diagnosticMetricsSum = null;
			List <File> allFolders = fileHelper.getAllSubdirectories(appSettings.csvSearchDirectory + "\\" + (j/10.0), appSettings.csvInputFolderName);
			for (File folder : allFolders) {
				String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
				logger.info("found: " + csvPath);
				List <String> lines = csvHelper.getLinesFromCsvData(csvHelper.readCsvDataFromPath(csvPath));
				//read the Csv from active directory
				DiagnosticMetricsCsv diagnosticMetricsCsv = new DiagnosticMetricsCsv(appSettings.caseProbabilitiesLength);
				for(int i = 0; i < lines.size(); i ++) {
					if(i > 0) {
						diagnosticMetricsCsv.insertRow(i+1, new DiagnosticMetricsRow(i+1, lines.get(i), appSettings.caseProbabilitiesLength)); 
					}
				}
				if(diagnosticMetricsSum == null) {
					diagnosticMetricsSum = diagnosticMetricsCsv;
				}
				else {
					diagnosticMetricsSum = diagnosticMetricsCalculator.addTwoCsv(diagnosticMetricsSum, diagnosticMetricsCsv);
				}	
			}
			DiagnosticMetricsCsv diagnosticMetricsAvg = diagnosticMetricsCalculator.divideCsv(diagnosticMetricsSum, allFolders.size());

			csvHelper.saveCsv(appSettings.csvOutputDirectory +"\\" + (j/10.0) + "\\MasterStateMasterReward_Avg\\" + appSettings.csvOutputFileName, diagnosticMetricsAvg.toString());
		}
	}
	
	
	public String getDir(String path) {		
		return path.substring(path.lastIndexOf("\\"), path.length());
	}
}
