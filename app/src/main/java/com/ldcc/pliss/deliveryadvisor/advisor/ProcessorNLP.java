package com.ldcc.pliss.deliveryadvisor.advisor;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        return requestKeywordExtraction(sentence);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    //Twitter-Korean-text 에 키워드 추출 요청
    private String requestKeywordExtraction(String sentence){
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL("https://open-korean-text.herokuapp.com/tokenize?text=" + sentence);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();
            httpCon.setRequestMethod(REQUEST_METHOD);
            httpCon.setReadTimeout(READ_TIMEOUT);
            httpCon.setConnectTimeout(CONNECTION_TIMEOUT);
            httpCon.connect();

            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "FAIL";
            }
            catch (IOException e) {
                result = "FAIL";
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            result ="FAIL";
        }
        catch (Exception e) {
            result="FAIL";
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) result += line;

        inputStream.close();
        return result;
    }
}
