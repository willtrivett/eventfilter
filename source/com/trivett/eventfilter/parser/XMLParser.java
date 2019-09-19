package com.trivett.eventfilter.parser;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.trivett.eventfilter.reports.Entries;
import com.trivett.eventfilter.reports.Entry;

public class XMLParser implements IParser {

    @Override
    public Set<Entry> parseFile(String fileName) {
        Set<Entry> entries = new LinkedHashSet<>();
        try {
            JAXBContext context = JAXBContext.newInstance(Entries.class);
            Unmarshaller unMarshaller = context.createUnmarshaller();
            Entries xmlEntries = (Entries) unMarshaller.unmarshal(new File(fileName));
            
            entries.addAll(xmlEntries.getEntries());
            return entries;
            
        } catch (JAXBException e) {
            System.out.println("Failed to parse XML input file: " + fileName);
            e.printStackTrace();
        }
        return entries;
    }

}
