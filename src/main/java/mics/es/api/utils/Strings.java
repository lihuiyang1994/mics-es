package mics.es.api.utils;

public class Strings {

    public static String forStr(Object... o){
        StringBuilder builder = new StringBuilder();
        for (int i = 0 ; i<o.length ;i++){
            builder.append(o[i]);
        }
        return builder.toString();
    };


    public static String camelToUnderline(String param) {
        if (isEmpty(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static boolean isEmpty(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private Strings(){};
}
