package com.vlpuvi.android.sampleasynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    private static String url = "http://api.androidhive.info/contacts/";

    ArrayList<HashMap<String,String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list_item);
        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please Wait....");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... args0)
        {
            HttpHandler httpHandler = new HttpHandler();

            String jsonStr = httpHandler.makeServiceCall(url);

            Log.e(TAG,"Response from URL : "+jsonStr);

            if(jsonStr != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray jsonArray = jsonObject.getJSONArray("contacts");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        JSONObject p = c.getJSONObject("phone");
                        String mobile = p.getString("mobile");
                        String home = p.getString("home");
                        String office = p.getString("office");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("id",id);
                        contact.put("name",name);
                        contact.put("email",email);
                        contact.put("address",address);
                        contact.put("gender",gender);
                        contact.put("mobile",mobile);
                        contact.put("home",home);
                        contact.put("office",office);

                        contactList.add(contact);
                    }
                }
                catch (final JSONException e)
                {
                    Log.e(TAG,"JSON parsing error : "+e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"JSON parsing error : "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else
            {
                Log.e(TAG,"Couldn't get JSON from server...");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't get JSON from Server. Please check Logcat...",Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            if(pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"id","name","email","address","gender","mobile","home","office"},
                    new int[]{R.id.id,R.id.name,R.id.email,R.id.address,R.id.gender,R.id.mobile,R.id.home,R.id.office}
            );

            lv.setAdapter(adapter);
        }
    }
}
