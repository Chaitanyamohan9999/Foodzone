package com.food.foodzone.models;

public class TableDo extends BaseDo {

    public String tableId = "";
    public String tableName = "";
    public String reservedBy = "";
    public String reservedAt = "";
    public int tableCapacity;

    public TableDo() {}

    public TableDo(String tableId, String tableName, int tableCapacity) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.tableCapacity = tableCapacity;
    }
}
