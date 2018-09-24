package com.example.anthonyanca.videoface;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class RegisterActivity extends AppCompatActivity{

    private  final int VIDEO_REQUEST_CODE = 100;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    public static final String UserName = "email";

    VideoView ressultVideo;

    Uri videoUri;
    Uri []imgUri;

    private boolean rostro;
    private boolean video;
    private boolean videoExito;

    private StorageReference storageReference;

    File file_video;
    ImageView img1;

    Image []images;

    //private FFmpeg ffmpeg;

    long maxDur;


    MediaMetadataRetriever mediaMetadataRetriever = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ressultVideo = (VideoView)findViewById(R.id.video_view);

        rostro=false;
        video = false;
        videoExito = false;

        storageReference = FirebaseStorage.getInstance().getReference();

        //img1 = (ImageView) findViewById(R.id.img1);

        mediaMetadataRetriever = new MediaMetadataRetriever();

        images = new Image[6];

        imgUri = new Uri[100];

    }



    /*public void captureVideo(View view) {
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File videoface = getFilepath();
        video_uri = Uri.fromFile(videoface);

        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(camera_intent,VIDEO_REQUEST_CODE);
    }

    public File getFilepath()
    {
        File folder = new File("sdcard/videoFace_app");
        if(!folder.exists())
        {
            folder.mkdir();
        }

        File video_file = new File(folder,"rostro.mp4");

        return video_file;
    }*/

    private void uploadFile(){

        for (int i=0;i<10;i++){

        if(imgUri[i] !=null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Registrando usuario...");
            progressDialog.show();


                StorageReference riversRef = storageReference.child(UserName+"/"+"imagen"+(i+1)+".png");

            riversRef.putFile(imgUri[i])
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                rostro=true;
                                progressDialog.dismiss();
                                videoExito= true;

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        /*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage((int) progress + "%...");
                            }
                        })*/
                ;



        } else {

            Toast.makeText(getApplicationContext(), "Intentelo de nuevo", Toast.LENGTH_SHORT).show();

        }
        }
        Toast.makeText(getApplicationContext(), "Registro completado", Toast.LENGTH_SHORT).show();
        goMainScreen();
    }

    public void Grabar(View view) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            //File sdcard = Environment.getExternalStorageDirectory();
            //file_video = new File(sdcard,"video.mp4");
            //takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,file_video);
            //takeVideoIntent.setType("video/mp4");
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);

            //initFrame();
            //startActivityForResult(takeVideoIntent,0);

        }
    }

    private void takeFrame() {

        long frametime = 100000;
        Bitmap []bmFrame = new Bitmap[100];
        for (int i =0; i<10 ; i++)
        {
            bmFrame[i] = mediaMetadataRetriever.getFrameAtTime(frametime);
            //img1.setImageBitmap(bmFrame);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmFrame[i].compress(Bitmap.CompressFormat.JPEG,100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bmFrame[i], "imagen"+(i+1),null);


            //grantUriPermission(path,imgUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //grantUriPermission(path,imgUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //grantUriPermission(path,imgUri,Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            //grantUriPermission(path,imgUri,Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            imgUri[i]=Uri.parse(path);


            //FileOutputStream stream = null;

            //file = new FileOutputStream(Environment.getExternalStorageDirectory().toString());
            //boolean compresee = bmFrame.compress(Bitmap.CompressFormat.JPEG,100, images[i] );

            frametime += i * 5000000;

            //Log.d("CREATION",imgUri[i].toString()+ "///" + frametime );

            //Toast.makeText(getApplicationContext(),imgUri[i].toString()+ "///" + frametime,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        //
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode==VIDEO_REQUEST_CODE){
            if (resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(),"Video subido con éxito",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Fallo al subir el video",Toast.LENGTH_LONG).show();
            }
        */
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = data.getData();



            ressultVideo.setVideoURI(videoUri);
            ressultVideo.setVisibility(View.VISIBLE);
            video=true;
            ressultVideo.start();

            data.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            data.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            data.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MediaMetadataRetriever tRetriever = new MediaMetadataRetriever();

            try{
                tRetriever.setDataSource(getBaseContext(),videoUri);

                mediaMetadataRetriever = tRetriever;

                //String DURATION = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                takeFrame();
            }catch (RuntimeException e){
                e.printStackTrace();
                Toast.makeText(RegisterActivity.this,"error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ok(View view) {
        if (video){
            uploadFile();

        }
        else{
            Toast.makeText(getApplicationContext(),"Error al subir el video",Toast.LENGTH_SHORT).show();
        }
    }

    private void goMainScreen() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /*private  void initFrame(){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(file_video.getAbsolutePath());

            int[] ids_of_images = new int[]{R.id.img1,R.id.img2,R.id.img3,R.id.img4,R.id.img5};
            int looper =100000;

            //img.setImageBitmap(retriever.getFrameAtTime(10000,MediaMetadataRetriever.OPTION_CLOSEST));

            for(int i=0 ;i <5; i++)
            {
                ImageView imageView = (ImageView)findViewById(ids_of_images[i]);

                imageView.setImageBitmap(retriever.getFrameAtTime(looper,MediaMetadataRetriever.OPTION_CLOSEST));

                looper +=100000;
            }

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
    }*/
}
