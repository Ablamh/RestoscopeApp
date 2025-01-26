package com.example.restoacopeapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.Map;

public class InformationRestaurantActivity extends AppCompatActivity {
    private ViewPager2 imageViewPager;
    private TabLayout tabLayout;
    private TextView restaurantNameText, addressText;
    private Button commentButton;
    private FirebaseFirestore db;
    private ImagePagerAdapter imagePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_restaurant);
        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupViewPager();
        loadRestaurantData();
    }

    private void initializeViews() {
        imageViewPager = findViewById(R.id.imageViewPager);
        tabLayout = findViewById(R.id.tabLayout);
        restaurantNameText = findViewById(R.id.restaurantName);
        addressText = findViewById(R.id.restaurantAddress);
        commentButton = findViewById(R.id.commentButton);

        commentButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommentaireActivity.class);
            intent.putExtra("restaurantId", getIntent().getStringExtra("restaurantId"));
            startActivity(intent);
        });
    }

    private void setupViewPager() {
        imagePagerAdapter = new ImagePagerAdapter(this);
        imageViewPager.setAdapter(imagePagerAdapter);
        new TabLayoutMediator(tabLayout, imageViewPager, (tab, position) -> {}).attach();
    }

    private void loadRestaurantData() {
        String restaurantId = getIntent().getStringExtra("restaurantId");
        String restaurantName = getIntent().getStringExtra("restaurantName");
        String restaurantAddress = getIntent().getStringExtra("restaurantAddress");

        restaurantNameText.setText(restaurantName);
        addressText.setText(restaurantAddress);

        db.collection("Etablissements")
                .document(restaurantId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        List<String> photos = (List<String>) document.get("photos");
                        if (photos != null && !photos.isEmpty()) {
                            imagePagerAdapter.setImages(photos);
                        }

                        updateAccessibilityFeatures(
                                (Map<String, Object>) document.get("mobilitePhysique"),
                                (Map<String, Object>) document.get("deficienceVisuelle"),
                                (Map<String, Object>) document.get("deficienceAuditive")
                        );
                    }
                });
    }

    private void updateAccessibilityFeatures(Map<String, Object> mobilitePhysique,
                                             Map<String, Object> deficienceVisuelle,
                                             Map<String, Object> deficienceAuditive) {
        LinearLayout mobiliteContainer = findViewById(R.id.mobilitePhysiqueContainer);
        LinearLayout visuelleContainer = findViewById(R.id.deficienceVisuelleContainer);
        LinearLayout auditiveContainer = findViewById(R.id.deficienceAuditiveContainer);

        mobiliteContainer.removeAllViews();
        visuelleContainer.removeAllViews();
        auditiveContainer.removeAllViews();

        if (mobilitePhysique != null) {
            for (Map.Entry<String, Object> entry : mobilitePhysique.entrySet()) {
                if (entry.getValue() instanceof Boolean && (Boolean) entry.getValue()) {
                    addFeature(mobiliteContainer, getFeatureLabel(entry.getKey()));
                }
            }
        }

        if (deficienceVisuelle != null) {
            for (Map.Entry<String, Object> entry : deficienceVisuelle.entrySet()) {
                if (entry.getValue() instanceof Boolean && (Boolean) entry.getValue()) {
                    addFeature(visuelleContainer, getFeatureLabel(entry.getKey()));
                }
            }
        }

        if (deficienceAuditive != null) {
            for (Map.Entry<String, Object> entry : deficienceAuditive.entrySet()) {
                if (entry.getValue() instanceof Boolean && (Boolean) entry.getValue()) {
                    addFeature(auditiveContainer, getFeatureLabel(entry.getKey()));
                }
            }
        }
    }

    private void addFeature(LinearLayout container, String feature) {
        TextView textView = new TextView(this);
        textView.setText("✓ " + feature);
        textView.setTextSize(16);
        textView.setPadding(40, 10, 20, 10);
        container.addView(textView);
    }

    private String getFeatureLabel(String key) {
        switch (key) {
            case "ascenseur": return "Ascenseur";
            case "chemainDaccesStable": return "Chemin d'accès stable";
            case "circulationsAise": return "Circulations aisées";
            case "parkingPMR": return "Parking PMR";
            case "porteAutomatique": return "Porte automatique";
            case "chiensGuideAcceptes": return "Chiens guides acceptés";
            case "menuBraille": return "Menu en braille";
            case "menusGrosCaracteres": return "Menus gros caractères";
            case "AlarmesLuminueuses": return "Alarmes lumineuses";
            case "BoucleMagnetique": return "Boucle magnétique";
            case "SupportEcrit": return "Support écrit";
            default: return key;
        }
    }
}