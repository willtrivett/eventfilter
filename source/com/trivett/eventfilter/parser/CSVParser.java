package com.trivett.eventfilter.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.trivett.eventfilter.mapping.FieldMapping;
import com.trivett.eventfilter.reports.Entry;

public class CSVParser implements IParser {

    // initialize with default order from example
    private static String[] headers = {"client-address","client-guid request-time","service-guid","retries-request","packets-requested","packets-serviced","max-hole-size"};

    
    @Override
    public Set<Entry> parseFile(String fileName) {
        Set<Entry> entries = new LinkedHashSet<>();
        try {
            Reader fileReader= new FileReader(fileName);
            CSVReader csvReader = new CSVReader(fileReader);
            headers = csvReader.peek(); // reset headers to whatever the input file has;
            HeaderColumnNameTranslateMappingStrategy<Entry> csvStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
            
            csvStrategy.setType(Entry.class);
            csvStrategy.setColumnMapping(FieldMapping.getMap());
            CsvToBean<Entry> csvToBean = new CsvToBean<>();
            csvToBean.setCsvReader(csvReader);
            csvToBean.setMappingStrategy(csvStrategy);
            entries.addAll(csvToBean.parse());
            
            csvReader.close();
        } catch (IOException e) {
            //this should have been hit during mime reading.
            e.printStackTrace();
        }
        return entries;
    }
    
    public static String[] getHeaders() {
        return headers;
        
    }

}
