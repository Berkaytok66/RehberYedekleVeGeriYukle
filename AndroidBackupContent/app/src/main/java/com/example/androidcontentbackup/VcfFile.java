package com.example.androidcontentbackup;

import android.net.Uri;

public class VcfFile {
    private int logo;
    private String vcfFileName;
    private Uri uri;


    public VcfFile() {
        this.logo = logo;
        this.vcfFileName = vcfFileName;
        this.uri = uri;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getVcfFileName() {
        return vcfFileName;
    }

    public void setVcfFileName(String vcfFileName) {
        this.vcfFileName = vcfFileName;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
