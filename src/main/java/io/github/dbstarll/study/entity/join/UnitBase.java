package io.github.dbstarll.study.entity.join;

import io.github.dbstarll.dubai.model.entity.JoinBase;
import org.bson.types.ObjectId;

/**
 * 用于关联所属的单元.
 */
public interface UnitBase extends JoinBase {
    String FIELD_NAME_UNIT_ID = "unitId";

    /**
     * 获得单元Id.
     *
     * @return 单元Id
     */
    ObjectId getUnitId();

    /**
     * 设置单元Id.
     *
     * @param unitId 单元Id
     */
    void setUnitId(ObjectId unitId);
}
