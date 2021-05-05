package md.intelectsoft.petrolexpert.fileVerifone.beeper;

import android.content.Context;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.Log;

public class Beeper {
   private static Beeper beeper = null;
   private static boolean isBeep = false;
   private AudioTrackManager audio = null;
   private Thread mBeeperThread = null;

   private Beeper() {
      this.audio = new AudioTrackManager();
   }

   public static Beeper getInstance() {
      if (beeper == null) {
         beeper = new Beeper();
      }

      return beeper;
   }

   private int getVolume(Context var1) {
      return ((AudioManager)var1.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(5);
   }

   private void playAudio(Context context, int count, int defaultVolume, long time, long interval) {
      synchronized(this){}
      int var8 = 0;

      while(true) {
         Throwable var10000;
         boolean var10001;
         if (var8 < count) {
            label141: {
               label150: {
                  try {
                     if (isBeep) {
                        this.audio.play();
                        SystemClock.sleep(interval);
                        break label150;
                     }
                  } catch (Throwable var21) {
                     var10000 = var21;
                     var10001 = false;
                     break label141;
                  }

                  try {
                     this.setVolume(context, defaultVolume);
                  } catch (Throwable var20) {
                     var10000 = var20;
                     var10001 = false;
                     break label141;
                  }

                  var8 = count;
               }

               ++var8;
               continue;
            }
         } else {
            label143:
            try {
               isBeep = false;
               this.setVolume(context, defaultVolume);
               return;
            } catch (Throwable var22) {
               var10000 = var22;
               var10001 = false;
               break label143;
            }
         }

         Throwable var9 = var10000;
         var9.printStackTrace();

         try {
            throw var9;
         } catch (Throwable throwable) {
            throwable.printStackTrace();
         }
      }
   }

   private void setVolume(Context var1, int var2) {
      ((AudioManager)var1.getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(5, var2, 0);
   }

   public void startBeep(Context context, int count, int defaultVolume, long time, long interval) {
      this.startBeep(context, count, defaultVolume, time, interval, 3);
   }

   public void startBeep(Context var1, int var2, int var3, long var4, long var6, int var8) {
      synchronized(this){}
      int var9 = 2730;
      int var10 = var2;
      if (var2 <= 20) {
         var10 = 2730;
      }

      if (var10 < 20000) {
         var9 = var10;
      }

      long var11;
      if (var4 < 100L) {
         var11 = 100L;
      } else {
         var11 = var4;
      }

      long var13;
      if (var6 < 0L) {
         var13 = 0L;
      } else {
         var13 = var6;
      }

      float var15 = (float)var11 / 1000.0F;

      try {
         StringBuilder var16 = new StringBuilder();
         var16.append("multiple:");
         var16.append(var15);
         Log.i("", var16.toString());
         this.audio.start(var9, var15);
         int var20 = this.getVolume(var1);
         this.setVolume(var1, var8);
         if (this.mBeeperThread != null) {
            isBeep = false;
            this.mBeeperThread = null;
         }

         isBeep = true;
         Thread test = new Thread(){
            public void run(){
               super.run();
               Beeper.this.playAudio(var1, var3, var20, var11, var13);
            }
         };


         this.mBeeperThread = test;
         this.mBeeperThread.start();
      } finally {
         ;
      }

   }

   public void stopBeep() {
      this.audio.stop();
      isBeep = false;
   }
}
