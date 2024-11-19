package com.example.storagesae;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "reservations")
public class Reservation implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "book_name") // Add bookName field
    private String bookName;
    @ColumnInfo(name = "borrow_date")
    private String borrowDate;

    @ColumnInfo(name = "start_date")
    private String startDate;

    @ColumnInfo(name = "return_date")
    private String returnDate;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
}
