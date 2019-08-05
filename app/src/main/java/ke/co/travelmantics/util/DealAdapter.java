package ke.co.travelmantics.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ke.co.travelmantics.DealActivity;
import ke.co.travelmantics.InsertActivity;
import ke.co.travelmantics.ListActivity;
import ke.co.travelmantics.R;
import ke.co.travelmantics.models.TravelDeal;

/**
 * Created by goodlife on 04,August,2019
 */
public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private ArrayList<TravelDeal> travelDeals;

    private ListActivity activity;

    public DealAdapter(ListActivity activity) {

        this.activity = activity;

        FireBaseutil.openFbReference("traveldeals", activity);
        firebaseDatabase = FireBaseutil.firebaseDatabase;
        databaseReference = FireBaseutil.databaseReference;
        travelDeals = FireBaseutil.travelDeals;

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);

                travelDeal.setId(dataSnapshot.getKey());
                travelDeals.add(travelDeal);
                notifyItemInserted(travelDeals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addChildEventListener(childEventListener);

    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();

        View viewItem = LayoutInflater.from(context).inflate(R.layout.rv_row, viewGroup, false);

        return new DealViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder dealViewHolder, int i) {

        TravelDeal travelDeal = travelDeals.get(i);
        dealViewHolder.bind(travelDeal);

    }

    @Override
    public int getItemCount() {

        if (travelDeals.size()>0)
            activity.hidePlaceHolder();
        else
            activity.showPlaceholder();

        return travelDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtDealTitle, txtDescription, txtPrice;
        ImageView imageView;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDealTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal travelDeal) {
            txtDealTitle.setText(travelDeal.getTitle());
            txtDescription.setText(travelDeal.getDescription());
            txtPrice.setText("Ksh. "+travelDeal.getPrice());
            showImage(travelDeal.getImageUrl());
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            TravelDeal travelDealSelected = travelDeals.get(position);
            Intent intent = new Intent(v.getContext(), DealActivity.class);
            intent.putExtra("Deal", travelDealSelected);
            itemView.getContext().startActivity(intent);
        }

        public void showImage(String uri) {

            if (uri != null && !uri.isEmpty()) {
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;

                Picasso.get()
                        .load(uri)
                        .resize(80, 80)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
}
