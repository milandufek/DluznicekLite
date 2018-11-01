package cz.milandufek.dluzniceklite.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    private static final String TAG = "CsvReader";

    private InputStream inputStream;

    public CsvReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List read() {
        List resultList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                resultList.add(row);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error while closing input stream: " + e);
        }

        return resultList;
    }
}
