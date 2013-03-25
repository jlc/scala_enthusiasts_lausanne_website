package controllers

import play.Logger
import play.api.{Application, GlobalSettings}
import play.api.libs.concurrent.Akka

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    Logger.info("Global.onStart: starting application")

  }

  override def onStop(app: Application) {
  }

}
