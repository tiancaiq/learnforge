package com.learnforge.authsdk.gateway.util;

import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.http.HttpUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.learnforge.auth.common.constants.JwtConstants;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.MarkedRunnable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class JwtSignerHolder {

    private volatile JWTSigner jwtSigner;

    private DiscoveryClient discoveryClient;

    public JwtSignerHolder(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    private final ExecutorService ses = new ThreadPoolExecutor(
            1,
            1,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            r -> new Thread(r, "AuthFetchJwkThread")
    );

    @PostConstruct
    public void init(){
        // Attempt to get jwk secret key
        ses.submit(new MarkedRunnable(new JwkTask(discoveryClient)));
    }

    public void shutdown(){
        ses.shutdown();
        log.debug("Destroy loading secret key thread AuthFetchJwkThread");
    }
    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    class JwkTask implements Runnable{
        private final DiscoveryClient discoveryClient;

        public JwkTask(DiscoveryClient discoveryClient) {
            this.discoveryClient = discoveryClient;
        }

        @Override
        public void run() {
            while (jwtSigner == null) {
                try {
                    log.info("Attempt to load auth service address");
                    List<ServiceInstance> instances = discoveryClient.getInstances("auth-service");
                    if(CollUtils.isEmpty(instances)){
                        log.error("Failed to load auth service address, reason: data is empty");
                        sleep(10000);
                        continue;
                    }
                    ServiceInstance instance = instances.get(0);
                    String jwkUri = String.format("http://%s:%d/jwks", instance.getHost(), instance.getPort());
                    log.info("Successfully loaded auth service address, {}", jwkUri);

                    log.info("Attempt to load jwk secret key");
                    // Request to get jwk
                    String result = HttpUtil.get(jwkUri, StandardCharsets.UTF_8);
                    if(result == null){
                        log.error("Failed to load jwk key, reason: data is empty");
                        sleep(10000);
                        continue;
                    }
                    // Analysis
                    PublicKey publicKey = KeyUtil.generatePublicKey(
                            AsymmetricAlgorithm.RSA_ECB_PKCS1.getValue(),
                            SecureUtil.decode(result)
                    );
                    jwtSigner = JWTSignerUtil.createSigner(JwtConstants.JWT_ALGORITHM, publicKey);
                    log.info("Successfully loaded jwk key!");
                } catch (Exception e) {
                    log.error("Failed to load jwk key, reason: {}", e.getMessage());
                    sleep(10000);
                }
            }
            // Close thread pool
            shutdown();
        }
    }
}
