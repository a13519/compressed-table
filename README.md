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

## To Use compressed-table
Use Jitpack to connect to GitHub repository:
put the following into settings.gradle
```
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
``` 
add dependency in build.gradle:
```gradle
implementation 'com.github.a13519:compressed-table:1.0.3'
```

## Concept 

### Headers
When the data is parsed, the first row (after ignored rows before it) is treated as headers. And the columns referenced by this header. 

### Compressed 
The Data carried by Table can be compressed to reduce the size of execution profile. Also it can be disabled by setting 
```java
CompressedTableFactory
        .build("csv")
        .compressed(false)
        .build();
```
This is useful in handling large data set.

### Table Type
* CSV: the source type of CSV file to parse
* Excel: the source type of Excel file to parse

### Key Sets
When a Table is parsed, the key set need to be set to Table thus table knows how to index the content and later those data can be queried. Each KeySet contains one or multiple columns which are represended by headers. 
To understand multiple Key Sets, you can imagine the name card, one person(Row in the case) could have multiple name cards, one is business card the other may be a social name card or family card, the name or identification on the card could be different but the purpose is to reveal to link it to the real human identity.
Here, in Table, a Row could have multiple Keys to represent itself.

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

## Comparison
The Compressed Comparator is to compare two Tables to find out the disparities.

### ComparatorListener
This is a call back interface usd by comparator to let outside world known what's happened during the process.

### CompressedComparatorFactory
This is the factory to initialize the Comparator you prefer. 
* before() and after() to specify two tables
* ignoredFields() to set the Fields/Columns you don't want to compare thus fields won't appear in result
* comparatorListener() to set the call back listener
* trim() is to let the process trim the data fields or not
* strictMissed() this is the switch in case the columns are missed in before or after, and it is to enable the comparison on this column. If it is true, then any rows in the tables will be mismatch if there are any missed columns in table.

## Examples

### Parse a table with single key set
In this case, the key is composition keys of two columns of "Customer Id" and "First Name":
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

### Parse a table with multiple key sets
In this case, there are two key sets used to identify a row by one set of {"Customer Id", "First Name"}, the other key set is {"Phone 1", "Phone 2", "Email"}
```java
CompressedTable beforetable = CompressedTableFactory
        .build("csv")
        .keyHeaderList(new KeyHeadersList()
                    .addHeaders(new String[]{"First Name", "Last Name"})
                    .addHeaders(new String[]{"Phone 1", "Phone 2", "Email"})
                )
                .ignoredLines(0)
                .delimeter(',')
                .parse(Paths.get("customers-1000b.csv")
                        .toAbsolutePath()
                        .toString());
```

### Compare two tables

```java
        CompressedComparatorFactory.builder()
                .before(beforetable)
                .after(aftertable)
                .comparatorListener(listener)
                .strictMissed(false)
                .ignoredFields(new HashSet(Arrays.asList(new String[]{})))
                .build().create()
                .compare();
```

### Executable examples 
There are couple executable examples in the Test:
```java
samples.ParseSingleKeySet.java
samples.ParseMultipleKeySets.java
samples.CompareTwoTables.java
samples.ParseAExcelFile.java
```
