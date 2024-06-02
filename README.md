# Compressed Table

This is a solution to handle tables from extraction ofcsv or Excel file. It provides a compression feature, also you can choose it un-compressed. The source data could come from a [CSV](https://en.wikipedia.org/wiki/Comma-separated_values#:~:text=Comma%2Dseparated%20values%20(CSV),typically%20represents%20one%20data%20record) file or [Excel](https://en.wikipedia.org/wiki/Microsoft_Excel) spread sheet.

Each CompressedTable represents a data set extracted from either a CSV or Excel file.

A handy tool named **Bold/CompressedComparator** can compare two tables to figure out 
* which records are missed in which table
* which columns/headers are missed in which table
* which records are mismatched
* which records are matched
* which mismatched record has how many fields mismatched
* which column/header has how many records mismatched

## Concept 

### Table Type
* CSV: the source type of CSV file to parse
* Excel: the source type of Excel file to parse

### Keys
When a Table is parsed, the key set need to be set to Table thus table knows how to index the content and later those data can be queried. Each KeySet contains one or multiple columns which are represended by headers. 

One **Bold/KeySet** contains:
* One or Multiple headers or Columns

You could set one KeySet or mulitple KeySet:
* SINGLE_KEY (one KeySet)
  In this mode, the key set is called **Bold/Main Key**
* MULTI_KEYS (multiple KeySets)
  In this mode, A key set is automatically generated called **Bold/Native Key**, it's unique. Also one or a serial KeySets are attached in you favior.
The **Bold/KeyHeadersList** represents the KeySets.

### Delimeter
This is for CSV parsing, a delimeter can be specified if it is other than comma



## Examples

### Parse a table
```java
CompressedTable beforetable = CompressedTableFactory
        .build("csv")
        .keyHeaderList(
                new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
        )
        .compressed(false)  // the content is not compressed
        .ignoredLines(0)    // ignore the first number of lines
        .delimeter(',')
        .parse(Paths.get("customers-1000b.csv")
                .toAbsolutePath()
                .toString());
```
