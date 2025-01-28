package com.example.restoacopeapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AcceuilFragment extends Fragment {
    private Button guideButton;

    public AcceuilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_acceuil, container, false);

        // Initialisation du bouton
        guideButton = view.findViewById(R.id.guideButton);

        // Ajout du listener pour le bouton
        guideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Création de l'intent pour démarrer l'activité GuideAccessibility
                Intent intent = new Intent(getActivity(), GuideAccessibility.class);
                startActivity(intent);
            }
        });

        return view;
    }
}