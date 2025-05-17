package bp.difference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class Helper {

	/**
	 * @return
	 * @throws IOException
	 */
	public static InputStream loadResource() throws IOException {
		InputStream is;
		ResourceLoader loader = new DefaultResourceLoader();
		Resource resource = loader.getResource("jflow.properties");
		is = resource.getInputStream();
		return is;
	}

	/**
	 * 获取classspath 路径
	 * @return
	 */
	public static String getResourcePath() {
		String resourcePath="";
		try {
			ResourceLoader resourceLoader = new DefaultResourceLoader();
			// 获取指定资源文件的路径
			Resource resource = resourceLoader.getResource("classpath:/");
			if (resource != null && resource.exists()) {
//                System.out.println("资源路径：" + resource.getURI());
				// 如果想获取资源的真实物理路径（仅限于本地文件系统）
				File file = resource.getFile();
//                System.out.println("资源物理路径：" + file.getAbsolutePath());
				resourcePath= file.getAbsolutePath();
			} else {
				System.err.println("未找到该资源");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resourcePath+"\\";
	}
}
