package com.ivan.procampo.fragmentsMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ivan.procampo.R;
import com.ivan.procampo.adaptadores.CultivoAdapter;
import com.ivan.procampo.funcionalidades.ActualizarCultivoActivity;
import com.ivan.procampo.funcionalidades.AnnadirCultivoActivity;
import com.ivan.procampo.funcionalidades.EliminarCultivo;
import com.ivan.procampo.modelos.Cultivos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CultivosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CultivosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // MIS VARIABLES
    private Button botonNuevoCultivo;

    RecyclerView recyclerViewCultivos;

    private ArrayList<Cultivos> listaCultivos = new ArrayList<>();

    private BottomNavigationView bottomNavigationView;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private CultivoAdapter adapter;

    int code = 0;

    //private SwipeRefreshLayout swipeContainer;

    public CultivosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CultivosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CultivosFragment newInstance(String param1, String param2) {
        CultivosFragment fragment = new CultivosFragment();


        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_cultivos, container, false);

        //Declaramos las variables para autenticación y BBDD
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Referencia a las variables
        botonNuevoCultivo = vista.findViewById(R.id.botonAnnadirCultivo);
        recyclerViewCultivos = vista.findViewById(R.id.recyclerViewCultivos);

        ///swipeContainer = vista.findViewById(R.id.srlContainer);

        //Lanzamos el LayoutManager
        recyclerViewCultivos.setLayoutManager(new LinearLayoutManager(getActivity()));



        //Pasamos el parametro
        registerForContextMenu(recyclerViewCultivos);


/*
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listaCultivos.clear();

                llenarLista();

                swipeContainer.setRefreshing(false);

                swipeContainer.setEnabled(false);
            }
        });
*/
        //swipeContainer.setEnabled(true);




        //Reemplazar el fragment para añadir

        botonNuevoCultivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Vamos a la activity de añadir cultivo
                listaCultivos.clear();
                Intent nuevoCultivo = new Intent(getActivity(), AnnadirCultivoActivity.class);
                startActivity(nuevoCultivo);

            }
        });


        // Inflate the layout for this fragment
        return vista ;

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        if (listaCultivos.size()==0){
            llenarLista();
        }else if (listaCultivos.size()!=0){
            listaCultivos.clear();
            llenarLista();
        }

    }

    /**
     * Metodo creado para coger los datos de Firebase
     *
     */
    public  void llenarLista() {
        mDatabase.child("CULTIVOS").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String codigoCultivo = ds.child("codigoCultivo").getValue().toString();
                        String nombreCultivo = ds.child("nombreCultivo").getValue().toString();
                        String hectareasCultivo = ds.child("hectareasCultivo").getValue().toString();
                        String tipoAceituna = ds.child("tipoDeAceituna").getValue().toString();
                        String localizacionCultivo = ds.child("localizacionCultivo").getValue().toString();

                        Log.i("CULTIVO:", String.valueOf(listaCultivos));



                        listaCultivos.add(new Cultivos(codigoCultivo,nombreCultivo,localizacionCultivo,hectareasCultivo,tipoAceituna));

                    }

                    adapter = new CultivoAdapter(listaCultivos,R.layout.cultivo_view);
                    recyclerViewCultivos.setAdapter(adapter);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_contextual_cultivos,menu);




    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //listaCultivos.clear();


        switch (item.getItemId()){

            //Editar cultivo
            case R.id.ctxModCultivo:



                Cultivos cultivo = listaCultivos.get(adapter.getIndex());


                //Vamos a la actividad, pasando los datos

                listaCultivos.clear();

                Intent irAEditarCultivo = new Intent(getActivity(), ActualizarCultivoActivity.class);

                irAEditarCultivo.putExtra("codigoCultivo",cultivo.getCodigoCultivo());
                irAEditarCultivo.putExtra("nombreCultivo",cultivo.getNombreCultivo());
                irAEditarCultivo.putExtra("hectareasCultivos",cultivo.getHectareasCultivo());
                irAEditarCultivo.putExtra("tipoDeAceituna",cultivo.getTipoDeAceituna());
                irAEditarCultivo.putExtra("localizacionCultivo",cultivo.getLocalizacionCultivo());
                //irAEditarCultivo.putExtra("lista",listaCultivos);

                //Toast.makeText(getActivity(),"Le mando: "+cultivo.getNombreCultivo(),Toast.LENGTH_LONG).show();
                startActivity(irAEditarCultivo);



                break;

            case R.id.ctxDelCultivo:
                Cultivos cultivoAdios = listaCultivos.get(adapter.getIndex());

                listaCultivos.clear();

                Intent irABorrarCultivo = new Intent(getActivity(), EliminarCultivo.class);

                irABorrarCultivo.putExtra("codigoCultivo",cultivoAdios.getCodigoCultivo());

                startActivity(irABorrarCultivo);



                break;


        }

        return super.onContextItemSelected(item);


    }



}
