/**
 * Copyright 2012 Ronen Hamias, Anton Kharenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicefabric.socketio.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities methods class. 
 * 
 * @author Anton Kharenko
 *
 */
final class PipelineUtils {

	private static final Logger log = LoggerFactory.getLogger(PipelineUtils.class);
	
	/**
	 * Don't let anyone instantiate this class.
	 */
	private PipelineUtils() {
	}

	public static String getSessionId(final String requestPath) {
		String[] parts = requestPath.split("[/]");

		if (parts.length > 4 && !parts[4].isEmpty()) {
			String[] idsplit = parts[4].split("[?]");

			if (idsplit[0] != null && idsplit[0].length() > 0) {
				return idsplit[0];
			}

			return parts[4];
		}

		return null;
	}

	public static String getOrigin(final HttpRequest req) {
		return req.headers().get(HttpHeaders.Names.ORIGIN);
	}

	public static String extractParameter(QueryStringDecoder queryDecoder, String key) {
		final Map<String, List<String>> params = queryDecoder.parameters();
		List<String> paramsByKey = params.get(key);
		return (paramsByKey != null) ? paramsByKey.get(0) : null;
	}

	public static HttpResponse createHttpResponse(final String origin, ByteBuf content, boolean json) {
		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
		if (json) {
			HttpHeaders.addHeader(res, HttpHeaders.Names.CONTENT_TYPE, "text/javascript; charset=UTF-8");
		} else {
			HttpHeaders.addHeader(res, HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
		}
		HttpHeaders.addHeader(res, HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		if (origin != null) {
			res.headers().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			res.headers().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		}
		HttpHeaders.setContentLength(res, content.readableBytes());

		return res;
	}

	public static ByteBuf copiedBuffer(ByteBufAllocator allocator, String message) {
		ByteBuf buffer = allocator.buffer();
		buffer.writeBytes(message.getBytes(CharsetUtil.UTF_8));
		return buffer;
	}
   
	public static SocketAddress getHeaderClientIPParamValue(HttpMessage message, String paramName) {

		SocketAddress result = null;

		if (paramName != null && !paramName.trim().isEmpty()) {
			String ip = null;
			try {
				ip = HttpHeaders.getHeader(message, paramName);
				if (ip != null) {
					result = new InetSocketAddress(InetAddress.getByName(ip), 0);
				}
			} catch (Exception e) {
				log.warn("Failed to parse IP address: {} from http header: {}", ip, paramName);
			}
		}
		return result;
	}

}
