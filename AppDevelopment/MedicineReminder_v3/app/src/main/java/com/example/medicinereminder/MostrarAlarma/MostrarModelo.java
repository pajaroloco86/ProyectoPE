package com.example.medicinereminder.MostrarAlarma;

public class MostrarModelo {
    String nombre, notas;

    public MostrarModelo() {
    }

    //Funcion constructora del modelo
    public MostrarModelo(String nombre, String notas) {
        this.nombre = nombre;
        this.notas = notas;
    }

    //Funcion para obtener el nombre de cada alarma que se mostrara en la lista
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    //Funcion para obtener las notas de cada alarma que se mostrara en la lista
    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

}
