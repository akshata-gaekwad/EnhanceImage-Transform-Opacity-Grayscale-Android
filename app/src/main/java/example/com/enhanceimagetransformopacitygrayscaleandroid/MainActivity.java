package example.com.enhanceimagetransformopacitygrayscaleandroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;

import java.io.FileOutputStream;

public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnReadQRImage = (Button) findViewById(R.id.btnReadQRImage);
        btnReadQRImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 1234;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                  Bitmap grayScaleImage= androidGrayScale(yourSelectedImage);

                   // Toast.makeText(MainActivity.this, "width: " + yourSelectedImage.getWidth() + "Height: " + yourSelectedImage.getHeight(), Toast.LENGTH_LONG).show();
                    Paint transparentpainthack = new Paint();
                    transparentpainthack.setAlpha(1);
                    Canvas canvas = new Canvas();
                    canvas.drawBitmap(grayScaleImage, 0, 0, transparentpainthack);
                    FileOutputStream fos = null;
                   try{ 
                       fos = new FileOutputStream(new File("/storage/emulated/0/Download/cbimage (5).jpg"));
                   } catch (Exception e)
                   {e.printStackTrace();
                   }
                    grayScaleImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                     Toast.makeText(MainActivity.this, "OPacity: "+ transparentpainthack.getAlpha() + "width: " + yourSelectedImage.getWidth() + "Height: " + yourSelectedImage.getHeight(), Toast.LENGTH_LONG).show();

                    /* Now you have choosen image in Bitmap format in object "yourSelectedImage". You can use it in way you want! */

                    int[] intArray = new int[grayScaleImage.getWidth() * grayScaleImage.getHeight()];
                    //copy pixel data from the Bitmap into the 'intArray' array
                    grayScaleImage.getPixels(intArray, 0, grayScaleImage.getWidth(), 0, 0, grayScaleImage.getWidth(), grayScaleImage.getHeight());

                    LuminanceSource source = new RGBLuminanceSource(grayScaleImage.getWidth(), grayScaleImage.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Reader reader = new MultiFormatReader();
                    Result result = null;
                    String contents = null;
                    try {
                        result = reader.decode(bitmap);
                        contents = result.getText();
                        Toast.makeText(MainActivity.this, contents, Toast.LENGTH_LONG).show();
                    } catch (NotFoundException e) {
                        Toast.makeText(MainActivity.this, "Selected image is not valid QR Code", Toast.LENGTH_LONG).show();
                    } catch (ChecksumException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Selected Image cannot be read", Toast.LENGTH_LONG).show();
                    } catch (FormatException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Selected Image does not have correct format", Toast.LENGTH_LONG).show();
                    }
                }
        }

    };

    private Bitmap androidGrayScale(final Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}


