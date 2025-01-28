package com.example.restoacopeapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.Map;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.widget.CompoundButton;
import android.content.Intent;
import android.app.Dialog;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RechercheFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RechercheFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private RestaurantAdapter adapter;
    private AccessibilityFilter filter;
    private ImageButton menuButton;

    public RechercheFragment() {}

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
        filter = new AccessibilityFilter();  // S'assure que filter est initialisé correctement
        Log.d("Fragment", "Filter initialized");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recherche, container, false);

        filter = new AccessibilityFilter();
        menuButton = view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> showFilterDialog());

        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.restaurantsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RestaurantAdapter();

        adapter.setOnRestaurantClickListener(restaurant -> {
            Intent intent = new Intent(getActivity(), InformationRestaurantActivity.class);
            intent.putExtra("restaurantId", restaurant.getRestaurateurId());
            intent.putExtra("restaurantName", restaurant.getName());
            intent.putExtra("restaurantAddress", restaurant.getAdresse());
            intent.putExtra("restaurantPhoto", restaurant.getPhotos());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        loadRestaurants();

        return view;
    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.filter_dialog);
        setupFilterCheckboxes(dialog);

        Button applyButton = dialog.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(v -> {
            Log.d("Filter", "Apply button clicked"); // Ajoutez ce log
            dialog.dismiss();
            loadFilteredRestaurants();
        });
        dialog.show();
    }

    private void setupFilterCheckboxes(Dialog dialog) {
        // Mobilité Physique
        // Mobilité Physique
        CheckBox rampeAccesCheckbox = dialog.findViewById(R.id.rampeAccesCheckbox);
        rampeAccesCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.MobilitePhysique.RAMPE, isChecked));

        CheckBox parkingPMRCheckbox = dialog.findViewById(R.id.parkingPMRCheckbox);
        parkingPMRCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.MobilitePhysique.PARKING, isChecked));

        CheckBox portesAutomatiquesCheckbox = dialog.findViewById(R.id.portesAutomatiquesCheckbox);
        portesAutomatiquesCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.MobilitePhysique.PORTES, isChecked));

        CheckBox chemainAccesStableCheckbox = dialog.findViewById(R.id.chemainAccesStableCheckbox);
        chemainAccesStableCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.MobilitePhysique.CHEMIN, isChecked));

        CheckBox toilettesCheckbox = dialog.findViewById(R.id.toilettesCheckbox);
        toilettesCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.MobilitePhysique.TOILETTES, isChecked));

        CheckBox ascenseurCheckbox = dialog.findViewById(R.id.ascenseurCheckbox);
        ascenseurCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.MobilitePhysique.ASCENSEUR, isChecked));

        CheckBox tablesHauteurAdapteeCheckbox = dialog.findViewById(R.id.tablesHauteurAdapteeCheckbox);
        tablesHauteurAdapteeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.MobilitePhysique.TABLES, isChecked));

        CheckBox circulationAiseeCheckbox = dialog.findViewById(R.id.circulationAiseeCheckbox);
        circulationAiseeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.MobilitePhysique.CIRCULATION, isChecked));

// Déficience Visuelle
        CheckBox menuBrailleCheckbox = dialog.findViewById(R.id.menuBrailleCheckbox);
        menuBrailleCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceVisuelle.MENU_BRAILLE, isChecked));

        CheckBox menuGrosCaracteresCheckbox = dialog.findViewById(R.id.menuGrosCaracteresCheckbox);
        menuGrosCaracteresCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceVisuelle.GROS_CARACTERES, isChecked));

        CheckBox descriptionAudioCheckbox = dialog.findViewById(R.id.descriptionAudioCheckbox);
        descriptionAudioCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceVisuelle.DESCRIPTION, isChecked));

        CheckBox eclairageAdapteCheckbox = dialog.findViewById(R.id.eclairageAdapteCheckbox);
        eclairageAdapteCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceVisuelle.ECLAIRAGE, isChecked));

        CheckBox chiensGuidesAcceptesCheckbox = dialog.findViewById(R.id.chiensGuidesAcceptesCheckbox);
        chiensGuidesAcceptesCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceVisuelle.CHIENS, isChecked));

        CheckBox formationPersonnelCheckbox = dialog.findViewById(R.id.formationPersonnelCheckbox);
        formationPersonnelCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceVisuelle.FORMATION, isChecked));

