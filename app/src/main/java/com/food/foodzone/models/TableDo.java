package com.food.foodzone.models;

public class TableDo extends BaseDo {

    public String tableId = "";
    public int tableNumber;
    public String tableType = "";
    public String reservedBy = "";
    public long reservedAt;
    public int tableCapacity;
    public String tableStatus = "";

    public TableDo() {}

    public TableDo(String tableId, int tableNumber, String tableType, int tableCapacity, String reservedBy, long reservedAt, String tableStatus) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.tableType = tableType;
        this.tableCapacity = tableCapacity;
        this.reservedBy = reservedBy;
        this.reservedAt = reservedAt;
        this.tableStatus = tableStatus;
    }
}
