package com.example.androidcontentbackup;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ezvcard.VCard;
import in.shadowfax.proswipebutton.ProSwipeButton;
import kotlin.jvm.JvmPackageName;

public class fragment_birinci extends Fragment {

    private  int artanDeger = 0;
    private Handler mHandler = new Handler();
    private Handler xHandler = new Handler();
    private ArrayList<String> list = new ArrayList<>();
    private TextView artanContacts;
    private TextView degisken_text1;
    private TextView degisken_text2;
    private TextView back;
    private Button BackupButton;
    private Button PaylasButton;
    private Button SaveButton;
    private ProgressBar progressBar;
    private Cursor cursor;
    public ArrayList<String> vCard =new ArrayList<>();
    private TextView timeDataText;
    private Date simdiki_zaman = new Date();
    private int i = 0;
    private AlertDialog.Builder builderDialog;
    private AlertDialog alertDialog;
    public String gelenVeri;
    public File konum;
    private ActivityResultLauncher<String> permissionLauncher;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_birinci_layout,container,false);



        //G??r??n??mlerin ca??r??lmas??
        artanContacts = rootView.findViewById(R.id.artanContacts);
        timeDataText = rootView.findViewById(R.id.timeDataText);
        degisken_text1=rootView.findViewById(R.id.text1);
        degisken_text2=rootView.findViewById(R.id.text2);
        back = rootView.findViewById(R.id.back);
        BackupButton = rootView.findViewById(R.id.backup_button);
        PaylasButton = rootView.findViewById(R.id.paylas_button);
        SaveButton = rootView.findViewById(R.id.save_button);
        progressBar = rootView.findViewById(R.id.progressBar);



        //g??r??n??rl??k ayar??
        PaylasButton.setVisibility(View.INVISIBLE);
        SaveButton.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);


        contactsList();
        registerLauncher();




        new Thread(new Runnable() { //rehber ki??i sayisini ekranda okutmak ??c??n thared kulan??yoruz liste direkde yaz??labilir ancak say?? artar gibi artd??ramad??m
            @Override
            public void run() {
                while (artanDeger  <list.size()){ // artan de??er liste eleman say??s??na ula????ncaya kadar arts??n
                    artanDeger++;
                    android.os.SystemClock.sleep(10); //art???? h??z??
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(artanDeger);//progres bar artan deger artd??kca ilerlesin
                            artanContacts.setText(String.valueOf(artanDeger)); //artan de??er artd??kca txt ye bas??yoruz
                            progressBar.setMax(list.size());//progres bar liste boyu kadar ilerlesin

                        }
                    });
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);//i??lem bitince progres bar kaybolsun
                       // artanContacts.setText(list.size()); // artan de??er liste say??s??na e??it oldugunda ekrana bas??lacak
                    }
                });
            }
        }).start();

        BackupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                degisken_text1.setText("Toplam");
                degisken_text2.setText("Ki??i Haz??r");
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                    while (i<list.size()){
                        i++;
                        android.os.SystemClock.sleep(10);
                        xHandler.post(new Runnable() {
                            @Override
                            public void run() {
                            artanContacts.setText(String.valueOf(i));
                            }
                        });
                    }
                    xHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            BackupButton.setVisibility(View.INVISIBLE);
                            PaylasButton.setVisibility(View.VISIBLE);
                            SaveButton.setVisibility(View.VISIBLE);
                            back.setVisibility(View.VISIBLE);
                        }
                    });
                 }
             }).start();
            }
        });

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeDataText.setText(dateFormat.format(simdiki_zaman));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackupButton.setVisibility(View.VISIBLE);
                PaylasButton.setVisibility(View.INVISIBLE);
                SaveButton.setVisibility(View.INVISIBLE);
                degisken_text1.setText("Rehberinizde");
                degisken_text2.setText("Ki??i Bulunmaktad??r");
            }
        });


        SaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        //sebep gostermek zorundaysak sebebp belirterek izin isti??icez
                        Snackbar.make(rootView,"dosyan??z??n yedeklenebilmesi i??in gerekli izinlere ihtiya?? duyar",Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);



                            }
                        }).show();

                    }else {
                        //  sebep g??stermek zorunda de??il isek izni direk isti??icez

                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        permissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                    }

                }else {
                    //izin verildi
                    FileSaveVCF();
                }

            }
        });


        return rootView;
    }


    public void registerLauncher(){
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //izinm verildi
                    FileSaveVCF();
                }else {
                    Toast.makeText(getActivity().getApplicationContext(),"izin verilmedi",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public void FileSaveVCF(){
        View tasarim = getLayoutInflater().inflate(R.layout.alert_tasarim,null);
        final EditText editTextAlert = tasarim.findViewById(R.id.editTextTextPersonName);
        AlertDialog.Builder ad= new AlertDialog.Builder(getActivity());
        ad.setTitle("Dosya ??smini Yaz??n");
        ad.setView(tasarim);
        ad.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()){
                        gelenVeri = editTextAlert.getText().toString();
                        try {

                            konum= new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.MY_FOLDER_NAME);
                            if (!konum.exists()) {
                                konum.mkdirs();
                            }

                            konum = new File(konum.getAbsolutePath(), gelenVeri + ".vcf");
                            if (!konum.exists()){
                                konum.createNewFile();
                            }

                            FileWriter fileWriter = new FileWriter(konum);
                            BufferedWriter fileYaz  = new BufferedWriter(fileWriter);

                            cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                            if(cursor!=null&&cursor.getCount()>0)
                            {
                                cursor.moveToFirst();
                                for(int x = 0;x<cursor.getCount();x++)
                                {
                                    get(cursor);



                                    fileYaz.write(vCard.get(x));
                                    cursor.moveToNext();

                                }
                                showAlertDialog(R.layout.my_succses_dialog);

                            } else
                            {
                                showAlertDialog(R.layout.my_failed_dialog);
                            }
                            fileYaz.flush();
                            fileYaz.close();
                            fileWriter.close();

                        }catch (IOException e) {
                            e.printStackTrace();
                        }

                            } else{
                                // do something for phones running an SDK before lollipop
                        View rootImplament = getLayoutInflater().inflate(R.layout.paylas_import,null);
                        final Button izin_git = rootImplament.findViewById(R.id.izin_sorgu);
                        AlertDialog.Builder alerdBuilder = new AlertDialog.Builder(getActivity());
                        alerdBuilder.setView(rootImplament);
                        izin_git.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Do something for lollipop and above versions
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                        alerdBuilder.create().show();
                    }
                }

            }
        });
        ad.setNegativeButton("IPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(getActivity().getApplicationContext(),"????LEM ??PTAL ED??LD??",Toast.LENGTH_LONG).show();
            }
        });
        ad.create().show();

    }



    private void showAlertDialog (int myLayout){//dialog ayarlamalar??

        builderDialog = new AlertDialog.Builder(getActivity());
        View layoutView= getLayoutInflater().inflate(myLayout,null);

        AppCompatButton appCompatButton = layoutView.findViewById(R.id.button_ok);
        builderDialog.setView(layoutView);
        alertDialog=builderDialog.create();
        alertDialog.show();

        //ok butonuna bas??ld??????nda
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();//dialog kapatma
            }
        });
    }
    public void contactsList(){
/*
* ContactsContract, ilgili ki??iyle ilgili bilgilerin geni??letilebilir bir veritaban??n?? tan??mlar. ??leti??im bilgileri ???? katmanl?? bir veri modelinde depolan??r:
* Tablodaki bir sat??r, telefon numaras?? veya e-posta adresleri gibi her t??rl?? ki??isel veriyi depolayabilir.
  Bu tabloda depolanabilecek veri t??rleri k??mesi a????k u??ludur. ??nceden tan??mlanm???? bir ortak t??r k??mesi vard??r, ancak herhangi bir uygulama kendi veri t??rlerini ekleyebilir. Data
* Tablodaki bir sat??r, bir ki??iyi tan??mlayan ve tek bir hesapla (??rne??in, kullan??c??n??n Gmail hesaplar??ndan biri) ili??kili bir veri k??mesini temsil eder. RawContacts
* Tablodaki bir sat??r, muhtemelen ayn?? ki??iyi tan??mlayan bir veya daha fazla RawContacts toplam??n?? temsil eder.
  RawContacts tablosundaki veya bu tabloyla ili??kili veriler de??i??tirildi??inde, etkilenen toplu ilgili ki??iler gerekti??i gibi g??ncelle??tirilir. Contacts
* */
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;


        ContentResolver contentResolver = getActivity().getContentResolver(); //Bu s??n??f, uygulamalar??n i??erik modeline eri??mesini sa??lar.
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null,null,null);
        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String content_id = cursor.getString(cursor.getColumnIndex(_ID));
                list.add(content_id);
            }
        }
    }
    public void get(Cursor cursor) throws IOException {

        @SuppressLint("Range") String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri vCardUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor assetFileDescriptor;
        String vcardstring= "";
        if (Build.VERSION.SDK_INT >= 24) {

            FileInputStream inputStream=null;
            byte[] buffer = new byte[4096];
            ByteArrayOutputStream ous = null;
            try {
                assetFileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(vCardUri, "r");

                if (assetFileDescriptor != null) {
                    ous = new ByteArrayOutputStream();
                    int read = 0;
                    inputStream = assetFileDescriptor.createInputStream();
                    while ((read = inputStream.read(buffer)) != -1) {
                        ous.write(buffer, 0, read);
                    }
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Vcard for the contact " + lookupKey + " not found", e);
            } catch (IOException e) {
                Log.e(TAG, "Problem creating stream from the assetFileDescriptor.", e);
            }finally {
                try {
                    if (ous != null)
                        ous.close();
                } catch (IOException e) {
                }

                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                }
            }
            vcardstring= new String(ous.toByteArray());

        }else{

            assetFileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(vCardUri, "r");

            FileInputStream fis = assetFileDescriptor.createInputStream();
            byte[] buf = new byte[(int) assetFileDescriptor.getDeclaredLength()];
            fis.read(buf);
            vcardstring= new String(buf);

        }
        vCard.add(vcardstring);


   }

}
