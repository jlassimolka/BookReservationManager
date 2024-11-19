package com.example.storagesae;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Reservation.class}, version = 2, exportSchema = false) // Updated version to 2
public abstract class
AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract ReservationDao reservationDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "reservation_database")
                    .fallbackToDestructiveMigration() // This will reset the database
                    .build();
        }
        return instance;
    }
}
