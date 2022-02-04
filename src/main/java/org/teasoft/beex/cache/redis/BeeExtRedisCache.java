/*
 * Copyright 2016-2021 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.cache.redis;

import org.teasoft.bee.osql.Serializer;
import org.teasoft.honey.osql.core.DefaultBeeExtCache;
import org.teasoft.honey.osql.core.Logger;

import redis.clients.jedis.Jedis;

/**
 * @author Kingstar
 * @since  1.11
 */
public class BeeExtRedisCache extends DefaultBeeExtCache {

	Jedis jedis; //TODO
	Serializer serializer = new JdkSerializer();

	private static final String id = "_SYS_BEE_REDIS_CACHE";
	private static final byte[] idBytes = id.getBytes();

	@Override
	public Object getInExtCache(String key) {
		Logger.info("doing in getInExtCache  for BeeExtRedisCache");

		return serializer.unserialize(jedis.hget(idBytes, key.toString().getBytes()));
	}

	@Override
	public void addInExtCache(String key, Object result) {
		Logger.info("doing in addInExtCache  for BeeExtRedisCache");
		jedis.hset(idBytes, key.toString().getBytes(), serializer.serialize(result));
	}

	@Override
	public void clearInExtCache(String key) {
		Logger.info("doing in clearInExtCache  for BeeExtRedisCache");
		jedis.hdel(id, key.toString());
	}

}
