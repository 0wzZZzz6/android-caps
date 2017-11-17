package com.android.curlytops.suroytabukidnon.Municipality;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.curlytops.suroytabukidnon.MainActivity;
import com.android.curlytops.suroytabukidnon.Model.Municipality;
import com.android.curlytops.suroytabukidnon.Model.MunicipalityItem;
import com.android.curlytops.suroytabukidnon.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan_frncs
 */
public class MunicipalityFragment extends Fragment {

    private RecyclerView recyclerView;

    public MunicipalityFragment() {
    }

    public static MunicipalityFragment newInstance() {
        MunicipalityFragment municipalityFragment = new MunicipalityFragment();
        Bundle args = new Bundle();
        municipalityFragment.setArguments(args);
        return municipalityFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setPadding(0, 0, 0, 52);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        MunicipalityAdapter adapter = new MunicipalityAdapter(getActivity(), getMunicipalityJson());
        recyclerView.setAdapter(adapter);
    }

    public List<Municipality> getMunicipalityJson() {

        List<Municipality> municipalityList = new ArrayList<>();
        InputStream inputStream;
        BufferedInputStream bufferedInputStream;
        JSONArray jsonArray;
        StringBuffer buffer;

        try {
            inputStream = getContext().getAssets().open("municipality.json");
            bufferedInputStream = new BufferedInputStream(inputStream);
            buffer = new StringBuffer();
            while (bufferedInputStream.available() != 0) {
                char c = (char) bufferedInputStream.read();
                buffer.append(c);
            }
            bufferedInputStream.close();
            inputStream.close();

            jsonArray = new JSONArray(buffer.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                String _id = jsonArray.getJSONObject(i).getString("ID");
                String _municipality = jsonArray.getJSONObject(i).getString("MUNICIPALITY");
                String _imgUrl = jsonArray.getJSONObject(i).getString("IMG_URL");
                municipalityList.add(new Municipality(_id, _municipality, _imgUrl));
                Log.d("json", _id + "-" + _municipality);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return municipalityList;
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

                            // new
                            object.put("coverURL", municipalityItem.getCoverURL());
                            object.put("coverName", municipalityItem.getCoverName());
                            object.put("starred", municipalityItem.getStarred());
                            data.put(object);
                            municipalityObject.put(municipality, data);
                            FileOutputStream fos = getContext().openFileOutput("municipality.json", Context.MODE_PRIVATE);
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
