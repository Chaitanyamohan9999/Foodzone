package com.food.foodzone.models;

public class TableDo extends BaseDo {

    public String tableId = "";
    public int tableNumber;
    public String tableType = "";
    public String reservedBy = "";
    public long reservedAt;
    public int reservedFor;
    public int tableCapacity;
    public String tableStatus = "";

    public TableDo() {}

    public TableDo(String tableId, int tableNumber, String tableType, int tableCapacity, String reservedBy, long reservedAt, int reservedFor, String tableStatus) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.tableType = tableType;
        this.tableCapacity = tableCapacity;
        this.reservedBy = reservedBy;
        this.reservedAt = reservedAt;
        this.reservedFor = reservedFor;
        this.tableStatus = tableStatus;
    }
}
