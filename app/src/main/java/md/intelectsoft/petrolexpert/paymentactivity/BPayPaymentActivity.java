package md.intelectsoft.petrolexpert.paymentactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.intelectsoft.petrolexpert.R;

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

        qrCode.setImageBitmap(createQRGradientImage("https://eservicii.md/clientportal/auth/fiscal?bill=37f64fb9-0f8c-4664-8ca1-5aaccb1c7616&device=d8ada9a7-2206-40ac-8fd6-cd159ff1eb5d", 200, 300));
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