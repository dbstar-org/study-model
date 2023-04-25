package io.github.dbstarll.study.service.impl;

import io.github.dbstarll.dubai.model.collection.Collection;
import io.github.dbstarll.dubai.model.service.validate.Validate;
import io.github.dbstarll.dubai.model.service.validation.GeneralValidation;
import io.github.dbstarll.dubai.model.service.validation.Validation;
import io.github.dbstarll.study.entity.Subscribe;
import io.github.dbstarll.study.entity.enums.SubscribeType;
import io.github.dbstarll.study.service.SubscribeService;
import io.github.dbstarll.study.service.attach.SubscribeServiceAttach;

import java.util.Objects;

public final class SubscribeServiceImplemental extends StudyImplementals<Subscribe, SubscribeService>
        implements SubscribeServiceAttach {
    /**
     * 构建SubscribeServiceImplemental.
     *
     * @param service    service
     * @param collection collection
     */
    public SubscribeServiceImplemental(final SubscribeService service, final Collection<Subscribe> collection) {
        super(service, collection);
    }

    /**
     * 订阅项的一致性检查.
     *
     * @return finalSubscribeValidation
     */
    @GeneralValidation
    public Validation<Subscribe> finalSubscribeValidation() {
        return new AbstractEntityValidation() {
            @Override
            public void validate(final Subscribe entity, final Subscribe original, final Validate validate) {
                if (original != null) {
                    if (!Objects.equals(entity.getType(), original.getType())) {
                        validate.addFieldError(Subscribe.FIELD_NAME_SUBSCRIBE_TYPE, "订阅类型后不得修改");
                    }
                    if (!Objects.equals(entity.getModule(), original.getModule())) {
                        validate.addFieldError(Subscribe.FIELD_NAME_MODULE, "订阅的模块设置后不得修改");
                    }
                    if (!Objects.equals(entity.getPage(), original.getPage())) {
                        validate.addFieldError(Subscribe.FIELD_NAME_PAGE, "订阅的页面设置后不得修改");
                    }
                    if (!Objects.equals(entity.getEntityId(), original.getEntityId())) {
                        validate.addFieldError(Subscribe.FIELD_NAME_ENTITY_ID, "订阅的实体ID设置后不得修改");
                    }
                } else if (entity.getType() == null) {
                    validate.addFieldError(Subscribe.FIELD_NAME_SUBSCRIBE_TYPE, "订阅类型未设置");
                } else if (entity.getType() == SubscribeType.PAGE) {
                    validatePageSubscribe(entity, validate);
                } else {
                    validateEntitySubscribe(entity, validate);
                }
            }

            private void validatePageSubscribe(final Subscribe entity, final Validate validate) {
                if (entity.getPage() == null) {
                    validate.addFieldError(Subscribe.FIELD_NAME_PAGE, "订阅的页面未设置");
                } else if (entity.getEntityId() != null) {
                    validate.addFieldError(Subscribe.FIELD_NAME_ENTITY_ID, "PAGE类型的订阅不应设置订阅的实体ID");
                } else {
                    entity.setModule(entity.getPage().getModule());
                }
            }

            private void validateEntitySubscribe(final Subscribe entity, final Validate validate) {
                if (entity.getModule() == null) {
                    validate.addFieldError(Subscribe.FIELD_NAME_MODULE, "订阅的模块未设置");
                }
                if (entity.getEntityId() == null) {
                    validate.addFieldError(Subscribe.FIELD_NAME_ENTITY_ID, "订阅的实体ID未设置");
                }
                if (entity.getPage() != null) {
                    validate.addFieldError(Subscribe.FIELD_NAME_PAGE, "ENTITY类型的订阅不应设置订阅的页面");
                }
            }
        };
    }
}
