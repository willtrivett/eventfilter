

# Data sorting and filtering

## Solution

For external dependencies I have included the following via Maven:

- Commons.collections: fast, freely available and commonly used tools for sorting the list for records.
- Gson: fast, simple, free and ubiquitous json parsing and exporting library. Used to read in json records
- OpenCSV: freely available csv for import/export. This was chosen because it handles edge cases in text that may contain commas or empty values. 

Internal Dependencies

- Jaxb Xml parsing: Simple and ubiquitous for small xml documents. If the files were not so small I would have used stax for memory/speed. With JDK8 the built-in jaxb is much improved over earlier versions. 


## Package and Build

In the root of the eventfilter project run 'mvn package' or setup an eclipse/intellij maven target for 'package'

The resulting package will build all required libraries into {Project.dir}/target/eventfilter-1.0.jar

## Running EventFilter

The exported java jar is compliant with jdk8u151 or greater. It can be run via: 
java -jar eventfilter-1.0.jar [../reports.csv [../reports.json[ ../reports.xml]]] 
Any finite number of input files can be handed into the program arguments. The full path or path with respect to the jar file are valid. 

The output as 'combined.csv' into the current working directory. 

## Requirements
Read the 3 input files reports.json, reports.csv, reports.xml and output a combined CSV file with the following characteristics:

- The same column order and formatting as reports.csv
- All report records with packets-serviced equal to zero should be excluded
- records should be sorted by request-time in ascending order

Additionally, the application should print a summary showing the number of records in the output file associated with each service-guid.

Please provide source, documentation on how to run the program and an explanation on why you chose the tools/libraries used.

## Submission

You may fork this repo, commit your work and let us know of your project's location, or you may email us your project files in a zip file.


