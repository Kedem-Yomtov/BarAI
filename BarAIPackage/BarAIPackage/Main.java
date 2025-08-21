package BarAIPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Main {

    public static void main(String[] args) throws IOException {
    	
    	Scanner scanner = new Scanner(System.in);
        List<String> fileNames = new ArrayList<>();
        
        System.out.println("Welcome to the BarAI Sales Analyser, Please Enter your Clause API Key:");
        String API_KEY = scanner.nextLine().trim();
        // Step 1: collect all filenames first
        while (true) {
            System.out.print("Enter file name (without extension) or 'done' to finish: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("done")) {
                break;
            }

            fileNames.add(input + ".xlsx");
        }

        // Step 2: now process all the collected files
        List<FileData> files = new ArrayList<>();
        for (String fileName : fileNames) {
            try {
                FileData fileData = DataImporter.loadExcelFile(fileName);
                fileData = ClaudeIntegration.inferFileInfo(fileData, API_KEY);
                files.add(fileData);
                System.out.println(fileName + " loaded successfully.");
            } catch (Exception e) {
                System.out.println("Could not load file: " + fileName);
                e.printStackTrace();
            }
        }

        // Step 3: run ClaudeIntegration on all loaded files together
        if (files.isEmpty()) {
            System.out.println("No files loaded. Exiting...");
        } else {
            System.out.println("Loading AI insights...");
            String insights = ClaudeIntegration.getAIInsights(files, API_KEY);
            System.out.println(insights);
            writeInsightsToFile(insights);
        }

        
        scanner.nextLine();
        scanner.close();

    }


public static void writeInsightsToFile(String content) {
	    // Format the current date as YYYY-MM-DD
	    String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
	    
	    // Build the file name
	    String fileName = "BarAI-Insights - " + currentDate + ".txt";
	    
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        writer.write(content);
	        System.out.println("Insights saved to: " + fileName);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}