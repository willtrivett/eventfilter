package com.trivett.eventfilter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.trivett.eventfilter.mapping.FieldMapping;
import com.trivett.eventfilter.parser.CSVParser;
import com.trivett.eventfilter.parser.JSONParser;
import com.trivett.eventfilter.parser.XMLParser;
import com.trivett.eventfilter.reports.Entry;

public class EventFilter {

    
    public static final String OUTPUT_FILENAME = "combined.csv";
    public static void main(String[] args) {
        if (args.length <= 0) {
            System.err.println("Event Filter Usage: java EventFilter file1 [file2 [fileN...]]");
            System.out.println("Please include space separated list of files for program arguments.");
            System.exit(1);
        }
        new EventFilter(args);
    }
    
    private void parseFile(Map<Entry,Object> entries, String fileName) {
        Path path = new File (fileName).toPath();
        String mimeType = "CSV";
        try {
            mimeType = Files.probeContentType(path) ==  null ? "CSV" : Files.probeContentType(path);
        } catch (IOException e) {
            System.err.println("Failed to find and extract mime for: " + fileName);
            e.printStackTrace();
        }
        Object blank = new Object();
        switch (mimeType) {
        case "application/json":
            new JSONParser().parseFile(fileName).forEach(entry -> entries.put(entry, blank));            
            break;
        case "text/xml":
            new XMLParser().parseFile(fileName).forEach(entry -> entries.put(entry, blank));
            break;
        default: // CSV defaults to null
            new CSVParser().parseFile(fileName).forEach(entry -> entries.put(entry, blank));
            break;
        }
    }
    
    public EventFilter(String[] fileNames) {
        long startTime = System.currentTimeMillis();
        Map<Entry,Object> entries = new ConcurrentHashMap<Entry,Object>();
        List<Entry> entriesFiltered = new ArrayList<Entry>();
        //setup parallel processing via parallel stream (simpler than work stealing pool, just to benchmark)
        List<String> fileNamesList = new ArrayList<String>(fileNames.length);
        Arrays.stream(fileNames).forEach(x -> fileNamesList.add(x));

        fileNamesList.parallelStream().forEach(xName -> parseFile(entries, xName));
        // parse and generate dates in parallel for speedup.
        entries.keySet().parallelStream().forEach(x -> x.getRequestDate());
        entriesFiltered = entries.keySet().stream().filter(m -> m.getPacketsServiced() != 0).collect(Collectors.toList());
        Collections.sort(entriesFiltered, Entry.ENTRY_COMPARATOR);
        
        try {
            Writer writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILENAME));
            CSVWriter csvWriter = new CSVWriter(writer);
            String[] headersEntryOrder = new String[CSVParser.getHeaders().length];
            int i = 0;
            for (String header : CSVParser.getHeaders()) {
                headersEntryOrder[i] = FieldMapping.getMap().get(header);
                i++;
            }
            ColumnPositionMappingStrategy<Entry> outputStrategy = new ColumnPositionMappingStrategy<Entry>();
            outputStrategy.setType(Entry.class);
            outputStrategy.setColumnMapping(headersEntryOrder);
            //write headers
            csvWriter.writeNext(CSVParser.getHeaders());
            
            StatefulBeanToCsvBuilder<Entry> csvBuilder = new StatefulBeanToCsvBuilder<>(csvWriter);
            StatefulBeanToCsv<Entry> csvEntryWriter = csvBuilder.withMappingStrategy(outputStrategy).build();
            entriesFiltered.stream().forEach(entry -> {
                try {
                    csvEntryWriter.write(entry);
                } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                    System.err.println("Failed to write parsed entry to CSV: " + entry);
                    e.printStackTrace();
                }
            });
            csvWriter.close();
            Map<Object, List<Entry>> stats = entriesFiltered.stream().collect(Collectors.groupingBy(entry -> entry.getServiceGuid()));
            for (Object obj : stats.keySet()) {
                System.out.println(obj +" Service GUID had " + stats.get(obj).size() + " entries");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("Could not open output file for writing " + OUTPUT_FILENAME);
            e.printStackTrace();
        }
        System.out.println("Total number of ingress entries: " + (entries.size()));
        System.out.println("Total number of egress entries: " + (entriesFiltered.size()));

        System.out.println("Total processing time (ms) is: " + (System.currentTimeMillis()-startTime));
        
    }

}
