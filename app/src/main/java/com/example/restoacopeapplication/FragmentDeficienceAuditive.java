package com.example.restoacopeapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FragmentDeficienceAuditive extends Fragment {
    private AccessibilityViewModel viewModel;
    private CheckBox cbPersonnelFormeLSF, cbSupportEcrit, cbSupportVisuel;
    private CheckBox cbBoucleMagnetique, cbAlertesVisuelles, cbAlarmesLuminueuses;
    private FirebaseFirestore firestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AccessibilityViewModel.class);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deficience_auditive, container, false);
        initializeViews(view);
        setupButtons(view);
        return view;
    }

    private void initializeViews(View view) {
        cbPersonnelFormeLSF = view.findViewById(R.id.cbPersonnelFormeLSF);
        cbSupportEcrit = view.findViewById(R.id.cbSupportEcrit);
        cbSupportVisuel = view.findViewById(R.id.cbSupportVisuel);
        cbBoucleMagnetique = view.findViewById(R.id.cbBoucleMagnetique);
        cbAlertesVisuelles = view.findViewById(R.id.cbAlertesVisuelles);
        cbAlarmesLuminueuses = view.findViewById(R.id.cbAlarmesLuminueuses);
    }

    private void setupButtons(View view) {
        view.findViewById(R.id.btnPrevious).setOnClickListener(v ->
                getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btnNext).setOnClickListener(v -> saveAndUpload());
    }

    private void saveAndUpload() {
        Map<String, Boolean> deficienceAuditive = collectData();
        viewModel.saveDeficienceAuditiveData(deficienceAuditive);
        uploadToFirebase();
    }

    private Map<String, Boolean> collectData() {
        Map<String, Boolean> data = new HashMap<>();
        data.put("PersonnelFormeLSF", cbPersonnelFormeLSF.isChecked());
        data.put("SupportEcrit", cbSupportEcrit.isChecked());
        data.put("SupportVisuel", cbSupportVisuel.isChecked());
        data.put("BoucleMagnetique", cbBoucleMagnetique.isChecked());
        data.put("AlertesVisuelles", cbAlertesVisuelles.isChecked());
        data.put("AlarmesLuminueuses", cbAlarmesLuminueuses.isChecked());
        return data;
    }

    private void uploadToFirebase() {
        Map<String, Object> allData = new HashMap<>();
        allData.put("mobilitePhysique", viewModel.getMobiliteData().getValue());
        allData.put("deficienceVisuelle", viewModel.getDeficienceVisuelleData().getValue());
        allData.put("deficienceAuditive", viewModel.getDeficienceAuditiveData().getValue());
        allData.put("photos", viewModel.getPhotosData().getValue());

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Firebase", "CurrentUserId: " + currentUserId);
        Log.d("Firebase", "Data to upload: " + allData.toString());

        // Upload directement dans Etablissements avec l'ID utilisateur
        firestore.collection("Etablissements")
                .document(currentUserId)
                .set(allData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Document successfully written");
                    Toast.makeText(getContext(), "Données enregistrées", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error writing document: " + e.getMessage());
                    Toast.makeText(getContext(), "Erreur d'enregistrement", Toast.LENGTH_SHORT).show();
                });
    }
}