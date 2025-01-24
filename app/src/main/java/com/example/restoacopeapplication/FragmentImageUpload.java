package com.example.restoacopeapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class FragmentImageUpload extends Fragment {
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private AccessibilityViewModel viewModel;
    private GridView gridView;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUris.add(uri);
                    updateGridView();
                }
            }
    );

    private final ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    imageUris.add(uri);
                    updateGridView();
                }
            }
    );

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    imagePickerLauncher.launch("image/*");
                } else {
                    Toast.makeText(getContext(), "Permission d'accès refusée", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AccessibilityViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_upload, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        gridView = view.findViewById(R.id.gridImages);
        View uploadZone = view.findViewById(R.id.uploadZone);
        Button btnSuivant = view.findViewById(R.id.btnNext);

        uploadZone.setOnClickListener(v -> checkPermissionAndOpenGallery());
        btnSuivant.setOnClickListener(v -> {
            if (!imageUris.isEmpty()) {
                saveImages();
                navigateToNext();
            } else {
                Toast.makeText(getContext(), "Veuillez ajouter des photos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            photoPickerLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*");
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            imagePickerLauncher.launch("image/*");
        }
    }

    private void saveImages() {
        List<String> photoUris = new ArrayList<>();
        for (Uri uri : imageUris) {
            photoUris.add(uri.toString());
        }
        viewModel.savePhotosData(photoUris);
    }

    private void updateGridView() {
        ImageAdapter imageAdapter = new ImageAdapter(getContext(), imageUris);
        gridView.setAdapter(imageAdapter);
        gridView.setVisibility(View.VISIBLE);
    }

    private void navigateToNext() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new FragmentMobilitePhysique())
                .addToBackStack(null)
                .commit();
    }

    private class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Uri> imageUris;

        public ImageAdapter(Context context, ArrayList<Uri> imageUris) {
            this.context = context;
            this.imageUris = imageUris;
        }

        @Override
        public int getCount() {
            return imageUris.size();
        }

        @Override
        public Object getItem(int position) {
            return imageUris.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageURI(imageUris.get(position));
            return imageView;
        }
    }
}