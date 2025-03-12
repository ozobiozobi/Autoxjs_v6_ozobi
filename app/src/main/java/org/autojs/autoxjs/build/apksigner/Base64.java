package org.autojs.autoxjs.build.apksigner;

import org.spongycastle.util.encoders.Base64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** Base64 encoding handling in a portable way across Android and JSE. */
public class Base64 {

	public static String encode(byte[] data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			new Base64Encoder().encode(data, 0, data.length, baos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new String(baos.toByteArray());
	}

}