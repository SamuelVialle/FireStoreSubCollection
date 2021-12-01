
package com.samuelvialle.firestoresubcollection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * Cette application affiche un spinner qui propose un premier choix, si Employeur est sélectionné alors on affiche le second spinner avec
     * les secteurs d'activités puis en sélectionnant le secteur cela affiche le troisième spinner avec les métiers du secteur en question
     *
     * Cette application se base sur un base de donnée Firestaore qui a cette forme :
     *   |    Collection   |     Documents        |     Collection      |         Documents         |   Collection  |                     Documents
     *   | Type de profils | Les types de profils | Secteurs d'activité |  Les secteurs d'activité  |    Métiers    |  La liste des métiers associé au secteur choisi
     *
     */
    

    private static final String TAG = "######>>>";
    private static final String TAG2 = "######222222>>>";

    // Variables des widgets
    private Spinner sTypeDeProfils, sSecteur, sMetier;
    private TextView tvResult1, tvResult2, tvResult3;

    // Variables Firebase
    private FirebaseFirestore db;
    private CollectionReference typeDeProfilsRef;
    private CollectionReference secteurRef;
    private CollectionReference metierRef;

    // Variables des listes
    List<String> typeDeProfilsList = new ArrayList<>();
    List<String> secteurList = new ArrayList<>();
    List<String> metierList = new ArrayList<>();

    // Variables pour passer le secteur dans le 3ème spinner (métier)
    String secteur;


    // Méthode init des widgets
    public void init() {
        sTypeDeProfils = findViewById(R.id.sTypeDeProfils);
        sSecteur = findViewById(R.id.sSecteur);
        sMetier = findViewById(R.id.sMetier);
        tvResult1 = findViewById(R.id.tvResult1);
        tvResult2 = findViewById(R.id.tvResult2);
        tvResult3 = findViewById(R.id.tvResult3);
    }

    // Méthode init Firestore
    public void initFirebase() {
        db = FirebaseFirestore.getInstance();
        typeDeProfilsRef = db.collection("Type de profils");
        secteurRef = db.collection("Type de profils/Employeur/Secteurs");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Appel de la méthode init()
        init();
        // Appel de la méthode initFirebase
        initFirebase();
        // Appel des méthodes des spinners
        // Spinner type de profils
        spinnerTypeDeProfils();
    }

    public void spinnerTypeDeProfils() {
        // Création de l'adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, typeDeProfilsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Affectation de la liste à l'adapter
        sTypeDeProfils.setAdapter(adapter);
        // Récupération des données depuis firestore
        typeDeProfilsRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String typeDeprofils = document.getId();
                                typeDeProfilsList.add(typeDeprofils);
                            }
                            adapter.notifyDataSetChanged();
                            // Ajout du listener en s'appuyant sur l'implémentation AdapterView.OnItemSelectedListener
                            // directement ajouter à la classe de l'activité
                            sTypeDeProfils.setOnItemSelectedListener(MainActivity.this);
                        }
                    }
                });
    }

    public void spinnerSecteur() {
        // Création de l'adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, secteurList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Affectation de la liste à l'adapter
        sSecteur.setAdapter(adapter);
        secteurRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String secteur = document.getId();
                                secteurList.add(secteur);
                            }
                            adapter.notifyDataSetChanged();
                            // Ajout du listener en s'appuyant sur l'implémentation AdapterView.OnItemSelectedListener
                            // directement ajouter à la classe de l'activité
                            sSecteur.setOnItemSelectedListener(MainActivity.this);
                        }
                    }
                });
    }

    public void spinnerMetier() {

        // Création de l'adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, metierList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Affectation de la liste à l'adapter
        sMetier.setAdapter(adapter);
        metierRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String metier = document.getId();
                                metierList.add(metier);
                            }
                            adapter.notifyDataSetChanged();
                            // Ajout du listener en s'appuyant sur l'implémentation AdapterView.OnItemSelectedListener
                            // directement ajouter à la classe de l'activité
                            sMetier.setOnItemSelectedListener(MainActivity.this);
                        }
                    }
                });
    }


    // Le callback de l'AdapterView.OnItemClickListener pour qu'il soit valable sur tout les spinners
    // Déclaration du Toast en dehors des méthodes pour l'utilisé quand il se passe et quand il ne se passe rien
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Ici on ajoute un Toast mais on peut très bien y ajouter la gestion des Intent via un switch
        // On affiche le nom du spinner et la valeur sélectionner dans le textView
        switch (parent.getId()) {
            case R.id.sTypeDeProfils:
                tvResult1.setText("Le profil sélectionné est " + typeDeProfilsList.get(position));
                if (typeDeProfilsList.get(position).equals("Employeur")) {
                    spinnerSecteur();
                    sSecteur.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.sSecteur:
                tvResult2.setText("Le secteur est " + secteurList.get(position));
                secteur = secteurList.get(position);
                if (!secteurList.isEmpty()) {
                    metierRef = db.collection("Type de profils/Employeur/Secteurs/").document(secteur).collection("Métiers");
                    spinnerMetier();
                    sMetier.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.sMetier:
                tvResult3.setText("Le métier choisi est " + metierList.get(position));
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Quelle action effectuer s'il ne se passe rien
        final Toast myToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        myToast.cancel();
    }
}