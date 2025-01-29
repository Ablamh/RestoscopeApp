package com.example.restoacopeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class AjouterFragment extends Fragment {
    private Button btnAjouter, btnConsulter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AjouterFragment() {
        // Required empty public constructor
    }

    public static AjouterFragment newInstance(String param1, String param2) {
        AjouterFragment fragment = new AjouterFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ajouter, container, false);

        // Initialize buttons
        btnAjouter = view.findViewById(R.id.btnAjouter);
        btnConsulter = view.findViewById(R.id.consulter);

        // Setup Ajouter button click listener
        btnAjouter.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new FragmentImageUpload())
                    .addToBackStack(null)
                    .commit();
        });

        // Setup Consulter button click listener
        btnConsulter.setOnClickListener(v -> {
            // Create intent to navigate to VisaliserRestaurantActivity
            Intent intent = new Intent(getActivity(), VisaliserRestaurantActivity.class);
            startActivity(intent);
        });

        return view;
    }
}