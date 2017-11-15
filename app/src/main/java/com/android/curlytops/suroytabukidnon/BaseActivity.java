package com.android.curlytops.suroytabukidnon;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by jan_frncs
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE = "url";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_MUNICIPALITY = "municipality";
    public static final String EXTRA_DATE = "date";

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        firebase();
    }

    private void firebase() {
        final JSONObject municipalityObject = new JSONObject();
        final String[] municipalities = getResources().getStringArray(R.array.municipalityId);

        for (final String municipality : municipalities) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference municipalityReference = database
                    .getReference("municipality")
                    .child(municipality);

            municipalityReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    JSONObject object;
                    JSONArray data = new JSONArray();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MunicipalityItem municipalityItem = snapshot.getValue(MunicipalityItem.class);
                        try {
                            object = new JSONObject();
                            object.put("id", snapshot.getKey());
                            object.put("title", municipalityItem.getTitle());
                            object.put("location", municipalityItem.getLocation());
                            object.put("contact", municipalityItem.getContact());
                            object.put("category", municipalityItem.getCategory());
                            object.put("imageURLS", municipalityItem.getImageURLS());
                            object.put("imageNames", municipalityItem.getImageNames());
                            data.put(object);
                            municipalityObject.put(municipality, data);
                            FileOutputStream fos = openFileOutput("municipality.json", MODE_PRIVATE);
                            fos.write(municipalityObject.toString().getBytes());
                            fos.flush();
                            fos.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.e("Error: ", e.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getMessage());
                }
            });
        }


    }

}

