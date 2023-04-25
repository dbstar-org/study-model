package io.github.dbstarll.study.entity;

import io.github.dbstarll.dubai.model.entity.Table;
import io.github.dbstarll.dubai.model.entity.func.Cacheable;
import io.github.dbstarll.dubai.model.entity.info.Namable;
import io.github.dbstarll.study.entity.join.BookBase;
import io.github.dbstarll.study.entity.join.UnitBase;
import io.github.dbstarll.study.entity.join.WordBase;

@Table
public interface UnitWord extends StudyEntities, Cacheable, Namable, BookBase, UnitBase, WordBase {

}
