package io.github.dbstarll.study.service;

import io.github.dbstarll.dubai.model.service.EntityService;
import io.github.dbstarll.study.entity.TestWordEntity;
import io.github.dbstarll.study.service.attach.WordAttach;

@EntityService
public interface TestWordService extends StudyServices<TestWordEntity>, WordAttach<TestWordEntity> {
}
