package com.example.storagesae;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert
    void insert(Reservation reservation);

    @Update
    void update(Reservation reservation);



    @Delete
    void delete(Reservation reservation);

    @Query("SELECT * FROM reservations WHERE id = :id")
    Reservation getReservationById(int id);

    @Query("SELECT * FROM reservations")
    List<Reservation> getAllReservations();

    @Query("SELECT * FROM reservations LIMIT :limit OFFSET :offset")
    List<Reservation> getReservationsByPage(int offset, int limit);

    // New method to search reservations by book name
    @Query("SELECT * FROM reservations WHERE book_name LIKE :bookName")
    List<Reservation> searchReservationsByBookName(String bookName);
}
