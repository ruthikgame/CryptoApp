package cipher.modern;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

public class RC5Block {

    private final int r = 12;
    private final int w = 32;
    private final int b = 16;
    private final int u = w / 8;
    private final int t = 2 * (r + 1);

    private final int P32 = 0xB7E15163;
    private final int Q32 = 0x9E3779B9;



    private int[] expandKey(byte[] key) {
        if (key.length < b) {
            key = Arrays.copyOf(key, b);
        } else if (key.length > b) {
            key = Arrays.copyOf(key, b);
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



    private int[] encryptBlock(int A, int B, int[] S) {
        A += S[0];
        B += S[1];

        for (int i = 1; i <= r; i++) {
            A = Integer.rotateLeft(A ^ B, B) + S[2 * i];
            B = Integer.rotateLeft(B ^ A, A) + S[2 * i + 1];
        }
        return new int[]{A, B};
    }

    private int[] decryptBlock(int A, int B, int[] S) {
        for (int i = r; i >= 1; i--) {
            B = Integer.rotateLeft(B - S[2 * i + 1], -A) ^ A;
            A = Integer.rotateLeft(A - S[2 * i], -B) ^ B;
        }
        B -= S[1];
        A -= S[0];
        return new int[]{A, B};
    }



    private byte[] pad(byte[] data) {
        int block = 8;
        int pad = block - (data.length % block);
        if (pad == 0) pad = block;

        byte[] out = Arrays.copyOf(data, data.length + pad);
        Arrays.fill(out, data.length, out.length, (byte) pad);
        return out;
    }

    private byte[] unpad(byte[] data) throws Exception {
        if (data.length == 0) throw new Exception("Пустые данные");

        int pad = data[data.length - 1] & 0xFF;
        if (pad < 1 || pad > 8 || pad > data.length)
            throw new Exception("Некорректный паддинг");

        for (int i = data.length - pad; i < data.length; i++) {
            if ((data[i] & 0xFF) != pad)
                throw new Exception("Некорректный паддинг");
        }
        return Arrays.copyOf(data, data.length - pad);
    }



    public String encrypt(String text, String key) throws Exception {
        if (text.isEmpty())
            throw new Exception("Пустой текст для шифрования");

        byte[] keyB = prepareKey(key);
        int[] S = expandKey(keyB);

        byte[] data = pad(text.getBytes());
        byte[] out = new byte[data.length];

        for (int i = 0; i < data.length; i += 8) {
            int A = ByteBuffer.wrap(data, i, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int B = ByteBuffer.wrap(data, i + 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            int[] enc = encryptBlock(A, B, S);

            ByteBuffer.wrap(out, i, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(enc[0]);
            ByteBuffer.wrap(out, i + 4, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(enc[1]);
        }

        return Base64.getEncoder().encodeToString(out);
    }

    public String decrypt(String text, String key) throws Exception {
        byte[] data;
        try {
            data = Base64.getDecoder().decode(text);
        } catch (Exception e) {
            throw new Exception("Некорректный Base64");
        }

        if (data.length % 8 != 0)
            throw new Exception("Длина шифра должна быть кратна 8");

        byte[] keyB = prepareKey(key);
        int[] S = expandKey(keyB);

        byte[] out = new byte[data.length];

        for (int i = 0; i < data.length; i += 8) {
            int A = ByteBuffer.wrap(data, i, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int B = ByteBuffer.wrap(data, i + 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            int[] dec = decryptBlock(A, B, S);

            ByteBuffer.wrap(out, i, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(dec[0]);
            ByteBuffer.wrap(out, i + 4, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(dec[1]);
        }

        return new String(unpad(out));
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

        if (k.length < b) return Arrays.copyOf(k, b);
        if (k.length > b) return Arrays.copyOf(k, b);
        return k;
    }
}
