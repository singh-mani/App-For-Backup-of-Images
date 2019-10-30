package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int  RESULT_LOAD_IMAGE = 1;
    private static final String SERVER_ADDRESS = "https://manindersandhu.000webhostapp.com/";
    ImageView imageToUpload;
    Button bUploadImage;
    EditText uploadImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageToUpload=(ImageView) findViewById(R.id.imageToUpload);
        bUploadImage=(Button) findViewById(R.id.bUploadImage);
        uploadImageName=(EditText) findViewById((R.id.etUploadName));

        imageToUpload.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);
        uploadImageName.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null){


            Uri selectedImage=data.getData();
            imageToUpload.setImageURI(selectedImage);

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imageToUpload:
                Intent galleryIntent = new Intent (Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                Log.i("****IMAGE", "  111 1111  ********** **** URL IS     ***********************************");

                startActivityForResult(galleryIntent , RESULT_LOAD_IMAGE);
                break;


            case R.id.bUploadImage:
                Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();


                Log.i("*****IMAGE", "  111 1111  ********** **** upload ImAGE     ***********************************");

                new UploadImage(image , uploadImageName.getText().toString()).execute();
                break;



        }



    }

    private class UploadImage extends AsyncTask<Void ,Void,Void>{

        Bitmap image;
        String name;

        public UploadImage(Bitmap image , String name) {
            this.image=image;
            this.name=name;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG , 100 ,byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray() , Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image" , encodedImage));
            dataToSend.add(new BasicNameValuePair("name" , name));


            HttpParams httpRequestParams = getHttpRequestParams();
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost( SERVER_ADDRESS + "SavePicture.php");


            try{
                Log.i("  *****  FOUNDDDD ", "  111 1111  ********** **** URL IS  "+SERVER_ADDRESS+"     ***********************************");

                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);

                Log.i("  *****  FOUNDDDD ", "  222 2222   ********** **** URL IS  "+SERVER_ADDRESS+"     ***********************************");
            }catch (Exception e)
            {

                e.printStackTrace();

                Toast.makeText(getApplicationContext() , " ERROR SHOWN " , Toast.LENGTH_SHORT).show();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext() , "IMAGE UPLOADED " , Toast.LENGTH_LONG).show();
        }

    }
    private  HttpParams getHttpRequestParams(){

        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams , 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams , 1000*30);
        return httpRequestParams;
    }
}
