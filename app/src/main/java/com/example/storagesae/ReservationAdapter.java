package com.example.storagesae;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.storagesae.ReservationListActivity.REQUEST_CODE_MODIFY;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private List<Reservation> reservationList;
    private ReservationDao reservationDao;
    private Context context;

    public ReservationAdapter(Context context,List<Reservation> reservationList, ReservationDao reservationDao) {
        this.context=context;
        this.reservationList = reservationList;
        this.reservationDao = reservationDao;
    }



    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.bookNameTextView.setText(reservation.getBookName());
        holder.borrowDateTextView.setText(reservation.getBorrowDate());
        holder.startDateTextView.setText(reservation.getStartDate());
        holder.returnDateTextView.setText(reservation.getReturnDate());

        holder.buttonModify.setOnClickListener(v -> {
            Intent intent = new Intent(context, ResmodifActivity.class);
            intent.putExtra("reservation_id",reservation.getId());  // Pass review ID
            intent.putExtra("current_book_name",reservation.getBookName());
            intent.putExtra("current_borrow_date",reservation.getBorrowDate());// Pass current comment
            intent.putExtra("current_start_date",reservation.getStartDate());
            intent.putExtra("current_return_date",reservation.getReturnDate());
            ((AppCompatActivity) context).startActivityForResult(intent, REQUEST_CODE_MODIFY);
        });

        holder.buttonDelete.setOnClickListener(v -> {
            new Thread(() -> {
                reservationDao.delete(reservation); // Delete from database
                reservationList.remove(position); // Remove from the list
                notifyItemRemoved(position); // Notify adapter about item removal
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public void addReservations(List<Reservation> newReservations) {
        int positionStart = reservationList.size();
        reservationList.addAll(newReservations);
        notifyItemRangeInserted(positionStart, newReservations.size());
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView bookNameTextView, borrowDateTextView, startDateTextView, returnDateTextView;
        Button buttonDelete,buttonModify;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            bookNameTextView = itemView.findViewById(R.id.textViewBookName);
            borrowDateTextView = itemView.findViewById(R.id.textViewBorrowDate);
            startDateTextView = itemView.findViewById(R.id.textViewStartDate);
            returnDateTextView = itemView.findViewById(R.id.textViewReturnDate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonModify = itemView.findViewById(R.id.buttonModify);// Initialize delete button
        }
    }
}

