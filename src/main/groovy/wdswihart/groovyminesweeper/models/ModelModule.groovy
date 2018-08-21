package wdswihart.groovyminesweeper.models

import com.google.inject.AbstractModule

class ModelModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameEngine).to(GameEngineImpl)
    }
}
