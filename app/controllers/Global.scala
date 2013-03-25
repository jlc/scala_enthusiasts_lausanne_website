package controllers

import play.Logger
import play.api.GlobalSettings
import play.api.libs.concurrent.Akka

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {

    Logger.info("Global.onStart: starting application")

  }

  override def onStop(app: play.api.Application) {
  }

}
