package com.example.storagesae;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.util.Calendar;
import java.util.concurrent.Executors;

public class ResmodifActivity extends AppCompatActivity {

    private EditText editTextModifyBookName, editTextModifyBorrowDate, editTextModifyStartDate, editTextModifyReturnDate;
    private Button buttonSaveChanges;
    private AppDatabase database;
    private int reservationId;  // Reservation ID to identify the reservation to modify

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resmodif);

        // Edge-to-edge configuration for modern UI appearance
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database and UI elements
        database = AppDatabase.getInstance(getApplicationContext());
        editTextModifyBookName = findViewById(R.id.editTextModifyBookName);
        editTextModifyBorrowDate = findViewById(R.id.editTextModifyBorrowDate);
        editTextModifyStartDate = findViewById(R.id.editTextModifyStartDate);
        editTextModifyReturnDate = findViewById(R.id.editTextModifyReturnDate);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        // Retrieve data from intent
        Intent intent = getIntent();
        reservationId = intent.getIntExtra("reservation_id", -1);
        String currentBookName = intent.getStringExtra("current_book_name");
        String currentBorrowDate = intent.getStringExtra("current_borrow_date");
        String currentStartDate = intent.getStringExtra("current_start_date");
        String currentReturnDate = intent.getStringExtra("current_return_date");

        if (reservationId != -1) {
            // Set current reservation details in the input fields
            editTextModifyBookName.setText(currentBookName);
            editTextModifyBorrowDate.setText(currentBorrowDate);
            editTextModifyStartDate.setText(currentStartDate);
            editTextModifyReturnDate.setText(currentReturnDate);
        }

        // Set up DatePicker dialogs for each date field
        setDatePickerDialog(editTextModifyBorrowDate);
        setDatePickerDialog(editTextModifyStartDate);
        setDatePickerDialog(editTextModifyReturnDate);

        // Update reservation details on Save button click
        buttonSaveChanges.setOnClickListener(v -> {
            String newBookName = editTextModifyBookName.getText().toString().trim();
            String newBorrowDate = editTextModifyBorrowDate.getText().toString().trim();
            String newStartDate = editTextModifyStartDate.getText().toString().trim();
            String newReturnDate = editTextModifyReturnDate.getText().toString().trim();

            if (!newBookName.isEmpty() && !newBorrowDate.isEmpty() && !newStartDate.isEmpty() && !newReturnDate.isEmpty()) {
                updateReservation(newBookName, newBorrowDate, newStartDate, newReturnDate);
            } else {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDatePickerDialog(EditText editText) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set DatePickerDialog on EditText click
        editText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format the date and set it to the EditText
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                editText.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void updateReservation(String newBookName, String newBorrowDate, String newStartDate, String newReturnDate) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Fetch the reservation by ID and update its details
            Reservation reservation = database.reservationDao().getReservationById(reservationId); // Assuming getReservationById exists
            if (reservation != null) {
                reservation.setBookName(newBookName);
                reservation.setBorrowDate(newBorrowDate);
                reservation.setStartDate(newStartDate);
                reservation.setReturnDate(newReturnDate);
                database.reservationDao().update(reservation);

                // Return to previous activity with a success message
                runOnUiThread(() -> {
                    Toast.makeText(this, "Reservation updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);  // Set result to indicate success
                    finish();
                });
            }
        });
    }
}