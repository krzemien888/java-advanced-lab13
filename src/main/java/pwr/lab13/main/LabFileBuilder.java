package pwr.lab13.main;

import java.io.File;
import java.io.FileInputStream;

public class LabFileBuilder {

    private boolean doMd5;
    private File f;
    private String name;
    private boolean sha256;
    private boolean sha512;

    private LabFileBuilder() {
    }

    public static LabFileBuilder newBuilder(File f, String name) {
        LabFileBuilder lfd = new LabFileBuilder();
        lfd.f = f;
        lfd.name = name;
        return lfd;
    }

    public LabFileBuilder build() {
        return new LabFileBuilder();
    }

    public LabFileBuilder withMd5(boolean calculcate) {
        this.doMd5 = calculcate;
        return this;
    }

    public LabFileBuilder withSha256(boolean calculate) {
        this.sha256 = calculate;
        return this;
    }

    public LabFileBuilder withSha512(boolean calc) {
        this.sha512 = calc;
        return this;
    }

    public LabFile convert() {
        LabFile lf = new LabFile();
        lf.setIsDirectory(f.isDirectory());
        lf.setFileName(name);
        lf.setFullPath(f.getAbsolutePath());
        if (!lf.isIsDirectory()) {
            if (doMd5) {
                lf.setMd5(calculateMd5(f));
            }
            if (sha256) {
                lf.setSha256(calculateSha256(f));
            }
            if (sha512) {
                lf.setSha512(calculateSha512(f));
            }
            lf.setSizeBytes(f.length());
        }
        return lf;
    }

    private String calculateSha256(File f) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            String md5 = org.apache.commons.codec.digest.DigestUtils.sha256Hex(fis);

            return md5;
        } catch (Exception ex) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    private String calculateMd5(File f) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            fis.close();
            return md5;
        } catch (Exception ex) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    private String calculateSha512(File f) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            String md5 = org.apache.commons.codec.digest.DigestUtils.sha512Hex(fis);

            return md5;
        } catch (Exception ex) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

}