// Déficience Auditive
        CheckBox personnelFormeLSFCheckbox = dialog.findViewById(R.id.personnelFormeLSFCheckbox);
        personnelFormeLSFCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceAuditive.LSF, isChecked));

        CheckBox supportEcritCheckbox = dialog.findViewById(R.id.supportEcritCheckbox);
        supportEcritCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceAuditive.SUPPORT_ECRIT, isChecked));

        CheckBox supportVisuelCheckbox = dialog.findViewById(R.id.supportVisuelCheckbox);
        supportVisuelCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceAuditive.SUPPORT_VISUEL, isChecked));

        CheckBox boucleMagnetiqueCheckbox = dialog.findViewById(R.id.boucleMagnetiqueCheckbox);
        boucleMagnetiqueCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceAuditive.BOUCLE, isChecked));

        CheckBox alertesVisuellesCheckbox = dialog.findViewById(R.id.alertesVisuellesCheckbox);
        alertesVisuellesCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceAuditive.ALERTES, isChecked));

        CheckBox alarmesLumineusesCheckbox = dialog.findViewById(R.id.alarmesLumineusesCheckbox);
        alarmesLumineusesCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                filter.toggleFilter(AccessibilityFilter.DeficienceAuditive.ALARMES, isChecked));
    }

    private void loadFilteredRestaurants() {
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
                        restaurant.setRestaurateurId(document.getId());

                        String restaurateurId = document.getId();
                        db.collection("Etablissements")
                                .document(restaurateurId)
                                .get()
                                .addOnSuccessListener(establishmentDoc -> {
                                    if (matchesFilters(establishmentDoc)) {
                                        List<String> photos = (List<String>) establishmentDoc.get("photos");
                                        if (photos != null && !photos.isEmpty()) {
                                            restaurant.setPhotos(photos.get(0));
                                        }
                                        restaurants.add(restaurant);
                                    }
                                    loadedRestaurants[0]++;
                                    if (loadedRestaurants[0] == totalRestaurants) {
                                        adapter.setRestaurants(restaurants);
                                    }
                                });
                    }
                });
    }

    private boolean matchesFilters(DocumentSnapshot doc) {
        Set<String> activeFilters = filter.getActiveFilters();
        Log.d("FilterDebug", "=== Début de vérification pour le restaurant ===");
        Log.d("FilterDebug", "Nombre de filtres actifs : " + activeFilters.size());

        if (activeFilters.isEmpty()) {
            Log.d("FilterDebug", "Aucun filtre actif, retourne true");
            return true;
        }

        // Récupérer les trois catégories
        Map<String, Object> mobilitePhysique = (Map<String, Object>) doc.get("mobilitePhysique");
        Map<String, Object> deficienceVisuelle = (Map<String, Object>) doc.get("deficienceVisuelle");
        Map<String, Object> deficienceAuditive = (Map<String, Object>) doc.get("deficienceAuditive");

        // Log des données reçues
        Log.d("FilterDebug", "Données mobilitePhysique: " + (mobilitePhysique != null ? mobilitePhysique.toString() : "null"));
        Log.d("FilterDebug", "Données deficienceVisuelle: " + (deficienceVisuelle != null ? deficienceVisuelle.toString() : "null"));
        Log.d("FilterDebug", "Données deficienceAuditive: " + (deficienceAuditive != null ? deficienceAuditive.toString() : "null"));

        for (String key : activeFilters) {
            Log.d("FilterDebug", "Vérification du filtre: " + key);

            boolean found = false;
            String category = "";

            if (mobilitePhysique != null && mobilitePhysique.containsKey(key)) {
                found = Boolean.TRUE.equals(mobilitePhysique.get(key));
                category = "mobilitePhysique";
            }
            else if (deficienceVisuelle != null && deficienceVisuelle.containsKey(key)) {
                found = Boolean.TRUE.equals(deficienceVisuelle.get(key));
                category = "deficienceVisuelle";
            }
            else if (deficienceAuditive != null && deficienceAuditive.containsKey(key)) {
                found = Boolean.TRUE.equals(deficienceAuditive.get(key));
                category = "deficienceAuditive";
            }

            Log.d("FilterDebug", "Filtre " + key + " trouvé dans " + category + " avec valeur: " + found);

            if (!found) {
                Log.d("FilterDebug", "Le filtre " + key + " ne correspond pas, retourne false");
                return false;
            }
        }

        Log.d("FilterDebug", "Tous les filtres correspondent, retourne true");
        return true;
    }

    private void loadRestaurants() {
        db.collection("users")
                .whereEqualTo("userType", "restaurateur")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Restaurant> restaurants = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Restaurant restaurant = new Restaurant();
                        restaurant.setName(document.getString("restaurantName"));
                        restaurant.setAdresse(document.getString("address"));
                        restaurant.setRestaurateurId(document.getId());
                        restaurants.add(restaurant);
                    }
                    adapter.setRestaurants(restaurants);
                });
    }
}