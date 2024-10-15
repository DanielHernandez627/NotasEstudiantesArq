package com.arq2.notasestudiantes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tlNombre;
    private TextView tlMateria;
    private TextView tlFecha;
    private TextView tlNota1;
    private TextView tlNota2;
    private TextView tlNota3;
    private TextView tlDef;
    private Button btnAnterior;
    private Button btnSiguiente;

    private List<Estudiante> estudiantes;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Referencia a vistas
        tlNombre = findViewById(R.id.tlNombre);
        tlMateria = findViewById(R.id.tlMateria);
        tlFecha = findViewById(R.id.tlFecha);
        tlNota1 = findViewById(R.id.n1);
        tlNota2 = findViewById(R.id.n2);
        tlNota3 = findViewById(R.id.n3);
        tlDef = findViewById(R.id.n4);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        reiniciarSharedPreferences();

        //Creacion de estudiantes
        generarEstudiantes();

        //Carga de estudiantes
        estudiantes = cargarEstudiantes();

        // Mostrar el primer estudiante si la lista no está vacía
        if (!estudiantes.isEmpty()) {
            mostrarEstudiante(currentIndex);
        }

        setUp();
    }

    /**
     * Metodo para el manejo de botones
     */
    public void setUp(){
       btnAnterior.setOnClickListener(v -> {
           if (currentIndex > 0) {
               currentIndex--;
               mostrarEstudiante(currentIndex);
           }
       });

       btnSiguiente.setOnClickListener(v -> {
           if (currentIndex < estudiantes.size() - 1) {
               currentIndex++;
               mostrarEstudiante(currentIndex);
           }
       });
    }


    /**
     * Metodo para la generacion de estudiantes por defecto
     */
    private void generarEstudiantes() {
        SharedPreferences sharedPreferences = getSharedPreferences("DatosEstudiantes", Context.MODE_PRIVATE);
        int totalEstudiantes = sharedPreferences.getInt("totalEstudiantes", 0);

        // Si no hay estudiantes guardados, agregar
        if (totalEstudiantes == 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Definir algunos estudiantes de ejemplo
            List<Estudiante> estudiantesEjemplo = new ArrayList<>();
            estudiantesEjemplo.add(new Estudiante("Juan Pérez", "Matemáticas", 3.5f, 4.0f, 3.8f));
            estudiantesEjemplo.add(new Estudiante("María López", "Química", 2.1f, 3.5f, 3.0f));
            estudiantesEjemplo.add(new Estudiante("Carlos García", "Física", 3.8f, 4.0f, 3.7f));
            estudiantesEjemplo.add(new Estudiante("Daniel Hernandez", "Arquitectura 2", 4.8f, 5.0f, 5.0f));

            // Guardar cada estudiante en SharedPreferences
            editor.putInt("totalEstudiantes", estudiantesEjemplo.size());
            for (int i = 0; i < estudiantesEjemplo.size(); i++) {
                Estudiante estudiante = estudiantesEjemplo.get(i);
                editor.putString("nombre_" + i, estudiante.getNombre());
                editor.putString("asignatura_" + i, estudiante.getAsignatura());
                editor.putFloat("nota1_" + i, estudiante.getNota1());
                editor.putFloat("nota2_" + i, estudiante.getNota2());
                editor.putFloat("nota3_" + i, estudiante.getNota3());
            }

            // Aplicar los cambios
            editor.apply();
        }
    }

    /**
     * Metodo para cargar estudiantes desde SharedPreferences
     */
    private List<Estudiante> cargarEstudiantes() {
        List<Estudiante> listaEstudiantes = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("DatosEstudiantes", Context.MODE_PRIVATE);
        int totalEstudiantes = sharedPreferences.getInt("totalEstudiantes", 0);

        for (int i = 0; i < totalEstudiantes; i++) {
            String nombre = sharedPreferences.getString("nombre_" + i, "");
            String asignatura = sharedPreferences.getString("asignatura_" + i, "");
            float nota1 = sharedPreferences.getFloat("nota1_" + i, 0);
            float nota2 = sharedPreferences.getFloat("nota2_" + i, 0);
            float nota3 = sharedPreferences.getFloat("nota3_" + i, 0);
            listaEstudiantes.add(new Estudiante(nombre, asignatura, nota1, nota2, nota3));
        }

        return listaEstudiantes;
    }

    /**
     * Metodo para calcular la nota final
     */
    public double calcularNotaDefinitiva(double corte1, double corte2, double corte3) {
        // Definición de los porcentajes de cada corte
        double pesoCorte1 = 0.30;
        double pesoCorte2 = 0.30;
        double pesoCorte3 = 0.40;

        // Cálculo de la nota definitiva basada en los pesos y las notas de los cortes
        double notaDefinitiva = (corte1 * pesoCorte1) + (corte2 * pesoCorte2) + (corte3 * pesoCorte3);

        // Formateo de la nota definitiva a 2 decimales
        DecimalFormat df = new DecimalFormat("#.##");

        return Double.parseDouble(df.format(notaDefinitiva));
    }

    /**
     * Metodo para mostrar el estudiante en las vistas
     */
    private void mostrarEstudiante(int index) {
        Date date = new Date();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormateada = formatoFecha.format(date);
        double def = 0;

        Estudiante estudiante = estudiantes.get(index);
        tlNombre.setText("Estudiante: " + estudiante.getNombre());
        tlMateria.setText("Asignatura: " + estudiante.getAsignatura());
        tlFecha.setText("Fecha: " + fechaFormateada);
        tlNota1.setText(String.valueOf(estudiante.getNota1()));
        tlNota2.setText(String.valueOf(estudiante.getNota2()));
        tlNota3.setText(String.valueOf(estudiante.getNota3()));

        def = calcularNotaDefinitiva(estudiante.getNota1(), estudiante.getNota2(), estudiante.getNota3());

        if (def < 3){
            tlDef.setText(def + " Reprobó");
        }else{
            tlDef.setText(def + " Aprobó");
        }
    }

    /**
     * Metodo de reinicio del SharedPreference para verificacion de cambio de notas
     * */
    private void reiniciarSharedPreferences() {
        // Obtener la instancia de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("DatosEstudiantes", MODE_PRIVATE);

        // Crear un editor para realizar modificaciones
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Limpiar todos los datos del archivo de SharedPreferences
        editor.clear();

        // Aplicar los cambios
        editor.apply();
    }

}