package com.example.androidcontentbackup;

import static ezvcard.util.IOUtils.closeQuietly;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.android.AndroidCustomFieldScribe;
import ezvcard.android.ContactOperations;
import ezvcard.io.text.VCardReader;
import ezvcard.property.Telephone;

public class VcfRecyclerAdapter extends RecyclerView.Adapter<VcfRecyclerAdapter.CardViewTasarimNesneleriniTutucu>{
    private Context mContext;
    private ArrayList<VcfFile> vcfDisardanGelenList;

    public VcfRecyclerAdapter(Context mContext, ArrayList<VcfFile> vcfDisardanGelenList) {
        this.mContext = mContext;
        this.vcfDisardanGelenList = vcfDisardanGelenList;
    }

    public class CardViewTasarimNesneleriniTutucu extends RecyclerView.ViewHolder{
        public TextView vcfFileTextViev;
        public CardView satirCardView;
        //private ImageView vcfFileImageViev;

        public CardViewTasarimNesneleriniTutucu(View view){
            super(view);
            vcfFileTextViev = view.findViewById(R.id.vcfFileTextViev);
            satirCardView = view.findViewById(R.id.satirCardView);
            // vcfFileImageViev = view.findViewById(R.id.vcfFileImageViev);
        }
    }


    @NonNull
    @Override
    public CardViewTasarimNesneleriniTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vcf_item_view,parent,false);

        return new CardViewTasarimNesneleriniTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewTasarimNesneleriniTutucu holder, int position) {

        final VcfFile vcffile = vcfDisardanGelenList.get(position);
        holder.vcfFileTextViev.setText(vcffile.getVcfFileName());
        holder.satirCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
            ad.setMessage(vcffile.getVcfFileName());
            ad.setTitle("Geri yükle");
            ad.setPositiveButton("GERİ YÜKLE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File konum =new File(vcffile.getUri().toString());
                    File vcardFile = new File(konum.getAbsolutePath(), vcffile.getVcfFileName());


                }
            });
            ad.setNegativeButton("PAYLAŞ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File konum =new File(vcffile.getUri().toString());
                    File vcardFile = new File(konum.getAbsolutePath(), vcffile.getVcfFileName());
                    Intent intent =new Intent(Intent.ACTION_SEND);
                    intent.setType(URLConnection.guessContentTypeFromName(vcardFile.getName()));
                    intent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://"+vcardFile.getAbsolutePath()));
                    mContext.startActivity(Intent.createChooser(intent,"dosya paylaş"));
                }
            });
            ad.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return vcfDisardanGelenList.size();
    }


}
