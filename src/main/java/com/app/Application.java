package com.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.app.calculator.DiagnosticMetricsCalculator;
import com.app.csv.DiagnosticMetricsCsv;
import com.app.csv.DiagnosticMetricsRow;
import com.app.csv.ModelsCsv;
import com.app.csv.ModelsRow;
import com.app.helper.CsvHelper;
import com.app.helper.FileHelper;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);
		
	public String [] ensembleLearningMethodes = new String [] {"RNN", "RF"};
	public String [] dataSets = new String [] {"BPIC2012", "BPIC2017", "traffic"};
	public String raw = "01 Raw";
	public String withK = "02 WithK";
	public String avgCsv = "03 AvgCsv";
	public String aftConv =  "04 AftConv";
	public String beforeConv =  "05 BeforeConv";
	public String avgRow = "06 AvgRow";
	public String modelsPath = "C:\\BA\\Inpit Models\\Traffic_RNN.csv";
	public int caseLengthBPIC2012 = 47;
	public int caseLengthBPIC2017 = 71;
	public int caseLengthTraffic = 4;
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
		
		/**
		Step 2
		we read all diagnostic_metrics.csv from all folders which are in input folder and contains "MasterStateMasterReward" in the name 
		and merge them as a new csv which contains average value from all csv files and save it in output directory
		**/
		//mergeAllCsv(); 
		
		/**
		 Strp 3
		 we delete all rows before the Reinforcement Learning agent has started convergence and save the generated csv in the nev folder
		**/
		//removeRowsBevoreConvergence();
		//removeRowsAfterConvergence();
		
		/**
		Step 4
		we read all diagnostic_metrics.csv from all folders which are in input folder and contains "MasterStateMasterReward" in the name,
		calculate average values from all columns and save it as a new row. we save the new csv in the output directory.	
		 */
		//calculateSumAndAvgFromAllRowsAndSaveAsTheNewRows();
		
		//printConvergenceLine();
		//printCosts();
		//printLearningProcess();
		
		test(modelsPath);
		

		
	}

	public void test(String csvPath) {

		ModelsCsv modelsCsv = csvHelper.getModelsCsv(csvPath);
		Map<Integer, ModelsRow> rows = modelsCsv.rows;
		Map<Double, ModelsCsv> sortdWithPosition = new HashMap<Double, ModelsCsv>();
		for(int i : rows.keySet()) {
			ModelsRow row = rows.get(i);
			if(sortdWithPosition.containsKey(row.position)) {
				sortdWithPosition.get(row.position).insertRow(row.rowId, row);
			}
			else {
				ModelsCsv modelsCsvSortedWithProcessLength = new ModelsCsv();
				modelsCsvSortedWithProcessLength.insertRow(row.rowId, row);
				sortdWithPosition.put(row.position, modelsCsvSortedWithProcessLength);
			}
		}
		for(Double d : sortdWithPosition.keySet()) {
			
			logger.info(d+ " getCorrelation " + sortdWithPosition.get(d).getCorrelation());
			logger.info(d+ " getMCC " + sortdWithPosition.get(d).getMCC());
		}
		
		logger.info(modelsCsv.getCorrelation()+"");
		logger.info(modelsCsv.getMCC()+"");
	}
	
	public void calculateCosts() {
		String searchDir = appSettings.csvSearchDirectory + "\\" + raw;
		for (String dataSet : dataSets) {
			String searchDirDataSet = searchDir + "\\" + dataSet;
			logger.info("searchDirDataSet=" + searchDirDataSet);
			int caseLength = getCaseLength(dataSet);
			for (String ensembleLearningMethode : ensembleLearningMethodes) {
				String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
				logger.info("searchDirEnsLearningMeht=" + searchDirEnsLearningMeht);
				for(int j = 1; j < 6; j ++) {
					List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht, appSettings.csvInputFolderName);
					for (File folder : allFolders) {
						String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
						logger.info("found: " + csvPath);
						List <String> lines = csvHelper.getLinesFromCsvData(csvHelper.readCsvDataFromPath(csvPath));
						
						DiagnosticMetricsCsv diagnosticMetricsCsv = new DiagnosticMetricsCsv(caseLength);
						double costSum = 0;
						double cost100 [] = new double [100];
						double revardsSum = 0;
						for(int i = 0; i < lines.size(); i ++) {
							if(i > 0) { //ignore header
								DiagnosticMetricsRow row = new DiagnosticMetricsRow(i, lines.get(i), caseLength);
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
						String outputDirPath = searchDirEnsLearningMeht.replace(raw, withK) + "\\" + (j/10.0) + getDir(f.getParentFile().getAbsolutePath());	
						csvHelper.saveCsv(outputDirPath + "\\" + appSettings.csvOutputFileName, diagnosticMetricsCsv.toString());
					}
				}
			}
		}
	}
	
	
	public void mergeAllCsv() {
		String searchDir = appSettings.csvSearchDirectory + "\\" + withK;
		for (String dataSet : dataSets) {
			String searchDirDataSet = searchDir + "\\" + dataSet;
			logger.info("searchDirDataSet=" + searchDirDataSet);
			int caseLength = getCaseLength(dataSet);
			for (String ensembleLearningMethode : ensembleLearningMethodes) {
				String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
				logger.info("searchDirEnsLearningMeht=" + searchDirEnsLearningMeht);
				for(int j = 1; j < 6; j ++) {
					DiagnosticMetricsCsv diagnosticMetricsSum = null;
					List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht  + (j/10.0), appSettings.csvInputFolderName);
					for (File folder : allFolders) {
						String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
						logger.info("found: " + csvPath);
						DiagnosticMetricsCsv diagnosticMetricsCsv = csvHelper.getDiagnosticMetricsCsv(csvPath, caseLength,0);
						if(diagnosticMetricsSum == null) {
							diagnosticMetricsSum = diagnosticMetricsCsv;
						}
						else {
							diagnosticMetricsSum = diagnosticMetricsCalculator.addTwoCsv(diagnosticMetricsSum, diagnosticMetricsCsv, caseLength);
						}	
					}
					DiagnosticMetricsCsv diagnosticMetricsAvg = diagnosticMetricsCalculator.divideCsv(diagnosticMetricsSum, allFolders.size(), caseLength);
					csvHelper.saveCsv(searchDirEnsLearningMeht.replace(withK, avgCsv) + (j/10.0) + "\\MasterStateMasterReward_Avg\\" + appSettings.csvOutputFileName, diagnosticMetricsAvg.toString());
				}
			}
		}
	}
	
	public void removeRowsBevoreConvergence() {
		String searchDir = appSettings.csvSearchDirectory + "\\" + avgCsv;
		for (String dataSet : dataSets) {
			String searchDirDataSet = searchDir + "\\" + dataSet;
			int caseLength = getCaseLength(dataSet);
			int convergenceLine = getMaxConvergenceLineFordataSet(dataSet);
			for (String ensembleLearningMethode : ensembleLearningMethodes) {
				String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
				for(int j = 1; j < 6; j ++) {
					List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht  + (j/10.0), appSettings.csvInputFolderName);
					for (File folder : allFolders) {
						String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
						DiagnosticMetricsCsv diagnosticMetricsCsv = csvHelper.getDiagnosticMetricsCsv(csvPath, caseLength, convergenceLine);
						csvHelper.saveCsv(searchDirEnsLearningMeht.replace(avgCsv, aftConv) + (j/10.0) + "\\MasterStateMasterReward_Avg\\" + appSettings.csvOutputFileName, diagnosticMetricsCsv.toString());
					}
				}
			}
		}
	}
	
	public void removeRowsAfterConvergence() {
		String searchDir = appSettings.csvSearchDirectory + "\\" + avgCsv;
		for (String dataSet : dataSets) {
			String searchDirDataSet = searchDir + "\\" + dataSet;
			int caseLength = getCaseLength(dataSet);
			int convergenceLine = getMaxConvergenceLineFordataSet(dataSet);
			for (String ensembleLearningMethode : ensembleLearningMethodes) {
				String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
				for(int j = 1; j < 6; j ++) {
					List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht  + (j/10.0), appSettings.csvInputFolderName);
					for (File folder : allFolders) {
						String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
						DiagnosticMetricsCsv diagnosticMetricsCsv = csvHelper.getDiagnosticMetricsCsvbeforeIndex(csvPath, caseLength, convergenceLine);
						csvHelper.saveCsv(searchDirEnsLearningMeht.replace(avgCsv, beforeConv) + (j/10.0) + "\\MasterStateMasterReward_Avg\\" + appSettings.csvOutputFileName, diagnosticMetricsCsv.toString());
					}
				}
			}
		}
	}

	public void calculateSumAndAvgFromAllRowsAndSaveAsTheNewRows() {
		String searchDir = appSettings.csvSearchDirectory + "\\" + aftConv;
		for (String dataSet : dataSets) {
			String searchDirDataSet = searchDir + "\\" + dataSet;
			int caseLength = getCaseLength(dataSet);
			for (String ensembleLearningMethode : ensembleLearningMethodes) {
				String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
				for(int j = 1; j < 6; j ++) {
					List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht + (j/10.0), appSettings.csvInputFolderName);
					for (File folder : allFolders) {
						String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
						logger.info("found: " + csvPath);
						DiagnosticMetricsCsv diagnosticMetricsCsv = csvHelper.getDiagnosticMetricsCsv(csvPath, caseLength,0);
						List<DiagnosticMetricsRow> sumAndAvg = diagnosticMetricsCalculator.calculateSumAndAvgFromAllRows(diagnosticMetricsCsv);
						for(DiagnosticMetricsRow row : sumAndAvg) {
							diagnosticMetricsCsv.insertRow(row.rowId, row);
						}
						
						File f = new File(csvPath);
						String outputDirPath = searchDirEnsLearningMeht.replace(aftConv,avgRow) + (j/10.0) + getDir(f.getParentFile().getAbsolutePath());	
						csvHelper.saveCsv(outputDirPath + "\\" + appSettings.csvOutputFileName, diagnosticMetricsCsv.toString());
					}	
				}
			}
		}

	}
	
	public void printCosts() {
		logger.info("----------- Start Printing Costs -----------");
		String searchDir = appSettings.csvSearchDirectory + "\\" + avgRow;
		for (String dataSet : dataSets) {
			String searchDirDataSet = searchDir + "\\" + dataSet;
			int caseLength = getCaseLength(dataSet);
			for (String ensembleLearningMethode : ensembleLearningMethodes) {
				String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
				String log = (dataSet.length()==7?dataSet+" ":dataSet) + " " + (ensembleLearningMethode.length()==2?ensembleLearningMethode+" ":ensembleLearningMethode) + " ";
				for(int j = 1; j < 6; j ++) {
					List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht + (j/10.0), appSettings.csvInputFolderName);
					for (File folder : allFolders) {
						String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
						DiagnosticMetricsCsv diagnosticMetricsCsv = csvHelper.getDiagnosticMetricsCsv(csvPath, caseLength,0);
						DiagnosticMetricsRow avg = diagnosticMetricsCsv.getRowWithAvgValues();
						String value = roundedDouble(avg.costPerEp, 1);
						log += "K=" + (j/10.0) + " " + (value.length()==3?value+";  ":value+", ");
					}	
				}
				logger.info(log);
			}
		}
	}
	
	public void printLearningProcess() {
		logger.info("----------- Start Printing Learning Process -----------");
		String searchDir = appSettings.csvSearchDirectory + "\\" + avgRow;
		for (String dataSet : dataSets) {
			String searchDirDataSet = searchDir + "\\" + dataSet;
			int caseLength = getCaseLength(dataSet);
			for (String ensembleLearningMethode : ensembleLearningMethodes) {
				String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
				StringBuilder log = new StringBuilder();
				log.append(dataSet.length()==7?dataSet+" ":dataSet).append(" ")
					.append(ensembleLearningMethode.length()==2?ensembleLearningMethode+" ":ensembleLearningMethode)
					.append(" ");
				List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht + "0.1", appSettings.csvInputFolderName);
				for (File folder : allFolders) {
					String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
					DiagnosticMetricsCsv diagnosticMetricsCsv = csvHelper.getDiagnosticMetricsCsv(csvPath, caseLength,0);
					DiagnosticMetricsRow avg = diagnosticMetricsCsv.getRowWithAvgValues();
					log.append(" true_avg_100=").append(roundedDouble(avg.trueAvg100, 4)).append(";")
					.append(" adaption_rate_avg=").append(roundedDouble(avg.adaptionRateAvg, 4)).append(";")
					.append(" rewards_avg=").append(roundedDouble(avg.rewardsAvg, 4)).append(";")
					.append(" earliness_avg=").append(roundedDouble(avg.earlinessAvg, 4)).append(";");
				}	
				
				logger.info(log.toString());
			}
		}
	}
	
	public void printConvergenceLine() {
		logger.info("----------- Start Printing Convergence Line -----------");
		for (String dataSet : dataSets) {
			for(String ensembleLearningMethode : ensembleLearningMethodes) {
				logger.info((dataSet.length()==7? dataSet + " ": dataSet) + " " + (ensembleLearningMethode.length()==2?ensembleLearningMethode + " " : ensembleLearningMethode) 
						+ " " + getConvergencLineFordataSet(dataSet, ensembleLearningMethode));
			}
		}
	}
	
	public String roundedDouble(double d, int decimal) {
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
	
	public String getDir(String path) {		
		return path.substring(path.lastIndexOf("\\"), path.length());
	}
	
	public int getCaseLength(String dataSet) {
		if (dataSet.contains("BPIC2012")) {
			return caseLengthBPIC2012;
		}
		if (dataSet.contains("BPIC2017")) {
			return caseLengthBPIC2017;
		}
		if (dataSet.contains("traffic")) {
			return caseLengthTraffic;
		}
		return 0;
	}
	
	public double getAvg(double [] arr) {
		double out = 0;
		for(double d : arr){
			out += d;
		}
		return out/arr.length;
	}

	public int getMaxConvergenceLineFordataSet(String dataSet) {
		String searchDir = appSettings.csvSearchDirectory + "\\" + avgCsv;
		String searchDirDataSet = searchDir + "\\" + dataSet;
		int caseLength = getCaseLength(dataSet);
		int convergenceRNN = 0;
		int convergenceRF = 0;
		for (String ensembleLearningMethode : ensembleLearningMethodes) {
			String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
			List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht  + "0.1", appSettings.csvInputFolderName);
			File folder = allFolders.get(0);
			String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
			DiagnosticMetricsCsv diagnosticMetricsCsv = csvHelper.getDiagnosticMetricsCsv(csvPath, caseLength, 0);	
			if(ensembleLearningMethode.contains("RNN")) {
				convergenceRNN = diagnosticMetricsCsv.getConvergenceStart();
			}
			else{
				convergenceRF = diagnosticMetricsCsv.getConvergenceStart();
			}
		}
		return Math.max(convergenceRNN, convergenceRF);
	}
	
	public int getConvergencLineFordataSet(String dataSet, String ensembleLearningMethode) {
		String searchDir = appSettings.csvSearchDirectory + "\\" + avgCsv;
		String searchDirDataSet = searchDir + "\\" + dataSet;
		int caseLength = getCaseLength(dataSet);
		String searchDirEnsLearningMeht = searchDirDataSet + "\\" + ensembleLearningMethode + "\\";
		List <File> allFolders = fileHelper.getAllSubdirectories(searchDirEnsLearningMeht  + "0.1", appSettings.csvInputFolderName);
		File folder = allFolders.get(0);
		String csvPath = fileHelper.getCsvFilePathFromDirectory(folder.getAbsolutePath(), appSettings.csvInputFileName);
		DiagnosticMetricsCsv diagnosticMetricsCsv = csvHelper.getDiagnosticMetricsCsv(csvPath, caseLength, 0);		
		return diagnosticMetricsCsv.getConvergenceStart();		
	}
}
