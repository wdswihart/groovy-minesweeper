package wdswihart.groovyminesweeper.factories

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

class FactoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CellFactory).to(CellFactoryImpl)
        bind(ObservableFactory).to(ObservableFactoryImpl)

        install(new FactoryModuleBuilder().build(ModelFactory))
    }
}
