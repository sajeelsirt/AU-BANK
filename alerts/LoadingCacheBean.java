package aubank.retail.liabilities.alerts;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class LoadingCacheBean {

    @Bean
    public LoadingCache<String, String> loadingCache() {
        return CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        return key;
                    }
                });
    }
}
