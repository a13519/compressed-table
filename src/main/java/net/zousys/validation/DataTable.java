package net.zousys.validation;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataTable {

    private String tableName;

    private List<DataRow> rows = new ArrayList<DataRow>();

}
