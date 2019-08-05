package ke.co.travelmantics;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ke.co.travelmantics.models.TravelDeal;
import ke.co.travelmantics.util.FireBaseutil;

public class InsertActivity extends AppCompatActivity {


    EditText txtTitle,txtPrice,txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      FireBaseutil.openFbReference("traveldeals",this);

        txtTitle=(EditText) findViewById(R.id.txtTitle);
        txtPrice=(EditText) findViewById(R.id.txtPrice);
        txtDescription=(EditText) findViewById(R.id.txtDescription);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.save_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDeal() {

        String title=txtTitle.getText().toString();
        String price=txtPrice.getText().toString();
        String description=txtDescription.getText().toString();

        TravelDeal travelDeal=new TravelDeal(title,description,price,"","");
        FireBaseutil.databaseReference.push().setValue(travelDeal);
        cleartxt();
    }

    private void cleartxt(){
        txtTitle.setText("");
        txtPrice.setText("");
        txtTitle.setText("");

    }

}
