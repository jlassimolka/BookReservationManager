package com.example.storagesae;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReservationAdapter reservationAdapter;
    private AppDatabase database;
    private Button buttonBack;
    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonPaginationPrev, buttonPaginationNext;
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 2;
    private ExecutorService executorService;

    public static final int REQUEST_CODE_MODIFY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);

        // Initialize UI components
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonBack = findViewById(R.id.buttonBack);
        buttonPaginationPrev = findViewById(R.id.buttonPaginationPrev);
        buttonPaginationNext = findViewById(R.id.buttonPaginationNext);

        // Initialize the database and executor
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewReservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up button actions
        buttonBack.setOnClickListener(v -> finish());
        buttonSearch.setOnClickListener(v -> searchReservations());
        buttonPaginationPrev.setOnClickListener(v -> previousPage());
        buttonPaginationNext.setOnClickListener(v -> nextPage());

        // Load reservations for the current page
        loadReservations(currentPage);
    }

    private void searchReservations() {
        String bookName = editTextSearch.getText().toString().trim();
        executorService.execute(() -> {
            List<Reservation> reservations = database.reservationDao().searchReservationsByBookName("%" + bookName + "%");
            runOnUiThread(() -> {
                if (reservations != null && !reservations.isEmpty()) {
                    reservationAdapter = new ReservationAdapter(this, reservations, database.reservationDao());
                    recyclerView.setAdapter(reservationAdapter);
                } else {
                    Toast.makeText(this, "No reservations found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadReservations(int page) {
        int offset = (page - 1) * ITEMS_PER_PAGE;
        executorService.execute(() -> {
            List<Reservation> reservations = database.reservationDao().getReservationsByPage(offset, ITEMS_PER_PAGE);
            runOnUiThread(() -> {
                if (reservations != null && !reservations.isEmpty()) {
                    reservationAdapter = new ReservationAdapter(this, reservations, database.reservationDao());
                    recyclerView.setAdapter(reservationAdapter);
                } else {
                    Toast.makeText(this, "No reservations found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadReservations(currentPage);
        }
    }

    private void nextPage() {
        currentPage++;
        loadReservations(currentPage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MODIFY && resultCode == RESULT_OK) {
            loadReservations(currentPage);  // Refresh the list after modification
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown executor service to avoid memory leaks
        executorService.shutdown();
    }
}
