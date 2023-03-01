package io.github.dbstarll.study.service;

import io.github.dbstarll.dubai.model.service.EntityService;
import io.github.dbstarll.study.entity.TestUnitEntity;
import io.github.dbstarll.study.service.attach.UnitAttach;

@EntityService
public interface TestUnitService extends StudyServices<TestUnitEntity>, UnitAttach<TestUnitEntity> {
}
