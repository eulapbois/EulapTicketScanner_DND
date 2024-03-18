package com.example.eulapticketscanner_dnd.text_reco;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.eulapticketscanner_dnd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Set<String> uniqueRecognizedTexts;
    private ImageCapture imageCapture;
    private FirestoreDatabase firestoreDatabase;
    ImageButton Capture, dataview;
    Button fetch;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    TextView textView;
    TextRecognizer textRecognizer;
    Dialog error;
    Dialog sucssess;
    private PreviewView previewView;
    private FirebaseFirestore db;
    private List<String> cachedTicketNumbers = new ArrayList<>();


    private boolean isCached = false;
    // This will hold the different data for both local scan and cloud storage.
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sucssess = new Dialog(this);
        error = new Dialog(this);
        //camera preview
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        firestoreDatabase = new FirestoreDatabase();
        previewView = findViewById(R.id.magnifiedTextureView);


        //buttons
        Capture = findViewById(R.id.Capture);
        dataview = findViewById(R.id.dataview);
        textView = findViewById(R.id.TicketView);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        // Enable offline persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder(db.getFirestoreSettings())
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                        .build();
        db.setFirestoreSettings(settings);

        db.collection("TVS")
//              .get(Source.CACHE) <- of you want to check the cache saved.
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String ticketNumber = document.getString("TicketNumber");
                                list.add(document.getId());
                                list.add(ticketNumber);
                                cachedTicketNumbers.add(ticketNumber);
//                                if(document.getMetadata().isFromCache()){
//                                    Toast.makeText(MainActivity.this, "Data is Cached" + ticketNumber, Toast.LENGTH_SHORT).show();
//                                }else {
//                                    Toast.makeText(MainActivity.this, "Data is from server: " + ticketNumber, Toast.LENGTH_SHORT).show();
//                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        uniqueRecognizedTexts = new HashSet<>(sharedPreferences.getStringSet("recognized_texts", new HashSet<>()));

        Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageCapture != null) {
                    File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
                    ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

                    imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Processing Image",  Toast.LENGTH_SHORT).show();
                                }
                            });
                            // Convert the captured image to Bitmap and recognize text
                            Bitmap capturedBitmap = BitmapFactory.decodeFile(file.getPath());
                            recognizeAndSaveText(capturedBitmap, file);
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        dataview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DataView.class);
                startActivity(intent);
                // TODO : transfer this to oncreate method
