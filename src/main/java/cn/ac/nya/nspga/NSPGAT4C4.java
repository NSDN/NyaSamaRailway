package cn.ac.nya.nspga;

/**
 * Created by drzzm32 on 2018.3.11.
 */
public class NSPGAT4C4 implements INSPGA {

    private byte iIEN;
    private byte oIEN;

    private byte iTSW;
    private byte tSWbit2(int i) { return (byte) ((iTSW >> (i * 2)) & 0x3); }

    private byte cntCMUX;
    private byte cntCMUXbit4(int i) { return (byte) ((cntCMUX >> ((i & 0x1) * 4)) & 0xF); }

    private byte[] oMUX = new byte[4];

    private byte oRSMUX[][] = new byte[4][2];
    private static final int RS_R = 0;
    private static final int RS_S = 1;

    private byte cCNTREG[] = new byte[4];

    private byte cntIMUX[] = new byte[2];
    private byte cntIMUXbit4(int i) { return (byte) ((cntIMUX[(byte) i / 2] >> (((byte) i % 2) * 4)) & 0xF); }

    private byte cRSMUX[] = new byte[2];
    private byte cRSMUXbit4(byte v, int t) { return (byte) ((v >> ((t & 0x1) * 4)) & 0xF); }

    /**** END OF CONF REGS ****/

    private byte _data;
    private byte getTrigBit(int i) { return (byte) (_data >> (i & 0x3)); }
    private byte getRSBit(int i) { return (byte) (_data >> ((i & 0x3) | 0x4)); }
    private void setTrigBit(int i, byte v) { _data = (byte) ((_data & ~(0x1 << (i & 0x3))) | (v << (i & 0x3))); }
    private void setRSBit(int i, byte v) { _data = (byte) ((_data & ~(0x1 << ((i & 0x3) | 0x4))) | (v << ((i & 0x3) | 0x4))); }

    private byte _cnt[] = new byte[4];

    public NSPGAT4C4() {
        iIEN = oIEN = 0x00;
        iTSW = cntCMUX = 0x00;
        for (int i = 0; i < 4; i++) oMUX[i] = 0x00;
        for (int i = 0; i < 4; i++) for (int j = 0; j < 2; j++) oRSMUX[i][j] = 0x00;
        for (int i = 0; i < 4; i++) cCNTREG[i] = 0x00;
        for (int i = 0; i < 2; i++) cntIMUX[i] = 0x00;
        for (int i = 0; i < 2; i++) cRSMUX[i] = 0x00;

        _data = 0x00;
        for (int i = 0; i < 4; i++) _cnt[i] = 0x00;
    }

    private byte parse(int[] data, int pos, int b) {
        return INSPGA.parse(12, data, pos, b);
    }

    @Override
    public void configure(int[] data) {
        iIEN = parse(data, 0, 0); oIEN = parse(data, 0, 1); // 1
        iTSW = parse(data, 1, 0); cntCMUX = parse(data, 1, 1); // 1
        for (int i = 0; i < 4; i++) oMUX[i] = parse(data, 2 + i / 2, i % 2); // 2
        for (int i = 0; i < 4; i++) for (int j = 0; j < 2; j++) oRSMUX[i][j] = parse(data, 4 + i, j); // 4
        for (int i = 0; i < 4; i++) cCNTREG[i] = parse(data, 8 + i / 2, i % 2); // 2
        for (int i = 0; i < 2; i++) cntIMUX[i] = parse(data, 10 + i / 2, i % 2); // 1
        for (int i = 0; i < 2; i++) cRSMUX[i] = parse(data, 11 + i / 2, i % 2); // 1
    }

