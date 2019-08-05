package ke.co.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import ke.co.travelmantics.models.TravelDeal;
import ke.co.travelmantics.util.DealAdapter;
import ke.co.travelmantics.util.FireBaseutil;

import static android.view.View.GONE;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout linearLayout;
TextView textViewHeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.reyclerview);
        linearLayout=(LinearLayout) findViewById(R.id.linearlayout);
        textViewHeadline=(TextView) findViewById(R.id.textViewHeadline);

        FireBaseutil.openFbReference("traveldeals", this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_activity_menu, menu);

        MenuItem menuInsert=menu.findItem(R.id.insert_menu);
        if (FireBaseutil.isAdmin){
            menuInsert.setVisible(true);
        }else{
            menuInsert.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.insert_menu:
                startActivity(new Intent(this, DealActivity.class));
                break;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "User has logged Out", Toast.LENGTH_SHORT).show();
                                FireBaseutil.attachListener();
                            }
                        });
                FireBaseutil.dettachListener();
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    public void invalidateMenu(Boolean isAdmin){
        invalidateOptionsMenu();

        if (isAdmin){
            textViewHeadline.setText("Create a new Travel Deal");

        }else {
            textViewHeadline.setText("Welcome to travelmantics! Deals dropping soon. ");
        }

    }
    @Override
    protected void onResume() {
        final DealAdapter dealAdapter = new DealAdapter(this);



        FireBaseutil.openFbReference("traveldeals", this);
        recyclerView.setAdapter(dealAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FireBaseutil.attachListener();


        super.onResume();
    }

    @Override
    protected void onPause() {
        FireBaseutil.dettachListener();
        super.onPause();
    }

    public void hidePlaceHolder() {

        linearLayout.setVisibility(GONE);
    }
    public void showPlaceholder(){
            linearLayout.setVisibility(View.VISIBLE);
    }
}