//                fetchCloudData();
            }
        });

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                BackgroudView(cameraProvider);
                //magnified
                magnifiedPreview(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
}

    private void recognizeAndSaveText(Bitmap bitmap, File imageFile) {
        if (bitmap != null) {
            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
            textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            String recognizedText = text.getText();
                            // Validate the recognized text
                            if (isValidText(recognizedText)) {
                                boolean isTicketNumberInCache = isTicketNumberinCache(recognizedText);
                                if(isTicketNumberInCache){
                                    if(!isTicketNumberScannedTwice(recognizedText)){
                                        Toast.makeText(MainActivity.this, "No Duplicate Found", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Duplicate Entry", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(recognizedText);
                                    }
                                });
                                // Save the recognized text to Firestore database
//                                firestoreDatabase.saveDataToFirestore(MainActivity.this, recognizedText);

                                // Delete the captured image file after processing
                                deleteCapturedImage(imageFile);
                                Toast.makeText(MainActivity.this, "Verifying Data Existence", Toast.LENGTH_SHORT).show();
                            } else {
                                // Delete the captured image file even for invalid text
                                deleteCapturedImage(imageFile);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.BackgroudView).setBackgroundColor(Color.parseColor("#990000"));
                                        Snackbar.make(findViewById(R.id.main), "Invalid text. Please scan a valid 6-digit number.", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean isTicketNumberFlagged(String ticketNumber) {
        return firstScanTicketNumbers.contains(ticketNumber);
    }
    HashSet<String> firstScanTicketNumbers = new HashSet<>();
    private boolean isTicketNumberScannedTwice(String ticketNumber) {
        // Check if the TicketNumber has been recognized before (flagged)
        boolean isFlagged = isTicketNumberFlagged(ticketNumber);
        if (isFlagged) {
            // If the TicketNumber has been recognized before, it's a duplicate scan
            Toast.makeText(this, "TicketNumber " + ticketNumber + " has been recognized before (flagged). It's a duplicate scan.", Toast.LENGTH_SHORT).show();
        } else {
            // If the TicketNumber hasn't been recognized before, add it to the HashSet
            firstScanTicketNumbers.add(ticketNumber);
            // Display a message indicating it's a new scan
            Toast.makeText(this, "TicketNumber " + ticketNumber + " has not been recognized before. It's a new scan.", Toast.LENGTH_SHORT).show();
        }
        return isFlagged;
    }
    private boolean isTicketNumberinCache(String ticketNumber){
        for(String cachedTicketNumber : cachedTicketNumbers){
            if(ticketNumber.equals(cachedTicketNumber)){
                return true;
            }
        }
        return false;
    }



//
//        private void fetchCloudData () {
//            // Replace with your actual Query and List<Ticket> initialization
//            Query initialQuery = db.collection("TVS").limit(50);
//            List<Ticket> initialTickets = new ArrayList<>();
//
//            // Show a progress dialog while fetching data
//            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//            progressDialog.setMessage("Syncing...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//
//            firestoreDatabase.fetchAllTickets(
//                    new OnSuccessListener<List<Ticket>>() {
//                        @Override
//                        public void onSuccess(List<Ticket> tickets) {
//                            // Successfully fetched data, manipulate and update UI
//                            for (Ticket ticket : tickets) {
//                                // Perform any manipulations on each ticket object
//                                // For example, modify the ticket number, set additional properties, etc.
//                            }
//                            // Update your TicketDataManager with the manipulated data
//                            ticketDataManager.setCloudTickets(tickets);
//                            // Save the fetched data to internal storage
//                            saveDataToInternalStorage(tickets);
//
//                            // Dismiss the progress dialog when data fetching is complete
//                            progressDialog.dismiss();
//                        }
//                    },
//                    new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Error while fetching data, handle accordingly
//                            Log.e("ticketNumber", "Error fetching data from Firestore: " + e.getMessage());
//                            Toast.makeText(MainActivity.this, "Error fetching data from Firestore", Toast.LENGTH_SHORT).show();
//
//                            // Dismiss the progress dialog and show an error message
//                            progressDialog.dismiss();
//                            showErrorDialog("Error fetching data");
//                        }
//                    },
//                    initialQuery,
//                    initialTickets
//            );
//        }
    // Helper method to show an error dialog
//    private void showErrorDialog(String errorMessage) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Error")
//                .setMessage(errorMessage)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do something on OK
//                    }
//                })
//                .show();
//    }
//    private void saveDataToInternalStorage(List<Ticket> tickets) {
//        Gson gson = new Gson();
//        String serializedData = gson.toJson(tickets);
//
//        // Save the serialized data to a file in internal storage
//        try (FileOutputStream fos = openFileOutput("ticket_data.json", Context.MODE_PRIVATE)) {
//            fos.write(serializedData.getBytes());
//            Log.d("ticketNumber", "Data saved to internal storage");
//        } catch (IOException e) {
//            Log.e("ticketNumber", "Error saving data to internal storage: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }


    private void deleteCapturedImage(File imageFile) {
        if (imageFile != null && imageFile.exists()) {
            boolean deleted = imageFile.delete();
        }
    }

    //BackgroudView
    private void BackgroudView(ProcessCameraProvider cameraProvider) {
        PreviewView bgpreviewView = findViewById(R.id.BackgroudView);

        Preview.Builder previewBuilder = new Preview.Builder();
        Preview previewbg = previewBuilder.build();
        previewbg.setSurfaceProvider(bgpreviewView.getSurfaceProvider());

        CameraSelector cameraSelectorBG = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        try {
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelectorBG, previewbg);
            // Set initial zoom level
            CameraControl cameraControlBg = camera.getCameraControl();
            cameraControlBg.setZoomRatio(0.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //magnified
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void magnifiedPreview(ProcessCameraProvider cameraProvider) {
        PreviewView previewView = findViewById(R.id.magnifiedTextureView);

        // ImageCapture initialization
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(new Size(195, 60))
                .build();
        //
        Preview.Builder previewBuilder = new Preview.Builder();
        Preview preview = previewBuilder.build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ViewPort viewPort = new ViewPort.Builder(
                new Rational(195, 60),
                getDisplay().getRotation())
                .build();

        UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture)
                .setViewPort(viewPort)
                .build();

        try {
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector,useCaseGroup);

            // Set initial zoom level
            CameraControl cameraControl = camera.getCameraControl();
            cameraControl.setZoomRatio(4.0f);

            // Set initial focus point
            MeteringPoint meteringPoint = new SurfaceOrientedMeteringPointFactory(1f, 1f)
                    .createPoint(previewView.getWidth() / 2f, previewView.getHeight() / 2f);

            FocusMeteringAction action = new FocusMeteringAction.Builder(meteringPoint, FocusMeteringAction.FLAG_AF)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    recognizeText(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the error
                }
            } else {
                // Handle the case where no image Uri is returned
            }
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizeText(Bitmap bitmap) {
        if (bitmap != null) {
            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
            textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            String recognizeText = text.getText();
                            // Validate the recognized text
                            if (isValidText(recognizeText)) {
                                    sucssess.setContentView(R.layout.sucssess);
                                    sucssess.show();
                                    textView.setText(recognizeText);

                            } else {
                                textView.setText("");  // Clear the TextView if the text is invalid
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private boolean isValidText(String text) {
        // Validate that the text contains only numeric characters and has a length of 6
        return text.matches("\\d{6}");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear Firestore persistence when the app is destroyed
        FirebaseFirestore.getInstance().clearPersistence();
        finish();
    }
}