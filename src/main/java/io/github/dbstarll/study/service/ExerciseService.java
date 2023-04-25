package io.github.dbstarll.study.service;

import io.github.dbstarll.dubai.model.service.EntityService;
import io.github.dbstarll.study.entity.Exercise;
import io.github.dbstarll.study.service.attach.ExerciseBookAttach;
import io.github.dbstarll.study.service.attach.ExerciseServiceAttach;
import io.github.dbstarll.study.service.attach.WordAttach;

@EntityService
public interface ExerciseService extends StudyServices<Exercise>, ExerciseBookAttach<Exercise>, WordAttach<Exercise>,
        ExerciseServiceAttach {

}
