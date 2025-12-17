package cipher.modern;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Base64;

public class RC5 {

    private final int r = 12;
    private final int w = 32;
    private final int b = 16;
    private final int u = w / 8;
    private final int t = 2 * (r + 1);

    private final int P32 = 0xB7E15163;
    private final int Q32 = 0x9E3779B9;



    private int[] expandKey(byte[] key) {
        if (key.length < b) {
            byte[] padded = new byte[b];
            System.arraycopy(key, 0, padded, 0, key.length);
            key = padded;
        } else if (key.length > b) {
            key = java.util.Arrays.copyOf(key, b);
        }

        int c = (b + u - 1) / u;
        int[] L = new int[c];

        for (int i = 0; i < b; i++) {
            L[i / u] |= (key[i] & 0xFF) << (8 * (i % u));
        }

        int[] S = new int[t];
        S[0] = P32;
        for (int i = 1; i < t; i++) {
            S[i] = S[i - 1] + Q32;
        }

        int A = 0, B = 0, i = 0, j = 0;
        int it = 3 * Math.max(t, c);

        for (int k = 0; k < it; k++) {
            A = S[i] = Integer.rotateLeft(S[i] + A + B, 3);
            B = L[j] = Integer.rotateLeft(L[j] + A + B, A + B);
            i = (i + 1) % t;
            j = (j + 1) % c;
        }

        return S;
    }



    private int[] encodeBlock(int A, int B, int[] S) {
        A += S[0];
        B += S[1];

        for (int i = 1; i <= r; i++) {
            A = Integer.rotateLeft(A ^ B, B) + S[2 * i];
            B = Integer.rotateLeft(B ^ A, A) + S[2 * i + 1];
        }
        return new int[]{A, B};
    }



    private byte[] generateKeyStream(int length, byte[] key) {
        int[] S = expandKey(key);
        byte[] gamma = new byte[length];

        long counter = 0;
        int pos = 0;

        while (pos < length) {
            ByteBuffer ctr = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            ctr.putLong(counter);

            int A = ctr.getInt(0);
            int B = ctr.getInt(4);

            int[] enc = encodeBlock(A, B, S);

            ByteBuffer block = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            block.putInt(enc[0]);
            block.putInt(enc[1]);

            int n = Math.min(8, length - pos);
            System.arraycopy(block.array(), 0, gamma, pos, n);

            pos += n;
            counter++;
        }
        return gamma;
    }



    public String encrypt(String text, String key) throws Exception {
        if (text.isEmpty()) {
            throw new Exception("Пустой текст");
        }

        byte[] keyB = prepareKey(key);
        byte[] data = text.getBytes();

        byte[] gamma = generateKeyStream(data.length, keyB);
        byte[] res = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            res[i] = (byte) (data[i] ^ gamma[i]);
        }

        return Base64.getEncoder().encodeToString(res);
    }

    public String decrypt(String text, String key) throws Exception {
        byte[] data;
        try {
            data = Base64.getDecoder().decode(text);
        } catch (Exception e) {
            throw new Exception("Некорректный Base64");
        }

        byte[] keyB = prepareKey(key);
        byte[] gamma = generateKeyStream(data.length, keyB);

        byte[] res = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = (byte) (data[i] ^ gamma[i]);
        }

        return new String(res);
    }



    public String generateRandomKey() {
        byte[] k = new byte[b];
        new SecureRandom().nextBytes(k);
        return Base64.getEncoder().encodeToString(k);
    }

    private byte[] prepareKey(String key) {
        byte[] k;
        try {
            k = Base64.getDecoder().decode(key);
        } catch (Exception e) {
            k = key.getBytes();
        }

        if (k.length < b) {
            byte[] padded = new byte[b];
            System.arraycopy(k, 0, padded, 0, k.length);
            k = padded;
        } else if (k.length > b) {
            k = java.util.Arrays.copyOf(k, b);
        }
        return k;
    }
}
