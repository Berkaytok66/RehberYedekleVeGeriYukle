package com.example.androidcontentbackup;


import static ezvcard.util.IOUtils.closeQuietly;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.android.AndroidCustomFieldScribe;
import ezvcard.android.ContactOperations;
import ezvcard.io.text.VCardReader;
import ezvcard.property.Telephone;

public class fragment_ikinci extends Fragment  {
    private RecyclerView recyclerView;
    private VcfRecyclerAdapter vcfRecyclerAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_iki_layout,container,false);



        recyclerView = rootView.findViewById(R.id.recyclerViev);


        vcfRecyclerAdapter = new VcfRecyclerAdapter(getActivity(),getDAta());
        viewSettings();
        vcfRecyclerAdapter.notifyDataSetChanged();



        return rootView;
    }
    private ArrayList<VcfFile> getDAta(){
        File konum= new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.MY_FOLDER_NAME);
        ArrayList<VcfFile> vcfFiles = new ArrayList<>();
        VcfFile s;
        if (konum.exists()){
            File[] files = konum.listFiles();
            for (int i =0 ; i<files.length;i++){
                File file = files[i];
                s = new VcfFile();
                s.setVcfFileName(file.getName());
                s.setLogo(R.drawable.file_76);
                s.setUri(Uri.fromFile(konum));
                vcfFiles.add(s);

            }
        }
        return vcfFiles;
    }
    private void viewSettings(){
        recyclerView.setAdapter(vcfRecyclerAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

}
