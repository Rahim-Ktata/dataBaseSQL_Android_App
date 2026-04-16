package com.example.databasesql;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button b1,b2;
    private EditText name,mail,phone;
    DataBase dbb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        b1=(Button)findViewById(R.id.button5);
        b2=(Button)findViewById(R.id.button4);
        name=(EditText) findViewById(R.id.editTextText2);
        mail=(EditText) findViewById(R.id.editTextText5);
        phone=(EditText) findViewById(R.id.editTextText6);
        dbb=new DataBase(this);


        b1.setOnClickListener(v -> {
            if (!name.getText().toString().equalsIgnoreCase("") &&
                    !mail.getText().toString().equalsIgnoreCase("") &&
                    !phone.getText().toString().equalsIgnoreCase(""))
            {
                Boolean insereted=dbb.insertData(name.getText().toString(), mail.getText().toString(), phone.getText().toString());
                if (insereted)
                    Toast.makeText(MainActivity.this, "Insertion avec succès", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Echec d'insertion", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int1=new Intent(MainActivity.this, ManagingDB.class);
                startActivity(int1);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



}