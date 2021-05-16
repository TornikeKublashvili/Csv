package com.app.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FileHelper {
	
	public List<File> getAllSubdirectories(String path, String name) {
		List<File> subDirectories = new ArrayList<File>();

		if(path == null || name == null) {
			return subDirectories;
		}
		
		File[] directories = new File(path).listFiles(File::isDirectory);
		
		for (int i = 0; i < directories.length; i++) {
			if (directories[i].getName().contains(name)) {
				subDirectories.add(directories[i]);
			}
		}	
		return subDirectories;
	}

	public String getCsvFilePathFromDirectory(String directory, String fileName) {
		if (directory == null) {
			return null;
		}
		if (directory.endsWith("\\")) {
			return directory + fileName;
		}
		return directory + "\\" + fileName;
	}
}
