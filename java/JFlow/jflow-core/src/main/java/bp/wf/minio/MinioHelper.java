package bp.wf.minio;

import bp.da.DataType;
import bp.da.Log;
import bp.da.LogType;
import bp.sys.FrmAttachmentDB;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class MinioHelper {

    private  static MinioClient minioClient;

    public  static  String host="";
    public  static  String accessKey="";
    public  static  String secretKey="";
    public  static  String bucket="";

    public  MinioHelper(){

    }

    static {

        if(minioClient==null){
            try {
                host= bp.difference.SystemConfig.getAppSettings().get("minio_host").toString();
                accessKey= bp.difference.SystemConfig.getAppSettings().get("minio_accessKey").toString();
                secretKey= bp.difference.SystemConfig.getAppSettings().get("minio_secretKey").toString();
                bucket= bp.difference.SystemConfig.getAppSettings().get("minio_bucket").toString();
                if(!DataType.IsNullOrEmpty(host) && !DataType.IsNullOrEmpty(accessKey) && !DataType.IsNullOrEmpty(secretKey) && !DataType.IsNullOrEmpty(bucket)){
                    minioClient = new MinioClient(host, accessKey, secretKey);
                }
            } catch (InvalidEndpointException e) {
                System.out.println(e);
                Log.DefaultLogWriteLine(LogType.Error,e.getMessage());
            } catch (InvalidPortException e) {
                System.out.println(e);
                Log.DefaultLogWriteLine(LogType.Error,e.getMessage());
            }catch (Exception ex){
                System.out.println(ex);
                Log.DefaultLogWriteLine(LogType.Error,ex.getMessage());
            }
        }
    }
    public static   String putObject(MultipartFile multipartFile,String savePath) throws Exception {
        // bucket 不存在，创建
        if (!minioClient.bucketExists(bucket)) {
            minioClient.makeBucket(bucket);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 上传文件的名称
            String fileName = savePath+multipartFile.getOriginalFilename();
            // PutObjectOptions，上传配置(文件大小，内存中文件分片大小)
            PutObjectOptions putObjectOptions = new PutObjectOptions(multipartFile.getSize(), PutObjectOptions.MIN_MULTIPART_SIZE);
            // 文件的ContentType
            putObjectOptions.setContentType(multipartFile.getContentType());
            minioClient.putObject(bucket, fileName, inputStream, putObjectOptions);
            // 返回访问路径
            return URLDecoder.decode(getObjectUrl(bucket,fileName)).replace(host+"/"+bucket+"/","");

        }catch (Exception ex){
            System.out.println(ex);
            Log.DefaultLogWriteLine(LogType.Error,ex.getMessage());
            return null;
        }
    }

    /***
     * 判断文件是否存在
     * @param objectName
     * @return
     */
    public static boolean isObjectExist(String objectName) {
        boolean exist = true;
        try {
            minioClient.statObject(bucket,objectName);
        } catch (Exception e) {
            System.out.println(e);
            Log.DefaultLogWriteLine(LogType.Error,e.getMessage());
            exist = false;
        }
        return exist;
    }
    /**
     * 文件下载
     */
    public  static  void download(String fileUrl, String contentType, String filePath){
        // 从链接中得到文件名
        InputStream inputStream;
        try {
            inputStream = minioClient.getObject(bucket, fileUrl);
            OutputStream outStream = new FileOutputStream(filePath);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
        } catch (Exception e){
            System.out.println("有异常：" + e);
            e.printStackTrace();
            Log.DefaultLogWriteLine(LogType.Error,e.getMessage());
        }
    }
    /***
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws Exception
     */
    public  static  String getObjectUrl(String bucketName, String objectName) throws Exception {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            url = minioClient.getObjectUrl(bucketName, objectName);
        }
        return url;
    }

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public static  boolean bucketExists(String bucketName) throws Exception {
        boolean flag = minioClient.bucketExists(bucketName);
        if (flag) {
            return true;
        }
        return false;
    }

}
