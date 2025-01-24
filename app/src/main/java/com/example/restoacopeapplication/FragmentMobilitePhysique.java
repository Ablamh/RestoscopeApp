package com.example.restoacopeapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;

public class FragmentMobilitePhysique extends Fragment {
    private AccessibilityViewModel viewModel;
    private CheckBox cbRampeAcces, cbParkingPMR, cbPorteAutomatique;
    private CheckBox cbCheminDaccesStable, cbToilettesAdaptes, cbAscenseur;
    private CheckBox cbTablesHauteurAdaptes, cbCirculationsAise;
    private SharedData sharedData;

    public FragmentMobilitePhysique() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedData = SharedData.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mobilite_physique, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(AccessibilityViewModel.class);

        initializeViews(view);
        setupButtons(view);

        return view;
    }

    private void initializeViews(View view) {
        cbRampeAcces = view.findViewById(R.id.cbRampeAcces);
        cbParkingPMR = view.findViewById(R.id.cbParkingPMR);
        cbPorteAutomatique = view.findViewById(R.id.cbPorteAutomatique);
        cbCheminDaccesStable = view.findViewById(R.id.cbCheminDaccesStable);
        cbToilettesAdaptes = view.findViewById(R.id.cbToilettesAdaptes);
        cbAscenseur = view.findViewById(R.id.cbAscenseur);
        cbTablesHauteurAdaptes = view.findViewById(R.id.cbTablesHauteurAdaptes);
        cbCirculationsAise = view.findViewById(R.id.cbCirculationsAise);
    }

    private void setupButtons(View view) {
        Button btnPrecedent = view.findViewById(R.id.btnPrevious);
        Button btnSuivant = view.findViewById(R.id.btnNext);

        btnPrecedent.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnSuivant.setOnClickListener(v -> {
            if (saveData()) {
                navigateToNext();
            }
        });
    }

    private boolean saveData() {
        Map<String, Boolean> mobilitePhysique = new HashMap<>();
        mobilitePhysique.put("rampeAcces", cbRampeAcces.isChecked());
        mobilitePhysique.put("parkingPMR", cbParkingPMR.isChecked());
        mobilitePhysique.put("porteAutomatique", cbPorteAutomatique.isChecked());
        mobilitePhysique.put("chemainDaccesStable", cbCheminDaccesStable.isChecked());
        mobilitePhysique.put("toilettesAdaptes", cbToilettesAdaptes.isChecked());
        mobilitePhysique.put("ascenseur", cbAscenseur.isChecked());
        mobilitePhysique.put("tablesHauteurAdaptes", cbTablesHauteurAdaptes.isChecked());
        mobilitePhysique.put("circulationsAise", cbCirculationsAise.isChecked());

        try {
            viewModel.saveMobiliteData(mobilitePhysique);
            return true;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void navigateToNext() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new FragmentDeficienceVisuelle())
                .addToBackStack(null)
                .commit();
    }
}