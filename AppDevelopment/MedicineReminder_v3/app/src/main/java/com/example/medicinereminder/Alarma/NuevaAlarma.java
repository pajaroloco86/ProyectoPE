package com.example.medicinereminder.Alarma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.medicinereminder.DataBase.AdminSQLiteOpenHelper;
import com.example.medicinereminder.MainActivity;
import com.example.medicinereminder.Notificaciones.AlertReceiver;
import com.example.medicinereminder.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class NuevaAlarma extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    EditText nombreMedicina, horasMedicina, minutosMedicina, notasMedicina;
    TextView mostrarFecha, mostrarHora, mostrarFechaFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nueva_alarma);

        nombreMedicina = (EditText)findViewById(R.id.nombreMedicina);
        horasMedicina = (EditText)findViewById(R.id.horasMedicina);
        minutosMedicina = (EditText)findViewById(R.id.minutosMedicina);
        notasMedicina = (EditText)findViewById(R.id.notasMedicina);
        mostrarFecha = (EditText) findViewById(R.id.fechaSeleccionada);
        mostrarHora = (EditText) findViewById(R.id.horaSeleccionada);
        mostrarFechaFinal = (EditText)findViewById(R.id.fechaFinalSeleccionada);

        //boton para abrir el date picker y seleccionar la fecha
        Button btDate = (Button) findViewById(R.id.seleccionarFecha);
        btDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        //boton para abrir el time picker y seleccionar la hora
        Button btTime = (Button) findViewById(R.id.seleccionarHora);
        btTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        //boton para abrir el date picker y seleccionar la fecha en la que se deberá auto desactivar la alarma
        Button btFinalDate = (Button) findViewById(R.id.seleccionarFechaFinal);
        btFinalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment finalDatePicker = new DatePickerFragment();
                finalDatePicker.show(getSupportFragmentManager(), "final date picker");
            }
        });

    }

    //Funcion para agregar una medicina
    public void agregarDatos(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase baseDatos = admin.getWritableDatabase(); //Abre la base de datos en modo escritura

        //Obtenemos los valores de los campos correspondientes
        String nombre = nombreMedicina.getText().toString();
        String hrs = horasMedicina.getText().toString();
        String min = minutosMedicina.getText().toString();
        String notas = notasMedicina.getText().toString();
        String fecha = mostrarFecha.getText().toString();
        String hora = mostrarHora.getText().toString();
        String fechaFinal = mostrarFechaFinal.getText().toString();

        //Verificamos que todos los campos estan llenados
        if(!nombre.isEmpty() && !hrs.isEmpty() && !min.isEmpty()){
            //Convertimos los datos para coincidir con los de la base de datos
            int posicion = 1; //Inicializado en 1 por conveniencia en la BD
            int horas = Integer.parseInt(hrs);
            int minutos = Integer.parseInt(min);

            //Obtenemos la ultima posicion de la base de datos
            Cursor fila = admin.obtenerPosicionesAlarmas();
            //Verificamos si existen mas datos en la BD
            if(fila != null){
                //Nos posicionamos a la ultima posicion
                if(fila.moveToLast()){
                    posicion = fila.getInt(0)+1;
                }
            }

            //Objeto que almacena los datos anteriores
            ContentValues agregar = new ContentValues();
            agregar.put("posicion",posicion);
            agregar.put("activar", 1);
            agregar.put("nombre", nombre);
            agregar.put("periodoHoras", horas);
            agregar.put("periodoMinutos", minutos);
            agregar.put("notas", notas);
            agregar.put("fecha", fecha);
            agregar.put("hora", hora);
            agregar.put("fechaFinal", fechaFinal);

            //Agregamos y cerramos la base de datos
            baseDatos.insert("datos", null, agregar);
            baseDatos.close();

            //Limpiamos los campos y avisamos al usuario del registro exitoso
            limpiado();

            //Iniciamos el funcionamiento de la alarma
            startAlarm();

            Toast.makeText(this, "Medicina almacenada", Toast.LENGTH_SHORT).show();
        }else{
            //Si los campos estan vacios se le avisa al usuario
            Toast.makeText(this, "Debes llenar todos los datos", Toast.LENGTH_SHORT).show();
        }
    }

    //Funcion que limpia los campos
    public void limpiado(){
        nombreMedicina.setText("");
        horasMedicina.setText("");
        minutosMedicina.setText("");
        notasMedicina.setText("");
        mostrarFecha.setText("");
        mostrarHora.setText("");
        mostrarFechaFinal.setText("");
    }

    //Funcion del date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();

        //Obtenemos el dia, mes, año seleccionado por el usuario al abrir el date picker
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);

        //Revisamos en cual Text View se debe de mostrar la fecha seleccionada
        if(mostrarFecha.getText().toString().isEmpty()){
            updateDateText(c);
        }else{
            updateFinalDateText(c);
        }
    }

    //Funcion para colocar la fecha seleccionada en la fecha de inicio de la alarma
    private void updateDateText(Calendar c) {
        String dateText = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        mostrarFecha.setText(dateText);
    }
    //Funcion para colocar la fecha seleccionada en la fecha final de la alarma
    private void updateFinalDateText(Calendar c){
        String dateText = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        mostrarFechaFinal.setText(dateText);
    }

    //Funcion del time picker
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();

        //Obtenemos las horas y minutos seleccionados por el usuario en el time picker
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        //Colocamos la hora en su respectivo Text View
        updateTimeText(c);
    }
    //Funcion para colocar la hora seleccionada como la hora de inicio de la alarma
    private void updateTimeText(Calendar c) {
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        mostrarHora.setText(timeText);
    }

    //Funcion que permite ingresar al alert receiver cada 10 segundos para verificar si se debe de activar alguna alarma
    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),10*1000, pendingIntent);
    }

    //Funcion para regresar al Main Activity
    public void regresar(View view){
        Intent regresar = new Intent(this, MainActivity.class);
        startActivity(regresar);
    }

}

