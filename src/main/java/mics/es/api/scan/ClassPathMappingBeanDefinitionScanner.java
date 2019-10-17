package mics.es.api.scan;


import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Set;

/**
 *  class path Mapping 扫包
 */
public interface ClassPathMappingBeanDefinitionScanner {

    String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    PathMatcher PATH_MATCHER = new AntPathMatcher();

    Set<Class> doScanEsMapping();
}
