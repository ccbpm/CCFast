package bp.tools;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public final class ZipCompress {

    private String zipFileName;      // 目的地Zip文件
    private String sourceFileName;   //源文件（带压缩的文件或文件夹）

    public ZipCompress(String sourceFileName,String zipFileName)
    {
        this.zipFileName=zipFileName;
        this.sourceFileName=sourceFileName;
    }

    public  void zip(){
        zip(true);
    }
    /**s
     * 压缩文件
     * param srcFilePath 压缩源路径
     * param destFilePath 压缩目的路径
     */
    public  void zip(boolean KeepDirStructure) {
        //
        File src = new File(zipFileName);

        if (!src.exists()) {
            throw new RuntimeException(zipFileName + "不存在");
        }
        File zipFile = new File(sourceFileName);

        try {

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            String baseDir = "";
            compressbyType(src, zos, baseDir,KeepDirStructure);
            zos.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }
    /**
     * 按照原路径的类型就行压缩。文件路径直接把文件压缩，
     * param src
     * param zos
     * param baseDir
     */
    private static void compressbyType(File src, ZipOutputStream zos,String baseDir,boolean  KeepDirStructure) {

        if (!src.exists())
            return;
        System.out.println("压缩路径" + baseDir + src.getName());
        //判断文件是否是文件，如果是文件调用compressFile方法,如果是路径，则调用compressDir方法；
        if (src.isFile()) {
            //src是文件，调用此方法
            compressFile(src, zos, baseDir);

        } else if (src.isDirectory()) {
            //src是文件夹，调用此方法
            compressDir(src, zos, baseDir,KeepDirStructure);

        }

    }

    /**
     * 压缩文件
     */
    private static void compressFile(File file, ZipOutputStream zos,String baseDir) {
        if (!file.exists())
            return;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zos.putNextEntry(entry);
            int count;
            byte[] buf = new byte[1024];
            while ((count = bis.read(buf)) != -1) {
                zos.write(buf, 0, count);
            }
            bis.close();

        } catch (Exception e) {
            // TODO: handle exception

        }
    }

    /**
     * 压缩文件夹
     */
    private static void compressDir(File dir, ZipOutputStream zos,String baseDir,boolean  KeepDirStructure) {
        if (!dir.exists())
            return;
        File[] files = dir.listFiles();
        if(files.length == 0){
            try {
                zos.putNextEntry(new ZipEntry(baseDir + dir.getName()+File.separator));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File file : files) {
            if(KeepDirStructure==true)
                compressbyType(file, zos, baseDir + dir.getName() + File.separator,true);
            else
                compressbyType(file, zos,"" ,true);
        }
    }


}
