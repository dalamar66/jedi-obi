package com.ldap.dsml;

/**
 * Byte to text encoder using base 64 encoding. To create a base 64 encoding of
 * a byte stream call {@link #translate} for every sequence of bytes and
 * {@link #getCharArray} to mark closure of the byte stream and retrieve the
 * text presentation.
 * 
 * @author Based on code from the Mozilla Directory SDK
 */
public final class Base64Encoder {

	private StringBuffer out = new StringBuffer();

	private int buf = 0; // a 24-bit quantity

	private int buf_bytes = 0; // how many octets are set in it

	private char line[] = new char[74]; // output buffer

	private int line_length = 0; // output buffer fill pointer

	static private final char map[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', // 0-7
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', // 8-15
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', // 16-23
			'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', // 24-31
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', // 32-39
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 40-47
			'w', 'x', 'y', 'z', '0', '1', '2', '3', // 48-55
			'4', '5', '6', '7', '8', '9', '+', '/', // 56-63
	};

	private final void encode_token() {
		int i = line_length;
		line[i] = map[0x3F & (buf >> 18)]; // sextet 1 (octet 1)
		line[i + 1] = map[0x3F & (buf >> 12)]; // sextet 2 (octet 1 and 2)
		line[i + 2] = map[0x3F & (buf >> 6)]; // sextet 3 (octet 2 and 3)
		line[i + 3] = map[0x3F & buf]; // sextet 4 (octet 3)
		line_length += 4;
		buf = 0;
		buf_bytes = 0;
	}

	private final void encode_partial_token() {
		int i = line_length;
		line[i] = map[0x3F & (buf >> 18)]; // sextet 1 (octet 1)
		line[i + 1] = map[0x3F & (buf >> 12)]; // sextet 2 (octet 1 and 2)

		if (buf_bytes == 1)
			line[i + 2] = '=';
		else
			line[i + 2] = map[0x3F & (buf >> 6)]; // sextet 3 (octet 2 and 3)

		if (buf_bytes <= 2)
			line[i + 3] = '=';
		else
			line[i + 3] = map[0x3F & buf]; // sextet 4 (octet 3)
		line_length += 4;
		buf = 0;
		buf_bytes = 0;
	}

	private final void flush_line() {
		out.append(line, 0, line_length);
		line_length = 0;
	}

	/**
	 * Given a sequence of input bytes, produces a sequence of output bytes
	 * using the base64 encoding. If there are bytes in `out' already, the new
	 * bytes are appended, so the caller should do `out.setLength(0)' first if
	 * that's desired.
	 */
	public final void translate(byte[] in) {
		int in_length = in.length;

		for (int i = 0; i < in_length; i++) {
			if (buf_bytes == 0)
				buf = (buf & 0x00FFFF) | (in[i] << 16);
			else if (buf_bytes == 1)
				buf = (buf & 0xFF00FF) | ((in[i] << 8) & 0x00FFFF);
			else
				buf = (buf & 0xFFFF00) | (in[i] & 0x0000FF);

			if ((++buf_bytes) == 3) {
				encode_token();
				if (line_length >= 72) {
					flush_line();
				}
			}

			if (i == (in_length - 1)) {
				if ((buf_bytes > 0) && (buf_bytes < 3))
					encode_partial_token();
				if (line_length > 0)
					flush_line();
			}
		}

		for (int i = 0; i < line.length; i++)
			line[i] = 0;
	}

	public char[] getCharArray() {
		char[] ch;

		if (buf_bytes != 0)
			encode_partial_token();
		flush_line();
		for (int i = 0; i < line.length; i++)
			line[i] = 0;
		ch = new char[out.length()];
		if (out.length() > 0)
			out.getChars(0, out.length(), ch, 0);
		return ch;
	}

}
