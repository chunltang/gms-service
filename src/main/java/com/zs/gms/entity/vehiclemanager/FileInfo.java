package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileInfo implements Serializable {

    private String fileName;

    private String filePath;

    private long fileSize;

    /**
     * 压缩文件类型
     * */
    public enum CompressedFileType implements IEnum {

        ZIP("zip"),
        RAR("rar");

        public String value;

        CompressedFileType(String value) {
            this.value = value;
        }

        @Override
        public Serializable getValue() {
            return value;
        }

        public static CompressedFileType getType(String value){
            for (CompressedFileType type : CompressedFileType.values()) {
                if(type.getValue().equals(value)){
                    return type;
                }
            }
            return null;
        }
    }

}
