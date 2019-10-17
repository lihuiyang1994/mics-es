package mics.es.api;

import lombok.extern.slf4j.Slf4j;
import mics.es.api.scan.ClassPathMappingBeanDefinitionScanner;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

/**
 * Top-level ES Properties convert class
 * @author lhy
 */
@Slf4j
public abstract class BasePropertiesConvert implements ClassPathMappingBeanDefinitionScanner {

    protected BaseProperties properties;

    protected final String MAPPING_ANNOTATION = "mics.es.api.aop.ESMapping";

    @Nullable
    private static Method equinoxResolveMethod;

    static {
        try {
            // Detect Equinox OSGi (e.g. on WebSphere 6.1)
            Class<?> fileLocatorClass = ClassUtils.forName("org.eclipse.core.runtime.FileLocator",
                    PathMatchingResourcePatternResolver.class.getClassLoader());
            equinoxResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
            log.debug("Found Equinox FileLocator for OSGi bundle URL resolution");
        }
        catch (Throwable ex) {
            equinoxResolveMethod = null;
        }
    }

    public BasePropertiesConvert(BaseProperties properties){
        this.properties = properties;
    }
    public BasePropertiesConvert(){
    }
    public void setProperties(BaseProperties properties){
        this.properties = properties;
    }


    @Override
    public Set<Class> doScanEsMapping() {
        final String[] basePackages = properties.getBasePackage();
        Set<Class> classSet = new HashSet<>();
        Assert.notEmpty(basePackages,"At least one base package must be specified");
        for (String basePackage: basePackages) {
            Set<Class> classes = findMappingBeanClass(basePackage);
            classSet.addAll(classes);
        }
        return classSet;
    }

    /**
     * Return the MetadataReaderFactory used by this component provider.
     */
    public final MetadataReaderFactory getMetadataReaderFactory() {
            return new SimpleMetadataReaderFactory();
    }

    protected  Set<Class> findMappingBeanClass(String basePackage){
        Set<Class> classes = new LinkedHashSet<>();
        try {
            String packageSearchPath = CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = getResources(packageSearchPath);
            boolean traceEnabled = log.isTraceEnabled();
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    try {
                        MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                        Set<String> annotationTypes = annotationMetadata.getAnnotationTypes();
                        boolean b = annotationTypes.contains(MAPPING_ANNOTATION);
                        if (b){
                            String className = metadataReader.getClassMetadata().getClassName();
                            Class<?> clazz = Class.forName(className);
                            classes.add(clazz);
                        }
                    }
                    catch (Throwable ex) {
                        throw new BeanDefinitionStoreException(
                                "Failed to read candidate component class: " + resource, ex);
                    }
                }
                else {
                    if (traceEnabled) {
                        log.trace("Ignored because not readable: " + resource);
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
        return classes;
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return (metadata.isIndependent() && (metadata.isConcrete() ||
                (metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName()))));
    }

    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        return true;
    }

