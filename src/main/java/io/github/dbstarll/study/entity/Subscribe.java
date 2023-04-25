package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.func.Cacheable;
import io.github.dbstarll.dubai.user.entity.join.PrincipalBase;
import io.github.dbstarll.study.entity.enums.Module;
import io.github.dbstarll.study.entity.enums.Page;
import io.github.dbstarll.study.entity.enums.SubscribeType;
import org.bson.types.ObjectId;

import java.util.Date;

@Table
public interface Subscribe extends StudyEntities, Cacheable, PrincipalBase {
    String FIELD_NAME_SUBSCRIBE_TYPE = "type";
    String FIELD_NAME_MODULE = "module";
    String FIELD_NAME_PAGE = "page";
    String FIELD_NAME_ENTITY_ID = "entityId";

    /**
     * 获得订阅类型.
     *
     * @return 订阅类型
     */
    SubscribeType getType();

    /**
     * 设置订阅类型.
     *
     * @param type 订阅类型
     */
    void setType(SubscribeType type);

    /**
     * 获得订阅的模块.
     *
     * @return 订阅的模块
     */
    Module getModule();

    /**
     * 设置订阅的模块.
     *
     * @param module 订阅的模块
     */
    void setModule(Module module);

    /**
     * 获得订阅的页面.
     *
     * @return 订阅的页面
     */
    Page getPage();

    /**
     * 设置订阅的页面.
     *
     * @param page 订阅的页面
     */
    void setPage(Page page);

    /**
     * 获得订阅的实体ID.
     *
     * @return 订阅的实体ID
     */
    ObjectId getEntityId();

    /**
     * 设置订阅的实体ID.
     *
     * @param entityId 订阅的实体ID
     */
    void setEntityId(ObjectId entityId);

    /**
     * 获得订阅开始时间.
     *
     * @return 订阅开始时间
     */
    Date getStart();

    /**
     * 设置订阅开始时间.
     *
     * @param start 订阅开始时间
     */
    void setStart(Date start);

    /**
     * 获得订阅结束时间.
     *
     * @return 订阅结束时间
     */
    Date getEnd();

    /**
     * 设置订阅结束时间.
     *
     * @param end 订阅结束时间
     */
    void setEnd(Date end);
}
