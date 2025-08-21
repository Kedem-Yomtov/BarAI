package BarAIPackage;

import java.util.List;

//generic class that gets data row from an excel file, gets context from outside source
public class DataRow {

    private List<String> fields; // raw row data from Excel

    // Constructor
    public DataRow(List<String> fields) {
        this.fields = fields;
    }

    // Getter
    public List<String> getValues() {
        return fields;
    }

    // Convenience method to safely get a specific column
    public String getField(int index) {
        if (index >= 0 && index < fields.size()) {
            return fields.get(index);
        }
        return null; // or throw exception if you prefer strictness
    }

    @Override
    public String toString() {
        return "{" + fields + '}';
    }
}
