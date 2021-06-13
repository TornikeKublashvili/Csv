package com.app.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.app.csv.DiagnosticMetricsCsv;
import com.app.csv.DiagnosticMetricsRow;
import com.app.csv.ModelsCsv;
import com.app.csv.ModelsRow;

@Component
public class CsvHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);

	

	public String readCsvDataFromPath(String path) {
		try {
			return new String(Files.readAllBytes(Paths.get(path)));
		}
		catch(Exception e) {
			logger.error(e.toString());
			return "";
		}
	}
	
	public List<String> getLinesFromCsvData(String data) {
		if (data != null) {
			return new ArrayList<String>(Arrays.asList(data.split("\\r?\\n")));
		}
		return Collections.emptyList();
	}
	
	public void saveCsv(String path, String csvData) {
		try {
			File f = new File(path);
			String outputDirPath = f.getParentFile().getAbsolutePath();
			File outputDir = new File(outputDirPath);
			
			if(!outputDir.exists()) {
				outputDir.mkdirs();
			}
	
       		PrintWriter writer = new PrintWriter(new File(path));
       		writer.write(csvData);
       		writer.close();
       		logger.info("csv saved! -> " + path);
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
		} 
	}
	
	public DiagnosticMetricsCsv getDiagnosticMetricsCsv(String path, int caseLength, int startRowIndex) {
		List <String> lines = getLinesFromCsvData(readCsvDataFromPath(path));
		DiagnosticMetricsCsv diagnosticMetricsCsv = new DiagnosticMetricsCsv(caseLength);
		for(int i = 0; i < lines.size(); i ++) {
			if(i > startRowIndex) {
				diagnosticMetricsCsv.insertRow(i, new DiagnosticMetricsRow(i, lines.get(i), caseLength)); 
			}
		}
		return diagnosticMetricsCsv;
	}
	public DiagnosticMetricsCsv getDiagnosticMetricsCsvbeforeIndex(String path, int caseLength, int startRowIndex) {
		List <String> lines = getLinesFromCsvData(readCsvDataFromPath(path));
		DiagnosticMetricsCsv diagnosticMetricsCsv = new DiagnosticMetricsCsv(caseLength);
		for(int i = 1; i < lines.size(); i ++) {
			if(i < startRowIndex) {
				diagnosticMetricsCsv.insertRow(i, new DiagnosticMetricsRow(i, lines.get(i), caseLength)); 
			}
		}
		return diagnosticMetricsCsv;
	}
	
	public ModelsCsv getModelsCsv(String path) {
		List <String> lines = getLinesFromCsvData(readCsvDataFromPath(path));
		ModelsCsv modelsCsv = new ModelsCsv();
		for(int i = 1; i < lines.size(); i ++) {
			modelsCsv.insertRow(i, new ModelsRow(lines.get(i))); 
			
		}
		return modelsCsv;
	}
}
