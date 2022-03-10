package com.eu.frame.common.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.eu.frame.common.thread.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class FieldFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        try {
            this.setFieldValByName("createBy", CurrentUser.getId(), metaObject);
            this.setFieldValByName("updateBy", CurrentUser.getId(), metaObject);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        try {
            this.setFieldValByName("updateBy", CurrentUser.getId(), metaObject);
        } catch (Exception ignored) {

        }
    }

}
