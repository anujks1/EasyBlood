package com.teachmate.teachmate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NiRavishankar on 1/21/2015.
 */
public class CommonMethods {

    boolean isFinished = false;
    boolean isAvailable = false;

    public boolean hasActiveInternetConnection(Context context) {
        HttpGetter getter = new HttpGetter();
        getter.execute(context);

        while(!isFinished){
        }

        if(isAvailable){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private class HttpGetter extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... context) {
            if (isNetworkAvailable(context[0])) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    if(urlc.getResponseCode() == 200) {
                        Log.d("Test", "Working fine");
                        isAvailable = true;
                    }
                    else {
                        isAvailable = false;
                    }
                } catch (IOException e) {
                    Log.e("INTERNET", "Error checking internet connection", e);
                    isAvailable = false;
                }
            }
            else {
                Log.d("INTERNET", "No network available!");
                isAvailable = false;
            }
            isFinished = true;
            return "false";
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("true")){
                isAvailable = true;
            }
            else{
                isAvailable = false;
            }
            isFinished = true;
        }
    }

    public static void scaleImage(Context context, ImageButton view, int boundBoxInDp)
    {
        // Get the ImageView and its bitmap
        Drawable drawing = view.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        // Get current dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int boundBoxInPx = dpToPx(context, boundBoxInDp);

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundBoxInPx) / width;
        float yScale = ((float) boundBoxInPx) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = dpToPx(context, 100);
        params.height = dpToPx(context, 100);;
        //view.setLayoutParams(params);
    }

    private static int dpToPx(Context context,int dp)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}
