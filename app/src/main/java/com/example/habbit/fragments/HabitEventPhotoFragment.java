package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.habbit.BuildConfig;
import com.example.habbit.R;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class HabitEventPhotoFragment extends DialogFragment {

    String currentPhotoPath;
    ImageView photoPreview;
    Uri imageUri;

    /**
     * The result launcher for selecting photo
     */
    ActivityResultLauncher<Intent> cameraActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (currentPhotoPath == null) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                    } else{
                    File bitmap = new File(currentPhotoPath);
                    try {
                        imageUri = Uri.parse(MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), String.valueOf(bitmap), null,null));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    }
                    Picasso.get().load(imageUri).into(photoPreview);
                }
            }
    );

    public HabitEventPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param habitEvent The HabitEvent whose details we want to view, of type {@link Habit}.
     * @return A new instance of fragment {@link HabitEventDetailsFragment}.
     */
    public static HabitEventPhotoFragment newInstance(HabitEvent habitEvent, Habit habit) {
        HabitEventPhotoFragment fragment = new HabitEventPhotoFragment();
        Bundle args = new Bundle();
        args.putSerializable("habitEvent", habitEvent);
        args.putSerializable("habit", habit);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_habit_event_photo,null);

        Button cameraBtn = view.findViewById(R.id.takePhotoCamera);
        Button galleryBtn = view.findViewById(R.id.takePhotoGallery);
        photoPreview = view.findViewById(R.id.photoPreview);

        cameraBtn.setOnClickListener(v -> dispatchTakePictureIntent());
        galleryBtn.setOnClickListener(v -> dispatchSelectPictureIntent());

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit", (dialogInterface, i) -> {
                    if (imageUri != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("bundleKey", imageUri.toString());
                        requireActivity().getSupportFragmentManager().setFragmentResult("requestKey", bundle);
                    } else {
                        Toast.makeText(getActivity(), "Please select a photo", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }

    /**
     * This method is to invoke android camera to take a picture
     **/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("Error while taking the photo");
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraActivity.launch(takePictureIntent);
            }
        } catch (ActivityNotFoundException e) {
            // display error state to the user
            System.out.println("Why are we still here...just no camera?");
        }
    }

    /**
     * This method is for invoke android media selection to select a picture
     */
    private void dispatchSelectPictureIntent() {
        Intent selectPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectPictureIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        Intent chooserIntent = Intent.createChooser(selectPictureIntent, "Select Picture");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        cameraActivity.launch(selectPictureIntent);
    }

    /**
     * This method is for selecting a filename for the taken picture
     * @return image, an empty {@link File} which has the appropriate file name
     * @throws IOException for error encountered
     */
    private File createImageFile() throws IOException {
        String imageFileName = "temp";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
