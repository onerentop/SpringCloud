package org.vista.ecms.turbine.dashboard;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Verticle for the Dashboard web app.
 * </p>
 * <p>
 * Serves static content and proxy the streams
 * </p>
 *
 * @author Kennedy Oliveira
 */
public class HystrixDashboardVerticle extends AbstractVerticle {

	private final static Logger log = LoggerFactory.getLogger(HystrixDashboardVerticle.class);

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		vertx.sharedData().getCounter("instance-count", counterAsync -> {
			if (counterAsync.failed()) {
				startFuture.fail(counterAsync.cause());
				System.out.println("counterAsync.failed!!!!!!!!!!!!!!!!!!!!");
			} else {
				counterAsync.result().incrementAndGet(counter -> {
					if (counter.failed()) {
						System.out.println("counter.failed!!!!!!!!!!!!!!!!!!!!");
						startFuture.fail(counter.cause());
					} else {
						System.out.println("succeed!!!!!!!!!!!!!!!!!!!!");
						log.info("Initializing the HystrixDashboardVerticle instance {}", counter.result());
						initialize(startFuture);
					}
				});
			}
		});
	}

	/**
	 * Initialize the Verticle and setup the Http Server
	 *
	 * @param startFuture
	 *            The start future to pass the initialization result
	 */
	private void initialize(Future<Void> startFuture) {
		final Router mainRouter = createRoutes();
		final HttpServerOptions options = getServerOptions();

		startServer(startFuture, mainRouter, options);
	}

	/**
	 * Start the HTTP server.
	 *
	 * @param startFuture
	 *            The start future to report failure or success for async start.
	 * @param mainRouter
	 *            {@link Router} with the routes for the server.
	 * @param options
	 *            {@link HttpServerOptions} with the server configuration.
	 */
	private void startServer(Future<Void> startFuture, Router mainRouter, HttpServerOptions options) {

		// System.out.println("===="+);
		vertx.createHttpServer(options).requestHandler(mainRouter::accept)// mainRouter.accept(request);
				.listen(res -> {
					if (res.failed()) {
						startFuture.fail(res.cause());
					} else {
						log.info("Listening on port: {}", options.getPort());
						log.info("Access the dashboard in your browser: http://{}:{}/hystrix-dashboard/",
								"0.0.0.0".equals(options.getHost()) ? "localhost" : options.getHost(), // NOPMD
								res.result().actualPort());
						startFuture.complete();
					}
				});
	}

	/**
	 * <p>
	 * Read configuration for different sources and generate a
	 * {@link HttpServerOptions} with the configurations provided.
	 * </p>
	 * <p>
	 * Currently reading configuration options from:
	 * <ul>
	 * <li>Vert.x Config Option</li>
	 * <li>Command Line Option passed with {@code -D}</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The list above is from the lesser priority to the highest, so currently the
	 * command line options is the highest priority and will override any other
	 * configuration option
	 * </p>
	 *
	 * @return {@link HttpServerOptions} with configuration for the server.
	 */
	private HttpServerOptions getServerOptions() {
		final Integer systemServerPort = Integer.getInteger(Configuration.SERVER_PORT);
		final String systemBindAddress = System.getProperty(Configuration.BIND_ADDRESS);
		final String systemDisableCompression = System.getProperty(Configuration.DISABLE_COMPRESSION);

		final Integer serverPort = systemServerPort != null ? systemServerPort
				: config().getInteger(Configuration.SERVER_PORT, 7979);
		final String bindAddress = systemBindAddress != null ? systemBindAddress
				: config().getString(Configuration.BIND_ADDRESS, "0.0.0.0"); // NOPMD
		final boolean disableCompression = systemDisableCompression != null ? Boolean.valueOf(systemDisableCompression)
				: config().getBoolean(Configuration.DISABLE_COMPRESSION, Boolean.FALSE);
		final HttpServerOptions options = new HttpServerOptions().setTcpKeepAlive(true).setIdleTimeout(10000)
				.setPort(serverPort).setHost(bindAddress).setCompressionSupported(!disableCompression);

		log.info("Compression support enabled: {}", !disableCompression);
		return options;
	}

	/**
	 * Create the routes for dashboard app
	 *
	 * @return A {@link Router} with all the routes needed for the app.
	 */
	private Router createRoutes() {

		final Router hystrixRouter = Router.router(vertx);

		// proxy stream handler
		// http://localhost:7979/hystrix-dashboard/proxy.stream?origin=http://localhost:9000/hystrix.stream
		hystrixRouter.get("/proxy.stream").handler(HystrixDashboardProxyConnectionHandler.create());

		// proxy the eureka apps listing
		// http://localhost:7979/hystrix-dashboard/eureka?url=http://10.7.254.90:8082
		hystrixRouter.get("/eureka").handler(HystrixDashboardProxyEurekaAppsListingHandler.create(vertx));

		hystrixRouter.get("/login").handler(context -> {
			context.response().sendFile("login.html");

		});

		// main
		hystrixRouter.get("/main").handler(context -> { // context.request().getParam("password")
			Cookie u_cookie = context.getCookie("username");
			Cookie p_cookie = context.getCookie("password");
			
			if (u_cookie!=null&&p_cookie!=null&&"admin".equals(u_cookie.getValue()) && "admin".equals(p_cookie.getValue())) {
				System.out.println("main-----" + u_cookie.getValue() + "---------" + p_cookie.getValue());
				context.response().sendFile("main.html");
			} else {
				context.response().sendFile("login.html");
			}

		});
		// monitor
		hystrixRouter.get("/monitor").handler(context -> { // context.request().getParam("password")
			Cookie u_cookie = context.getCookie("username");
			Cookie p_cookie = context.getCookie("password");
			
			if (u_cookie!=null&&p_cookie!=null&&"admin".equals(u_cookie.getValue()) && "admin".equals(p_cookie.getValue())) {
				System.out.println("monitor-----" + u_cookie.getValue() + "---------" + p_cookie.getValue());
			/*	String stream = context.request().getParam("stream");
				System.out.println("stream----" + stream);*/
				context.response().sendFile("webroot/monitor/monitor.html");
			} else {
				context.response().sendFile("login.html");
			}
		});

		hystrixRouter.route("/*")
				.handler(StaticHandler.create().setCachingEnabled(true).setCacheEntryTimeout(1000L * 60 * 60 * 24));

		final Router mainRouter = Router.router(vertx);
		// if send a route without the trailing '/' some problems will occur, so i
		// redirect the guy using the trailing '/'
		mainRouter.route("/hystrix-dashboard").handler(context -> {
			if (context.request().path().endsWith("/")) {
				// context.next();
				context.response().sendFile("login.html");
			} else {
				context.response().setStatusCode(HttpResponseStatus.MOVED_PERMANENTLY.code())
						.putHeader(HttpHeaders.LOCATION, "/hystrix-dashboard/").end();
			}
		});

		/*
		 * JsonObject authConfig = new JsonObject().put("keyStore", new JsonObject()
		 * .put("type", "jceks") .put("path", "keystore.jceks") .put("password",
		 * "secret"));
		 * 
		 * JWTAuth authProvider = JWTAuth.create(vertx, authConfig);
		 * 
		 * AuthHandler basicAuthHandler = BasicAuthHandler.create(authProvider);
		 */
		mainRouter.route("/*").handler(CookieHandler.create());
		mainRouter.mountSubRouter("/hystrix-dashboard", hystrixRouter);
		return mainRouter;
	}
}
