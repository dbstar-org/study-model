package io.github.dbstarll.study.service;

import io.github.dbstarll.dubai.model.service.EntityService;
import io.github.dbstarll.dubai.user.service.attach.PrincipalAttach;
import io.github.dbstarll.study.entity.Subscribe;
import io.github.dbstarll.study.service.attach.SubscribeServiceAttach;

@EntityService
public interface SubscribeService extends StudyServices<Subscribe>, PrincipalAttach<Subscribe>, SubscribeServiceAttach {

}
