package io.github.dbstarll.study.service;

import io.github.dbstarll.dubai.model.service.EntityService;
import io.github.dbstarll.study.entity.TestBookEntity;
import io.github.dbstarll.study.service.attach.BookAttach;

@EntityService
public interface TestBookService extends StudyServices<TestBookEntity>, BookAttach<TestBookEntity> {
}
