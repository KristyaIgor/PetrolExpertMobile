package md.intelectsoft.petrolexpert.fileVerifone.beeper;

import android.media.AudioTrack;

public class AudioTrackManager {
   public static final int DOUBLE = 3;
   public static final int LEFT = 1;
   public static final float MAXVOLUME = 100.0F;
   public static final int RATE = 44100;
   public static final int RIGHT = 2;
   int Hz = 2730;
   AudioTrack audioTrack;
   int channel;
   int length;
   float volume;
   byte[] wave = new byte['걄'];
   int waveLen;

   public AudioTrackManager() {
      int var1 = AudioTrack.getMinBufferSize(44100, 2, 3);
      AudioTrack var2 = new AudioTrack(5, 44100, 2, 3, var1, 1);
      this.audioTrack = var2;
   }

   public void play() {
      if (this.audioTrack != null) {
         this.audioTrack.write(this.wave, 0, this.length);
      }

   }

   public void setChannel(int var1) {
      this.channel = var1;
      this.setVolume(this.volume);
   }

   public void setVolume(float var1) {
      this.volume = var1;
      if (this.audioTrack != null) {
         switch(this.channel) {
         case 1:
            this.audioTrack.setStereoVolume(var1 / 100.0F, 0.0F);
            break;
         case 2:
            this.audioTrack.setStereoVolume(0.0F, var1 / 100.0F);
            return;
         case 3:
            AudioTrack var4 = this.audioTrack;
            float var5 = var1 / 100.0F;
            var4.setStereoVolume(var5, var5);
            return;
         default:
            return;
         }
      }

   }

   public void start(int var1, float var2) {
      this.stop();
      if (var1 > 0) {
         this.Hz = var1;
         this.waveLen = '걄' / this.Hz;
         this.length = this.waveLen * this.Hz;
         this.length = (int)(var2 * (float)this.length);
         this.wave = new byte[this.length];
         this.wave = SinWave.sin(this.wave, this.waveLen, this.length);
         if (this.audioTrack != null && this.audioTrack.getState() == 1) {
            this.audioTrack.play();
         }
      }

   }

   public void stop() {
      if (this.audioTrack != null) {
         this.audioTrack.stop();
      }

   }
}
