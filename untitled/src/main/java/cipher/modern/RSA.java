package cipher.modern;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class RSA {

    private KeyPair keyPair;
    private final char[] chars;
    private String lastMessage = "";

    public RSA() {

        String charString = "#абвгдеёжзийклмнопрстуфхцчшщьыъэюя" +
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЬЫЪЭЮЯ" +
                " 0123456789" +
                "abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                ".,!?;:\"'`-–—()[]{}<>@#$%^&*+=/\\|~";

        chars = charString.toCharArray();
    }

    public void generateKeys(BigInteger p, BigInteger q) {
        if (p.equals(q)) throw new IllegalArgumentException("p и q не должны быть равны");

        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.valueOf(65537);
        if (e.compareTo(phi) >= 0) e = BigInteger.valueOf(3);


        while (!e.gcd(phi).equals(BigInteger.ONE)) {
            e = e.add(BigInteger.TWO);
        }

        BigInteger d = e.modInverse(phi);

        keyPair = new KeyPair(n, e, d);
    }

    public String encrypt(String message) {
        if (keyPair == null) throw new IllegalStateException("Сначала сгенерируйте ключи");

        List<String> encryptedParts = new ArrayList<>();
        message = message.toLowerCase();

        for (char c : message.toCharArray()) {
            int index = findCharIndex(c);
            if (index < 0) index = 0;

            BigInteger m = BigInteger.valueOf(index);
            if (m.compareTo(keyPair.n) >= 0)
                throw new IllegalArgumentException("Модуль n слишком мал. Увеличьте p и q");

            BigInteger cipher = m.modPow(keyPair.e, keyPair.n);
            encryptedParts.add(cipher.toString());
        }

        lastMessage = String.join(" ", encryptedParts);
        return lastMessage;
    }

    public String decrypt(String cipherText) {
        if (keyPair == null) throw new IllegalStateException("Сначала сгенерируйте ключи");

        String[] parts = cipherText.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            BigInteger c;
            try {
                c = new BigInteger(part);
            } catch (NumberFormatException ex) {
                result.append('?');
                continue;
            }

            if (c.compareTo(keyPair.n) >= 0 || c.signum() < 0) {
                result.append('?');
                continue;
            }

            BigInteger m = c.modPow(keyPair.d, keyPair.n);
            int index = m.intValue();
            if (index >= 0 && index < chars.length) {
                result.append(chars[index]);
            } else {
                result.append('?');
            }
        }

        return result.toString();
    }

    private int findCharIndex(char c) {
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == c) return i;
        }
        return -1;
    }

    public String publicKey() {
        if (keyPair == null) return "Ключи не сгенерированы";
        return "n: " + keyPair.n + "\ne: " + keyPair.e;
    }

    public String privateKey() {
        if (keyPair == null) return "Ключи не сгенерированы";
        return "d: " + keyPair.d;
    }

    public static BigInteger randomPrime(int bits) {
        SecureRandom random = new SecureRandom();
        return BigInteger.probablePrime(bits, random);
    }


    private static class KeyPair {
        private final BigInteger n;
        private final BigInteger e;
        private final BigInteger d;

        public KeyPair(BigInteger n, BigInteger e, BigInteger d) {
            this.n = n;
            this.e = e;
            this.d = d;
        }
    }
}
