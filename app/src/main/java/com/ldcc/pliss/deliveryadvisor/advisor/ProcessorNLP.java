package com.ldcc.pliss.deliveryadvisor.advisor;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;



//import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
//import org.openkoreantext.processor.phrase_extractor.KoreanPhraseExtractor;
//import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
//
//import java.util.List;
//
//import scala.collection.Seq;

/**
 * Created by pliss on 2018. 3. 19..
 */

public class ProcessorNLP extends AsyncTask<String, Void, String> {
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String sentence = params[0];
        return sendOpenResultPOST(sentence);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }


    public String sendOpenResultPOST(String sentence){
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL("https://open-korean-text.herokuapp.com/tokenize?text=" + sentence);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // Set some headers to inform server about the type of the content
            httpCon.setRequestMethod(REQUEST_METHOD);
            httpCon.setReadTimeout(READ_TIMEOUT);
            httpCon.setConnectTimeout(CONNECTION_TIMEOUT);

            httpCon.connect();

            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null){
                    Log.d("오픈성공","오픈성공");
                    result = convertInputStreamToString(is);
                }
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        Log.d("result","result");
        inputStream.close();
        return result;

    }


}
