package io.github.dbstarll.study.entity.info;

import io.github.dbstarll.dubai.model.entity.InfoBase;
import org.bson.types.ObjectId;

/**
 * 预置了语音Id字段的信息类实体.
 */
public interface Voiceable extends InfoBase {
    String FIELD_NAME_VOICE_ID = "voiceId";

    /**
     * 获得语音Id.
     *
     * @return 语音Id
     */
    ObjectId getVoiceId();

    /**
     * 设置语音Id.
     *
     * @param voiceId 语音Id
     */
    void setVoiceId(ObjectId voiceId);
}
