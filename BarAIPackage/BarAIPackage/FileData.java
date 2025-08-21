package BarAIPackage;

import java.util.ArrayList;
import java.util.List;

public class FileData {

    private String fileName;
    private List<DataRow> rows;
    private List<String> categories; // optional column names
    private String description;

    public FileData(String fileName, List<DataRow> rows, String description) {
        this.fileName = fileName;
        this.rows = rows;
        this.description = description;
        this.categories = null; // can be set later
    }

    public FileData(String fileName, List<DataRow> rows, List<String> categories, String description) {
        this.fileName = fileName;
        this.rows = rows;
        this.categories = categories;
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public List<DataRow> getRows() {
        return rows;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("File: ").append(fileName).append("\n");
        sb.append("Description: ").append(description).append("\n");

        for (DataRow row : rows) {
            if (categories != null && categories.size() == row.getValues().size()) {
                for (int i = 0; i < row.getValues().size(); i++) {
                    sb.append(categories.get(i)).append(" = ").append(row.getValues().get(i));
                    if (i < row.getValues().size() - 1) sb.append(", ");
                }
                sb.append("\n");
            } else {
                sb.append(row.getValues()).append("\n");
            }
        }

        return sb.toString();
    }
    public String getCategoriesString()
    {
    	return categories.toString();
    }

    //for building string to send AI prompt
    public String toStringRaw() {
        StringBuilder sb = new StringBuilder();
        sb.append("File: ").append(fileName).append("\n");
        sb.append("Description: ").append(description).append("\n");

        for (DataRow row : rows) {
            sb.append(row.getValues()).append("\n");
        }

        return sb.toString();
    }
}