    protected Resource[] getResources(String locationPattern) throws IOException {
        Assert.notNull(locationPattern, "Location pattern must not be null");
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            // a class path resource (multiple resources for same name possible)
            if (PATH_MATCHER.isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
                // a class path resource pattern
                return findPathMatchingResources(locationPattern);
            }
            else {
                // all class path resources with the given name
                return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
            }
        }
        else {
            // Generally only look for a pattern after a prefix here,
            // and on Tomcat only after the "*/" separator for its "war:" protocol.
            int prefixEnd = (locationPattern.startsWith("war:") ? locationPattern.indexOf("*/") + 1 :
                    locationPattern.indexOf(':') + 1);
            if (PATH_MATCHER.isPattern(locationPattern.substring(prefixEnd))) {
                // a file pattern
                return findPathMatchingResources(locationPattern);
            }
          /*  else {
                // a single resource with the given name
                return new Resource[] {getResourceLoader().getResource(locationPattern)};
            }*/
        }
        return null;
    }

    public ClassLoader getClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }
    protected Resource[] findAllClassPathResources(String location) throws IOException {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Set<Resource> result = doFindAllClassPathResources(path);
        if (log.isDebugEnabled()) {
            log.debug("Resolved classpath location [" + location + "] to resources " + result);
        }
        return result.toArray(new Resource[0]);
    }
    protected  Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        for (Resource rootDirResource : rootDirResources) {
            rootDirResource = rootDirResource;
            URL rootDirUrl = rootDirResource.getURL();
            if (equinoxResolveMethod != null && rootDirUrl.getProtocol().startsWith("bundle")) {
                URL resolvedUrl = (URL) ReflectionUtils.invokeMethod(equinoxResolveMethod, null, rootDirUrl);
                if (resolvedUrl != null) {
                    rootDirUrl = resolvedUrl;
                }
                rootDirResource = new UrlResource(rootDirUrl);
            }

            else if (ResourceUtils.isJarURL(rootDirUrl)) {
                result.addAll(doFindPathMatchingJarResources(rootDirResource, rootDirUrl, subPattern));
            }
            else {
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Resolved location pattern [" + locationPattern + "] to resources " + result);
        }
        return result.toArray(new Resource[0]);
    };
    /**
     *
     * @param rootDirResource
     * @param rootDirURL
     * @param subPattern
     * @return
     * @throws IOException
     */
    protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, URL rootDirURL, String subPattern)
            throws IOException {

        URLConnection con = rootDirURL.openConnection();
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean closeJarFile;

        if (con instanceof JarURLConnection) {
            // Should usually be the case for traditional JAR files.
            JarURLConnection jarCon = (JarURLConnection) con;
            ResourceUtils.useCachesIfNecessary(jarCon);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
            closeJarFile = !jarCon.getUseCaches();
        }
        else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = rootDirURL.getFile();
            try {
                int separatorIndex = urlFile.indexOf(ResourceUtils.WAR_URL_SEPARATOR);
                if (separatorIndex == -1) {
                    separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
                }
                if (separatorIndex != -1) {
                    jarFileUrl = urlFile.substring(0, separatorIndex);
                    rootEntryPath = urlFile.substring(separatorIndex + 2);  // both separators are 2 chars
                    jarFile = getJarFile(jarFileUrl);
                }
                else {
                    jarFile = new JarFile(urlFile);
                    jarFileUrl = urlFile;
                    rootEntryPath = "";
                }
                closeJarFile = true;
            }
            catch (ZipException ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping invalid jar classpath entry [" + urlFile + "]");
                }
                return Collections.emptySet();
            }
        }

        try {
            if (log.isDebugEnabled()) {
                log.debug("Looking for matching resources in jar file [" + jarFileUrl + "]");
            }
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }
            Set<Resource> result = new LinkedHashSet<>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (PATH_MATCHER.match(subPattern, relativePath)) {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
            }
            return result;
        }
        finally {
            if (closeJarFile) {
                jarFile.close();
            }
        }
    }
    /**
     * Resolve the given jar file URL into a JarFile object.
     */
    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            try {
                return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
            }
            catch (URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new JarFile(jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX.length()));
            }
        }
        else {
            return new JarFile(jarFileUrl);
        }
    }
    protected String determineRootDir(String location) {
        int prefixEnd = location.indexOf(':') + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && PATH_MATCHER.isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }
    protected  String resolveBasePackage(String basePackage){
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    };
    /**
     * Find all class location resources with the given path via the ClassLoader.
     * Called by {@link #findAllClassPathResources(String)}.
     * @param path the absolute path within the classpath (never a leading slash)
     * @return a mutable Set of matching Resource instances
     * @since 4.1.1
     */
    protected Set<Resource> doFindAllClassPathResources(String path) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(16);
        ClassLoader cl = getClassLoader();
        Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        if ("".equals(path)) {
            // The above result is likely to be incomplete, i.e. only containing file system references.
            // We need to have pointers to each of the jar files on the classpath as well...
            addAllClassLoaderJarRoots(cl, result);
        }
        return result;
    }
    /**
     * Search all {@link URLClassLoader} URLs for jar file references and add them to the
     * given set of resources in the form of pointers to the root of the jar file content.
     * @param classLoader the ClassLoader to search (including its ancestors)
     * @param result the set of resources to add jar roots to
     * @since 4.1.1
     */
    protected void addAllClassLoaderJarRoots(@Nullable ClassLoader classLoader, Set<Resource> result) {
        if (classLoader instanceof URLClassLoader) {
            try {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    try {
                        UrlResource jarResource = new UrlResource(
                                ResourceUtils.JAR_URL_PREFIX + url + ResourceUtils.JAR_URL_SEPARATOR);
                        if (jarResource.exists()) {
                            result.add(jarResource);
                        }
                    }
                    catch (MalformedURLException ex) {
                        if (log.isDebugEnabled()) {
                            log.debug("Cannot search for matching files underneath [" + url +
                                    "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
                        }
                    }
                }
            }
            catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot introspect jar files since ClassLoader [" + classLoader +
                            "] does not support 'getURLs()': " + ex);
                }
            }
        }

        if (classLoader == ClassLoader.getSystemClassLoader()) {
            // "java.class.path" manifest evaluation...
            addClassPathManifestEntries(result);
        }

        if (classLoader != null) {
            try {
                // Hierarchy traversal...
                addAllClassLoaderJarRoots(classLoader.getParent(), result);
            }
            catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot introspect jar files in parent ClassLoader since [" + classLoader +
                            "] does not support 'getParent()': " + ex);
                }
            }
        }
    }
    /**
     * Determine jar file references from the "java.class.path." manifest property and add them
     * to the given set of resources in the form of pointers to the root of the jar file content.
     * @param result the set of resources to add jar roots to
     * @since 4.3
     */
    protected void addClassPathManifestEntries(Set<Resource> result) {
        try {
            String javaClassPathProperty = System.getProperty("java.class.path");
            for (String path : StringUtils.delimitedListToStringArray(
                    javaClassPathProperty, System.getProperty("path.separator"))) {
                try {
                    String filePath = new File(path).getAbsolutePath();
                    int prefixIndex = filePath.indexOf(':');
                    if (prefixIndex == 1) {
                        // Possibly "c:" drive prefix on Windows, to be upper-cased for proper duplicate detection
                        filePath = StringUtils.capitalize(filePath);
                    }
                    UrlResource jarResource = new UrlResource(ResourceUtils.JAR_URL_PREFIX +
                            ResourceUtils.FILE_URL_PREFIX + filePath + ResourceUtils.JAR_URL_SEPARATOR);
                    // Potentially overlapping with URLClassLoader.getURLs() result above!
                    if (!result.contains(jarResource) && !hasDuplicate(filePath, result) && jarResource.exists()) {
                        result.add(jarResource);
                    }
                }
                catch (MalformedURLException ex) {
                    if (log.isDebugEnabled()) {
                        log.debug("Cannot search for matching files underneath [" + path +
                                "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
                    }
                }
            }
        }
        catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to evaluate 'java.class.path' manifest entries: " + ex);
            }
        }
    }
    /**
     * Check whether the given file path has a duplicate but differently structured entry
     * in the existing result, i.e. with or without a leading slash.
     * @param filePath the file path (with or without a leading slash)
     * @param result the current result
     * @return {@code true} if there is a duplicate (i.e. to ignore the given file path),
     * {@code false} to proceed with adding a corresponding resource to the current result
     */
    private boolean hasDuplicate(String filePath, Set<Resource> result) {
        if (result.isEmpty()) {
            return false;
        }
        String duplicatePath = (filePath.startsWith("/") ? filePath.substring(1) : "/" + filePath);
        try {
            return result.contains(new UrlResource(ResourceUtils.JAR_URL_PREFIX + ResourceUtils.FILE_URL_PREFIX +
                    duplicatePath + ResourceUtils.JAR_URL_SEPARATOR));
        }
        catch (MalformedURLException ex) {
            // Ignore: just for testing against duplicate.
            return false;
        }
    }
    /**
     * Convert the given URL as returned from the ClassLoader into a {@link Resource}.
     * <p>The default implementation simply creates a {@link UrlResource} instance.
     * @param url a URL as returned from the ClassLoader
     * @return the corresponding Resource object
     * @see java.lang.ClassLoader#getResources
     * @see org.springframework.core.io.Resource
     */
    protected Resource convertClassLoaderURL(URL url) {
        return new UrlResource(url);
    }
    /**
     * Find all resources in the file system that match the given location pattern
     * via the Ant-style PathMatcher.
     * @param rootDirResource the root directory as Resource
     * @param subPattern the sub pattern to match (below the root directory)
     * @return a mutable Set of matching Resource instances
     * @throws IOException in case of I/O errors
     * @see #retrieveMatchingFiles
     * @see org.springframework.util.PathMatcher
     */
    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern)
            throws IOException {

        File rootDir;
        try {
            rootDir = rootDirResource.getFile().getAbsoluteFile();
        }
        catch (IOException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Cannot search for matching files underneath " + rootDirResource +
                        " because it does not correspond to a directory in the file system", ex);
            }
            return Collections.emptySet();
        }
        return doFindMatchingFileSystemResources(rootDir, subPattern);
    }
    /**
     * Find all resources in the file system that match the given location pattern
     * via the Ant-style PathMatcher.
     * @param rootDir the root directory in the file system
     * @param subPattern the sub pattern to match (below the root directory)
     * @return a mutable Set of matching Resource instances
     * @throws IOException in case of I/O errors
     * @see #retrieveMatchingFiles
     * @see org.springframework.util.PathMatcher
     */
    protected Set<Resource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Looking for matching resources in directory tree [" + rootDir.getPath() + "]");
        }
        Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
        Set<Resource> result = new LinkedHashSet<>(matchingFiles.size());
        for (File file : matchingFiles) {
            result.add(new FileSystemResource(file));
        }
        return result;
    }
    /**
     * Retrieve files that match the given path pattern,
     * checking the given directory and its subdirectories.
     * @param rootDir the directory to start from
     * @param pattern the pattern to match against,
     * relative to the root directory
     * @return a mutable Set of matching Resource instances
     * @throws IOException if directory contents could not be retrieved
     */
    protected Set<File> retrieveMatchingFiles(File rootDir, String pattern) throws IOException {
        if (!rootDir.exists()) {
            // Silently skip non-existing directories.
            if (log.isDebugEnabled()) {
                log.debug("Skipping [" + rootDir.getAbsolutePath() + "] because it does not exist");
            }
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
            // Complain louder if it exists but is no directory.
            if (log.isWarnEnabled()) {
                log.warn("Skipping [" + rootDir.getAbsolutePath() + "] because it does not denote a directory");
            }
            return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
            if (log.isWarnEnabled()) {
                log.warn("Cannot search for matching files underneath directory [" + rootDir.getAbsolutePath() +
                        "] because the application is not allowed to read the directory");
            }
            return Collections.emptySet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
        if (!pattern.startsWith("/")) {
            fullPattern += "/";
        }
        fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
        Set<File> result = new LinkedHashSet<>(8);
        doRetrieveMatchingFiles(fullPattern, rootDir, result);
        return result;
    }
    /**
     * Recursively retrieve files that match the given pattern,
     * adding them to the given result list.
     * @param fullPattern the pattern to match against,
     * with prepended root directory path
     * @param dir the current directory
     * @param result the Set of matching File instances to add to
     * @throws IOException if directory contents could not be retrieved
     */
    protected void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Searching directory [" + dir.getAbsolutePath() +
                    "] for files matching pattern [" + fullPattern + "]");
        }
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            if (log.isWarnEnabled()) {
                log.warn("Could not retrieve contents of directory [" + dir.getAbsolutePath() + "]");
            }
            return;
        }
        Arrays.sort(dirContents);
        for (File content : dirContents) {
            String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
            if (content.isDirectory() && PATH_MATCHER.matchStart(fullPattern, currPath + "/")) {
                if (!content.canRead()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping subdirectory [" + dir.getAbsolutePath() +
                                "] because the application is not allowed to read the directory");
                    }
                }
                else {
                    doRetrieveMatchingFiles(fullPattern, content, result);
                }
            }
            if (PATH_MATCHER.match(fullPattern, currPath)) {
                result.add(content);
            }
        }
    }
    /**
     * about elasticsearch Server  HttpHost
     * @return
     */
    public abstract HttpHost[] convertServerHttpHost();
    /**
     * about request setHeaders
     * @return
     */
    public abstract Set<Header> convertHeaders();
    /**
     * about failure listener
     * @return
     */
    public abstract RestClient.FailureListener convertFailureListener()throws Exception;
    /**
     * about elasticsearch node selector type
     * @return
     */
    public abstract NodeSelector convertNodeSelector() throws Exception;
    /**
     * @return RequestConfigCallback
     */
    public abstract RestClientBuilder.RequestConfigCallback convertRequestConfigCallback()throws Exception;
    /**
     * @return HttpClientConfigCallback
     */
    public abstract RestClientBuilder.HttpClientConfigCallback convertHttpClientConfigCallback()throws Exception;

}