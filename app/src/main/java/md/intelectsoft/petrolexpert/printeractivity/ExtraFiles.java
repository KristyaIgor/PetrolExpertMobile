package md.intelectsoft.petrolexpert.printeractivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Simon on 2018/5/9.
 */

public class ExtraFiles {

    private static final String TAG = "VFI.ExtraFiles";


    /**
     * @param fileName read the file in assets
     * @param assets the assets set by getAssets
     * @return the content of the file in byte[]
     */
    public static byte[] readAssets(String fileName, AssetManager assets) {
        byte[] buffer = null;
        try {
            //
            InputStream is = assets.open(fileName);
            // get the size
            int size = is.available();
            // crete the array of byte
            buffer = new byte[size];
            is.read(buffer);
            // close the stream
            is.close();
//            // byte to String
//            String text = new String(buffer, "UTF-8");
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }

        return buffer;
    }

    void test(Context ctxDealFile) {
        try {
            ctxDealFile = ctxDealFile.createPackageContext("com.example.test",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String uiFileName = "applist";
        deepFile(ctxDealFile, uiFileName, "");
    }

    /** 上边的内容卸载onCreate中
     * 遍历assets下的所有文件
     * @param ctxDealFile
     * @param path
     */
    public byte[] deepFile(Context ctxDealFile, String path, String wantFileName) {

        byte[] buffer = null;
        try {
            String str[] = ctxDealFile.getAssets().list(path);
            if (str.length > 0) {// 如果是目录
                File file = new File("/data/" + path);
//                file.mkdirs();
                for (String string : str) {
                    path = path + "/" + string;
                    System.out.println("zhoulc:\t" + path);
                    // textView.setText(textView.getText()+"\t"+path+"\t");
                    buffer = deepFile(ctxDealFile, path, wantFileName);
                    if( buffer != null) {
                        return buffer;
                    }
                    path = path.substring(0, path.lastIndexOf('/'));
                }
            } else {// 如果是文件

                if( wantFileName.equals(path) ) {
                    return readAssets( path, ctxDealFile.getAssets() );
                }
//                InputStream is = ctxDealFile.getAssets().open(path);
//                FileOutputStream fos = new FileOutputStream(new File("/data/"
//                        + path));
//                byte[] buffer = new byte[1024];
//                int count = 0;
//                while (true) {
//                    count++;
//                    int len = is.read(buffer);
//                    if (len == -1) {
//                        break;
//                    }
//                    fos.write(buffer, 0, len);
//                }
//                is.close();
//                fos.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }


    /**
     *
     * 将assets的文件写到固定目录下
     */
    private void importDB(String assetFileName, String innerFileParent, String innerFileChild, AssetManager assets) {
        innerFileParent = "data/data/com.example.test/";
        innerFileChild = "applist/applist.preincluded.description";
        File file = new File(innerFileParent, innerFileChild);
        // String DbName = "people_db";
        // 判断是否存在
        if (file.exists() && file.length() > 0) {
        } else {
            // 使用AssetManager类来访问assets文件夹
            AssetManager asset = assets;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = asset.open(assetFileName);
                fos = new FileOutputStream(file);
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * copy file in assets to system (sdcard)
     * @param sourceAssetFileName the source file in assets
     * @param targetPath the target path
     * @param targetFileName the target fileName
     * @param assets the AssetManager from getAssets
     * @return true for success, false for failure
     */
    public static boolean copy(String sourceAssetFileName,
                               String targetPath, String targetFileName, AssetManager assets, boolean overWriteIfExists) {
        String fullFileName = targetPath + targetFileName;

        File file = new File(fullFileName);
        if( file.exists() && (false == overWriteIfExists) ){
            Log.i( TAG, "return while file [" + fullFileName + "] exists");
            return true;
        }
        File dir = new File(targetPath);
        // try create the fold if not exists
        if (!dir.exists())
            dir.mkdirs();
        try {
            if (!(new File(fullFileName)).exists()) {
                InputStream is = assets.open(sourceAssetFileName);
                FileOutputStream fos = new FileOutputStream(fullFileName);
                byte[] buffer = new byte[4096];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    Bitmap loadImage (AssetManager assets) {
//        ImageView imageView = (ImageView) findViewById(R.id.image);

        /**
         * 使用assets下的图片
         * http://www.2cto.com/kf/201408/322920.html
         */
        Bitmap bmp = null;
        InputStream is;
        try {
            is = assets.open("applist/applogo.png");
            bmp = BitmapFactory.decodeStream(is);
//            imageView.setImageBitmap(bmp);
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        return bmp;
    }


    /**
     * 往固定的目录下的文件中写内容
     * @param fileName 要操作的绝对路径/data/data/包名/路径+文件名
     * @param write_str 要写入的内容
     * @throws IOException
     */
    public void writeSDFile(String fileName, String write_str, Context context)
            throws IOException {

        File file = new File(fileName);

        FileOutputStream fos = new FileOutputStream(file);

        FileOutputStream fos1 = context.openFileOutput(fileName,
                context.MODE_PRIVATE);

        byte[] bytes = write_str.getBytes();

        fos1.write(bytes);

        fos.close();
    }

    /**
     * 读取固定目录下的文件（外部存储的操作。真机没有root是不可以的）
     * @param fileName
     * @return
     * @throws IOException
     * 参考博客：http://blog.csdn.net/ztp800201/article/details/7322110
     */
    public String readSDFile(String fileName, Context context) throws IOException {

        File file = new File(fileName);
        // fileinputstream是不能传入路径的，只传入名称就找不到文件。所以需要传入file
        FileInputStream fis = new FileInputStream(file);

        FileInputStream fis1 = context.openFileInput(fileName);

        int length = fis1.available();

        byte[] buffer = new byte[length];
        fis1.read(buffer);

        // String res = EncodingUtils.getString(buffer, "UTF-8");

        fis1.close();
        // return res;

        return new String(buffer);
    }



    /**
     * 内部存储的写方法
     */
    public void writeNeibu( Context context) {
        String str = "测试内容111";
        // getFileDir()方法获得是file的路径，就是data/data/包名/file
        // 但是我想在自定义的路径下生成文件，我就获得file路径的父路径
        File dataDir = context.getFilesDir().getParentFile();
        File mydir = new File(dataDir, "aaa");
        // 创建data/data/包名/aaa路径
        mydir.mkdir();
        File file = new File(mydir, "test.txt");
        BufferedWriter bw = null;
        try {
            file.createNewFile();
            // fileoutputstream的第二个参数，就是决定是否追加 ，false为替换，true就会在尾部追加内容
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, false), "UTF-8"));
            // fw.append("测试内容");
            bw.write(str);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部存储的读方法
     */
    public void readNeibu( Context context) {
        File dataDir = context.getFilesDir().getParentFile();
        File mydir = new File(dataDir, "aaa");
        File file = new File(mydir, "test.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file), "UTF-8"));
            String str1 = null;
            int a;
            while ((a = br.read()) != -1) {
                str1 = br.readLine();
                System.out.println(str1);
            }
            br.close();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



}
