package md.intelectsoft.petrolmpos.paymentactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolmpos.R;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
@SuppressLint("NonConstantResourceId")
public class BPayPaymentActivity extends AppCompatActivity {
    @BindView(R.id.imageForBPayQR)  ImageView qrCode;

    @OnClick(R.id.layoutCloseBPayActivity) void onCancel(){
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_pay_payment);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        qrCode.setImageBitmap(createQRGradientImage("11f47ad5-7b73-42c0-abae-878b1e16adee", 200, 300));
    }


    public static Bitmap createQRGradientImage(String url, final int width, final int height){
        try {
        // Determine the legality of the URL
            if (url == null || "".equals(url) || url.length() < 1){
                return null;
            }
            Hashtable<EncodeHintType, Object> hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 2);
            // Image data conversion, using matrix conversion
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];

            // Gradient color draw from top to bottom
            for (int y = 0; y < height; y++){
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {// QR code color
                        int red = (int)(2 - (2.0 - 3.0) / height * (y + 1));
                        int green = (int)(119 - (119.0 - 7.0) / height * (y + 1));
                        int blue =  (int)(189 - (189.0 - 4.0) / height * (y + 1));
                        Color color = new Color();
//                        int colorInt = color.argb( red, green, blue);
                        int col = Color.rgb(red, green, blue);
                    // Modify the color of the QR code, you can separately develop the color of the QR code and background
                        pixels[y * width + x] = bitMatrix.get(x, y) ? col: 16777215;// 0x000000:0xffffff
                    } else {
                        pixels[y * width + x] = Color.rgb(255, 255, 255);;// background color
                    }
                }
            }

            // Generate the format of the QR code image, using ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}