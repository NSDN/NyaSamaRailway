package cn.ac.nya.nspga;

/**
 * Created by drzzm32 on 2018.3.11.
 */
public interface INSPGA {
    void configure(int[] data);
    byte output(byte input);
    static byte parse(int len, int[] data, int pos, int b) {
        if (pos < data.length && data.length <= len) {
            if ((b & 0x1) == 0) return (byte) (data[pos] & 0xFF);
            else return (byte) ((data[pos] >> 8) & 0xFF);
        }
        return (byte) 0x00;
    }
}
