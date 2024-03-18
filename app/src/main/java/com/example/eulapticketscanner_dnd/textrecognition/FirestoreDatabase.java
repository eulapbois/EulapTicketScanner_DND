package com.example.eulapticketscanner_dnd.textrecognition;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreDatabase {

    private FirebaseFirestore db;

    // Add a counter to keep track of the incrementing number

    public FirestoreDatabase() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

//    private Ticket documentToTicket(QueryDocumentSnapshot document) {
//        // Convert the Firestore document to a Ticket object
//        // Modify this method based on your document structure
//        String ticketNumber = document.getString("ticket_number");
//        boolean isValidated = document.getBoolean("isValidated");
//        // Extract other fields as needed
//
//        return new Ticket(ticketNumber, isValidated, new Date());
//    }

//    public void fetchAllTickets(OnSuccessListener<List<Ticket>> successListener, OnFailureListener failureListener,
//                                Query query,
//                                List<Ticket> tickets) {
//        tickets = new ArrayList<>();
//        final String collectionPath = "TVS";
//        query = db.collection(collectionPath).limit(50);
//        Query[] queryRef = {query};
//        List<Ticket> finalTickets = tickets;
//        Query finalQuery = query;
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//                if (querySnapshot != null) {
//                    // Process the current chunk of data
//                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        // Access the document data
//                        Ticket ticket = documentToTicket(document);
//                        finalTickets.add(ticket);
//                    }
//
//                    // If there are more documents, fetch the next chunk
//                    DocumentSnapshot lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
//                    queryRef[0] = db.collection(collectionPath)
//                            .startAfter(lastVisible)
//                            .limit(50);
//
//                    // Continue fetching the next chunk
//                    fetchAllTickets(successListener, failureListener, finalQuery, finalTickets);
//                } else {
//                    Log.d("ticketNumber", "No more documents to fetch");
//                    // All data has been retrieved, call the success listener
//                    successListener.onSuccess(finalTickets);
//                }
//            } else {
//                Log.e("ticketNumber", "Error getting documents: ", task.getException());
//                // Call the failure listener in case of an error
//                failureListener.onFailure(task.getException());
//            }
//        });
//    }

    public void saveDataToFirestore(Context context, String recognizedText) {
        // Ensure recognizedText is a valid document ID (numbers up to 6 digits)
        String sanitizedText = sanitizeDocumentId(recognizedText);

        // Specify the document ID as the sanitizedText
        DocumentReference documentRef = db.collection("TVS").document(sanitizedText);

        // Check if the document with the specified ID already exists
        documentRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document with the same ID already exists, show an error message
                            Toast.makeText(context, "Document with ID " + sanitizedText + " already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Document with the specified ID does not exist, proceed to save the data
                            saveDocument(documentRef, recognizedText, context);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error checking document existence: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error checking document existence", e);
                    }
                });

    }

//    private void saveDocument(DocumentReference documentRef, String recognizedText, boolean isValidated, Context context) {
//        // Create a new document with ticket details
//        Map<String, Object> data = new HashMap<>();
//        data.put("ticket_number", recognizedText);
//        data.put("isValidated", isValidated);
//        data.put("timestamp", FieldValue.serverTimestamp());
//
//        // Add the document to the "Tickets" collection
//        documentRef.set(data)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(context, "Document added successfully with ID: " + recognizedText, Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(context, "Error adding document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e("Firestore", "Error adding document", e);
//                });
//    }

    private void saveDocument(DocumentReference documentRef, String recognizedText, Context context) {
        String sanitizedText = sanitizeDocumentId(recognizedText);
        // Create a new document with the recognized text
        Map<String, Object> data = new HashMap<>();
        data.put("Date", FieldValue.serverTimestamp());  // Use server timestamp for the current date
        data.put("TicketNumber", sanitizedText);
        data.put("IsValidated", false);  // Set to false initially, update as needed

        // Set the data to the specified document
        documentRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Document added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error adding document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error adding document", e);
                    }
                });
    }

    private String sanitizeDocumentId(String input) {
        // Remove non-numeric characters
        String numericText = input.replaceAll("[^0-9]", "");

        // Limit the length to 6 digits
        if (numericText.length() > 6) {
            numericText = numericText.substring(0, 6);
        }

        return numericText;
    }

    public void checkDuplicateInFirestore(Context context, String ticketNumber) {
        DocumentReference ticketRef = db.collection("ticketNumber").document(ticketNumber);

        ticketRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            // No duplicate found in Firestore, proceed to save
                            saveDataToFirestore(context, ticketNumber);
                        } else {
                            Log.d("Firestore Database", "Duplicate entry found in Firestore. Not saving.");
                            // Handle duplicate entry
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore Database", "Error checking duplicate in Firestore: " + e.getMessage());
                        // Handle failure
                    }
                });
    }
//    public void checkValidationAndSave(DocumentReference documentRef, String recognizedText, Context context) {
//        // Check if the ticket is validated
//        boolean isLocalValidationPassed = validateLocally(recognizedText);
//
//        if (!isLocalValidationPassed) {
//            // The ticket is not validated locally, proceed to Firestore validation
//            db.collection("Tickets")
//                    .whereEqualTo("TicketNum", recognizedText)
//                    .get()
//                    .addOnSuccessListener(querySnapshot -> {
//                        boolean isFirestoreValidationPassed = querySnapshot.isEmpty();
//
//                        if (isFirestoreValidationPassed) {
//                            // Update the isValidated field in the existing documents in Firestore
//                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
//                                documentSnapshot.getReference().update("isValidated", true);
//                            }
//                            // Save the document with the specified ID and validation status to local storage
//                            saveDocumentLocally(documentRef, recognizedText, true, context);
//                            Toast.makeText(context, "Document validated and saved successfully.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Firestore validation failed, show a prompt about duplicate ticket number
//                            Toast.makeText(context, "Duplicate Ticket Number. Validation failed.", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("Firestore Database", "Error checking validation in Firestore: " + e.getMessage());
//                        // Assume false for validation in case of failure, adjust as needed
//                        saveDocumentLocally(documentRef, recognizedText, false, context);
//                        Toast.makeText(context, "Local validation failed. Document saved locally.", Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            // Local validation passed, show a prompt or take appropriate action
//            Toast.makeText(context, "Local validation passed. No action needed.", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void saveDocumentLocally(DocumentReference documentRef, String recognizedText, boolean isValidated, Context context) {
//        // Create a new document with ticket details
//        Map<String, Object> data = new HashMap<>();
//        data.put("ticket_number", recognizedText);
//        data.put("isValidated", isValidated);
//        data.put("timestamp", FieldValue.serverTimestamp());
//
//        // Save the document to local storage
//        // Add your logic to save the document locally as needed
//        // For example, use SharedPreferences or a local database
//        // ...
//
//        // You can also display a prompt or take other UI actions based on local saving
//        Toast.makeText(context, "Document saved locally.", Toast.LENGTH_SHORT).show();
//    }
}