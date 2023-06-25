package com.ebaotech.datainjection.dev.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

public class ExcelUtil {

    public static boolean isBlankRow(Row row) {
        if (row == null) {
            return true;
        }
        if (isBlankCell(row.getCell(0))) {
            return true;
        }
        return false;
    }

    public static boolean isBlankCell(Cell cell) {
        if (cell == null) {
            return true;
        }
        if (cell.getCellType() == CellType.BLANK) {
            return true;
        }
        if (cell.getCellType() == CellType.ERROR) {
            throw new RuntimeException("Worksheet: " + cell.getSheet().getSheetName() + " cell: " + cellName(cell) + " contains invalid entry[" + cell + "]");
        }
        if (stripWhitespace(formatCellValue(cell)).isEmpty()) {
            return true;
        }
        return false;
    }

    public static String cellName(Cell cell) {
        return cellName(cell.getColumnIndex(), cell.getRowIndex());
    }

    public static String cellName(int col, int row) {
        CellReference cellRef = new CellReference(row, col, false, false);
        return cellRef.formatAsString();
    }

    public static String stripWhitespace(String str) {
        return str.replaceAll("\\s", "");
    }

    public static final ThreadLocal<DataFormatter> TL_DataFormatter = new ThreadLocal<DataFormatter>() {
        @Override
        protected DataFormatter initialValue() {
            return new DataFormatter();
        }
    };

    public static String formatCellValue(Cell cell) {

        String result;
        try {
            result = TL_DataFormatter.get().formatCellValue(cell, getEvaluator(cell.getSheet()));
        } catch (Exception e) {
            result = "#N/A";
        }

        return result;
    }

    public static FormulaEvaluator getEvaluator(Sheet sheet) {
        return null;
    }
}
