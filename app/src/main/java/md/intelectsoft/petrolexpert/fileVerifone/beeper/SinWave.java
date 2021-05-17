package md.intelectsoft.petrolexpert.fileVerifone.beeper;

public class SinWave {
   public static final int HEIGHT = 127;
   public static final double TWOPI = 6.283D;

   public static byte[] sin(byte[] var0, int var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         var0[var3] = (byte)((int)(127.0D * (1.0D - Math.sin(6.283D * (1.0D * (double)(var3 % var1) / (double)var1)))));
      }

      return var0;
   }
}
