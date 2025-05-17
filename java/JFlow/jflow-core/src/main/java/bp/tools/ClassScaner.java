package bp.tools;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClassScaner implements ResourceLoaderAware {

    //保存过滤规则要排除的注解
    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();
    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    public static Set<Class<?>> scan(String[] basePackages, Class<?> parent) {
        ClassScaner cs = new ClassScaner();

        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (String s : basePackages)
            classes.addAll(cs.doScan(s,parent));
        return classes;
    }

    public static Set<Class<?>> scan(String basePackages, Class<?> parent) {
        return ClassScaner.scan(StringUtils.tokenizeToStringArray(basePackages, ",; \t\n"), parent);
    }

    public final ResourceLoader getResourceLoader() {
        return this.resourcePatternResolver;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils
                .getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(
                resourceLoader);
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    public void resetFilters(boolean useDefaultFilters) {
        this.includeFilters.clear();
        this.excludeFilters.clear();
    }

    public Set<Class<?>> doScan(String basePackage, Class<?> parent) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils
                    .convertClassNameToResourcePath(SystemPropertyUtils
                            .resolvePlaceholders(basePackage))
                    + "/**/*.class";
            Resource[] resources = this.resourcePatternResolver
                    .getResources(packageSearchPath);
            ClassUtils.Test test = new ClassUtils.IsA(parent);

            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                String path = resource.getDescription().replace("\\","/");
                if(path.indexOf("bp/dts")!=-1 ||path.indexOf("bp/difference")!=-1||path.indexOf("bp/tools")!=-1 || path.indexOf("bp/wf/dts")!=-1 || path.indexOf("bp/wf/httphandler")!=-1)
                    continue;
                if (resource.isReadable()) {

                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    if ((includeFilters.size() == 0 && excludeFilters.size() == 0)
                            || matches(metadataReader)) {
                        try {
                            Class<?> type = Class.forName(metadataReader.getClassMetadata().getClassName());
                            if (test.matches(type)){
                                classes.add(type);
                            }


                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                   "I/O failure during classpath scanning", ex);
        }
        return classes;
    }

    protected boolean matches(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        /*Class parent = null;
        try {
            parent = Class.forName("bp.en.*;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ClassScaner.scan("BP", parent)
                .forEach(clazz -> System.out.println(clazz));*/
        String path="DataUser/XML/AdminMenu.xml";
        ClassPathResource classPathResource = new ClassPathResource(path);
        String line = null;
        try {
            InputStream inputStream = classPathResource.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            boolean firstLine = true;
            while((line = bufferedReader.readLine()) != null){
                if(!firstLine){
                    stringBuilder.append(System.getProperty("line.separator"));
                }else{
                    firstLine = false;
                }
                stringBuilder.append(line);
            }
            System.out.println(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


       /* try {
            //String line = null;
            StringBuffer strBuffer = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)));
            StringBuffer buffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            in.close();
            System.out.println(buffer.toString());
        }catch(Exception e){
                System.out.println(e.getMessage());
        }*/
    }
}
