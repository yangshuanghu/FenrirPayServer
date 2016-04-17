package com.fenrir.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by yume on 16/1/13.
 */
public class StringUtil {
    private static final Random random = new Random(System.currentTimeMillis());

    private static final char[] charList = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final DateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public static String getDateStringForMySql(Date date) {
        return DATE_FORMAT.format(date);
    }

    //yyyy/MM/dd hh:mm
    public static Date getDateFromString(String dateString) {
        if(StringUtil.isEmpty(dateString))
            return null;

        Date date = null;
        try {
            date = API_DATE_FORMAT.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String generateRandomString(int length){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++)
            stringBuilder.append(charList[random.nextInt(charList.length)]);
        return stringBuilder.toString();
    }

    /**
     * Input stream to string.
     *
     * @param is the InputStream
     * @return the string from InputStream
     */
    public static String inputStream2String(InputStream is){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder sb = new StringBuilder();

        try {
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char)c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getTokenString(Object... items){
        List<Object> list = Arrays.asList(items, StringUtil.generateRandomString(16));
        return StringUtil.getMD5String(
                Arrays.toString(
                        list.toArray(new Object[list.size()])));
    }

    /**
     * Get string's md5 string.
     *
     * @param str the origin string
     * @return the string's MD5
     */
    public static String getMD5String(String str){
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md5.update(str.getBytes());
        return convertToHexString(md5.digest());
    }

    private static String convertToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte a : b) {
            sb.append(HEX_DIGITS[(a & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[a & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * Separate string by separator.
     *
     * eg: separator = "_"
     * <ul>
     *   <li>someFieldName ---> some_Field_Name</li>
     *   <li>_someFieldName ---> _some_Field_Name</li>
     *   <li>aStringField ---> a_String_Field</li>
     *   <li>aURL ---> a_U_R_L</li>
     * </ul>
     *
     * @param string      the origin string
     * @param separator   the separator
     * @return the separate string.
     */
    public static String separateCamelCase(String string, String separator) {
        StringBuilder translation = new StringBuilder();
        char oldChar = 0;
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            String s = string.substring(0, i);
            if (!s.endsWith(separator) && Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }
            translation.append(character);
            oldChar = character;
        }
        return translation.toString();
    }

    /**
     * Upper case first letter string.
     *
     * <ul>
     *   <li>someFieldName ---> SomeFieldName</li>
     *   <li>_someFieldName ---> _SomeFieldName</li>
     * </ul>
     * 
     * @param string the string
     * @return the string
     */
    public static String upperCaseFirstLetter(String string) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = string.charAt(index);

        while (index < string.length() - 1) {
            if (Character.isLetter(firstCharacter)) {
                break;
            }

            fieldNameBuilder.append(firstCharacter);
            firstCharacter = string.charAt(++index);
        }

        if (index == string.length()) {
            return fieldNameBuilder.toString();
        }

        if (!Character.isUpperCase(firstCharacter)) {
            String modifiedTarget = modifyString(Character.toUpperCase(firstCharacter), string, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        } else {
            return string;
        }
    }

    private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
        return (indexOfSubstring < srcString.length())
                ? firstCharacter + srcString.substring(indexOfSubstring)
                : String.valueOf(firstCharacter);
    }

    public static boolean convertBoolean(String string) {
        if (string == null) {
            return false;
        }
        return string.equals("true") || string.equals("1");
    }

    /**
     * 半角 -> 全角
     */
    public static String convertToFullWidth(String target) {
        return Normalizer.normalize(target, Normalizer.Form.NFKC);
    }

    /**
     * カタカナ以外の文字を排除する
     */
    public static String removeNotKatakana(String target) {
        return target.replaceAll("[^ァ-ー]+", "");
    }

    /**
     * 引数に渡した文字列を全角カタカナのみの文字列に変換する
     */
    public static String convertToKatakana(String target) {
        if (target.isEmpty()) {
            return target;
        }

        StringBuilder builder = new StringBuilder(convertToFullWidth(target));
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (c >= 'ぁ' && c <= 'ん') {
                builder.setCharAt(i, (char) (c - 'ぁ' + 'ァ'));
            }
        }
        return removeNotKatakana(builder.toString());
    }
}
