package com.ebaotech.datainjection.testapi.testmodule.service;

import com.ebaotech.datainjection.testapi.testmodule.model.MDBAttendance;
import com.ebaotech.datainjection.dev.util.DateUtil;
import com.healthmarketscience.jackcess.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MDBMetadataService {

    @Value("${mdb.filePath}")
    private String filePath;

    @Value("${mdb.dbName}")
    private String dbName;

    @Value("${mdb.dbFullPath}")
    private String dbFullPath;

    @Value("${mdb.tableName}")
    private String tableName;

    public Set<String> getTableNames() {
        Set<String> tables = null;
        try (Database db = DatabaseBuilder.open(new File(dbFullPath))) {
            tables = db.getTableNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public List<String> getColumnNames(String tableName) {

        List<String> columns = null;
        try (Database db = DatabaseBuilder.open(new File(dbFullPath))) {
            Table table = db.getTable(tableName);
            List<Column> allColumns = (List<Column>) table.getColumns();
            columns = allColumns.stream().map(item -> item.getName()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return columns;
    }

    public String createTable() {

        try (Database db = DatabaseBuilder.open(new File(dbFullPath))) {
            Collection<ColumnBuilder> columnbuilders = new ArrayList<ColumnBuilder>();
            columnbuilders.add(new ColumnBuilder("id",DataType.LONG).setAutoNumber(true));
            columnbuilders.add(new ColumnBuilder("empId",DataType.TEXT).setLength(100));
            columnbuilders.add(new ColumnBuilder("date",DataType.TEXT).setLength(100));
            columnbuilders.add(new ColumnBuilder("attended",DataType.BOOLEAN));

            Table table = new TableBuilder(tableName)
                    .addColumns(columnbuilders)
                    .toTable(db);

            return "table " + tableName + " created successfully";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "table " + tableName + " can be create";
    }

    /*public MDBAttendance insertData(MDBAttendance dataModel) {
        try (Database db = DatabaseBuilder.open(new File(dbFullPath))) {
            Table table = db.getTable(tableName);
            table.addRow(Column.AUTO_NUMBER, dataModel.getEmpId(), dataModel.getDate(), dataModel.isAttended());
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return dataModel;
    }*/

    public String getAllRecord(String dbName, String tableName) {

        String extention = ".mdb";
        String dbFullPath = filePath + dbName + extention;
        try (Database db = DatabaseBuilder.open(new File(dbFullPath))) {
            List<String> columns = null;
            Table table = db.getTable(tableName);
            List<Column> allColumns = (List<Column>) table.getColumns();
            columns = allColumns.stream().map(item -> item.getName()).collect(Collectors.toList());

            List<List<String>> rowList = new ArrayList<>();

            for (Row row : table) {
                List<String> columnList = new ArrayList<>();
                for (String col: columns) {
                    String value = (row.get(col) != null && row.get(col).toString().length() > 0) ? row.get(col).toString() : null;
                    columnList.add(value);
                }
                rowList.add(columnList);
            }

            System.out.print("Row_No\t");
            columns.stream().forEach(item -> System.out.print(item + "\t"));
            System.out.println("");
            int i = 1;
            for (List<String> row: rowList) {
                System.out.print("row"+i++ +" => ");
                for (String col : row) {
                    System.out.print(col + "\t");
                }
                System.out.println("");
            }

            if (rowList.size() > 0) {
                return rowList.size() + " record found";
            } else {
                return "No record found";
            }
        } catch (IOException ioEsce) {
            ioEsce.printStackTrace();
        } catch (Exception esce) {
            esce.printStackTrace();
        }
        return null;
    }

    public String downloadAllRecord(String dbName, String tableName) {
        String downloadLocaion = "D:\\workspace\\attendancesystem\\downloads\\";
        String downloadFilePath = downloadLocaion + tableName + ".xlsx";
        String extention = ".mdb";
        String dbFullPath = filePath + "//" + dbName + extention;
        try (Database db = DatabaseBuilder.open(new File(dbFullPath))) {
            List<String> columns = null;
            Table table = db.getTable(tableName);
            List<Column> allColumns = (List<Column>) table.getColumns();
            columns = allColumns.stream().map(item -> item.getName()).collect(Collectors.toList());

            List<List<String>> rowList = new ArrayList<>();

            for (Row row : table) {
                List<String> columnList = new ArrayList<>();
                for (String col: columns) {
                    String value = (row.get(col) != null && row.get(col).toString().length() > 0) ? row.get(col).toString() : null;
                    columnList.add(value);
                }
                rowList.add(columnList);
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");
            // Create header row
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);

            for (int i = 0; i < columns.size(); i++) {
                headerRow.createCell(i).setCellValue(columns.get(i));
            }

            int rowNumber = 1;
            for (List<String> row: rowList) {
                // Create Data Row
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowNumber++);
                for (int col = 0; col < row.size(); col++) {
                    dataRow.createCell(col).setCellValue(row.get(col));
                }
            }

            // Write workbook to file
            FileOutputStream fileOut = new FileOutputStream(new File(downloadFilePath));
            workbook.write(fileOut);
            fileOut.close();
            System.out.println(tableName + ".xlsx written successfully on disk.");

            if (rowList.size() > 0) {
                return rowList.size() + " record found";
            } else {
                return "No record found";
            }
        } catch (IOException ioEsce) {
            ioEsce.printStackTrace();
        } catch (Exception esce) {
            esce.printStackTrace();
        }
        return null;
    }

    public String getIncrementalRecord(String dbName, String tableName) {
        String extention = ".mdb";
        String dbFullPath = filePath + "//" + dbName + extention;
        try (Database db = DatabaseBuilder.open(new File(dbFullPath))) {
            Table table = db.getTable(tableName);
            List<List<Object>> rowList = new ArrayList<>();

            //this.filterManualy(table, rowList);

            this.filterUsingCursor(table, rowList);


            // Create a custom RowFilter for filtering criteria
            /*RowFilter filter = new RowFilter() {
                @Override
                public boolean matches(Row row) {
                    int value = (Integer) row.get("ColumnName");
                    return value < 100; // Filter for values less than 100
                }
            };*/

            /*Cursor cursor = CursorBuilder.createCursor(table);

            Map<String, String> testData = new Hashtable<String, String>();
            for (Row row : cursor.newIterable().addMatchPattern("EmployeeId", 71)) {
                //testData.put(row.get("Key").toString(), row.get("Data") != null ? row.get("Data").toString() : null);
                System.out.println(row.get("AttendanceLogId") + " = " +  row.get("AttendanceDate") + " = " +  row.get("EmployeeId"));
            }
*/
            /*Iterator<Map.Entry<String, String>> it = testData.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                System.out.println(String.format(
                        "Key: %s, Data: %s",
                        entry.getKey(),
                        entry.getValue()));
            }*/

            //List<Column> allColumns = (List<Column>) table.getColumns();
            //columns = allColumns.stream().map(item -> item.getName()).collect(Collectors.toList());

            //Map<String, Object> criteria = new HashMap<String, Object>();
            //criteria.put("EmployeeId", "71");

            //Row rowH = CursorBuilder.findRow(table, criteria);



            //List<String> columnList = new ArrayList<>();
            /*for (String col: columns) {
                String value = (rowH.get(col) != null && rowH.get(col).toString().length() > 0) ? rowH.get(col).toString() : null;
                columnList.add(value);
            }
            rowList.add(columnList);*/

            /*for (Row row : table) {
                List<String> columnList = new ArrayList<>();
                for (String col: columns) {
                    String value = (row.get(col) != null && row.get(col).toString().length() > 0) ? row.get(col).toString() : null;
                    columnList.add(value);
                }
                rowList.add(columnList);
            }
*/




            /*for (List<Object> colL : rowList) {
                for (Object col: colL) {
                    System.out.print(col + "\t");
                }
                System.out.println();
            }*/
            /*System.out.print("Row_No\t");
            columns.stream().forEach(item -> System.out.print(item + "\t"));
            System.out.println("");
            int i = 1;
            for (List<String> row: rowList) {
                System.out.print("row"+i++ +" => ");
                for (String col : row) {
                    System.out.print(col + "\t");
                }
                System.out.println("");
            }*/

            /*if (rowList.size() > 0) {
                return rowList.size() + " record found";
            } else {
                return "No record found";
            }*/

            print(rowList);
        } catch (IOException ioEsce) {
            ioEsce.printStackTrace();
        } catch (Exception esce) {
            esce.printStackTrace();
        }
        return null;
    }

    private void filterUsingCursor(Table table, List<List<Object>> rowList){

        String filterCriteria = "AttendanceLogId";
        Column filterColumn = table.getColumn(filterCriteria);

        try {
            IndexCursor cursor = CursorBuilder.createCursor(table.getIndex(filterCriteria));
            String fiiterIndex = "37684";
            cursor.findClosestRowByEntry(fiiterIndex);

            if (cursor.isAfterLast()) {
                System.out.println(String.format("There are no rows with %s >= %s", filterCriteria, fiiterIndex));
            } else {
                while (fiiterIndex.equals(cursor.getCurrentRowValue(filterColumn))) {
                    if (!cursor.moveToNextRow()) break;
                }
                if (cursor.isAfterLast()) {
                    System.out.println(String.format("There are no rows with %s >= %s", filterCriteria, fiiterIndex));
                }
            }

            while (!cursor.isAfterLast()) {
                List<Object> columnList = new ArrayList<>();
                try {
                    //System.out.print(cursor.getCurrentRowValue(filterColumn));
                    //System.out.println("\t" + cursor.getCurrentRowValue(table.getColumn("AttendanceDate")));
                    columnList.add(cursor.getCurrentRowValue(table.getColumn("AttendanceLogId")).toString());
                    columnList.add(cursor.getCurrentRowValue(table.getColumn("AttendanceDate")).toString());
                    columnList.add(cursor.getCurrentRowValue(table.getColumn("EmployeeId")).toString());
                    columnList.add(cursor.getCurrentRowValue(table.getColumn("Present")).toString());
                    rowList.add(columnList);
                    cursor.moveToNextRow();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filterManually(Table table, List<List<Object>> rowList) {
        for (Row row : table) {
            List<Object> columnList = new ArrayList<>();
            if (row.get("EmployeeId").toString().equalsIgnoreCase("71")) {
                columnList.add(row.get("AttendanceLogId").toString());
                columnList.add(row.get("AttendanceDate").toString());
                columnList.add(DateUtil.getDate(row.get("AttendanceDate").toString()));
                columnList.add(row.get("EmployeeId").toString());
                columnList.add(row.get("Present").toString());
                rowList.add(columnList);
            }
        }
    }

    private void print(List<List<Object>> rowList) {
        for (List<Object> colL : rowList) {
            for (int col = 0; col < colL.size(); col++) {

                if (col == 4) {
                    System.out.print(Double.parseDouble(colL.get(col).toString()) + "\t");
                } else {
                    System.out.print(colL.get(col) + "\t");
                }
            }
            System.out.println();
        }
    }
}
