package com.trivett.eventfilter.parser;

import java.util.Set;

import com.trivett.eventfilter.reports.Entry;

public interface IParser {
    public Set<Entry> parseFile(String fileName);
}
