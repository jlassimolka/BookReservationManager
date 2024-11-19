package com.example.storagesae;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ReservationActivity extends AppCompatActivity {

    private AppDatabase database;
    private ReservationDao reservationDao;
    private CalendarView calendarView;

    private EditText editTextBorrowDate, editTextStartDate, editTextReturnDate, editTextBookName;

    private String selectedDate; // Store the selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        calendarView = findViewById(R.id.calendarView);

        database = AppDatabase.getInstance(this);
        reservationDao = database.reservationDao();
        editTextBookName = findViewById(R.id.editTextBookName);
        editTextBorrowDate = findViewById(R.id.editTextBorrowDate);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextReturnDate = findViewById(R.id.editTextReturnDate);

        // Set the calendar listener to update the dates
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Adjust month (starts from 0)
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

            // Set the selected date to the appropriate EditText field
            if (editTextBorrowDate.getText().toString().isEmpty()) {
                editTextBorrowDate.setText(selectedDate); // First select Borrow Date
            } else if (editTextStartDate.getText().toString().isEmpty()) {
                editTextStartDate.setText(selectedDate); // Then select Start Date
            } else if (editTextReturnDate.getText().toString().isEmpty()) {
                editTextReturnDate.setText(selectedDate); // Then select Return Date
            }

            Toast.makeText(ReservationActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        Button buttonCreate = findViewById(R.id.buttonCreate);
        Button buttonRead = findViewById(R.id.buttonRead);


        buttonCreate.setOnClickListener(v -> createReservation());
        buttonRead.setOnClickListener(v -> readReservation());

    }

    private void createReservation() {

        if (editTextBookName.getText().toString().isEmpty()) {
            editTextBookName.setError("Veuillez entrer le nom du livre");
            editTextBookName.requestFocus();
            return;
        }
        if (editTextBorrowDate.getText().toString().isEmpty()) {
            editTextBorrowDate.setError("Veuillez sélectionner une date d'emprunt");
            editTextBorrowDate.requestFocus();
            return;
        }
        if (editTextStartDate.getText().toString().isEmpty()) {
            editTextStartDate.setError("Veuillez sélectionner une date de début");
            editTextStartDate.requestFocus();
            return;
        }
        if (editTextReturnDate.getText().toString().isEmpty()) {
            editTextReturnDate.setError("Veuillez sélectionner une date de retour");
            editTextReturnDate.requestFocus();
            return;
        }

        new Thread(() -> {
            Reservation reservation = new Reservation();
            reservation.setBookName(editTextBookName.getText().toString());
            reservation.setBorrowDate(editTextBorrowDate.getText().toString());
            reservation.setStartDate(editTextStartDate.getText().toString());
            reservation.setReturnDate(editTextReturnDate.getText().toString());
            reservationDao.insert(reservation);

            runOnUiThread(() -> Toast.makeText(this, "Reservation created", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void readReservation() {
        Intent intent = new Intent(this, ReservationListActivity.class);
        startActivity(intent);
    }

    private void updateReservation() {
        new Thread(() -> {
            Reservation reservation = reservationDao.getReservationById(1); // Example ID
            if (reservation != null) {
                reservation.setBorrowDate(editTextBorrowDate.getText().toString());
                reservation.setStartDate(editTextStartDate.getText().toString());
                reservation.setReturnDate(editTextReturnDate.getText().toString());
                reservation.setBookName(editTextBookName.getText().toString());
                reservationDao.update(reservation);

                runOnUiThread(() -> Toast.makeText(this, "Reservation updated", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deleteReservation() {
        new Thread(() -> {
            Reservation reservation = reservationDao.getReservationById(1); // Example ID
            if (reservation != null) {
                reservationDao.delete(reservation);
                runOnUiThread(() -> Toast.makeText(this, "Reservation deleted", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
