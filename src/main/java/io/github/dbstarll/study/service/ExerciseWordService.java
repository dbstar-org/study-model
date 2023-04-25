package io.github.dbstarll.study.service;

import io.github.dbstarll.dubai.model.service.EntityService;
import io.github.dbstarll.study.entity.ExerciseWord;
import io.github.dbstarll.study.service.attach.ExerciseBookAttach;
import io.github.dbstarll.study.service.attach.ExerciseWordServiceAttach;
import io.github.dbstarll.study.service.attach.WordAttach;

@EntityService
public interface ExerciseWordService extends StudyServices<ExerciseWord>, ExerciseBookAttach<ExerciseWord>,
        WordAttach<ExerciseWord>, ExerciseWordServiceAttach {

}
