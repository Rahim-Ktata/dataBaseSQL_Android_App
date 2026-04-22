package com.example.databasesql;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;

public class ManagingDB extends AppCompatActivity {

    private Button b;
    private EditText searchBar;
    private ListView lv;
    DataBase dataBase;
    private ArrayList<String> fullList = new ArrayList<>();
    private ArrayList<String> filteredList = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_managing_db);

        b=(Button)findViewById(R.id.button2);
        searchBar=(EditText)findViewById(R.id.searchBar);
        lv= findViewById(R.id.list);
        dataBase=new DataBase(this);
        viewData();


        b.setOnClickListener(v -> {
            Intent int2=new Intent(ManagingDB.this, MainActivity.class);
            startActivity(int2);
        });

        // Search filter
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String []items={"Modifier", "Supprimer"};
                AlertDialog.Builder builder=new AlertDialog.Builder(ManagingDB.this);
                builder.setTitle("Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0)
                        {
                            showUpdate(ManagingDB.this, lv, position);
                        }
                        else if(which==1)
                        {
                            delete(lv, position);
                        }
                    }
                });
                builder.show();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void filterList(String query) {
        filteredList.clear();
        for (String item : fullList) {
            // Name is the second token (after ID)
            String[] parts = item.split(" ");
            String name = parts.length > 1 ? parts[1] : "";
            if (name.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        sortList();
        adapter.notifyDataSetChanged();
    }

    private void sortList() {
        Collections.sort(filteredList, (a, b1) -> {
            String nameA = a.split(" ").length > 1 ? a.split(" ")[1] : a;
            String nameB = b1.split(" ").length > 1 ? b1.split(" ")[1] : b1;
            return nameA.compareToIgnoreCase(nameB);
        });
    }

    public void viewData(){
        Cursor c = dataBase.getAllData();
        fullList.clear();
        filteredList.clear();

        if(c.getCount()==0){
            Toast.makeText(ManagingDB.this, "La base est vide", Toast.LENGTH_SHORT).show();
        }
        else {
            while (c.moveToNext()) {
                String entry = c.getString(0)+" "+c.getString(1) + " " + c.getString(2) + " " + c.getString(3) + " " + c.getString(4);
                fullList.add(entry);
            }
        }
        filteredList.addAll(fullList);
        sortList();
        adapter = new ArrayAdapter<>(ManagingDB.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, filteredList);
        lv.setAdapter(adapter);
    }

    private void showUpdate(Activity ac, ListView lv, int p)
    {
        Dialog dialog=new Dialog(ac);
        dialog.setContentView(R.layout.update_db);
        dialog.setTitle("Update");

        final EditText name=(EditText)dialog.findViewById(R.id.editTextText);
        final EditText mail=(EditText)dialog.findViewById(R.id.editTextText3);
        final EditText phone=(EditText)dialog.findViewById(R.id.editTextText4);
        final RadioGroup radioGroupRole=(RadioGroup)dialog.findViewById(R.id.radioGroupRoleUpdate);
        Button bt=(Button)dialog.findViewById(R.id.button);

        final String[] chaine=lv.getAdapter().getItem(p).toString().split(" ");

        name.setText(chaine[1]);
        mail.setText(chaine[2]);
        phone.setText(chaine[3]);

        // Pre-select current role
        if (chaine.length > 4) {
            String currentRole = chaine[4];
            if (currentRole.equals("Manager")) {
                radioGroupRole.check(R.id.radioManagerUpdate);
            } else if (currentRole.equals("Développeur")) {
                radioGroupRole.check(R.id.radioDevUpdate);
            } else if (currentRole.equals("Stagiaire")) {
                radioGroupRole.check(R.id.radioStagiaireUpdate);
            }
        }

        int width=(int)(ac.getResources().getDisplayMetrics().widthPixels*0.9);
        int height=(int)(ac.getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=Integer.parseInt(chaine[0]);
                int selectedId = radioGroupRole.getCheckedRadioButtonId();
                RadioButton selectedRadio = dialog.findViewById(selectedId);
                String role = selectedRadio.getText().toString();
                dataBase.update(name.getText().toString(), mail.getText().toString(), phone.getText().toString(), role, i);
                Toast.makeText(ManagingDB.this, "Mise à jour avec succès", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(ManagingDB.this, ManagingDB.class);
                startActivity(intent);
                viewData();
            }
        });
    }

    private void delete(ListView lv, int p)
    {
        String[] chaine=lv.getAdapter().getItem(p).toString().split(" ");
        int i=Integer.parseInt(chaine[0]);
        dataBase.delete(i);
        Toast.makeText(this, "Suppression avec succès", Toast.LENGTH_SHORT).show();
        viewData();
    }

}