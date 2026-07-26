package net.zousys.validation;

import lombok.Data;

import java.util.Map;

@Data
public class ValidationContext {

    private Map<String, DataTable> tables;
}
