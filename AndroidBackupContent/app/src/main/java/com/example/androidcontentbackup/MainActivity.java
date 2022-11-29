package com.example.androidcontentbackup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private Toolbar toolbar;
    private ArrayList<Fragment> fragmentsListesi = new ArrayList<>();
    private ArrayList<String> fragmentBaşlıkList = new ArrayList<>();
    private int izinKontrol;
    private android.app.AlertDialog.Builder builderDialog;
    private AlertDialog alertDialog;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerLauncher();
        tabLayout = findViewById(R.id.tablayout);
        viewPager2 = findViewById(R.id.viewpager2);
        toolbar = findViewById(R.id.toolbar);

        fragmentsListesi.add(new fragment_birinci()); //fragmentleri liste ye ekledik
        fragmentsListesi.add(new fragment_ikinci());


        registerLauncher();


        Toolbar();

        // tablayout la viewpager2 yi beraber çaliştirma birleştirme ve başlık oluşturma
        /*
        * başlıkları bir liste olarak oluşturup yapıya aktarıyoruz
        * TabLoyout la ViewPager2 yi beraber calıştır ve başlıklar lıstedekiler olsun
        */
        fragmentBaşlıkList.add("BACKUP");
        fragmentBaşlıkList.add("RESTORE");
      go();
    }

    public void uygulama_git(){
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(this); //MyViewPagerAdapter bu sınıftan bir nesne oluşturduk
        viewPager2.setAdapter(adapter); // adapterı viewpager2 ye aktardık
        new TabLayoutMediator(tabLayout,viewPager2,((tab, position) -> tab.setText(fragmentBaşlıkList.get(position)))).attach();

    }

    public void go(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)){
                //sebep gostermek zorundaysak sebebp belirterek izin istiğicez
                Snackbar.make(viewPager2,"uygulamanın çalışabilmesi için izine ihtiyaç duyar",Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS);
                    }
                }).show();

            }else {
              //  sebep göstermek zorunda değil isek izni direk istiğicez
                permissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            }

        }else {
            //izin verildi
            uygulama_git();
        }
    }
public void registerLauncher(){
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                        //izinm verildi
                    uygulama_git();
                }else {
                    Toast.makeText(getApplicationContext(),"izin verilmedi",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class MyViewPagerAdapter extends FragmentStateAdapter {

        public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) { // adapterı kullanmak ıcın
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) { // adapterin göruntuleyecegi fragmentleri belirtdiğimiz yer
            return fragmentsListesi.get(position);
        }

        @Override
        public int getItemCount() { // kactane fragment organıze edecek belirtmemiz gerekli

            return fragmentsListesi.size();
        }
    }
    public void Toolbar(){ //toolbar dizayn
        toolbar.setTitle("   Rehberinizi Yedekleyin");
        toolbar.setLogo(R.drawable.ic_baseline_settings_backup_restore_24);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);



    }

}