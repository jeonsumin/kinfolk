package com.terry.backend.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terry.backend.core.security.util.SessionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serializable;
import java.sql.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseDTO implements Serializable {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private boolean useYn;

    private Date createDt;

    @JsonIgnore
    private String createId;

    private Date updateDt;

    @JsonIgnore
    private String updateId;


    public String getCreateBy(){
        return SessionUtils.getMetaUsername(createId);
    }

    public String getUpdateBy(){
        return SessionUtils.getMetaUsername(updateId);
    }

}
