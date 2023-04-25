package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.func.Cacheable;
import io.github.dbstarll.dubai.model.entity.info.Sourceable;
import io.github.dbstarll.study.entity.enums.Mode;

import java.util.Map;

@Table
public interface Principal extends StudyEntities, Cacheable, Sourceable {
    /**
     * 获取主体的模式.
     *
     * @return 模式
     */
    Mode getMode();

    /**
     * 设置主体的模式.
     *
     * @param mode 模式
     */
    void setMode(Mode mode);

    /**
     * 获取用户信息.
     *
     * @return 用户信息
     */
    Map<String, Object> getUserInfo();

    /**
     * 设置用户信息.
     *
     * @param userInfo 用户信息
     */
    void setUserInfo(Map<String, Object> userInfo);
}
