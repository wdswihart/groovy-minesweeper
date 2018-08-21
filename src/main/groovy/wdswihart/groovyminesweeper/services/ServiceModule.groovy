package wdswihart.groovyminesweeper.services

import com.google.inject.AbstractModule

class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NavigationService).to(NavigationServiceImpl)
    }
}
