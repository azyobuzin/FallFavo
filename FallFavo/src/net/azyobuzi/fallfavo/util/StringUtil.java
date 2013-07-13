package net.azyobuzi.fallfavo.util;

import java.util.Collection;

public class StringUtil {
	public static boolean isNullOrEmpty(CharSequence s) {
    	return s == null || s.length() <= 0;
    }

	public static String join(CharSequence separator, Collection<CharSequence> values) {
		StringBuffer sb = new StringBuffer();
		for (CharSequence value : values) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append(value);
		}
		return sb.toString();
	}
}
