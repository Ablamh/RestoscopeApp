package com.example.restoacopeapplication;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class VisaliserRestaurantActivity extends AppCompatActivity {
    private LinearLayout mobilitePhysiqueContainer;
    private LinearLayout deficienceVisuelleContainer;
    private LinearLayout deficienceAuditiveContainer;
    private TextView restaurantName;
    private TextView restaurantAddress;
    private ViewPager2 imageViewPager;
    private TabLayout tabLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visaliser_restaurant);

        // Initialize views
        initializeViews();

        // Setup EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Load restaurant data
        loadRestaurantData();
    }

    private void initializeViews() {
        mobilitePhysiqueContainer = findViewById(R.id.mobilitePhysiqueContainer);
        deficienceVisuelleContainer = findViewById(R.id.deficienceVisuelleContainer);
        deficienceAuditiveContainer = findViewById(R.id.deficienceAuditiveContainer);
        restaurantName = findViewById(R.id.restaurantName);
        restaurantAddress = findViewById(R.id.restaurantAddress);
        imageViewPager = findViewById(R.id.imageViewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void loadRestaurantData() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (currentUserId != null) {
            // First, get restaurant info from users collection
            db.collection("users")
                    .document(currentUserId)
                    .get()
                    .addOnSuccessListener(userDocument -> {
                        if (userDocument.exists()) {
                            // Get restaurant basic info from users collection
                            String name = userDocument.getString("nomRestaurant");
                            String address = userDocument.getString("adresse");
                            restaurantName.setText(name != null ? name : "");
                            restaurantAddress.setText(address != null ? address : "");

                            // Now get accessibility features from Etablissements collection
                            db.collection("Etablissements")
                                    .document(currentUserId)
                                    .get()
                                    .addOnSuccessListener(document -> {
                                        if (document.exists()) {
                                            displayMobilitePhysique(document);
                                            displayDeficienceVisuelle(document);
                                            displayDeficienceAuditive(document);
                                        } else {
                                            Toast.makeText(this, "Aucune information d'accessibilité trouvée", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Toast.makeText(this, "Information du restaurant non trouvée", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_LONG).show();
        }
    }

    private void displayMobilitePhysique(DocumentSnapshot document) {
        mobilitePhysiqueContainer.removeAllViews();
        Map<String, Object> data = document.getData();
        if (data != null && data.containsKey("mobilitePhysique")) {
            Map<String, Boolean> mobilitePhysique = (Map<String, Boolean>) data.get("mobilitePhysique");
            if (mobilitePhysique != null) {
                for (Map.Entry<String, Boolean> entry : mobilitePhysique.entrySet()) {
                    if (Boolean.TRUE.equals(entry.getValue())) {
                        addCheckboxItem(mobilitePhysiqueContainer, translateKey(entry.getKey()));
                    }
                }
            }
        }
    }

    private void displayDeficienceVisuelle(DocumentSnapshot document) {
        deficienceVisuelleContainer.removeAllViews();
        Map<String, Object> data = document.getData();
        if (data != null && data.containsKey("deficienceVisuelle")) {
            Map<String, Boolean> deficienceVisuelle = (Map<String, Boolean>) data.get("deficienceVisuelle");
            if (deficienceVisuelle != null) {
                for (Map.Entry<String, Boolean> entry : deficienceVisuelle.entrySet()) {
                    if (Boolean.TRUE.equals(entry.getValue())) {
                        addCheckboxItem(deficienceVisuelleContainer, translateKey(entry.getKey()));
                    }
                }
            }
        }
    }

    private void displayDeficienceAuditive(DocumentSnapshot document) {
        deficienceAuditiveContainer.removeAllViews();
        Map<String, Object> data = document.getData();
        if (data != null && data.containsKey("deficienceAuditive")) {
            Map<String, Boolean> deficienceAuditive = (Map<String, Boolean>) data.get("deficienceAuditive");
            if (deficienceAuditive != null) {
                for (Map.Entry<String, Boolean> entry : deficienceAuditive.entrySet()) {
                    if (Boolean.TRUE.equals(entry.getValue())) {
                        addCheckboxItem(deficienceAuditiveContainer, translateKey(entry.getKey()));
                    }
                }
            }
        }
    }

    private void addCheckboxItem(LinearLayout container, String text) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(text);
        checkBox.setChecked(true);
        checkBox.setEnabled(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 8);
        checkBox.setLayoutParams(params);

        container.addView(checkBox);
    }

    private String translateKey(String key) {
        switch (key) {
            case "ascenseur": return "Ascenseur";
            case "porteAutomatique": return "Porte automatique";
            case "cheminDaccesStable": return "Chemin d'accès stable";
            case "circulationsAise": return "Circulations à l'aise";
            case "parkingPMR": return "Parking PMR";
            case "rampeAcces": return "Rampe d'accès";
            case "tablesHauteurAdaptes": return "Tables à hauteur adaptée";
            case "toilettesAdaptes": return "Toilettes adaptées";
            case "chiensGuideAcceptes": return "Chiens guides acceptés";
            case "descriptionAudio": return "Description audio";
            case "eclairageAdapte": return "Éclairage adapté";
            case "menuBraille": return "Menu en braille";
            case "menusGrosCaracteres": return "Menus en gros caractères";
            case "AlarmesLumineuses": return "Alarmes lumineuses";
            case "AlertesVisuelles": return "Alertes visuelles";
            case "BoucleMagnetique": return "Boucle magnétique";
            case "PersonnelFormeLSF": return "Personnel formé LSF";
            case "SupportEcrit": return "Support écrit";
            case "SupportVisuel": return "Support visuel";
            default: return key;
        }
    }
}