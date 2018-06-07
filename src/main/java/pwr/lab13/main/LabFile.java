package pwr.lab13.main;

import org.apache.commons.io.FileUtils;

public class LabFile {

    private String fullPath;
    private String fileName;
    private String extension;
    private boolean isDirectory;
    private long sizeBytes;
    private String md5;
    private String sha256;
    private String sha512;

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * @return the isDirectory
     */
    public boolean isIsDirectory() {
        return isDirectory;
    }

    /**
     * @param isDirectory the isDirectory to set
     */
    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    /**
     * @return the fullPath
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * @param fullPath the fullPath to set
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * @return the md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * @param md5 the md5 to set
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * @return the sha256
     */
    public String getSha256() {
        return sha256;
    }

    /**
     * @param sha256 the sha256 to set
     */
    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }


    public String getSha512() {
        return sha512;
    }

    public void setSha512(String sha512) {
        this.sha512 = sha512;
    }

    /**
     * @return the sizeBytes
     */
    public long getSizeBytes() {
        return sizeBytes;
    }

    /**
     * @param sizeBytes the sizeBytes to set
     */
    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return sizeBytes == 0 ? "" : FileUtils.byteCountToDisplaySize(sizeBytes);
    }

}
