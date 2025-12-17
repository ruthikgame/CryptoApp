package cipher.classical;

public class CaesarCipher {

    private int shift = 0;


    public void setShift(int shift) {
        this.shift = shift;
    }

    public String encrypt(String text) {
        return shiftText(text, shift);
    }

    public String decrypt(String text) {
        return shiftText(text, -shift);
    }

    public String bruteForce(String text) {
        StringBuilder sb = new StringBuilder();

        sb.append("=== ЛАТИНИЦА ===\n");
        for (int k = 1; k < 26; k++) {
            sb.append("Сдвиг ").append(k).append(": ")
                    .append(shiftLatin(text, -k))
                    .append("\n");
        }

        sb.append("\n=== КИРИЛЛИЦА ===\n");
        for (int k = 1; k < 33; k++) {
            sb.append("Сдвиг ").append(k).append(": ")
                    .append(shiftCyrillic(text, -k))
                    .append("\n");
        }

        return sb.toString();
    }



    private String shiftText(String text, int s) {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (isLatinLower(c)) {
                result.append(shiftLatinChar(c, s));
            } else if (isLatinUpper(c)) {
                result.append(shiftLatinCharUpper(c, s));
            } else if (isCyrillicLower(c)) {
                result.append(shiftCyrillicChar(c, s));
            } else if (isCyrillicUpper(c)) {
                result.append(shiftCyrillicCharUpper(c, s));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }



    private char shiftLatinChar(char c, int s) {
        int k = ((s % 26) + 26) % 26;
        return (char) ('a' + (c - 'a' + k) % 26);
    }

    private char shiftLatinCharUpper(char c, int s) {
        int k = ((s % 26) + 26) % 26;
        return (char) ('A' + (c - 'A' + k) % 26);
    }

    private String shiftLatin(String text, int s) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (isLatinLower(c)) {
                sb.append(shiftLatinChar(c, s));
            } else if (isLatinUpper(c)) {
                sb.append(shiftLatinCharUpper(c, s));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }



    private char shiftCyrillicChar(char c, int s) {
        int k = ((s % 33) + 33) % 33;
        return (char) ('а' + (c - 'а' + k) % 33);
    }

    private char shiftCyrillicCharUpper(char c, int s) {
        int k = ((s % 33) + 33) % 33;
        return (char) ('А' + (c - 'А' + k) % 33);
    }

    private String shiftCyrillic(String text, int s) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (isCyrillicLower(c)) {
                sb.append(shiftCyrillicChar(c, s));
            } else if (isCyrillicUpper(c)) {
                sb.append(shiftCyrillicCharUpper(c, s));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }



    private boolean isLatinLower(char c) {
        return c >= 'a' && c <= 'z';
    }

    private boolean isLatinUpper(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private boolean isCyrillicLower(char c) {
        return c >= 'а' && c <= 'я';
    }

    private boolean isCyrillicUpper(char c) {
        return c >= 'А' && c <= 'Я';
    }
}
