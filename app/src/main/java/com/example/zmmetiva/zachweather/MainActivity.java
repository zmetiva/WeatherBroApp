package com.example.zmmetiva.zachweather;

import android.content.Context;
import android.content.Intent;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private ArrayList<ListModel> forecastArray = new ArrayList<>();
    private String zip;
    private final int[] drawables = {R.drawable.tornado,
            R.drawable.storm,
            R.drawable.storm,
            R.drawable.storm,
            R.drawable.storm,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.rain,
            R.drawable.rain,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.cloudy,
            R.drawable.partly,
            R.drawable.partly,
            R.drawable.sunny,
            R.drawable.partly,
            R.drawable.partly,
            R.drawable.partly,
            R.drawable.snow,
            R.drawable.sunny,
            R.drawable.storm,
            R.drawable.storm,
            R.drawable.storm,
            R.drawable.rain,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.snow,
            R.drawable.partly,
            R.drawable.storm,
            R.drawable.snow,
            R.drawable.storm
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        zip = readFromFile();

        if (zip == "") {
            Toast.makeText(MainActivity.this, "Set Location from Menu", Toast.LENGTH_LONG).show();
            writeToFile("HI");
        } else {

            String codeZ;

            Intent intent = getIntent();

            TextView cityName = (TextView) findViewById(R.id.cityNameText);
            TextView curTemp = (TextView) findViewById(R.id.currentTempText);
            TextView curCond = (TextView) findViewById(R.id.currentConditionText);

            if (intent.getCharSequenceExtra("ZIP_CODE") == null) {
                //Toast.makeText(MainActivity.this, "Set Location from Menu", Toast.LENGTH_LONG).show();
                codeZ = readFromFile();
            } else {
                codeZ = intent.getCharSequenceExtra("ZIP_CODE").toString();
                writeToFile(codeZ);
            }

            SaveTheFeed feed = new SaveTheFeed();
            feed.setPrefix(codeZ);

            try {
                feed.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            cityName.setText(feed.getCity() + ", " + feed.getState());
            curTemp.setText(feed.getCurrentTemp() + " F");
            curCond.setText(feed.getCurrentCondition());

            setListData(feed);

            ListView listView = (ListView) findViewById(R.id.listView);

            CustomAdapter adapter = new CustomAdapter(this, forecastArray);

            listView.setAdapter(adapter);

            int code = Integer.parseInt(feed.getConditionCode());

            ImageView curImage = (ImageView) findViewById(R.id.currentConditionImage);
            curImage.setBackgroundResource(drawables[code]);

        }


    }

    public void setListData(SaveTheFeed feed)
    {
        for (int i = 0; i < 5; i++) {

            final ListModel sched = new ListModel();

            sched.setHigh(feed.getHigh(i));
            sched.setLow(feed.getLow(i));
            sched.setImage(drawables[Integer.parseInt(feed.getCode(i))]);
            sched.setDate(feed.getDate(i));

            forecastArray.add( sched );
        }
    }
    public void onItemClick(int mPosition) {

    }

    // Allows you to perform background operations without locking up the user interface
    // until they are finished
    // The void part is stating that it doesn't receive parameters, it doesn't monitor progress
    // and it won't pass a result to onPostExecute
    class SaveTheFeed extends AsyncTask<Void, Void, Void> {

        // Holds JSON data in String format
        String jsonString = "";

        // Will hold the translations that will be displayed on the screen
        List<String> result = new ArrayList<String>();

        private String prefix = "";

        private String city;
        private String state;
        private String currentTemp;
        private String currCondition;
        private String conditionCode;

        private String[] highs = new String[5];
        private String[] lows = new String[5];
        private String[] dates = new String[5];
        private String[] conditions = new String[5];
        private String[] codes = new String[5];


        public void setPrefix(String p) {
            prefix = p;
        }

        // Everything that should execute in the background goes here
        // You cannot edit the user interface from this method
        @Override
        protected Void doInBackground(Void... voids) {

            // Get the text from EditText
            String text = prefix;

            // Client used to grab data from a provided URL
            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());

            // Provide the URL for the post request
            HttpPost httpPost = new HttpPost("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text=%22" + text + "%22)&format=json");

            // Define that the data expected is in JSON format
            httpPost.setHeader("Content-type", "application/json");

            // Allows you to input a stream of bytes from the URL
            InputStream inputStream = null;

            try {

                // The client calls for the post request to execute and sends the results back
                HttpResponse response = httpClient.execute(httpPost);

                // Holds the message sent by the response
                HttpEntity entity = response.getEntity();

                // Get the content sent
                inputStream = entity.getContent();

                // A BufferedReader is used because it is efficient
                // The InputStreamReader converts the bytes into characters
                // My JSON data is UTF-8 so I read that encoding
                // 8 defines the input buffer size
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                // Storing each line of data in a StringBuilder
                StringBuilder sb = new StringBuilder();

                String line = null;

                // readLine reads all characters up to a \n and then stores them
                while ((line = reader.readLine()) != null) {

                    sb.append(line + "\n");

                }

                System.out.println(sb.toString());

                // Save the results in a String
                jsonString = sb.toString();

                // Create a JSONObject by passing the JSON data
                JSONObject jObject = new JSONObject(jsonString);

                JSONObject object = jObject.getJSONObject("query");
                object = object.getJSONObject("results");
                object = object.getJSONObject("channel");

                JSONObject curWeather = object.getJSONObject("item");

                object = object.getJSONObject("location");

                city = object.getString("city");
                state = object.getString("region");

                object = curWeather.getJSONObject("condition");
                conditionCode = object.getString("code");
                currentTemp = object.getString("temp");
                currCondition = object.getString("text");

                JSONArray jArray= curWeather.getJSONArray("forecast");

                for (int i = 0; i < jArray.length(); ++i) {
                    object = jArray.getJSONObject(i);
                    highs[i] = object.getString("high");
                    lows[i] = object.getString("low");
                    conditions[i] = object.getString("text");

                    switch (object.getString("day")) {
                        case "Tue": dates[i] = object.getString("day") + "sday"; break;
                        case "Wed": dates[i] = object.getString("day") + "nesday"; break;
                        case "Thu": dates[i] = object.getString("day") + "rsday"; break;
                        case "Sat": dates[i] = object.getString("day") + "urday"; break;
                        default: dates[i] = object.getString("day") + "day"; break;

                    }

                    codes[i] = object.getString("code");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        // Called after doInBackground finishes executing
        @Override
        protected void onPostExecute(Void aVoid) {

        }

        public String getHigh(int index) {
            return highs[index];
        }

        public String getLow(int index) {
            return lows[index];
        }

        public String getDate(int index) {
            return dates[index];
        }

        public String getCode(int index) {
            return codes[index];
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getCurrentTemp() {
            return currentTemp;
        }

        public List<String> getArray() {
            return result;

        }

        public String getConditionCode() {
            return conditionCode;
        }

        public String getCurrentCondition() {
            return currCondition;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.set_location:
                Intent intent = new Intent(this, LocationActivity.class);
                intent.putExtra("EXTRA", "Fun");
                startActivity(intent);
                finish();
                return true;
            case R.id.exit:
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.getBaseContext().openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}


