/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.access;

import java.io.File;
import java.io.IOException;

import com.healthmarketscience.jackcess.CryptCodecProvider;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import net.ucanaccess.jdbc.JackcessOpenerInterface;

/**
 * @author Kingstar
 * @since  2.0
 */
public class BeeAccessCryptOpener implements JackcessOpenerInterface {

	@Override
	public Database open(File file, String _pwd0) throws IOException {
		DatabaseBuilder builder = new DatabaseBuilder(file);
		builder.setAutoSync(false);
		builder.setCodecProvider(new CryptCodecProvider(_pwd0));
		builder.setReadOnly(false);
		return builder.open();
	}

}
