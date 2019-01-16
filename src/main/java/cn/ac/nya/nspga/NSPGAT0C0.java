package cn.ac.nya.nspga;

/**
 * Created by drzzm32 on 2018.3.12.
 */
public class NSPGAT0C0 implements INSPGA {

    private byte iIEN;
    private byte oIEN;

    private byte[] MUX = new byte[8];

    public NSPGAT0C0() {
        iIEN = oIEN = 0x00;
        for (int i = 0; i < 8; i++) MUX[i] = 0x00;
    }

    private byte parse(int[] data, int pos, int b) {
        return INSPGA.parse(5, data, pos, b);
    }

    @Override
    public void configure(int[] data) {
        iIEN = parse(data, 0, 0);
        oIEN = parse(data, 0, 1);
        for (int i = 0; i < 8; i++)
            MUX[i] = parse(data, 1 + i / 2, i % 2);
    }

    @Override
    public byte output(byte input) {
        byte in = (byte) (input ^ iIEN);

        byte out = 0x00;
        for (int i = 0; i < 8; i++) {
            out |= (((in & MUX[i]) != 0) ? 0x1 : 0x0) << i;
        }

        return (byte) (out ^ oIEN);
    }

}
