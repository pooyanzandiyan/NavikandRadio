package com.kdz.navikandradio;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.kdz.item.ItemCat;
import com.kdz.utils.JsonUtils;
import com.kdz.utils.ZProgressHUD;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FragmentMag extends Fragment {
    PDFView pdfView;
    ArrayList<ItemCat> arrayList;
    ZProgressHUD progressHUD;
    GridLayoutManager gridLayoutManager;
    String pdf_url;

    @SuppressLint("ValidFragment")
    public FragmentMag(String pdf_url) {
        this.pdf_url = pdf_url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mag, container, false);

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getActivity().getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);


        arrayList = new ArrayList<>();
        pdfView = rootView.findViewById(R.id.pdfView);
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new RetrievePDFStream().execute(this.pdf_url);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
        }
        setHasOptionsMenu(true);
        return rootView;
    }

    class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
            progressHUD.dismissWithFailure(getResources().getString(R.string.success));
        }

    }


}
