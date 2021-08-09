package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import com.google.android.gms.ads.AdSize;
import com.example.myapplication2.CircleDisplay;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;

public class DataActivity extends AppCompatActivity{

    //private AdView mAdView;

    Switch auto_switch;
    SeekBar window_move;

    CircleDisplay tem_in;
    CircleDisplay tem_out;
    CircleDisplay hum_in;
    CircleDisplay hum_out;
    CircleDisplay dust_in;
    CircleDisplay dust_out;
    ImageView gas_img;
    ImageView rain_img;
    ImageButton wifi;
    ImageButton refresh;
    ImageView window_back;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //MobileAds.initialize(this, "ca-app-pub-3752767244634057~1510075998");

        //mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
       // mAdView.loadAd(adRequest);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        tem_in = (CircleDisplay)findViewById(R.id.tem_in);
        tem_out = (CircleDisplay)findViewById(R.id.tem_out);
        hum_in = (CircleDisplay)findViewById(R.id.hum_in);
        hum_out = (CircleDisplay)findViewById(R.id.hum_out);
        dust_in = (CircleDisplay)findViewById(R.id.dust_in);
        dust_out = (CircleDisplay)findViewById(R.id.dust_out);
        gas_img = (ImageView)findViewById(R.id.gas_image);
        rain_img = (ImageView)findViewById(R.id.rain_image);
        auto_switch = (Switch)findViewById(R.id.mode_switch);
        window_move = (SeekBar)findViewById(R.id.seekBar2);
        wifi = (ImageButton)findViewById(R.id.wifi_btn);
        refresh = (ImageButton)findViewById(R.id.refresh_btn);
        window_back = (ImageView)findViewById(R.id.imageView3);


        new JsonLoadingTask().execute();

