package ninja.droiddojo.rickandmorty.analytics.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ninja.droiddojo.rickandmorty.analytics.AnalyticsTracker
import ninja.droiddojo.rickandmorty.analytics.AppLogger
import ninja.droiddojo.rickandmorty.analytics.LogcatAnalyticsTracker
import ninja.droiddojo.rickandmorty.analytics.LogcatLogger

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(impl: LogcatAnalyticsTracker): AnalyticsTracker

    @Binds
    @Singleton
    abstract fun bindAppLogger(impl: LogcatLogger): AppLogger
}
