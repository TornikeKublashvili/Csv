package com.app;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationSettings {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationSettings.class);
	
	@Value ("${csv.separator}")
	public String csvSeparator;
	
	@Value ("${csv.search.directory}")
	public String csvSearchDirectory;
	
	@Value ("${csv.input.folder.name}")
	public String csvInputFolderName;
	
	@Value ("${csv.input.file.name}")
	public String csvInputFileName;
	
	@Value ("${csv.output.directory}")
	public String csvOutputDirectory;
	
	@Value ("${csv.output.file.name}")
	public String csvOutputFileName;
	
	@Value ("${csv.search.directory.models}")
	public String csvSearchDirectoryModels;

	@PostConstruct
	public void initialisation() {
		logger.info("---------- Start Initialisation ----------");
		logger.info("csv.separator= " + csvSeparator);
		logger.info("csv.search.directory= " + csvSearchDirectory);
		logger.info("csv.input.folder.name= " + csvInputFolderName);
		logger.info("csv.input.file.name= " + csvInputFileName);
		logger.info("csv.output.directory= " + csvOutputDirectory);
		logger.info("csv.output.file.name= " + csvOutputFileName);
		logger.info("csv.search.directory.models= " + csvSearchDirectoryModels);

		logger.info("---------- End Initialisation ----------");
	}
}
