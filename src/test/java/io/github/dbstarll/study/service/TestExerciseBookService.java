package io.github.dbstarll.study.service;

import io.github.dbstarll.dubai.model.service.EntityService;
import io.github.dbstarll.study.entity.TestExerciseBookEntity;
import io.github.dbstarll.study.service.attach.ExerciseBookAttach;

@EntityService
public interface TestExerciseBookService extends StudyServices<TestExerciseBookEntity>,
        ExerciseBookAttach<TestExerciseBookEntity> {
}
