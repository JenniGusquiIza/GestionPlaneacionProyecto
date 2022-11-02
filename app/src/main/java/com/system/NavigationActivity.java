package com.system;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.system.databinding.ActivityNavigationBinding;
import com.system.models.Usuario;
import com.system.ui.utils.Utils;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationBinding binding;
    private  NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        navigationView = binding.navView;
        View viewHeader = navigationView.getHeaderView(0);
        TextView titulo= (TextView) viewHeader.findViewById(R.id.txtNavTitle);
        TextView subTitulo= (TextView) viewHeader.findViewById(R.id.txtNavSubTitle);
        titulo.setText(Utils.auth.getCodigo());
        subTitulo.setText(currentUser.getEmail());
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_proyecto,
                R.id.nav_proyectoAprobar,
                R.id.nav_trabajador,
                R.id.nav_cliente,
                R.id.nav_producto,
                R.id.nav_grupo,
                R.id.nav_fase,
                R.id.nav_usuario
        )
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        Segurity();
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    //region METODOS ADICIONAL
    private void Segurity(){
        Menu nav_Menu = navigationView.getMenu();
        Usuario usuario=Utils.auth;

        for (String rol:usuario.getRoles()){
            char cadena=rol.toUpperCase().charAt(0);
            if(cadena=='G' || cadena=='A'){
                nav_Menu.findItem(R.id.nav_menuProceso).setVisible(true);
                nav_Menu.findItem(R.id.nav_menuMantenimiento).setVisible(true);
                nav_Menu.findItem(R.id.nav_cliente).setVisible(true);
                nav_Menu.findItem(R.id.nav_producto).setVisible(true);
                nav_Menu.findItem(R.id.nav_fase).setVisible(true);
                nav_Menu.findItem(R.id.nav_grupo).setVisible(true);
                nav_Menu.findItem(R.id.nav_usuario).setVisible(true);
                nav_Menu.findItem(R.id.nav_trabajador).setVisible(true);
                nav_Menu.findItem(R.id.nav_proyecto).setVisible(true);
                nav_Menu.findItem(R.id.nav_proyectoAprobar).setVisible(true);
            }else if(cadena=='J'){
                nav_Menu.findItem(R.id.nav_proyecto).setVisible(true);
                nav_Menu.findItem(R.id.nav_menuMantenimiento).setVisible(true);
                nav_Menu.findItem(R.id.nav_cliente).setVisible(true);
                nav_Menu.findItem(R.id.nav_producto).setVisible(true);
                nav_Menu.findItem(R.id.nav_fase).setVisible(true);
                nav_Menu.findItem(R.id.nav_grupo).setVisible(true);
            }else if(cadena=='S'){
                nav_Menu.findItem(R.id.nav_proyecto).setVisible(true);
                nav_Menu.findItem(R.id.nav_proyectoAprobar).setVisible(true);
            }
        }
    }
    //endregion


}