        refresh.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "getData", Toast.LENGTH_SHORT).show();

                tem_in = (CircleDisplay)findViewById(R.id.tem_in);
                tem_out = (CircleDisplay)findViewById(R.id.tem_out);
                hum_in = (CircleDisplay)findViewById(R.id.hum_in);
                hum_out = (CircleDisplay)findViewById(R.id.hum_out);
                dust_in = (CircleDisplay)findViewById(R.id.dust_in);
                dust_out = (CircleDisplay)findViewById(R.id.dust_out);
                gas_img = (ImageView)findViewById(R.id.gas_image);
                rain_img = (ImageView)findViewById(R.id.rain_image);
                auto_switch = (Switch)findViewById(R.id.mode_switch);
                window_move = (SeekBar)findViewById(R.id.seekBar2);
                wifi = (ImageButton)findViewById(R.id.wifi_btn);
                refresh = (ImageButton)findViewById(R.id.refresh_btn);
                window_back = (ImageView)findViewById(R.id.imageView3);

                new JsonLoadingTask().execute();
            }
        });

        wifi.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), wifi_connect.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }

        });

        auto_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(auto_switch.isChecked()){
                    auto_switch.setText("자동모드");
                    window_move.setVisibility(View.INVISIBLE);
                    System.out.println("열림");
                    change ch = new change();
                    ch.execute("http://175.205.244.188/update_mode.php", "1", id);
                    window_back.setVisibility(View.INVISIBLE);
                    new JsonLoadingTask().execute();
                }
                else{
                    auto_switch.setText("수동모드");
                    window_move.setVisibility(View.VISIBLE);
                    System.out.println("닫힘");
                    change ch = new change();
                    ch.execute("http://175.205.244.188/update_mode.php", "0", id);
                    window_back.setVisibility(View.VISIBLE);
                    new JsonLoadingTask().execute();
                }

            }
        });

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar2);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println(seekBar.getProgress());
                int tmp = seekBar.getProgress() - 650;                                                    ////////////////////////////////////////////////////////////////////
                if(tmp < 0){
                    tmp = tmp * -1;
                }



                String value = Integer.toString(tmp);
                seek_change sc = new seek_change();
                sc.execute("http://175.205.244.188/update_window.php", value, id);
            }
        });

    }

    private class seek_change extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DataActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strs){
            String conn_url = (String)strs[0];
            String send_data = "value=" + (String)strs[1] + "&id=" + (String)strs[2];

            try{
                URL url = new URL(conn_url);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                httpConn.setReadTimeout(5000);
                httpConn.setConnectTimeout(5000);

                httpConn.setRequestMethod("POST");
                httpConn.connect();

                OutputStream outputS = httpConn.getOutputStream();
                outputS.write(send_data.getBytes("UTF-8"));

                outputS.flush();
                outputS.close();

                int responseStatusCode = httpConn.getResponseCode();
                InputStream inputS;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputS = httpConn.getInputStream();
                }
                else{
                    inputS = httpConn.getErrorStream();
                }

                InputStreamReader inputStreamR = new InputStreamReader(inputS, "UTF-8");
                BufferedReader bufferedR = new BufferedReader(inputStreamR);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedR.readLine()) != null){
                    sb.append(line);
                }

                bufferedR.close();

                return sb.toString();

            }catch (Exception e){

            }

            return strs[0];
        }
    }

    private class change extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DataActivity.this, "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strs){
            String conn_url = (String)strs[0];
            String send_data = "mode=" + (String)strs[1] + "&id=" + (String)strs[2];

            try{
                URL url = new URL(conn_url);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                httpConn.setReadTimeout(5000);
                httpConn.setConnectTimeout(5000);

                httpConn.setRequestMethod("POST");
                httpConn.connect();

                OutputStream outputS = httpConn.getOutputStream();
                outputS.write(send_data.getBytes("UTF-8"));

                outputS.flush();
                outputS.close();

                int responseStatusCode = httpConn.getResponseCode();
                InputStream inputS;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputS = httpConn.getInputStream();
                }
                else{
                    inputS = httpConn.getErrorStream();
                }

                InputStreamReader inputStreamR = new InputStreamReader(inputS, "UTF-8");
                BufferedReader bufferedR = new BufferedReader(inputStreamR);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedR.readLine()) != null){
                    sb.append(line);
                }

                bufferedR.close();

                return sb.toString();

            }catch (Exception e){

            }

            return strs[0];
        }

    }


    private class JsonLoadingTask extends AsyncTask<String, Void, String>{
        @Override
        protected  String doInBackground(String... strs){
            return getJsonText();
        }
        @Override
        protected void onPostExecute(String result){
            String[] arr = result.split("\n");
            System.out.println(result);
            System.out.println(arr[0]);


            int wifi_stat = Integer.parseInt(arr[11]);
            if(wifi_stat == 1){
                wifi.setImageResource(R.drawable.wifi_on);
            }
            else{
                wifi.setImageResource(R.drawable.wifi);
            }

            float rain = Float.parseFloat(arr[9]);
            if(rain > 1000){
                rain_img.setImageResource(R.drawable.sun);
            }
            else if(rain <= 1000 && rain > 800){
                rain_img.setImageResource(R.drawable.little);
            }
            else if(rain <= 800 && rain > 600){
                rain_img.setImageResource(R.drawable.normal);
            }
            else if(rain <= 600 && rain > 400){
                rain_img.setImageResource(R.drawable.rain);
            }
            else{
                rain_img.setImageResource(R.drawable.rainnn);
            }

            int mode = Integer.parseInt(arr[10]);
            if(mode == 1){
                auto_switch.setText("자동모드");
                auto_switch.setChecked(true);
                window_move.setVisibility(View.INVISIBLE);
                window_back.setVisibility(View.INVISIBLE);
            }
            else if(mode == 0){
                auto_switch.setText("수동모드");
                auto_switch.setChecked(false);
                window_move.setVisibility(View.VISIBLE);
                window_back.setVisibility(View.VISIBLE);
            }
            else if(mode == 2){
                auto_switch.setVisibility(View.INVISIBLE);
                window_move.setVisibility(View.INVISIBLE);
                window_back.setVisibility(View.INVISIBLE);
            }

            int motor_stat = Integer.parseInt(arr[6]) - 650;                      //////////////////////////////////////////////////////////////////////////////////////
            if(motor_stat < 0){
                motor_stat = motor_stat * -1;
            }
            window_move.setProgress(motor_stat);

            float gas_stat = Float.parseFloat(arr[8]);
            if(gas_stat > 90){
                gas_img.setImageResource(R.drawable.siren);
            }
            else{
                gas_img.setImageResource(R.drawable.siren_off);
            }


            float ftem_in = Float.parseFloat(arr[0]);
            tem_in.showValue(ftem_in, 45f, true);
            tem_in.setUnit("℃");
            tem_in.setTextSize(15f);
            if(ftem_in > 30){
                tem_in.setColor(0xFF5B5B);
            }
            else if(ftem_in <= 30 && ftem_in > 20){
                tem_in.setColor(0x2E82FF);
            }
            else{
                tem_in.setColor(0x5BFF5B);
            }

            float ftem_out = Float.parseFloat(arr[1]);
            tem_out.showValue(ftem_out, 45f, true);
            tem_out.setUnit("℃");
            tem_out.setTextSize(15f);
            if(ftem_out > 30){
                tem_out.setColor(0xFF5B5B);
            }
            else if(ftem_out <= 30 && ftem_out > 20){
                tem_out.setColor(0x2E82FF);
            }
            else{
                tem_out.setColor(0x5BFF5B);
            }

            float fhum_in = Float.parseFloat(arr[2]);
            hum_in.showValue(fhum_in, 100f, true);
            hum_in.setTextSize(15f);
            if(fhum_in > 70){
                hum_in.setColor(0xFF5B5B);
            }
            else if(fhum_in <= 70 && fhum_in > 50){
                hum_in.setColor(0x2E82FF);
            }
            else{
                hum_in.setColor(0x5BFF5B);
            }

            float fhum_out = Float.parseFloat(arr[3]);
            hum_out.showValue(fhum_out, 100f, true);
            hum_out.setTextSize(15f);
            if(fhum_out > 70){
                hum_out.setColor(0xFF5B5B);
            }
            else if(fhum_out <= 70 && fhum_out > 50){
                hum_out.setColor(0x2E82FF);
            }
            else{
                hum_out.setColor(0x5BFF5B);
            }

            float fdust_in = Float.parseFloat(arr[4]);
            dust_in.showValue(fdust_in, 80f, true);
            dust_in.setUnit("㎍");
            dust_in.setTextSize(15f);
            if(fdust_in > 40){
                dust_in.setColor(0xFF5B5B);
            }
            else if(fdust_in <= 40 && fdust_in > 15){
                dust_in.setColor(0x2E82FF);
            }
            else {
                dust_in.setColor(0x5BFF5B);
            }

            float fdust_out = Float.parseFloat(arr[5]);
            dust_out.showValue(fdust_out, 80f, true);
            dust_out.setUnit("㎍");
            dust_out.setTextSize(15f);
            if(fdust_out > 40){
                dust_out.setColor(0xFF5B5B);
            }
            else if(fdust_out <= 40 && fdust_in > 15){
                dust_out.setColor(0x2E82FF);
            }
            else {
                dust_out.setColor(0x5BFF5B);
            }

        }
    }

    public String getJsonText(){
        StringBuffer sb = new StringBuffer();
        try{
            String jsonPage = getStringFromUrl("http://175.205.244.188/conn.php");

            JSONObject json = new JSONObject(jsonPage);

            JSONArray jArr = json.getJSONArray("result");

            for(int i=0; i<jArr.length(); i++){
                json = jArr.getJSONObject(i);
                if(json.getString("id").equals(id)){
                    String dust_in = json.getString("dust_in");
                    String dust_out = json.getString("dust_out");
                    String motor_stat = json.getString("motor_stat");
                    String temperature = json.getString("temperature_in");
                    String temper_out = json.getString("temperature_out");
                    String humidity = json.getString("humidity_in");
                    String humi_out = json.getString("humidity_out");
                    String fan_stat = json.getString("fan_stat");
                    String gas_stat = json.getString("gas_stat");
                    String is_rain = json.getString("is_rain");
                    String mode = json.getString("mode");
                    String wifi = json.getString("wifi");

                    if(wifi.equals("0")){
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("0\n");
                        sb.append("2\n");
                        sb.append("0\n");
                    }
                    else {
                        sb.append(temperature + "\n");
                        sb.append(temper_out + "\n");
                        sb.append(humidity + "\n");
                        sb.append(humi_out + "\n");
                        sb.append(dust_in + "\n");
                        sb.append(dust_out + "\n");
                        sb.append(motor_stat + "\n");
                        sb.append(fan_stat + "\n");
                        sb.append(gas_stat + "\n");
                        sb.append(is_rain + "\n");
                        sb.append(mode + "\n");
                        sb.append(wifi + "\n");
                    }

                }
            }

        }catch (Exception e){

        }

        return sb.toString();
    }

    public String getStringFromUrl(String pUrl){

        BufferedReader bufreader=null;
        HttpURLConnection urlConnection = null;

        StringBuffer page=new StringBuffer(); //읽어온 데이터를 저장할 StringBuffer객체 생성

        try {
            //[Type2]
            URL url= new URL(pUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream contentStream = urlConnection.getInputStream();

            bufreader = new BufferedReader(new InputStreamReader(contentStream,"UTF-8"));
            String line = null;

            //버퍼의 웹문서 소스를 줄단위로 읽어(line), Page에 저장함
            while((line = bufreader.readLine())!=null){
                Log.d("line:",line);
                page.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //자원해제
            try {
                bufreader.close();
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return page.toString();
    }
}
