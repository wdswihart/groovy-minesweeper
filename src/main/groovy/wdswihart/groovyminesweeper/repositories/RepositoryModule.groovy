package wdswihart.groovyminesweeper.repositories

import com.google.inject.AbstractModule

class RepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PropertiesRepository).to(PropertiesRepositoryImpl)
        bind(ScoreRepository).to(ScoreRepositoryImpl)
        bind(SaveRepository).to(SaveRepositoryImpl)
    }
}
