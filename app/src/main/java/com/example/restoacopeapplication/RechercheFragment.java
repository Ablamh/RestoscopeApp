package com.example.restoacopeapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RechercheFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RechercheFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RechercheFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RechercheFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RechercheFragment newInstance(String param1, String param2) {
        RechercheFragment fragment = new RechercheFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private FirebaseFirestore db;
    private RestaurantAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recherche, container, false);

        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.restaurantsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RestaurantAdapter();
        recyclerView.setAdapter(adapter);

        loadRestaurants();

        return view;
    }

    private void loadRestaurants() {
        db.collection("users")
                .whereEqualTo("userType", "restaurateur")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Restaurant> restaurants = new ArrayList<>();
                    int totalRestaurants = queryDocumentSnapshots.size();
                    int[] loadedRestaurants = {0};

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Restaurant restaurant = new Restaurant();
                        restaurant.setName(document.getString("restaurantName"));
                        restaurant.setAdresse(document.getString("address"));

                        String restaurateurId = document.getId();
                        Log.d("DEBUG", "RestaurateurID: " + restaurateurId);

                        db.collection("Etablissements")
                                .document(restaurateurId)
                                .get()
                                .addOnSuccessListener(establishmentDoc -> {
                                    List<String> photos = (List<String>) establishmentDoc.get("photos");
                                    Log.d("DEBUG", "Photos trouvées: " + (photos != null ? photos.toString() : "null"));

                                    if (photos != null && !photos.isEmpty()) {
                                        restaurant.setPhotos(photos.get(0));
                                        Log.d("DEBUG", "Photo URL set: " + photos.get(0));
                                    }

                                    restaurants.add(restaurant);
                                    loadedRestaurants[0]++;
                                    if (loadedRestaurants[0] == totalRestaurants) {
                                        adapter.setRestaurants(restaurants);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("DEBUG", "Erreur Etablissements: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> Log.e("DEBUG", "Erreur users: " + e.getMessage()));
    }
}
