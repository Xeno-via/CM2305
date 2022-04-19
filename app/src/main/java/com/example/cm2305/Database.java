package com.example.cm2305;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
   Class used to communicate with Firebase Firestore database.
 */


public class Database {

    FragmentActivity activity;
    String TAG;

    public Database() {
        TAG = "DATABASE_CLASS";
    }




    /**
     PLEASE REMEMBER TO MAKE THE DOCUMENT KEY A HASHCODED VERSION OF THE PRIMARY KEYS

     FOR EXAMPLE:
     THE DOCUMENT KEYS FOR DOCUMENTS IN 'ACCOUNTS' SHOULD BE HASHCODED VERSIONS OF THE JOINT STRING:
     [email][password]
     */
    public static void addData(FirebaseFirestore instance, String collectionChoice, String key, Map<String, String> data) {

        instance.collection(collectionChoice) // Table Name
                .document(key) // Entry Name/Key
                .set(data); // Entry Data

    }

    public static HashMap<String, String> getData(FirebaseFirestore instance, String collectionChoice, String key) {
        DocumentReference docRef = instance.collection(collectionChoice).document(key);

        final Map<String, String>[] output = new HashMap[1];
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        output[0] = createGetOutput(document);
                    }
                    else {
                        // Document doesn't exist.
                        output[0] = null;
                    }
                }
            }
        });
        return new HashMap<>(output[0]);
    }

    private static Map<String, String> createGetOutput(DocumentSnapshot document){
        Map<String, Object> rawData = new HashMap<String, Object>(document.getData());
        Map<String, String> output = new HashMap<String, String>();

        for (String key : rawData.keySet()) {
            output.put(key, document.getString(key));
        }
        return output;
    }



}
