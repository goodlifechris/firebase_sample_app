package ke.co.travelmantics.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ke.co.travelmantics.ListActivity;
import ke.co.travelmantics.models.TravelDeal;

/**
 * Created by goodlife on 04,August,2019
 */
public class FireBaseutil {

    private static final int RC_SIGN_IN =123 ;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static FireBaseutil fireBaseutil;

    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;

    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;


    public static ArrayList<TravelDeal> travelDeals;
    private static Activity caller;


    public static Boolean isAdmin;

    public static Boolean isDataAvailable;

    private FireBaseutil(){}

    public static void openFbReference(String childRef,final Activity callerActivity){

        if (fireBaseutil==null){

            fireBaseutil=new FireBaseutil();
            firebaseDatabase=FirebaseDatabase.getInstance();
            caller= callerActivity;
            firebaseAuth=FirebaseAuth.getInstance();
            authStateListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if (firebaseAuth.getCurrentUser()==null){
                        FireBaseutil.signIn();
                        Toast.makeText(caller.getBaseContext(), "Welcome Back", Toast.LENGTH_SHORT).show();
                    }else {

                        String userId=firebaseAuth.getUid();

                        checkIfAdmin(userId);
                    }



                }
            };
            connectStorage();
        }
        travelDeals=new ArrayList<TravelDeal>();
        databaseReference=firebaseDatabase.getReference().child(childRef);

    }

    private static void checkIfAdmin(String uid) {

        FireBaseutil.isAdmin=false;

        DatabaseReference reference=firebaseDatabase.getReference().child("administrators").child(uid);
        ChildEventListener childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FireBaseutil.isAdmin=true;
                Toast.makeText(caller.getBaseContext(), "you are an Administrator", Toast.LENGTH_SHORT).show();

                if ( caller instanceof ListActivity){

                    ListActivity activity = (ListActivity) caller;
                    activity.invalidateMenu(true);

                }

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

        reference.addChildEventListener(childEventListener);

        if ( caller instanceof ListActivity){

            ListActivity activity = (ListActivity) caller;
            activity.invalidateMenu(false);

        }






    }


    public static void attachListener(){


        firebaseAuth.addAuthStateListener(authStateListener);
    }


    public static void dettachListener(){
        firebaseAuth.removeAuthStateListener(authStateListener);

    }

    public static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void connectStorage(){

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("deals_pictures");
    }
}
