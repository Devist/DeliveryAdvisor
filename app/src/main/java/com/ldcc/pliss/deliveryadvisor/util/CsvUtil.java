package com.ldcc.pliss.deliveryadvisor.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import com.ldcc.pliss.deliveryadvisor.R;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pliss on 2018. 2. 27..
 */

public class CsvUtil {

    public List<String[]> readCSV(Context context){
        List<String[]> data = new ArrayList<String[]>();

        try {
            AssetManager assets = context.getResources().getAssets();
            // UTF-8
            CSVReader reader = new CSVReader(new InputStreamReader(assets.open("shipping_invoice_sample.csv"),"UTF-8"));
            String[] s;
            while ((s = reader.readNext()) != null) { data.add(s); }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
