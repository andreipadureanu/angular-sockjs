package ro.home;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.*;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.SpringServletContainerInitializer;


import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.*;
import java.security.cert.CertificateException;


import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;


public class Main {

    private static final String rootResourcePath = "/angular-sockjs";
    private static final Integer SERVER_PORT = 9090;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static Undertow server;

    public static void main(String[] args) throws Exception {

        LOGGER.info("Embedded Undertow server is starting.");

        ServletContainerInitializerInfo servletContainerInitializerInfo = injectSpringWebAppInitializerIntoUndertowContainer();
        // Deploy app into container and get deployment manager.
        DeploymentManager manager = initialiseDeploymentManager(servletContainerInitializerInfo);

        // Add Handlers and start server
        PathHandler path = Handlers.path().addPrefixPath(rootResourcePath , manager.start());

        //SSLContext sslContext = createSSLContext(loadKeyStore("./gcrsSecureUser.jks", "rr599904"), "rr599904", null);

        server = Undertow.builder()
                .addHttpListener(SERVER_PORT, "0.0.0.0")
                //.addHttpsListener(SERVER_PORT, "0.0.0.0", sslContext)
                .setHandler(path)
                .build();

        server.start();
        LOGGER.info("Undertow server started on port : " + SERVER_PORT);
        LOGGER.info("Hit enter to stop it...");
        System.in.read();
        manager.getDeployment().getApplicationListeners().stop();
        server.stop();
        LOGGER.info("Server stopped on user command.");
    }

    /*
    private static IdentityManager initialiseIdentityManaqer(){
        Map<String, char[]> users = new HashMap<>();
        users.put("secureUser", "******".toCharArray());
        return new MapIdentityManager(users);
    }
    private static SecurityConstraint initializeSecurityConstraint(String urlPattern, String userAllowed) {
        WebResourceCollection  webResourceCollection = new WebResourceCollection();
        webResourceCollection.addUrlPatterns(urlPattern);

        SecurityConstraint secureConstraint = new SecurityConstraint();
        secureConstraint.addWebResourceCollection(webResourceCollection);
        if (userAllowed != null)  {
            secureConstraint.addRoleAllowed(userAllowed);
        }

        return secureConstraint;
    }
    private static LoginConfig initialiseLoginConfig(String securityRealm, String authorizationMethod) {
        AuthMethodConfig authConfig = new AuthMethodConfig(authorizationMethod);
        LoginConfig loginConfig = new LoginConfig(securityRealm);
        loginConfig.getAuthMethods().add(authConfig);
        return loginConfig;
    }
    */

    private static DeploymentManager initialiseDeploymentManager(ServletContainerInitializerInfo servletContainerInitializerInfo) throws IOException, Exception {
        addClassPath(".");
        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(Main.class.getClassLoader())
                .setResourceManager(new ClassPathResourceManager(Main.class.getClassLoader(), "META-INF/resources"))
                .setContextPath(rootResourcePath)
                .setDeploymentName("")
                .setDefaultEncoding("UTF-8")
                .setUrlEncoding("UTF-8")
                .setDefaultServletConfig(new DefaultServletConfig(true, new HashSet(Arrays.asList("resolve-against-context-root"))))
                .addWelcomePage("index.html")
                .addServletContainerInitalizer(servletContainerInitializerInfo);

        servletBuilder.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, new WebSocketDeploymentInfo());

        DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(servletBuilder);
        deploymentManager.deploy();
        return deploymentManager;
    }

    private static ServletContainerInitializerInfo injectSpringWebAppInitializerIntoUndertowContainer() {
        Set<Class<?>> webappinit = new HashSet<>();
        webappinit.add(Bootstrap.class);

        // add additional WebAppInitializers if needed
        SpringServletContainerInitializer initializer = new SpringServletContainerInitializer();
        InstanceFactory<SpringServletContainerInitializer> instanceFactory = new ImmediateInstanceFactory<>(initializer);
        return new ServletContainerInitializerInfo(SpringServletContainerInitializer.class, instanceFactory, webappinit);
    }

    private static SSLContext createSSLContext(final KeyStore keyStore, String keyStorePassword, final KeyStore trustStore)
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {

        KeyManager[] keyManagers;

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
        keyManagers = keyManagerFactory.getKeyManagers();

        TrustManager[] trustManagers;
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        return sslContext;
    }

    private static KeyStore loadKeyStore(String keyStoreName, String keyStorePassWord)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {

        final InputStream stream = new FileInputStream(keyStoreName);
        if (stream == null) {
            throw new RuntimeException("Could not load keystore");
        }
        try (InputStream is = stream) {
            KeyStore loadedKeystore = KeyStore.getInstance("JKS");
            loadedKeystore.load(is, keyStorePassWord.toCharArray());
            return loadedKeystore;
        }
    }


    private static void addClassPath(String s) throws Exception {
        File f = new File(s);
        URI u = f.toURI();
        //URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        //Class<URLClassLoader> urlClass = URLClassLoader.class;
        Class.forName(ServiceLoader.class.getName(), true, new URLClassLoader(new URL[]{u.toURL()}));
        /*
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{u.toURL()});
        */
    }
}
