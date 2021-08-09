package com.example.myapplication2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText id;
    EditText pw;
    List<String> id_list = new ArrayList<>();
    List<String> pw_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) { // layout 아이디 패스워드 가져오기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText)findViewById(R.id.id_text);
        pw = (EditText)findViewById(R.id.pw_text);

        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){ // 클릭

        id = (EditText)findViewById(R.id.id_text);
        pw = (EditText)findViewById(R.id.pw_text);
        System.out.println(id);
        new JsonLoadingTask().execute();
    }

    private class JsonLoadingTask extends AsyncTask<String, Void, String>{ //
        @Override
        protected String doInBackground(String... strs){
            return getJsonText();
        }
        @Override
        protected void onPostExecute(String result){
            System.out.println(id);
            if(!result.equals("FAIL")){
                Intent intent = new Intent(getApplicationContext(), DataActivity.class);
                intent.putExtra("id", result);
                System.out.println(intent);
                startActivity(intent);
            }
            else{ //
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.setMessage("아이디 혹은 비밀번호를 잘못 입력하였습니다.");
                alert.show();
            }
        }
    }

    public String getJsonText(){
        String result = "";
        StringBuffer sb = new StringBuffer();
        try{
            String jsonPage = getStringFromUrl("http://175.205.244.188/conn.php");

            JSONObject json = new JSONObject(jsonPage);

            JSONArray jArr = json.getJSONArray("result");

            for(int i=0; i<jArr.length(); i++){
                json = jArr.getJSONObject(i);
                pw_list.add(json.getString("pw"));
                id_list.add(json.getString("id"));

            }
            id = (EditText)findViewById(R.id.id_text);
            System.out.println(id.getText().toString());
            System.out.println(id_list);

            for(int i = 0; i<id_list.size(); i++){
                if(id.getText().toString().equals(id_list.get(i)) && pw.getText().toString().equals(pw_list.get(i))){
                    result = id.getText().toString();
                    break;
                }
                else{
                    result = "FAIL";
                }
            }
        }catch (Exception e){

        }

        return result;
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
