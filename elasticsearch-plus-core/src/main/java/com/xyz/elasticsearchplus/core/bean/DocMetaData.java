package com.xyz.elasticsearchplus.core.bean;

import com.xyz.utils.ValidationUtils;
import lombok.Getter;

import java.util.Objects;

/**
 * @author sxl
 * @since 2021/3/3 19:39
 */
@Getter
public class DocMetaData<T> {

    private String index;

    private String type;

    private Class<T> docType;

    private String idSourceName;

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private String index;
        private String type;
        private Class<T> docType;
        private String idSourceName;

        public Builder() {
        }

        public Builder<T> index(String index) {
            ValidationUtils.notBlank(index, "index is required");
            this.index = index;
            return this;
        }

        public Builder<T> idField(String idSourceName) {
            ValidationUtils.notBlank(idSourceName, "idSourceName is required");
            this.idSourceName = idSourceName;
            return this;
        }

        public Builder<T> type(String type) {
            this.type = type;
            return this;
        }

        public Builder<T> docType(Class<T> docType) {
            Objects.requireNonNull(docType, "docType is required");
            this.docType = docType;
            return this;
        }

        public DocMetaData<T> build() {
            DocMetaData<T> docMetaData = new DocMetaData<>();
            docMetaData.index = this.index;
            docMetaData.docType = this.docType;
            docMetaData.type = this.type;
            docMetaData.idSourceName = this.idSourceName;
            return docMetaData;
        }
    }
}
