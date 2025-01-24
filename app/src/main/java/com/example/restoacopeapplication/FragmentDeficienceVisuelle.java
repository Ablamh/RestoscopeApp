package com.example.restoacopeapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;
import android.widget.Toast;
import android.content.Context;

public class FragmentDeficienceVisuelle extends Fragment {
    private AccessibilityViewModel viewModel;
    private CheckBox cbMenuBraille, cbMenusGrosCaracteres, cbDescriptionAudio;
    private CheckBox cbEclairageAdapte, cbChiensGuideAcceptes, cbFormationDuPersonnel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AccessibilityViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deficience_visuelle, container, false);
        initializeViews(view);
        setupButtons(view);
        return view;
    }

    private void initializeViews(View view) {
        cbMenuBraille = view.findViewById(R.id.cbMenuBraille);
        cbMenusGrosCaracteres = view.findViewById(R.id.cbMenusGrosCaracteres);
        cbDescriptionAudio = view.findViewById(R.id.cbDescriptionAudio);
        cbEclairageAdapte = view.findViewById(R.id.cbEclairageAdapte);
        cbChiensGuideAcceptes = view.findViewById(R.id.cbChiensGuideAcceptes);
        cbFormationDuPersonnel = view.findViewById(R.id.cbFormationDuPersonnel);
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
        Map<String, Boolean> deficienceVisuelle = new HashMap<>();
        deficienceVisuelle.put("menuBraille", cbMenuBraille.isChecked());
        deficienceVisuelle.put("menusGrosCaracteres", cbMenusGrosCaracteres.isChecked());
        deficienceVisuelle.put("descriptionAudio", cbDescriptionAudio.isChecked());
        deficienceVisuelle.put("eclairageAdapte", cbEclairageAdapte.isChecked());
        deficienceVisuelle.put("chiensGuideAcceptes", cbChiensGuideAcceptes.isChecked());
        deficienceVisuelle.put("formationDuPersonnel", cbFormationDuPersonnel.isChecked());

        try {
            viewModel.saveDeficienceVisuelleData(deficienceVisuelle);
            return true;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erreur de sauvegarde", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void navigateToNext() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new FragmentDeficienceAuditive())
                .addToBackStack(null)
                .commit();
    }
}