    @Override
    public byte output(byte input) {
        byte in = (byte) (input ^ iIEN);

        byte trigIn = (byte) ((in >> 4) & 0xF);
        byte trigOut = 0x0, trigBit = 0x0;
        for (int i = 0; i < 4; i++) {
            trigBit = (byte) ((trigIn >> i) & 0x1);
            switch (tSWbit2(i)) {
                case 0x0:
                    trigOut |= (trigBit << i);
                    setTrigBit(i, trigBit);
                    break;
                case 0x1:
                    if (getTrigBit(i) == 0x0 && trigBit == 0x1)
                        trigOut |= (0x1 << i);
                    setTrigBit(i, trigBit);
                    break;
                case 0x2:
                    if (getTrigBit(i) == 0x1 && trigBit == 0x0)
                        trigOut |= (0x1 << i);
                    setTrigBit(i, trigBit);
                    break;
                case 0x3:
                    if (getTrigBit(i) != trigBit)
                        trigOut |= (0x1 << i);
                    setTrigBit(i, trigBit);
                    break;
                default:
                    break;
            }
        }

        in = (byte) ((in & 0x0F) | trigOut);
        byte out = 0x00;

        for (int i = 0; i < 4; i++) {
            out |= (((in & oMUX[i]) != 0) ? 0x1 : 0x0) << i;
        }

        for (int i = 0; i < 2; i++) {
            if ((in & oRSMUX[i][RS_R]) != 0) {
                setRSBit(i, (byte) 0x0);
            } else if ((in & oRSMUX[i][RS_S]) != 0) {
                setRSBit(i, (byte) 0x1);
            }
            out |= (getRSBit(i) << (i + 4));
        }

        byte cntTrigIn = 0x0; // 0 0 0 0 S3 R3 S2 R2
        for (int i = 2; i < 4; i++) {
            if ((in & oRSMUX[i][RS_R]) != 0) {
                cntTrigIn |= 0x1 << ((i - 2) * 2);
            } else if ((in & oRSMUX[i][RS_S]) != 0) {
                cntTrigIn |= 0x2 << ((i - 2) * 2);
            }
        }

        byte cntOut = 0x0;
        for (int i = 0; i < 2; i++) {
            if ((trigOut & cntIMUXbit4(i)) != 0) {
                if (_cnt[i] < cCNTREG[i]) _cnt[i] += 1;
                else {
                    _cnt[i] = 0;
                    cntOut |= (0x1 << i);
                }
            }
        }

        byte cnt23Buf = 0x0; // 0 0 0 0 0 0 c3 c2
        cnt23Buf |= ((trigOut & cntIMUXbit4(2)) != 0) ? 0x1 : 0x0;
        cnt23Buf |= ((trigOut & cntIMUXbit4(3)) != 0) ? 0x2 : 0x0;

        byte cntBuf = 0x0; // 0 0 0 0 0 0 h l
        cntBuf |= ((cntOut & (cntCMUXbit4(0) & 0x3)) != 0) ? 0x1 : 0x0;
        cntBuf |= ((cntOut & (cntCMUXbit4(1) & 0x3)) != 0) ? 0x2 : 0x0;

        byte cnt23In = 0x0; // 0 0 0 0 0 0 c3 c2
        cnt23In |= ((cnt23Buf & ((cntCMUXbit4(0) >> 2) & 0x3)) != 0) ? 0x1 : 0x0;
        cnt23In |= ((cnt23Buf & ((cntCMUXbit4(1) >> 2) & 0x3)) != 0) ? 0x2 : 0x0;

        cnt23In |= cntBuf;
        for (int i = 2; i < 4; i++) {
            if (((cnt23In >> (i - 2)) & 0x1) != 0) {
                if (_cnt[i] < cCNTREG[i]) _cnt[i] += 1;
                else {
                    _cnt[i] = 0;
                    cntOut |= (0x1 << i);
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            if (((cntOut & cRSMUXbit4(cRSMUX[i], RS_R)) != 0) || ((cntTrigIn >> (i * 2)) != 0)) {
                setRSBit(i + 2, (byte) 0x0);
            } else if (((cntOut & cRSMUXbit4(cRSMUX[i], RS_S)) != 0) || ((cntTrigIn >> (i * 2 + 1)) != 0)) {
                setRSBit(i + 2, (byte) 0x1);
            }
        }

        for (int i = 0; i < 4; i++) {
            out |= (getRSBit(i) << (i + 4));
        }

        return (byte) (out ^ oIEN);
    }

}
