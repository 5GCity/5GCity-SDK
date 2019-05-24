package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.Embeddable;

import java.util.Set;
import java.util.HashSet;


/**
 * SwImageData
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "imgName",
    "imgVersion",
    "checksum",
    "containerFormat",
    "diskFormat",
    "minDisk",
    "minRam",
    "minCpu",
    "size"
})


@Embeddable
public class SwImageData {

    private String  imgName;

    private String  imgVersion;

    private String  checksum;

    private String  containerFormat;

    private String  diskFormat;

    private Integer minDisk; // in MB

    private Integer minRam; // in MB

    private Integer minCpu = 1;

    private Integer size; // in MB

    public SwImageData() {
    }

    @JsonProperty("imgName")
    public String getImgName() {
        return imgName;
    }

    @JsonProperty("imgName")
    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    @JsonProperty("imgVersion")
    public String getVersion() {
        return imgVersion;
    }

    @JsonProperty("imgVersion")
    public void setImgVersion(String imgVersion) {
        this.imgVersion = imgVersion;
    }

    @JsonProperty("checksum")
    public String getChecksum() {
        return checksum;
    }

    @JsonProperty("checksum")
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @JsonProperty("containerFormat")
    public String getContainerFormat() {
        return containerFormat;
    }

    @JsonProperty("containerFormat")
    public void setContainerFormat(String containerFormat) {
        this.containerFormat = containerFormat;
    }

    @JsonProperty("diskFormat")
    public String getDiskFormat() {
        return diskFormat;
    }

    @JsonProperty("diskFormat")
    public void setDiskFormat(String diskFormat) {
        this.diskFormat = diskFormat;
    }

    @JsonProperty("minDisk")
    public Integer getMinDisk() {
        return minDisk;
    }

    @JsonProperty("minDisk")
    public void setMinDisk(Integer minDisk) {
        this.minDisk = minDisk;
    }

    @JsonProperty("minRam")
    public Integer getMinRam() {
        return minRam;
    }

    @JsonProperty("minRam")
    public void setMinRam(Integer minRam) {
        this.minRam = minRam;
    }

    @JsonProperty("minCpu")
    public Integer getMinCpu() {
        return minCpu;
    }

    @JsonProperty("minCpu")
    public void setMinCpu(Integer minCpu) {
        this.minCpu = minCpu;
    }

    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SwImageData.class.getName()).append('[');

        sb.append("imgName");
        sb.append('=');
        sb.append(((this.imgName == null)?"<null>":this.imgName));
        sb.append(',');
        sb.append("imgVersion");
        sb.append('=');
        sb.append(((this.imgVersion == null)?"<null>":this.imgVersion));
        sb.append(',');
        sb.append("checksum");
        sb.append('=');
        sb.append(((this.checksum == null)?"<null>":this.checksum));
        sb.append(',');
        sb.append("containerFormat");
        sb.append('=');
        sb.append(((this.containerFormat == null)?"<null>":this.containerFormat));
        sb.append(',');
        sb.append("diskFormat");
        sb.append('=');
        sb.append(((this.diskFormat == null)?"<null>":this.diskFormat));
        sb.append(',');
        sb.append("minDisk");
        sb.append('=');
        sb.append(((this.minDisk == null)?"<null>":this.minDisk));
        sb.append(',');
        sb.append("minRam");
        sb.append('=');
        sb.append(((this.minRam == null)?"<null>":this.minRam));
        sb.append(',');
        sb.append("minCpu");
        sb.append('=');
        sb.append(((this.minCpu == null)?"<null>":this.minCpu));
        sb.append(',');
        sb.append("size");
        sb.append('=');
        sb.append(((this.size == null)?"<null>":this.size));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.imgName == null)? 0 :this.imgName.hashCode()));
        result = ((result* 31)+((this.imgVersion == null)? 0 :this.imgVersion.hashCode()));
        result = ((result* 31)+((this.checksum == null)? 0 :this.checksum.hashCode()));
        result = ((result* 31)+((this.containerFormat == null)? 0 :this.containerFormat.hashCode()));
        result = ((result* 31)+((this.diskFormat == null)? 0 :this.diskFormat.hashCode()));
        result = ((result* 31)+((this.minDisk == null)? 0 :this.minDisk.hashCode()));
        result = ((result* 31)+((this.minRam == null)? 0 :this.minRam.hashCode()));
        result = ((result* 31)+((this.minCpu == null)? 0 :this.minCpu.hashCode()));
        result = ((result* 31)+((this.size == null)? 0 :this.size.hashCode()));

        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SwImageData) == false) {
            return false;
        }
        SwImageData rhs = ((SwImageData) other);
        return super.equals(other) && (
                ((this.imgName == rhs.imgName) || ((this.imgName != null) && this.imgName.equals(rhs.imgName))) &&
                ((this.imgVersion == rhs.imgVersion) || ((this.imgVersion!= null) && this.imgVersion.equals(rhs.imgVersion))) &&
                ((this.checksum == rhs.checksum) || ((this.checksum!= null) && this.checksum.equals(rhs.checksum))) &&
                ((this.containerFormat == rhs.containerFormat) || ((this.containerFormat!= null) && this.containerFormat.equals(rhs.containerFormat))) &&
                ((this.diskFormat == rhs.diskFormat) || ((this.diskFormat!= null) && this.diskFormat.equals(rhs.diskFormat))) &&
                ((this.minDisk == rhs.minDisk) || ((this.minDisk!= null) && this.minDisk.equals(rhs.minDisk))) &&
                ((this.minRam == rhs.minRam) || ((this.minRam!= null) && this.minRam.equals(rhs.minRam))) &&
                ((this.minCpu == rhs.minCpu) || ((this.minCpu!= null) && this.minCpu.equals(rhs.minCpu))) &&
                ((this.size == rhs.size) || ((this.size!= null) && this.size.equals(rhs.size)))
            );
     }

    @JsonIgnore
    public boolean isValid() {
        Set<String> validContainerFormats = new HashSet<String>();
        validContainerFormats.add("aki");
        validContainerFormats.add("ami");
        validContainerFormats.add("ari");
        validContainerFormats.add("bare");
        validContainerFormats.add("docker");
        validContainerFormats.add("ova");
        validContainerFormats.add("ovf");
            /* validContainerFormats = [ aki, ami, ari, bare, docker, ova, ovf]
            The container format describes the container
            file format in which software image is provided.
            Description of valid values:
            aki:a kernel image
            ami: a machine image
            ari: a ramdisk image
            bare: the image does not have a container or  metadata envelope
            docker: docker container format
            ova: OVF package in a tarfile
            ovf: OVF container format
            */

        Set<String> validDiskFormats = new HashSet<String>();
        validDiskFormats.add("aki");
        validDiskFormats.add("ami");
        validDiskFormats.add("ari");
        validDiskFormats.add("iso");
        validDiskFormats.add("qcow2");
        validDiskFormats.add("raw");
        validDiskFormats.add("vdi");
        validDiskFormats.add("vhd");
        validDiskFormats.add("vhdx");
        validDiskFormats.add("vmdk");
            /* validDiskFormats =[ aki, ami, ari, iso, qcow2, raw, vdi, vhd, vhdx, vmdk ]
            aki: a kernel image
            ami: a machine image
            ari: a ramdisk image
            iso: an archive format for the data contents of an optical disc, such as CD-ROM
            qcow2: a common disk image format, which can expand dynamically and supports copy on write
            raw: an unstructured disk image format
            vdi: a common disk image format
            vhd: a common disk image format
            vhdx: enhanced version of VHD format
            vmdk: a common disk image format
            */

        return (((this.imgName != null) && (this.imgName.length() != 0)) &&
                ((this.imgVersion!= null) && (this.imgVersion.length() != 0)) &&
                ((this.checksum!= null) && (this.checksum.length() != 0)) &&
                ((this.containerFormat!= null) && (this.containerFormat.length() != 0) && validContainerFormats.contains(this.containerFormat)) &&
                ((this.diskFormat!= null) && (this.diskFormat.length() != 0) && validDiskFormats.contains(this.diskFormat)) &&
                (this.minDisk > 0) &&
                (this.minRam > 0 ) &&
                (this.minCpu > 0) &&
                (this.size > 0));
    }
}
