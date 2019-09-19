package com.trivett.eventfilter.parser;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.trivett.eventfilter.reports.Entry;

public class JSONParser implements IParser {

    @Override
    public Set<Entry> parseFile(String fileName) {
        Set<Entry> entries = new LinkedHashSet<>();
        
        try {
            Type listType = new TypeToken<List<Entry>>(){}.getType();
            Gson gson = new GsonBuilder().create();
            JsonReader reader = new JsonReader(new FileReader(fileName));
            List<Entry> jsonEntries = gson.fromJson(reader,listType);
            entries.addAll(jsonEntries);
            
        } catch (IOException ioe) {
            System.out.println("Failed to parse JSON input file: " +fileName);
            ioe.printStackTrace();
        }
        return entries;
    }

}
