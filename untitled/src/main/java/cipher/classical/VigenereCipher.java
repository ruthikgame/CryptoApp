package cipher.classical;

public class VigenereCipher {

    private static final String LATIN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CYRILLIC = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    private String letters;

    public String encode(String text, String key) {
        setupAlphabet(text);
        text = normalize(text);
        key = normalize(key);

        StringBuilder result = new StringBuilder();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char t = text.charAt(i);
            char k = key.charAt(j % key.length());
            int newIndex = (letters.indexOf(t) + letters.indexOf(k)) % letters.length();
            result.append(letters.charAt(newIndex));
            j++;
        }
        return result.toString();
    }

    public String decode(String text, String key) {
        setupAlphabet(text);
        text = normalize(text);
        key = normalize(key);

        StringBuilder result = new StringBuilder();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char t = text.charAt(i);
            char k = key.charAt(j % key.length());
            int newIndex = (letters.indexOf(t) - letters.indexOf(k) + letters.length()) % letters.length();
            result.append(letters.charAt(newIndex));
            j++;
        }
        return result.toString();
    }

    private void setupAlphabet(String src) {
        if (src.matches(".*[А-Яа-яЁё].*")) {
            letters = CYRILLIC;
        } else {
            letters = LATIN;
        }
    }

    private String normalize(String s) {
        s = s.toUpperCase();
        if (letters.equals(CYRILLIC)) {
            return s.replace("Ё", "Е").replaceAll("[^А-Я]", "");
        } else {
            return s.replace("Q", "").replaceAll("[^A-Z]", "");
        }
    }
}
