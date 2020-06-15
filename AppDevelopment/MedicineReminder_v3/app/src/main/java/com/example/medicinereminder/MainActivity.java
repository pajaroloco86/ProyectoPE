package com.example.medicinereminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medicinereminder.Alarma.BorrarAlarma;
import com.example.medicinereminder.Alarma.ModificarAlarma;
import com.example.medicinereminder.Alarma.NuevaAlarma;
import com.example.medicinereminder.DataBase.AdminSQLiteOpenHelper;
import com.example.medicinereminder.MostrarAlarma.MostrarAdaptador;
import com.example.medicinereminder.MostrarAlarma.MostrarAlarmas;
import com.example.medicinereminder.MostrarAlarma.MostrarModelo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    EditText buscarAlarma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        buscarAlarma = (EditText) findViewById(R.id.buscarAlarma);

        //Recycler View para mostrar la lista de las alarmas activas
        RecyclerView recyclerViewAlarma = (RecyclerView) findViewById(R.id.recyclerAlarmas);
        recyclerViewAlarma.setLayoutManager(new LinearLayoutManager(this));

        MostrarAdaptador mostrarAdaptador = new MostrarAdaptador(mostrarAlarmas());
        recyclerViewAlarma.setAdapter(mostrarAdaptador);

    }

    public void buscarAlarma(View view) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);

        String nombre = buscarAlarma.getText().toString();

        Cursor alarmaBuscada = admin.obtenerAlarmaBuscada(nombre);

        int posicion = alarmaBuscada.getInt(0);

        if (!nombre.isEmpty()) {
            Bundle busqueda = new Bundle();
            busqueda.putInt("posicion", posicion);
            Intent buscar = new Intent(this, ModificarAlarma.class);
            buscar.putExtras(buscar);
            startActivity(buscar);

        } else {
            Toast.makeText(this, "Campo vacío", Toast.LENGTH_SHORT).show();
        }

    }

    //funcion para ir al activity de agregar alarma
    public void agregarAlarma(View view) {
        Intent agregar = new Intent(this, NuevaAlarma.class);
        startActivity(agregar);
    }

    //funcion para ir al activiy de modificar alarma
    public void modificarAlarma(View view) {
        Intent modificar = new Intent(this, ModificarAlarma.class);
        startActivity(modificar);
    }

    //funcion para ir al activity de eliminar alarma
    public void eliminarAlarma(View view) {
        Intent eliminar = new Intent(this, BorrarAlarma.class);
        startActivity(eliminar);
    }

    //funcion para ir al activity de mostrar todas las alarmas
    public void mostrarAlarma(View view) {
        Intent mostrar = new Intent(this, MostrarAlarmas.class);
        startActivity(mostrar);
    }

    //Funcion para obtener todas las alarmas que se van a mostrar en el recycler view
    public List<MostrarModelo> mostrarAlarmas() {
        //Abrimos la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase baseDatos = admin.getWritableDatabase();

        //Seleccionamos de la base de datos unicamente las alarmas activas y las mostramos
        Cursor lista = admin.obtenerAlarmasActivas();
        //Creamos una lista y alamcenamos en ella el nombre y notas de todas las alarmas
        List<MostrarModelo> mostrar = new ArrayList<>();
        if (lista.moveToFirst()) {
            do {
                mostrar.add(new MostrarModelo(lista.getString(2), lista.getString(5)));
            } while (lista.moveToNext());
        }
        baseDatos.close();
        //Regresamos la lista
        return mostrar;
    }
}
