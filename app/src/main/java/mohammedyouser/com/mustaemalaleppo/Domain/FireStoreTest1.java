package mohammedyouser.com.mustaemalaleppo.Domain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import mohammedyouser.com.mustaemalaleppo.R;

public class FireStoreTest1 extends AppCompatActivity {

    public static final String TAG = "FireStoreTest Tag";
    private DocumentReference ref=FirebaseFirestore.getInstance().collection("myCollection1").document("document1");
    private TextView result;
    private EditText name;
    private EditText surname;
    private  Map<String,String> dataToSave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestroe_test1);
        name=(EditText) findViewById(R.id.editText);
        surname=(EditText) findViewById(R.id.editText2);
        result=(TextView) findViewById(R.id.tv_logo_);
        dataToSave =new  HashMap <String, String>();



    }
    @Override
    protected void onStart() {


        super.onStart();
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    result.setText(documentSnapshot.getString("Name")+"  "+documentSnapshot.getString("Surname"));



                }

            }
        });
    }

    public void fetchData(View view){

        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                result.setText(documentSnapshot.getString("Name")+"  "+documentSnapshot.getString("Surname"));


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    public void saveData(View view){

        dataToSave.put("Name",String.valueOf(name.getText()));
        dataToSave.put("Surname",String.valueOf(surname.getText()));
        ref.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");

            }
        });

    }
}
