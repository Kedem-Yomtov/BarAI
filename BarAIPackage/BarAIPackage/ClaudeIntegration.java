package BarAIPackage;

import okhttp3.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ClaudeIntegration {

	private static final String API_URL = "https://api.anthropic.com/v1/messages";
	public static String getAIInsights(List<FileData> files, String API_KEY) throws IOException {
	    String informationSummary = InformationProcessor.infoSummaryGenerator(files);
	    
	    String prompt = "You are a bar consultant AI. I am providing you sales, menu, and events data from our bar:\n" + 
	    informationSummary +
	    "First, give a brief overview of how the bar is performing overall, including strengths, weaknesses, and trends you notice in the data.\n" +  
	    "Then, provide **3 actionable insights in total**. They should focus on improving the bar’s performance, which could involve any of these areas: menu, catered sales, or special events — but you don’t have to provide exactly one insight per area.\n" + 
	    "Be specific, practical, and concise in your recommendations. Format your response clearly, with the overview first and numbered actionable insights.";

	    // Properly escape the prompt for JSON
	    String escapedPrompt = prompt
	        .replace("\\", "\\\\")  // Escape backslashes first
	        .replace("\"", "\\\"")  // Escape quotes
	        .replace("\n", "\\n")   // Escape newlines
	        .replace("\r", "\\r")   // Escape carriage returns
	        .replace("\t", "\\t");  // Escape tabs

	    String jsonPayload = "{"
	        + "\"model\": \"claude-opus-4-1-20250805\","
	        + "\"max_tokens\": 10000,"
	        + "\"messages\": [{\"role\": \"user\", \"content\": \""
	        + escapedPrompt + "\"}]"
	        + "}";

	    // Debug: Print the JSON payload to check it
	    //System.out.println("JSON Payload: " + jsonPayload);

	    OkHttpClient client = new OkHttpClient.Builder()
	            .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS)  // connection timeout
	            .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS)    // request body write timeout
	            .readTimeout(600, java.util.concurrent.TimeUnit.SECONDS)     // response read timeout
	            .build();
	    
	    RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
	    Request request = new Request.Builder()
	        .url("https://api.anthropic.com/v1/messages")
	        .post(body)
	        .addHeader("x-api-key", API_KEY)
	        .addHeader("Content-Type", "application/json")
	        .addHeader("anthropic-version", "2023-06-01")
	        .build();

	    try (Response response = client.newCall(request).execute()) {
	        if (response.isSuccessful() && response.body() != null) {
	            return extractStringFromResponse(response.body().string());
	        } else {
	            String errorBody = response.body() != null ? response.body().string() : "No error body";
	            return "AI request failed: " + response.code() + " - " + response.message() + " - " + errorBody;
	        }
	    }
	}
	
	
	public static String extractStringFromResponse(String response) {
	    String marker = "\"text\":\"";
	    int start = response.indexOf(marker);
	    if (start != -1) {
	        start += marker.length();
	        int end = response.indexOf("]", start);
	        if (end != -1) {
	            String insights = response.substring(start, end);
	            // Replace escaped newlines and quotes
	            insights = insights.replace("\\n", "\n")   // escaped newlines
                        .replace("\\r", "\r")   // escaped carriage returns
                        .replace("\\t", "\t")   // escaped tabs
                        .replace("\\\"", "\""); // escaped quotes
	            return insights;
	        }
	    }
	    return "Error: Could not extract AI text from response";
	}
	
	
	public static FileData inferFileInfo(FileData fileData, String API_KEY) throws IOException {
	    // Get the raw string representation of the file
	    String rawData = fileData.toStringRaw();

	    // Build a prompt asking the AI to analyze the raw rows
	    String prompt = "You are a data analyst AI. I am providing you raw spreadsheet data:\n"
	            + rawData
	            + "\nPlease provide:\n"
	            + "1. A concise description of what this file represents.\n"
	            + "2. A list of column names/categories corresponding to each value in the rows.\n"
	            + "Return your response in plain text, first the description, then the categories as a comma-separated list.";

	    // Escape the prompt for JSON
	    String escapedPrompt = prompt
	            .replace("\\", "\\\\")
	            .replace("\"", "\\\"")
	            .replace("\n", "\\n")
	            .replace("\r", "\\r")
	            .replace("\t", "\\t");

	    String jsonPayload = "{"
		        + "\"model\": \"claude-opus-4-1-20250805\","
		        + "\"max_tokens\": 10000,"
		        + "\"messages\": [{\"role\": \"user\", \"content\": \""
		        + escapedPrompt + "\"}]"
		        + "}";

	    OkHttpClient client = new OkHttpClient.Builder()
	            .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
	            .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
	            .readTimeout(600, java.util.concurrent.TimeUnit.SECONDS)
	            .build();

	    RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
	    Request request = new Request.Builder()
	            .url("https://api.anthropic.com/v1/messages")
	            .post(body)
	            .addHeader("x-api-key", API_KEY)
	            .addHeader("Content-Type", "application/json")
	            .addHeader("anthropic-version", "2023-06-01")
	            .build();

	    try (Response response = client.newCall(request).execute()) {
	        if (response.isSuccessful() && response.body() != null) {
	            String aiResponse = extractStringFromResponse(response.body().string());

	            // Parse AI response assuming description is first line, categories second line
	            String[] lines = aiResponse.split("\n", 2);
	            String description = lines.length > 0 ? lines[0].trim() : "No description returned";
	            List<String> categories = new ArrayList<>();
	            if (lines.length > 1) {
	                for (String cat : lines[1].split(",")) {
	                    categories.add(cat.trim());
	                }
	            }
	            System.out.println("Discovered Categories: " + categories);
	            System.out.println("Discovered Description: " + description + "\n");

	            fileData.setCategories(categories);
	            return new FileData(fileData.getFileName(), fileData.getRows(), categories, description);
	        } else {
	            String errorBody = response.body() != null ? response.body().string() : "No error body";
	            throw new IOException("AI request failed: " + response.code() + " - " + response.message() + " - " + errorBody);
	        }
	    }
}
}