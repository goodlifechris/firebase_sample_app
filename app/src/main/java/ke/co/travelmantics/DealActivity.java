package ke.co.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import ke.co.travelmantics.models.TravelDeal;
import ke.co.travelmantics.util.FireBaseutil;

public class DealActivity extends AppCompatActivity {


    private static final int PICTURE_REQUEST_CODE = 2;
    EditText txtTitle, txtPrice, txtDescription;
    ImageView imageView;
    FloatingActionButton btnImage;

    ProgressBar progressBar;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        FireBaseutil.openFbReference("traveldeals", this);

        Intent intent = getIntent();
        TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("Deal");

        if (travelDeal == null) {
            travelDeal = new TravelDeal();
        }
        this.deal = travelDeal;

        txtTitle.setText(deal.getTitle());
        txtPrice.setText(deal.getPrice());
        txtDescription.setText(deal.getDescription());
        showImage(deal.getImageUrl());

    }

    public void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnImage = (FloatingActionButton) findViewById(R.id.fab);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "insert picture"), PICTURE_REQUEST_CODE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);

        MenuItem menuSave = menu.findItem(R.id.save_menu);
        MenuItem menuDelete = menu.findItem(R.id.delete_menu);

        if (FireBaseutil.isAdmin) {
            menuSave.setVisible(true);
            menuDelete.setVisible(true);
            setEdittextEnabled(true);
        } else {
            menuSave.setVisible(false);
            menuDelete.setVisible(false);
            setEdittextEnabled(false);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.save_menu:
                saveDeal();
                break;


            case R.id.delete_menu:
                deleteDeal();
                break;
        }
        onBackPressed();


        return super.onOptionsItemSelected(item);
    }

    private void saveDeal() {

        deal.setTitle(txtTitle.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        deal.setDescription(txtDescription.getText().toString());

        if (deal.getId() == null) {
            FireBaseutil.databaseReference.push().setValue(deal);
            Toast.makeText(this, "New Deal Saved", Toast.LENGTH_SHORT).show();

        } else {
            FireBaseutil.databaseReference.child(deal.getId()).setValue(deal);
            Toast.makeText(this, "Deal Updated", Toast.LENGTH_SHORT).show();

        }

        cleartxt();
    }

    private void deleteDeal() {

        if (deal == null) {
            Toast.makeText(this, "Please save deal to delete", Toast.LENGTH_SHORT).show();

            return;
        }
        FireBaseutil.databaseReference.child(deal.getId()).removeValue();
        if (deal.getImageName() != null && !deal.getImageName().isEmpty()) {

//            StorageReference storageReference=FireBaseutil.firebaseStorage.getReferenceFromUrl(deal.getImageUrl());
            StorageReference storageReference = FireBaseutil.firebaseStorage.getReference().child(deal.getImageName());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Delete Succesffully", Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getApplicationContext(), "error is" + e, Toast.LENGTH_SHORT).show();

                }
            });
        }

    }


    private void cleartxt() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");

    }

    private void setEdittextEnabled(Boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Uploading", Toast.LENGTH_SHORT).show();

        if (requestCode == PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            StorageReference storageReference = FireBaseutil.storageReference.child(imageUri.getLastPathSegment());
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTaskList = taskSnapshot.getStorage().getDownloadUrl();
                            uriTaskList.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    progressBar.setVisibility(View.GONE);
                                    String url = uri.toString();
                                    String pictureName = uri.getPath();

                                    deal.setImageUrl(url);
                                    deal.setImageName(pictureName);


                                    Log.e("url", url);
                                    Log.e("name", pictureName);
                                    showImage(url);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            int currentprogress = (int) progress;
                            progressBar.setProgress(currentprogress);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


                }
            });

        }
    }


    public void showImage(String uri) {

        if (uri != null && uri.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;

            Picasso.get()
                    .load(uri)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
